package parser.crud;

import datastructs.Node;

public interface ISQLQuery {    
    public boolean parse(String selectStmt);
    public String getTable();
    public String[] getTables();
    public String[] getColumns();
    public String[] getValues();
    public String getColString();
    public String getValString();
    public String getJoinConstraint();
    public Node getCondTree();
    public String getConditionString();
}
