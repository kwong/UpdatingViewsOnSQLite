package parser.crud;

import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import datastructs.Node;

import parser.WhereParser;

public class Select implements ISQLQuery {

    // cannot regex: SELECT select , sno#, sname, pno FROM table WHERE cond
    private static final String SELECT_REGEX = new String(
            "(((S|s)(E|e)(L|l)(E|e)(C|c)(T|t) )(.*?))|" +
            "(((F|f)(R|r)(O|o)(M|m) )(?!.*((F|f)(R|r)(O|o)(M|m) )))|" +
            "(((W|w)(H|h)(E|e)(R|r)(E|e) )(?!.*((W|w)(H|h)(E|e)(R|r)(E|e) )))");
    
    private String[] columns = null;
    private String colString = null;
    private String table = null;
    private Node condTree = null;
    private String conditionString = null;

    @Override
    public boolean parse(String selectStmt) {
        columns = null;
        table = null;
        condTree = null;

        String[] result = Pattern.compile(SELECT_REGEX).split(selectStmt);
        
        if (result.length >= 2) processColumns(result[1]);
        if (result.length >= 3) table = result[2].trim();
        if (result.length >= 4) {
            conditionString = result[3].trim();
            processConditions(conditionString);
        }
        
        return true;
    }

    private void processColumns(String colStr) {
        if (colStr != null) {
            colString = colStr.trim();
            StringTokenizer st = new StringTokenizer(colString, ",");
            columns = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                columns[i] = st.nextToken().trim();
                i++;
            }
        }
    }
    
    private void processConditions(String condStr) {
        if (condStr != null) {
            WhereParser wp = new WhereParser();
            Stack<Object> whereStack = wp.parse(condStr.trim());

            while (!whereStack.empty()) {
                condTree = (Node) whereStack.pop();
            }
        }
    }
    
    public String[] getColumns() {
        return columns;
    }
    
    public String getTable() {
        return table;
    }

    @Override
    public String[] getTables() {
        String[] t = { table };
        return t;
    }
    
    public Node getCondTree() {
        return condTree;
    }
    
    public String getConditionString() {
        return conditionString;
    }

    @Override
    public String[] getValues() {
        return null;
    }

    @Override
    public String getColString() {
        return colString;
    }

    @Override
    public String getValString() {
        return null;
    }

    @Override
    public String getJoinConstraint() {
        return null;
    }
}