package parser;

import driver.DBConn;
import parser.view.CreateView;

public class Tester {

    public static void main(String[] args) {
        //String whereString = "POSITION!='Manager' AND(SALARY>50000 OR BENEFIT>=10000)";
        //String whereString = "POSITION!='Manager'AND SALARY>50000+2*3 OR BENEFIT>=10000*3+2/3 AND BENEFITS<30000";
        //String whereString = "POSITION!='Manager' AND SALARY>50000+2*3 OR BENEFIT>=10000*3/2+3 AND BENEFITS<30000/2+4*5";
        //String whereString = "BENEFIT>=(10*3)/2+3";
        //String whereString = "(((POSITION != 'Manager') AND SALARY > 50000+(2*3)) OR BENEFIT >= ((10000*(3))/2+3) AND (BENEFITS < (30000/2)+4*5))";
                
        /*
         * Testing SQL parsing
         * 
        SQLParser.getInstance(SQLParser.SELECT).parse("SELECT sno#, sname, pno FROM table");
        System.out.println();
        
        
        
        SQLParser.getInstance(SQLParser.DELETE).parse("DELETE FROM table");
        System.out.println();
        
        SQLParser.getInstance(SQLParser.UPDATE).parse("UPDATE table SET ID = 1, settlement = 2 where cond");
        System.out.println();
        
        SQLParser.getInstance(SQLParser.INSERT).parse(
                "INSERT INTO table (col1, col2, col3) VALUES (1,2,3)");
        System.out.println();
        */

        //System.out.println("\'"+"a b c d e".split("\\s* \\s*")[0]+"\'");
        
        //Update upd = (Update) SQLParser.getInstance(SQLParser.UPDATE);
        //upd.parse("UPDATE table SET ID = 1, settlement = 2 where BENEFIT>=(10*3)/2+3");
        
        //String sqlCmd = "SELECT position, salary, benefit FROM employee WHERE (((POSITN != 'Manager') AND SALARY > 50000+(2*3)) OR BENEFIT >= ((10000*(3))/2+3) AND (BENEFIT < (30000/2)+4*5))";
        ///String sqlCmd = "INSERT INTO table(col1, col2, col3) VALUES(11,2,3)";
        //String sqlCmd = "UPDATE table SET ID = 1, settlement = 2 where BENEFIT>=(10*3)/2+3";
        //String sqlCmd = "DELETE FROM table whERe BENEFIT>=(10*3)";
        //String sqlCmd = "SELECT * FROM emp, dept";
        //String sqlCmd = "SELECT col1,col2,col3 FROM table_name1 INNER JOIN table_name2 ON table_name1.column_name=table_name2.column_name WHERE POSITION >=(10*3)";
        //String sqlCmd = "SELECT position, salary, benefit FROM employee WHERE POSITON != 'Manager'";
        //String sqlCmd = "UPDATE hh SET salary = 1000 WHERE empid = 1";
        
        /*
        SQLParser sqlP = new SQLParser(sqlCmd);
        int queryType = sqlP.getQueryType();
        String viewTable = sqlP.getTable();
        String[] columns = sqlP.getColumns();
        Node condTree = sqlP.getCondTree();
        String condSting = sqlP.getConditionString();
        String[] values = sqlP.getValues();
        */
        
        //String cvCmd = "CREATE VIEW firstView AS SELECT * FROM department WHERE BENEFIT='not bad' ";
        //String cvCmd = "CREATE VIEW firstView AS SELECT educationdetails.name,employee.empid, educationdetails.education FROM employee INNER JOIN educationdetails ON employee.empid=educationdetails.empid";
        //CreateView cv = new CreateView(cvCmd);

        try {
            DBConn.queryDB("INSERT INTO educationdetails(EMPID,empid, education) VALUES (113,113, 'SIM');" +
            		"INSERT INTO employee(EMPID,empid, salary) VALUES (113,113, 3400)");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println();
    }
}