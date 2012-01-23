package parser.crud;

import java.util.Stack;
import java.util.regex.Pattern;

import datastructs.Node;

import parser.WhereParser;

public class Delete implements ISQLQuery {

    private static final String DELETE_REGEX = new String("" +
    		"(((D|d)(E|e)(L|l)(E|e)(T|t)(E|e) (F|f)(R|r)(O|o)(M|m) ).*?)|" +
    		"(((W|w)(H|h)(E|e)(R|r)(E|e) )(?!.*((W|w)(H|h)(E|e)(R|r)(E|e) )))");
    
    private String table = null;
    private Node condTree = null;
    private String conditionString = null;
    
    @Override
    public boolean parse(String selectStmt) {
        table = null;
        
        String[] result = Pattern.compile(DELETE_REGEX).split(selectStmt);
        
        if (result.length >= 2) table = result[1].trim();
        if (result.length >= 3) {
            conditionString = result[2].trim();
            processConditions(conditionString);
        }
        
        return true;
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
    
    @Override
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
    public String[] getColumns() {
        return null;
    }

    @Override
    public String[] getValues() {
        return null;
    }

    @Override
    public String getColString() {
        return null;
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