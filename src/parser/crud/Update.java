package parser.crud;

import java.util.Stack;
import java.util.regex.Pattern;

import datastructs.Node;

import parser.WhereParser;

public class Update implements ISQLQuery {
    
    private static final String UPDATE_REGEX = new String(
            "(((U|u)(P|p)(D|d)(A|a)(T|t)(E|e) )(.*?))|" +
            "(((S|s)(E|e)(T|t) )(?!.*((S|s)(E|e)(T|t) )))|" +
            "(((W|w)(H|h)(E|e)(R|r)(E|e) )(?!.*((W|w)(H|h)(E|e)(R|r)(E|e) )))");

    private String table = null;
    private String[] columns = null;
    private String[] values = null;
    private String colsAndVals = null;
    private Node condTree = null;
    private String conditionString = null;

    @Override
    public boolean parse(String selectStmt) {
        String[] result = Pattern.compile(UPDATE_REGEX).split(selectStmt);
        
        if (result.length >= 2) table = result[1].trim();
        if (result.length >= 3) processColsNVals(result[2]);
        if (result.length >= 4) {
            conditionString = result[3].trim();
            processConditions(conditionString);
        }

        return true;
    }
    
    private void processColsNVals(String colToVal) {
        colsAndVals = colToVal;
        String[] pairs = colToVal.split("\\s*,\\s*");
        columns = new String[pairs.length];
        values = new String[pairs.length];
        for (int i = 0; i < pairs.length; i++) {
            String[] pair = pairs[i].split("\\s*=\\s*");
            columns[i] = pair[0];
            values[i] = pair[1];
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

    public Node getCondTree() {
        return condTree;
    }
    
    public String getConditionString() {
        return conditionString;
    }

    @Override
    public String getColString() {
        return colsAndVals;
    }

    @Override
    public String getValString() {
        return colsAndVals;
    }

    @Override
    public String getJoinConstraint() {
        return null;
    }
}