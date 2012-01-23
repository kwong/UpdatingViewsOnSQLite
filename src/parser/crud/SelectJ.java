package parser.crud;

import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import datastructs.Node;

import parser.WhereParser;

public class SelectJ implements ISQLQuery {

    // cannot regex: SELECT select , sno#, sname, pno FROM table WHERE cond
    private static final String SELECT_FROM_REGEX = new String(
            "(((S|s)(E|e)(L|l)(E|e)(C|c)(T|t) )(.*?))|" +
            "(((F|f)(R|r)(O|o)(M|m) )(?!.*((F|f)(R|r)(O|o)(M|m) )))");
    private static final String INNER_JOIN_ON_REGEX = new String(
            "(( (I|i)(N|n)(N|n)(E|e)(R|r) (J|j)(O|o)(I|i)(N|n) )(?!.*( (I|i)(N|n)(N|n)(E|e)(R|r) (J|j)(O|o)(I|i)(N|n) )))|" +
            "(( (O|o)(N|n) )(?!.*( (O|o)(N|n) )))");
    private static final String WHERE_REGEX = new String(
            "(((W|w)(H|h)(E|e)(R|r)(E|e) )(?!.*((W|w)(H|h)(E|e)(R|r)(E|e) )))");
    
    private String[] columns = null;
    private String colString = null;
    private String[] tables = null;
    private String joinConstraint = null;
    private Node condTree = null;
    private String conditionString = null;

    @Override
    public boolean parse(String selectStmt) {
        columns = null;
        tables = null;
        joinConstraint = null;
        condTree = null;
        conditionString = null;
        String SELECT_REGEX = null;

        if (Pattern.compile(INNER_JOIN_ON_REGEX).split(selectStmt).length == 3) {
            SELECT_REGEX = SELECT_FROM_REGEX + "|" + INNER_JOIN_ON_REGEX + "|" + WHERE_REGEX;
            String[] result = Pattern.compile(SELECT_REGEX).split(selectStmt);
            
            if (result.length >= 2) processColumns(result[1]);
            if (result.length >= 4) {
                tables = new String[2];
                tables[0] = result[2].trim();
                tables[1] = result[3].trim();
            }
            if (result.length >= 5) {
                joinConstraint = result[4].trim();
            }
            if (result.length >= 6) {
                conditionString = result[5].trim();
                processConditions(conditionString);
            }
        } else {
            SELECT_REGEX = SELECT_FROM_REGEX + "|" + WHERE_REGEX;
            String[] result = Pattern.compile(SELECT_REGEX).split(selectStmt);
            
            if (result.length >= 2) processColumns(result[1]);
            if (result.length >= 3) {
                tables = new String[1];
                tables[0] = result[2].trim();
            }
            if (result.length >= 4) {
                conditionString = result[3].trim();
                processConditions(conditionString);
            }
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
    
    // Not define in interface
    public String getJoinConstraint() {
        return joinConstraint;
    }
    
    public String[] getColumns() {
        return columns;
    }

    @Override
    public String getTable() {
        return tables[0];
    }

    @Override
    public String[] getTables() {
        return tables;
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
}
