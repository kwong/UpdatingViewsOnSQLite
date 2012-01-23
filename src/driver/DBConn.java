package driver;

import java.sql.*;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import datastructs.*;

public class DBConn {

	public static final String DATABASE = "jdbc:sqlite:DB.db";

	static boolean connOpened = false;

	/*
	 * public static Connection openConn() throws Exception {
	 * Class.forName("org.sqlite.JDBC"); Connection conn =
	 * DriverManager.getConnection(DATABASE); conn.setAutoCommit(false);
	 * connOpened = true; // set connOpened flag to true. return conn; }
	 * 
	 * public static boolean closeConn(Connection conn) { try { conn.close();
	 * return true; } catch (Exception e) { return false; } }
	 * 
	 * public static boolean cleanup(ResultSet rs, Statement stat) throws
	 * SQLException { rs.close(); stat.close(); return true; }
	 */

	// Connection c = openConn(...)
	// ....
	// Rs = queryInsertDB(c, query)
	// ....
	// cleanup(
	public static boolean getTupleExist(String query) throws Exception {
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection(DATABASE);
		try {
			//System.out.println("TUPLE EXIST : " + query);
			Statement stat = conn.createStatement();
			conn.setAutoCommit(false);

			ResultSet rs;
			rs = stat.executeQuery(query);
			return rs.next();
			/*
			if (rs.getString(1) != null)
				return true;
			else
				return false;*/

		} catch (SQLException e) {
			Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, e);
			return false;
		} finally{
			conn.close();
		}

	}

	public static boolean queryDB(String query) throws Exception {
	    //System.out.println("THIS IS : " + query);
	    Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:DB.db");
        try {
            conn.setAutoCommit(false);
            
            query = query.trim();
            StringTokenizer st = new StringTokenizer(query, ";");
            int i = 0;
            int s = 0;
            while (st.hasMoreTokens()) {

            	String temppppp= st.nextToken().trim();
                PreparedStatement prep = conn.prepareStatement(temppppp);
                s += prep.executeUpdate();
                prep.close();
                
                i++;
            }
            
            conn.commit();
            if (s > 0)
                return true;
            else
                return false;
        } catch (SQLException e) {
            Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, e);
            return false;
        } finally{
            conn.close();
        }
	    
	    /*
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:DB.db");
		try {
			conn.setAutoCommit(false);
			PreparedStatement prep = conn.prepareStatement(query);

			int s = prep.executeUpdate();
			prep.close();
			conn.commit();
			if (s > 0)
				return true;
			else
				return false;
		} catch (SQLException e) {
			Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, e);
			return false;
		} finally{
			conn.close();
		}
        */
	}

	// View can only be created or deleted
	public static boolean queryNCDB(String query) throws Exception {
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:DB.db");
		try {
			conn.setAutoCommit(false);

			PreparedStatement prep = conn.prepareStatement(query);

			prep.executeUpdate();
			prep.close();
			conn.commit();

			return true;
		} catch (SQLException e) {
			//Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, e);
		    System.out.println("Potential SQL error: "+e.getMessage());
			return false;
		} finally{
			conn.close();
		}

	}

	// Get column values (For table join)
	public static Set<String> getColumnValue(String query) throws Exception{
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection(DATABASE);
		try {
			Statement stat = conn.createStatement();
			conn.setAutoCommit(false);
			Set<String> s = new HashSet<String>();
			ResultSet rs;
			rs = stat.executeQuery(query);
			while (rs.next()) {
				s.add(rs.getString(1));
			}
			return s;
		} catch (Exception e) {
			
		}finally{
			conn.close();
		}
		return null;
	}

	public static boolean checkValueinColumn(String value, String tableName,
			String columnName) throws Exception{
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection(DATABASE);
		try {
			
			Statement stat = conn.createStatement();
			conn.setAutoCommit(false);

			ResultSet rs;
			rs = stat.executeQuery("Select " + columnName + " from "
					+ tableName);
			while (rs.next()) {
				if (value.equalsIgnoreCase(rs.getString(1)))
					return true;
			}
			return false;
		} catch (Exception e) {

		}finally{
			conn.close();
		}
		return false;
	}

	// get unique keys in the relation
	public static Set<String> getCandidateKey(String tableName){
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection(DATABASE);
			conn.setAutoCommit(false);
			Set<String> keys = new HashSet<String>();
			ResultSet rs;
			DatabaseMetaData dbmd = conn.getMetaData();
			rs = dbmd.getIndexInfo(conn.getCatalog(), null, tableName, true,
					true);
			while (rs.next()) {
				String columnName = rs.getString("COLUMN_NAME");
				keys.add(columnName);
			}

			conn.commit();
			conn.close();
			return keys;
		} catch (Exception e) {
			Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
	}

	public static String getPrimaryKey(String tableName) {
		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection(DATABASE);
			conn.setAutoCommit(false);
			ResultSet rs;
			DatabaseMetaData dbmd = conn.getMetaData();
			rs = dbmd.getPrimaryKeys(null, null, tableName);
			String colName = rs.getString("COLUMN_NAME");

			conn.commit();
			conn.close();

			return colName;

		} catch (Exception e) {
			Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}
	}

	public static ViewTable querySelectDB(String query) {

		try {
			Class.forName("org.sqlite.JDBC");
			Connection conn = DriverManager.getConnection(DATABASE);
			conn.setAutoCommit(false);
			Statement stat = conn.createStatement();
			ResultSet rs = stat.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numCols = rsmd.getColumnCount();
			ViewTable vt = new ViewTable(numCols);

			// get all the attribute names

			for (int i = 1; i < numCols + 1; i++)
				vt.colNames[i - 1] = rsmd.getColumnName(i);

			while (rs.next()) {
				Tuple tempTuple = new Tuple(numCols);
				for (int i = 1; i < numCols + 1; i++)
					tempTuple.tuple[i - 1] = rs.getString(i);

				vt.add(tempTuple);
			}

			conn.commit();
			conn.close();

			return vt;

		} catch (Exception e) {
			Logger.getLogger(DBConn.class.getName()).log(Level.SEVERE, null, e);
			return null;
		}

	}

}
