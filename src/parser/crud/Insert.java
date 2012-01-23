package parser.crud;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import datastructs.Node;

public class Insert implements ISQLQuery {

    private static final String INSERT_REGEX = new String(
            "(((I|i)(N|n)(S|s)(E|e)(R|r)(T|t) (I|i)(N|n)(T|t)(O|o) ).*?)|" +
            "(((V|v)(A|a)(L|l)(U|u)(E|e)(S|s))(?!.*((V|v)(A|a)(L|l)(U|u)(E|e)(S|s))))");
    
    private String table = null;
    private String[] columns = null;
    private String[] values = null;
    private String colString = null;
    private String valString = null;
    
    @Override
    public boolean parse(String selectStmt) {        
        String[] result = Pattern.compile(INSERT_REGEX).split(selectStmt);
        
        if (result.length >= 2) processTableCols(result[1]);
        if (result.length >= 3) processValues(result[2]);
         
        return true;
    }
    
    private void processTableCols(String table_columns) {
        if (table_columns != null) {
            StringTokenizer st = new StringTokenizer(table_columns, "(");
            
            // process viewTable
            if (st.hasMoreTokens()) table = st.nextToken().trim();
            
            // process columns
            String colsTemp;
            if (st.hasMoreTokens()) {
                colsTemp = st.nextToken().trim();
                colString = colsTemp.substring(0, colsTemp.length()-1);
                st = new StringTokenizer(colString, ",");
                
                columns = new String[st.countTokens()];
                int i = 0;
                while (st.hasMoreTokens()) {
                    columns[i] = st.nextToken().trim();
                    //System.out.print(columns[i]+" ");
                    i++;
                }
            }
        }
    }

    private void processValues(String valStr) {
        if (valStr != null) {
            valString = valStr.trim().substring(1,valStr.trim().length()-1);
            StringTokenizer st = new StringTokenizer(valString, ",");
            values = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                values[i] = st.nextToken().trim();
                //System.out.print(values[i]+" ");
                i++;
            }
        }
    }
    
    @Override
    public String getTable() {
        return table;
    }

    @Override
    public String[] getTables() {
        String[] t = { table };
        return t;
    }

    public String[] getColumns() {
        return columns;
    }

    public String[] getValues() {
        return values;
    }

    public String getColString() {
        return colString;
    }
    
    public String getValString() {
        return valString;
    }

    @Override
    public Node getCondTree() {
        return null;
    }

    @Override
    public String getConditionString() {
        return null;
    }

    @Override
    public String getJoinConstraint() {
        return null;
    }
}