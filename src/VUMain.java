import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import logic.UVController;

public class VUMain {

    private static BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    private static UVController ctrl = new UVController();

    public static void main(String[] args) {
        String sql;
        
        //dropTempTable();
        printProgTitle();
        while (true) {
            System.out.print("  > ");
            
            sql = readSQL(input);
            if (sql.matches("(?i)create view .*") || 
                    sql.matches("(?i)drop view .*") ||
                    sql.matches("(?i)drop table temp.*") ||
                    sql.matches("(?i)insert .*") || 
                    sql.matches("(?i)delete .*") || 
                    sql.matches("(?i)update .*") ||
                    sql.matches("(?i)select .*")) {
                String out = executeSQL(sql);
                System.out.println("  *** " + out);
            } else if (sql.matches("(?i)help.*")) {
                System.out.println(" Projection/Restriction: ");
                System.out.println("  CREATE VIEW view_name AS SELECT <projection> FROM physical_table");
                System.out.println("                           [WHERE <restriction>] ;");
                System.out.println(" (PK-PK) Inner Joins: ");
                System.out.println("  CREATE VIEW view_name AS SELECT <projection> FROM phyTable1");
                System.out.println("                           INNER JOIN phyTable2");
                System.out.println("                           ON phyTable1.id = phyTable2.id");
                System.out.println("                           [WHERE <restriction>] ;");
                System.out.println("  !!! For Joins with Projections, column names should be in the format:");
                System.out.println("                                                   TABLE_NAME.COL_NAME");
                System.out.println("  !!! For subsequent view updates and queries, use COL_NAME ONLY.");
                printNewLine();
                printNewLine();
                System.out.println("  DROP VIEW view_name;");
                printNewLine();
                printNewLine();
                System.out.println("  INSERT INTO view_name (col1, col2, col3, ...) VALUES (val1, val2, val3, ...);");
                System.out.println("  DELETE FROM view_name [WHERE <restriction>];");
                System.out.println("  UPDATE view_name SET col1=val1, col2=val2, ... [WHERE <restriction>];");
                printNewLine();
                printNewLine();
                System.out.println("  SELECT ... FROM view_name [WHERE ...];");
                printNewLine();
            } else if (sql.matches("(?i)exit.*")) {
                ctrl.dropAllView();
                System.out.println("  Good bye!");
                return;
            } else {
                printInvalidCmd();
            }
            
            /*
            printMenu();
            printHorizontalBar();
            
            switch (readCmd(input)) {
            case 1: // Create view
                System.out.println("  Projection/Restriction: CREATE VIEW view_name AS SELECT ... FROM physical_table");
                System.out.println("                                                   [WHERE ...] ;");
                System.out.println("  (PK-PK) Inner Joins:    CREATE VIEW view_name AS SELECT ... FROM phytable1 ...");
                System.out.println("                                                   INNER JOIN phyTable2");
                System.out.println("                                                   ON phyTable1.id = phyTable2.id");
                System.out.println("                                                   [WHERE ...] ;");
                System.out.println("  !!! For Joins with Projections, column names should be in the format:");
                System.out.println("                                                   TABLE_NAME.COL_NAME");
                System.out.println("  !!! For subsequent view updates and queries, use COL_NAME ONLY.");
                printNewLine();
                System.out.print("  > ");
                
                sql = readSQL(input);
                if (sql.matches("(?i)create view .*")) {
                    String out = executeSQL(sql);
                    System.out.println("  *** " + out);
                } else {
                    printInvalidCmd();
                }
                break;
            case 2: // Drop view
                System.out.println("  DROP VIEW view_name;");
                printNewLine();
                System.out.print("  > ");

                sql = readSQL(input);
                if (sql.matches("(?i)drop view .*")) {
                    String out = executeSQL(sql);
                    System.out.println("  *** " + out);
                } else {
                    printInvalidCmd();
                }
                break;
            case 3: // Update view
                System.out.println("  INSERT INTO view_name (col1, col2, col3, ...) VALUES (val1, val2, val3, ...);");
                System.out.println("  DELETE FROM view_name [WHERE ...];");
                System.out.println("  UPDATE view_name SET col1=val1, col2=val2, ... [WHERE ...];");
                printNewLine();
                System.out.print("  > ");

                sql = readSQL(input);
                if (sql.matches("(?i)insert .*") || 
                        sql.matches("(?i)delete .*") || 
                        sql.matches("(?i)update .*")) {
                    String out = executeSQL(sql);
                    System.out.println("  *** " + out);
                } else {
                    printInvalidCmd();
                }
                break;
            case 4: // Query view
                System.out.println("  SELECT ... FROM view_name [WHERE ...];");
                printNewLine();
                System.out.print("  > ");
                
                sql = readSQL(input);
                if (sql.matches("(?i)select .*")) {
                    String out = executeSQL(sql);
                    System.out.println("  *** " + out);
                } else {
                    printInvalidCmd();
                }
                break;
            case 0: // Exit
            	ctrl.dropAllView();
                System.out.println("  Good bye!");
                return;
            default:
                printWrongCommand();
                printNewLine();
                continue;
            }
            */
            printHorizontalBar();
        }
    }
    
    private static String executeSQL(String sql) {
        try {
            return ctrl.UVControl(sql);
        } catch (Exception e) {
            System.out.println("  *** Error executing SQL: "+e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private static String readSQL(BufferedReader in) {
        String sqlCmd = "";
        
        try {        
            while (true) {
                sqlCmd = sqlCmd + " " + in.readLine().trim();
                if (sqlCmd.toCharArray()[sqlCmd.length()-1] == ';') break;
            }
        } catch (IOException e) {}
        
        return sqlCmd.substring(0, sqlCmd.length()-1).trim();
    }

    private static int readCmd(BufferedReader in) {
        System.out.print("  > ");
        
        int cmd = -1;
        try {
            cmd = Integer.parseInt(in.readLine().trim());
        } catch (NumberFormatException nfe) {
        } catch (IOException e) {}
        
        return cmd;
    }

    
    
    private static void printInvalidCmd() {
        System.out.println("  *** Error: Invalid SQL command.");
    }
    
    private static void printWrongCommand() {
        System.out.println("  *** Error: Please select the correct command.");
    }

    private static void printProgTitle() {
        printHorizontalBar();
        System.out.println("  View Updates for SQLite v0.1");
        printHorizontalBar();
    }
    
    private static void printMenu() {
        System.out.println("  Commands available:");
        System.out.println("   1. Create view");
        System.out.println("   2. Drop view");
        System.out.println("   3. Update view");
        System.out.println("   4. Query view");
        System.out.println("   0. Exit");
    }
    
    private static void printHorizontalBar() {
        System.out.println("===================================================================");
    }
    
    private static void printNewLine() {
        System.out.println(" ");
    }
}