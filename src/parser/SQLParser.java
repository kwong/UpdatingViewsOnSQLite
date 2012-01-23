package parser;

import datastructs.Node;
import parser.crud.Delete;
import parser.crud.ISQLQuery;
import parser.crud.Insert;
import parser.crud.SelectJ;
import parser.crud.Update;

public class SQLParser {
    public static final int SELECT = 0;
    public static final int INSERT = 1;
    public static final int DELETE = 2;
    public static final int UPDATE = 3;
    
    private int queryType;
    private ISQLQuery sq;
    
    private static class SQLParserHolder {
        private static final ISQLQuery SELECT = new SelectJ();
        private static final ISQLQuery INSERT = new Insert();
        private static final ISQLQuery DELETE = new Delete();
        private static final ISQLQuery UPDATE = new Update();
    }
    
    public SQLParser(String sqlCmd) {        
        parseNew(sqlCmd);
    }

    private static ISQLQuery getInstance(final int QUERY_TYPE) {
        switch (QUERY_TYPE) {
        case(SELECT):
            return SQLParserHolder.SELECT;
        case(INSERT):
            return SQLParserHolder.INSERT;
        case(DELETE):
            return SQLParserHolder.DELETE;
        case(UPDATE):
            return SQLParserHolder.UPDATE;
        default:
            return null;
        }
    }
    
    public boolean parseNew(String sqlCmd) {
        String type = sqlCmd.split("\\s* \\s*")[0].toUpperCase();
        if (type.equals("SELECT")) {
            queryType = SELECT;
        } else if (type.equals("INSERT")) {
            queryType = INSERT;
        } else if (type.equals("DELETE")) {
            queryType = DELETE;
        } else if (type.equals("UPDATE")) {
            queryType = UPDATE;
        } else {
            queryType = -1;
        }
        
        if (queryType != -1) {
            sq = getInstance(this.getQueryType());
            sq.parse(sqlCmd);
            return true;
        }
        
        return false;
    }
    
    public int getQueryType() {
        return queryType;
    }
    
    public String getTable() {
        return sq.getTable();
    }
    
    public String[] getTables() {
        return sq.getTables();
    }

    public String[] getColumns() {
        return sq.getColumns();
    }

    public String[] getValues() {
        return sq.getValues();
    }

    public Node getCondTree() {
        return sq.getCondTree();
    }
    
    public String getConditionString() {
        return sq.getConditionString();
    }
    
    public String getColString() {
        return sq.getColString();
    }
    
    public String getValString() {
        return sq.getValString();
    }
    
    public String getJoinConstraint() {
        return sq.getJoinConstraint();
    }
}
