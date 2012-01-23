package logic;

import java.util.Enumeration;
import java.util.Hashtable;

import parser.SQLParser;
import parser.view.CreateView;
import parser.view.TableMap;
import datastructs.Node;
import datastructs.UpdateQuery;
import datastructs.ViewTable;
import driver.DBConn;

public class UVController {
	// Data Structure
	private Hashtable<String, CreateView> VA;

	public UVController() {
		VA = new Hashtable<String, CreateView>();
	}

	public String UVControl(String SqlQuery) throws Exception {
		// Declarations
		SQLParser sp = new SQLParser(SqlQuery);
		int queryType = sp.getQueryType();
		boolean isValid = false;

		if (queryType > SQLParser.SELECT) { // Handle Insert, Delete, Update
			isValid = QueryProcessor(sp, queryType);
			if (isValid == true) { // QueryProcessor pass the query
				return "Operation has been successfully performed!";
			} else
				return "Operation failed!";
		} // Handle select statement
		else if (queryType == SQLParser.SELECT) {
			String str = "";
			str = generateResult(SqlQuery);
			return str;
		} else { // Handle Create Statement
			if (SqlQuery.toUpperCase().startsWith("CREATE VIEW")) {
				try {
					isValid = DBConn.queryNCDB(SqlQuery);
					if (isValid == true) {
						CreateView cv = new CreateView(SqlQuery);
						String ViewName = cv.getViewName();
						VA.put(ViewName, cv);
						return outputView(cv);
					} else
						return "Error in Creating View";
				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (SqlQuery.toUpperCase().startsWith("CREATE TABLE")) {
				try {
					isValid = DBConn.queryNCDB(SqlQuery);
					if (isValid == true) {
						return "Table is created";
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (SqlQuery.toUpperCase().startsWith("DROP VIEW ")) {
			    try {
                    isValid = DBConn.queryNCDB(SqlQuery);
                    VA.remove(SqlQuery.substring(10, SqlQuery.length()));
                    if (isValid == true) {
                        return "View is deleted";
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
			} else if (SqlQuery.toUpperCase().startsWith("DROP TABLE TEMP")) {
			    try {
                    DBConn.queryDB("DROP TABLE TEMP");
                } catch (Exception e) {
                    e.printStackTrace();
                }
			} else
				return "Invalid SQL Query!";
		}
		return null;
	}

	private String outputView(CreateView cv) {
		String ret;
		ret = "View: " + cv.getViewName()
				+ " is created with the following column(s): ";
		for (int i = 0; i < cv.getPhyMap().length; i++) {
			TableMap t = cv.getPhyMap()[i];
			for (int j = 0; j < t.getTotalColumns(); j++) {
				ret += t.getColumnAtIndex(j) + ",";
			}
		}
		return ret;
	}

	private Hashtable<String, String> getAttributesToValues(SQLParser sp) {
		String[] attributes;
		String[] values;
		Hashtable<String, String> map = new Hashtable<String, String>();
		attributes = sp.getColumns();
		values = sp.getValues();
		for (int i = 0; i < attributes.length; i++) {
			map.put(attributes[i], values[i]);
		}
		return map;
	}

	private String generateResult(String SqlQuery) {
		String str = "";
		ViewTable v = DBConn.querySelectDB(SqlQuery);
		for (int i = 0; i < v.colNames.length; i++)
			str += v.colNames[i] + "\t";
		str += "\n";
		System.out.println(v);
		return str;
	}

	private boolean QueryProcessor(SQLParser sp, int queryType)
			throws Exception {
		CreateView v;
		String[] pkVal = new String[2];
		//System.out.println(sp.getTable());
		v = VA.get(sp.getTable());
		//System.out.println(v);
		UpdateQuery uq = new UpdateQuery();

		String pk = "";
		//System.out.println("len" +v.getPhyMap().length);
		for (int i = 0; i < v.getPhyMap().length; i++) {
			String tempPK = DBConn.getPrimaryKey(v.getPhyMap()[i]
					.getTableName());
			//System.out.println("tempPK: "+tempPK);
			if (v.getPhyMap()[i].getColumnAtIndex(0).equals("*")) {
				pk = tempPK;
				break;
			} else {
				for (int j = 0; j < v.getPhyMap()[i].getTotalColumns(); j++) {
					if (v.getPhyMap()[i].getColumnAtIndex(j).equalsIgnoreCase(tempPK)) {
						pk = tempPK;
						break;
					} else if (v.getPhyMap()[i].getColumnAtIndex(j).equalsIgnoreCase(
							tempPK + "_0")) {
						pk = tempPK;
						break;
					}
				}
			}
		}

		if (queryType == SQLParser.INSERT) {
			// find primary key in database
			String[] columnName = sp.getColumns();
			String[] columnVal = sp.getValues();
			
			for (int i = 0; i < columnName.length; i++) {
				//System.out.println(columnName[i].toLowerCase() + " " + pk.toLowerCase());
				if (columnName[i].toLowerCase().startsWith(pk.toLowerCase()+"_") || columnName[i].equalsIgnoreCase(pk)) {
					// key-value pair
					pkVal[0] = pk;
					pkVal[1] = columnVal[i];
					break;
				}
			}
			
			Hashtable<String, String> map = getAttributesToValues(sp);
			Node queryCondTree = sp.getCondTree();
			Node viewCondTree = v.getCondTree();

			if (queryCondTree != null)
				queryCondTree.setAttributeToValues(map);
			if (viewCondTree != null)
				viewCondTree.setAttributeToValues(map);
			//System.out.println(pkVal[0]);
			uq = new UpdateQuery(v, sp, pkVal);
		} else if (queryType == SQLParser.UPDATE) {
			// Parse mapping of AttributesToValues to CondTree Node
			Hashtable<String, String> map = getAttributesToValues(sp);
			Node queryCondTree = sp.getCondTree();
			Node viewCondTree = v.getCondTree();
			if (queryCondTree != null)
				queryCondTree.setAttributeToValues(map);
			if (viewCondTree != null)
				viewCondTree.setAttributeToValues(map);
			//System.out.println("Successful setAttributeToValues");
			pkVal[0] = pk;
			//pkVal[0] = Pattern.compile("[_]+").split(pk)[0];
			uq = new UpdateQuery(v, sp, pkVal);
		} else { // DELETE
			pkVal[0] = pk;
			uq = new UpdateQuery(v, sp, pkVal);
		}
		QueryProcessor qp = new QueryProcessor(uq);
		qp.debug = 0;
		if (v.isJoin()) {
			switch (queryType) {
			case 1:
				return qp.doJoinInsert();
			case 2:
				return qp.doJoinDelete();
			case 3:
				boolean p;
				p = qp.doJoinUpdate();
				/*if (p)
					//System.out.println("UPDATE SUCESSFUL");
				else
					System.out.println("UPDATE FAILED");*/
				return p;
			default:
				return false;
			}
		} else {
			switch (queryType) {
			case 1:
				return qp.doInsert();
			case 2:
				return qp.doDelete();
			case 3:
				boolean p;
				p = qp.doUpdate();
				/*if (p)
					System.out.println("UPDATE SUCESSFUL");
				else
					System.out.println("UPDATE FAILED");*/
				return p;
			default:
				return false;
			}
		}
	}
	public void dropAllView(){
		Enumeration<String> e = VA.keys();
		while (e.hasMoreElements()){
			try {
				DBConn.queryDB("DROP VIEW "+e.nextElement().toString());
			} catch (Exception e1) {}
		}
			
	}
}
