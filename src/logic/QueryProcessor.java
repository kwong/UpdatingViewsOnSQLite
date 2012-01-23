package logic;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import datastructs.BoolNode;
import datastructs.Node;
import datastructs.UpdateQuery;
import driver.DBConn;
import parser.*;
import parser.view.CreateView;
import parser.view.TableMap;

/*         -------------------
 *         ** Preliminaries **
 *         ===================
 *   The query processor handles the translation of updates to external schema
 *   to update to the physical schema. In order to fulfill these 
 *   requests, the query processor requires information about the following:
 *          <Type> Name (description)
 *       1. <ConditionTree.Node>ViewCondTree (predicates of which the view was defined)
 *       2. <ConditionTree.Node>WhereCondTree (in the case of insert/update with restriction)
 *       3. <String>UpdateQueryWhereString (to facilitate transforming of update queries)
 *       4. <String>ViewWhereString (to facilitate transforming of update queries).
 *       5. <String>View Name (defining name of view)
 *       6. <String>AttributeAssignments (i.e. UPDATE [TableName] SET [AttributeAssignments] WHERE ...)
 *       7  <String:String>[TableName]s (that the view/query uses) mapped to [ColumnNames] involved.
 *       			- Good to have [ColumnNames] as the query substring itself.
 *       8. <String>ColumnNames (i.e. INSERT INTO [TableName] [ColumnNames] VALUES [ValueString]
 *       9. <String>ValuesString (i.e. INSERT INTO [TableName] [ColumnNames] VALUES [ValueString]
 *      10. <String>primaryKeyOfView (i.e. the primary key of the View)
 *   
 *       
 */

public class QueryProcessor {

	private Node viewTree;
	// private Node whereTree;
	private String updateWhereString;
	private String viewWhereString;
	private String viewName;
	private String attributeAssignments;
	private String valuesString;
	private String primaryKeyOfView;
	private SQLParser sqlparser;
	private CreateView cv;
	public static int debug = 1;
	private String physicalTable;
	private String[] pkey_info;

	public QueryProcessor(UpdateQuery uq) {
		sqlparser = uq.getSp();
		cv = uq.getView();
		viewTree = cv.getCondTree();
		// whereTree = sqlparser.getCondTree();
		updateWhereString = sqlparser.getConditionString();
		viewWhereString = cv.getCondString();
		valuesString = sqlparser.getValString();
		pkey_info = uq.getPkVal();
		primaryKeyOfView = pkey_info[0];
		physicalTable = cv.getPhyMap()[0].getTableName();
		attributeAssignments = sqlparser.getColString();
		viewName = cv.getViewName();

	}

	public boolean doInsert() throws Exception {
		if (viewTree != null)
			viewTree.setEvalType(SQLParser.INSERT);
		// viewTree.setEvalType(SQLParser.INSERT);
		if (fulfilsDefiningPred() == false) {
			return false;
		}

		String query = "";

		query = "INSERT INTO " + physicalTable + "(" + sqlparser.getColString()
				+ ") VALUES (" + valuesString + ");";
		debug(query);
		return DBConn.queryDB(query);

	}

	public boolean doDelete() throws Exception {

		String query = "";
		String end_clause = "";
		if (viewWhereString == null && updateWhereString == null)
			end_clause = "";
		if (viewWhereString != null && updateWhereString == null)
			end_clause = viewWhereString;
		if (viewWhereString == null && updateWhereString != null)
			end_clause = updateWhereString;
		if (viewWhereString != null && updateWhereString != null)
			end_clause = viewWhereString + " AND " + updateWhereString;

		query = "DELETE FROM " + physicalTable + " WHERE " + end_clause;
		debug(query);
		return DBConn.queryDB(query);

	}

	public boolean doUpdate() throws Exception {
		if (viewTree != null)
			viewTree.setEvalType(SQLParser.UPDATE);
		if (fulfilsDefiningPred() == false) { // !
			return false;
		}
		String query = "";
		// String physicalTable = "";

		String end_clause = "";
		if (viewWhereString == null && updateWhereString == null)
			end_clause = "";
		if (viewWhereString != null && updateWhereString == null)
			end_clause = viewWhereString;
		if (viewWhereString == null && updateWhereString != null)
			end_clause = updateWhereString;
		if (viewWhereString != null && updateWhereString != null)
			end_clause = viewWhereString + " AND " + updateWhereString;

		query = "UPDATE " + physicalTable + " SET " + attributeAssignments
				+ " WHERE " + end_clause;

		debug(query);
		return DBConn.queryDB(query);

	}

	// Update rules for J = A JOIN B
	// ----------------------------------
	// INSERT: The new row j must satisfy PJ. If the A-portion of j does not
	// appear
	// in A, it is inserted in A. * if the B-portion of j does not appear in B,
	// it is inserted into B

	private static String formAttributeAssignmentChain(IndexAssoc ia,
			String[] attr, String[] values) {
		ArrayList<Integer> indices = ia.indices;
		String query = "";

		int i = 0;

		for (; i < (indices.size()); i++) {
			query = query + /* stripSuffix */(attr[indices.get(i)]) + "="
					+ values[indices.get(i)].trim() + ", ";

		}

		query = query.substring(0, (query.length() == 0) ? 0
				: query.length() - 2);
		return query;
	}

	private static String formWhereAssignmentChain(IndexAssoc ia,
			String[] attr, String[] values, String[] pkey_info) {
		ArrayList<Integer> indices = ia.indices;
		String query = pkey_info[0] + "=" + pkey_info[1] + " AND ";
		int i = 0;
		for (; i < (indices.size()); i++) {
			query = query + /* stripSuffix */(attr[indices.get(i)].trim())
					+ "=" + values[indices.get(i)].trim() + " AND ";
		}

		query = query.substring(0, (query.length() == 0) ? 0
				: query.length() - 4);

		return query;
	}

	private static String formAttributeChain(IndexAssoc ia, String[] attr) {
		ArrayList<Integer> indices = ia.indices;
		String query = "";
		// System.out.println("size is " + indices.size());
		// System.out.println(""+indices.get(0)+""+in);
		int i = 0;
		for (; i < indices.size(); i++) {
			query = query + /* stripSuffix */(attr[indices.get(i)].trim())
					+ ", ";
			// System.out.println(i+": " +query);

		}
		query = query.substring(0, (query.length() == 0) ? 0
				: query.length() - 2);

		return query;
	}

	private static String formValueChain(IndexAssoc ia, String[] values) {
		ArrayList<Integer> indices = ia.indices;
		String query = "";
		int i = 0;
		for (i = 0; i < indices.size(); i++) {
			query = query + values[indices.get(i)].trim() + ", ";
		}

		query = query.substring(0, (query.length() == 0) ? 0
				: query.length() - 2);

		return query;
	}

	public boolean doJoinInsert() throws Exception {
		if (viewTree != null)
			viewTree.setEvalType(SQLParser.INSERT);

		if (fulfilsDefiningPred() == false) {
			debug("QUERY FAILS VIEW TABLE RESTRICTION");
			return false;
		}
		String physicalTable1 = cv.getPhyMap()[0].getTableName();
		String physicalTable2 = cv.getPhyMap()[1].getTableName();
		debug("P1 = " + physicalTable1);
		debug("P2 = " + physicalTable2);

		// String columnString = sqlparser.getColString();
		String[] arrayedColumnString = sqlparser.getColumns();
		String[] arrayedValuesString = sqlparser.getValues();// stringToArray(valuesString);

		IndexAssoc[] iassoc = buildTableIndexAssoc(cv.getPhyMap(),
				arrayedColumnString);

		/* Here we form our sub-queries that are part of the query injection */
		debug(iassoc[0].tablename);
		debug(iassoc[1].tablename);
		String leftPartCheck = formWhereAssignmentChain(iassoc[0],
				arrayedColumnString, arrayedValuesString, pkey_info);

		String rightPartCheck = formWhereAssignmentChain(iassoc[1],
				arrayedColumnString, arrayedValuesString, pkey_info);

		String leftPartCol = formAttributeChain(iassoc[0], arrayedColumnString);
		String rightPartCol = formAttributeChain(iassoc[1], arrayedColumnString);
		String leftPartVal = formValueChain(iassoc[0], arrayedValuesString);
		String rightPartVal = formValueChain(iassoc[1], arrayedValuesString);

		/* augment primary key (name/val) to subquery */
		String lz = (leftPartCol.length() == 0 ? "" : ",");
		String rz = (rightPartCol.length() == 0 ? "" : ",");
		leftPartCol = primaryKeyOfView + lz + leftPartCol;
		rightPartCol = primaryKeyOfView + rz + rightPartCol;
		leftPartVal = pkey_info[1] + lz + leftPartVal;
		rightPartVal = pkey_info[1] + rz + rightPartVal;
		String primaryKeyValueOfInsert = pkey_info[1];

		debug("values string is" + valuesString);
		debug(leftPartCol);
		debug(rightPartCol);
		debug(leftPartVal);
		debug(rightPartVal);
		debug("primary key value of insert is: " + primaryKeyValueOfInsert);

		String query = "";
		// leftPartCheck
		query = "SELECT * FROM " + viewName + " WHERE " + primaryKeyOfView
				+ "=" + primaryKeyValueOfInsert;
		boolean v_exist = DBConn.getTupleExist(query);
		debug("Checking if tuple to be inserted already exists in the view \n\t "
				+ query + ": " + v_exist);
		query = "SELECT * FROM " + physicalTable1 + " WHERE " + leftPartCheck;

		boolean r_exist = DBConn.getTupleExist(query);
		debug("Checking if part of tuple to be inserted already exists in "
				+ physicalTable1 + "\n\t " + query + ": " + r_exist);
		query = "SELECT * FROM " + physicalTable2 + " WHERE " + rightPartCheck;
		boolean s_exist = DBConn.getTupleExist(query);
		debug("Checking if part of tuple to be inserted already exists in "
				+ physicalTable2 + "\n\t " + query + ": " + s_exist);

		// if a tuple with Vp = vp already exists in V, ERROR;
		if (v_exist)
			return false;

		if (r_exist && (!s_exist)) {
			query = "INSERT INTO " + physicalTable2 + "(" + rightPartCol + ")"
					+ " VALUES (" + rightPartVal + ")";
			debug("Part of tuple already exists in " + physicalTable1
					+ ".. Inserting to " + physicalTable2 + "\n\t" + query);
			return DBConn.queryDB(query); // should always return true
		}

		if (s_exist && (!r_exist)) {
			query = "INSERT INTO " + physicalTable1 + "(" + leftPartCol + ")"
					+ " VALUES (" + leftPartVal + ")";

			debug("Part of tuple already exists in " + physicalTable2
					+ ".. Inserting to " + physicalTable1 + "\n\t" + query);

			return DBConn.queryDB(query); // should always return true
		} else {

			String iQuery1 = "INSERT INTO " + physicalTable2 + "("
					+ rightPartCol + ")" + " VALUES (" + rightPartVal + ")";
			debug("No part of the tuple exists in either physical tables. First adding to "
					+ physicalTable2 + "\n\t" + query);

			String iQuery2 = "INSERT INTO " + physicalTable1 + "("
					+ leftPartCol + ")" + " VALUES (" + leftPartVal + ")";
			debug("No part of the tuple exists in either physical tables. First adding to "
					+ physicalTable1 + "\n\t" + query);

			boolean b1 = DBConn.queryDB(iQuery1 + ";" + iQuery2);
			return b1;
		}

	}

	public boolean doJoinDelete() throws Exception {
		String physicalTable1 = cv.getPhyMap()[0].getTableName();
		String physicalTable2 = cv.getPhyMap()[1].getTableName();

		
			dropTEMP();
		
		
		String query = "CREATE TABLE TEMP (" + primaryKeyOfView + ");";
		String end_clause = "";
		debug("Creating Table TEMP in your database");

		debug(query);
		boolean p1 = DBConn.queryNCDB(query);

		if (p1 == false) {

			return false;
		}

		if (updateWhereString != null)
			end_clause = /* stripSuffixS */("WHERE " + updateWhereString);
		// there exists case where i.e. end_clause = "WHERE empid_0 = 2"

		query = "INSERT INTO TEMP (" + primaryKeyOfView + ") SELECT ("
				+ primaryKeyOfView + ") FROM (" + viewName + ") " + end_clause;

		debug("Inserting into TEMP all primary Keys in the view name which fulfil the defining view predicate");
		debug(query);

		boolean p2 = DBConn.queryDB(query);
		if (p2 == false) {

			dropTEMP();

			return false;
		}

		query = "DELETE FROM " + physicalTable1
				+ " WHERE EXISTS ( SELECT * FROM TEMP WHERE TEMP."
				+ primaryKeyOfView + "=" + physicalTable1 + "."
				+ primaryKeyOfView + ");";

		debug("Deleting from "
				+ physicalTable1
				+ "all primary Keys in the view name which have matching primary keys in TEMP");

		debug(query);
		boolean p3 = DBConn.queryDB(query);
		if (p3 == false) {

			dropTEMP();
			return false;
		}

		query = "DELETE FROM " + physicalTable2
				+ " WHERE EXISTS ( SELECT * FROM TEMP WHERE TEMP."
				+ primaryKeyOfView + "=" + physicalTable2 + "."
				+ primaryKeyOfView + ");";

		debug("Deleting from "
				+ physicalTable2
				+ " TEMP all primary Keys in the view name which have matching primary keys in TEMP");
		debug(query);

		boolean p4 = DBConn.queryDB(query);
		if (p4 == false) {

			dropTEMP();
			return false;
		}

		dropTEMP();
		return true;

	}

	/**
	 * @TODO: check if UPDATE.. SET.. [] empty
	 * @return
	 * @throws Exception
	 */
	public boolean doJoinUpdate() throws Exception {

		if (viewTree != null)
			viewTree.setEvalType(SQLParser.UPDATE);
		if (fulfilsDefiningPred() == false) {
			debug("QUERY FAILS VIEW TABLE RESTRICTION");
			return false;
		}
		/* retrieve essentials before performing transformation */
		String viewName = cv.getViewName();
		String r_pred = sqlparser.getConditionString();
		String physicalTable1 = cv.getPhyMap()[0].getTableName();
		String physicalTable2 = cv.getPhyMap()[1].getTableName();
		String[] arrayedColumnString = (sqlparser.getColumns());
		String[] arrayedValuesString = sqlparser.getValues();
		IndexAssoc[] iassoc = buildTableIndexAssoc(cv.getPhyMap(),
				arrayedColumnString);

		
			dropTEMP();

		
		String query = "CREATE TABLE TEMP (" + primaryKeyOfView + ");";
		debug("Creating TEMP table \n\t" + query);

		
		if (DBConn.queryNCDB(query) == false) {

			debug("Failed to create temp");
			// dropTEMP();
			return false;
		}
			

		query = "INSERT INTO TEMP (" + primaryKeyOfView + ") " + "SELECT "
				+ primaryKeyOfView + " FROM " + viewName + " WHERE "
				+ /* stripSuffixS */(r_pred);

		debug(query);
		if (DBConn.queryNCDB(query) == false) {
			debug("failed to insert to temp");
			dropTEMP();

			return false;
		}
		//;

		/*
		 * for(int i = 0; i < 2 ; i ++) { for( int j = 0; j <
		 * iassoc[i].indices.size() ; j++){
		 * 
		 * if(arrayedColumnString[iassoc[i].indices.get(j)].trim().equalsIgnoreCase
		 * (primaryKeyOfView)){ pkey_info[1] = arrayedValuesString[j];
		 * 
		 * break; } else { debug("COULD NOT FIND"); } }
		 */
		// Here we retrieve all primary key values of R, S and V tables
		String set_part = "";
		String t_p = "";
		t_p = formAttributeAssignmentChain(iassoc[0], arrayedColumnString,
				arrayedValuesString);
		set_part = t_p;

		String update_q1 = "";
		if (set_part.trim().length() > 0) {
			debug("SET_PART IS " + set_part);

			update_q1 = "UPDATE " + physicalTable1 + " SET " + set_part
					+ " WHERE " + physicalTable1 + "." + primaryKeyOfView
					+ " IN (SELECT " + primaryKeyOfView + " FROM TEMP);";

			debug("UPDATE tuples in physicaltable1 that have matching pkey in View \n\t"
					+ query);
			if (DBConn.queryDB(query) == false) {
				dropTEMP();
				return false;
			}
		}
		String values_part = "";
		String columnNames = "";

		columnNames = formAttributeChain(iassoc[0], arrayedColumnString);
		values_part = formValueChain(iassoc[0], arrayedValuesString);

		/*
		 * AUGMENT COLUMNNAMES AND VALUES WITH (PKEYNAME/VALUE)
		 */
		String lz = (columnNames.length() == 0 ? "" : ",");
		String rz = (values_part.length() == 0 ? "" : ",");

		columnNames = primaryKeyOfView + lz + columnNames;
		values_part = primaryKeyOfView + rz + values_part;

		query = "INSERT INTO " + physicalTable1 + " (" + columnNames + ")"
				+ " SELECT " + values_part
				+ " FROM TEMP WHERE NOT EXISTS (SELECT " + primaryKeyOfView
				+ " FROM " + physicalTable1 + " WHERE " + physicalTable1 + "."
				+ primaryKeyOfView + " = TEMP." + primaryKeyOfView + ")";

		debug("INSERT tuples in physicaltable1 that dont have matching pkey in view\n\t"
				+ query);
		if (DBConn.queryNCDB(query) == false) {
			dropTEMP();
			return false;
		}

		t_p = formAttributeAssignmentChain(iassoc[1], arrayedColumnString,
				arrayedValuesString);
		set_part = t_p;
		debug("SET PART FOR physicalTable 2 is " + set_part);
		debug("Size of columns " + iassoc[1].indices.size());

		columnNames = formAttributeChain(iassoc[1], arrayedColumnString);
		values_part = formValueChain(iassoc[1], arrayedValuesString);
		/*
		 * AUGMENT COLUMNNAMES AND VALUES WITH (PKEYNAME/VALUE)
		 */
		lz = (columnNames.length() == 0 ? "" : ",");
		rz = (values_part.length() == 0 ? "" : ",");

		columnNames = primaryKeyOfView + lz + columnNames;
		values_part = primaryKeyOfView + rz + values_part;

		String update_q2 = "";
		if (set_part.trim().length() > 0) {
			update_q2 = "UPDATE " + physicalTable2 + " SET " + set_part
					+ " WHERE " + physicalTable2 + "." + primaryKeyOfView
					+ " IN (SELECT " + primaryKeyOfView + " FROM TEMP);";

			debug("UPDATE tuples in physicaltable2 that dont have matching pkey in view\n\t"
					+ query);

		}

		query = "INSERT INTO " + physicalTable2 + " (" + columnNames + ")"
				+ " SELECT " + values_part
				+ " FROM TEMP WHERE NOT EXISTS (SELECT " + primaryKeyOfView
				+ " FROM " + physicalTable2 + " WHERE " + physicalTable2 + "."
				+ primaryKeyOfView + " = TEMP." + primaryKeyOfView + ")";

		debug("INSERT tuples in physicaltable2 that dont have matching pkey in view\n\t"
				+ query);

		if (DBConn.queryNCDB(query) == false) {
			dropTEMP();
			return false;
		}
		// commit update queries
		if (DBConn.queryDB(update_q1 + ";" + update_q2) == false) {
			debug("FAILED TO PERFORM UPDATE ON BOTH TABLES");
			dropTEMP();
			return false;
		}

		if (DBConn.queryNCDB(update_q1 + ";" + update_q2) == false) {
			dropTEMP();
			return false;
		} else
			return true;
		// return DBConn.queryNCDB(update_q1 + ";" + update_q2);
	}

	
	private static void dropTEMP() {
		
		try {
			DBConn.queryNCDB("DROP TABLE TEMP;");
		} catch (Exception e) {
			// TODO Auto-generated catch block

		}
					
		

	}


	private static IndexAssoc[] buildTableIndexAssoc(TableMap[] phyMap,
			String[] attributesT) {

		IndexAssoc[] associations = new IndexAssoc[2];
		associations[0] = new IndexAssoc(phyMap[0].getTableName());
		associations[1] = new IndexAssoc(phyMap[1].getTableName());

		for (int attribute_index = 0; attribute_index < attributesT.length; attribute_index++) {

			for (int phyIndex = 0; phyIndex < 2; phyIndex++) {

				for (int colIndex = 0; colIndex < phyMap[phyIndex]
						.getTotalColumns(); colIndex++) {

					if (attributesT[attribute_index]
							.equalsIgnoreCase(phyMap[phyIndex]
									.getColumnAtIndex(colIndex))) {

						debug("matching " + attributesT[attribute_index]
								+ " with "
								+ phyMap[phyIndex].getColumnAtIndex(colIndex));
						associations[phyIndex].indices.add(attribute_index);

						break;
					}
				}
			}
		}
		return associations;

	}

	// Job of checking for satisfaction of Table Predicates is delegated to JDBC
	// driver.
	private boolean fulfilsDefiningPred() {

		// boolean cond2 = false;
		boolean cond1 = false;

		if (viewTree == null)
			cond1 = true;
		else {

			try {
				cond1 = ((BoolNode) viewTree.eval()).eval(); // restrictions
				debug("viewTree not null and evaluated to " + cond1);
			} catch (NullPointerException e) {
				cond1 = true;
				debug("NullPointerException caused viewTree to eval to true <since no mapping can be configured for viewTree>");
			}

		}
		debug("viewTree evaluated to: " + cond1);

		return cond1;

	}

	public static void debug(String s) {
		if (debug == 1)
			System.out.println("DEBUGMODE:" + s);
	}


}
