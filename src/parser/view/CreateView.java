package parser.view;

import java.util.regex.Pattern;

import parser.SQLParser;

import datastructs.Node;
import datastructs.ViewTable;
import driver.DBConn;

public class CreateView {

    public static final int PROJECTION = 1;
    public static final int RESTRICTION = 10;
    public static final int JOIN = 100;
    
    /*
    private static final String CREATE_VIEW_REGEX = new String(
            "(((C|c)(R|r)(E|e)(A|a)(T|t)(E|e) (V|v)(I|i)(E|e)(W|w) ).*?)|" +
            "(((A|a)(S|s)[ ]*\\().*?)|" +
            "(([ ]*\\))(?!.*([ ]*\\))))");
    */
    private static final String CREATE_VIEW_REGEX = new String(
            "(((C|c)(R|r)(E|e)(A|a)(T|t)(E|e) (V|v)(I|i)(E|e)(W|w) ).*?)|" +
            "(((A|a)(S|s) )(?!.*((A|a)(S|s) )))");
    
    private String viewString = null;
    private String selectString = null;
    private String condString = null;
    
    private String viewName = null;
    private int viewType = 0;
    private Node condTree = null;
    private String joinConstraints = null;
    private TableMap[] phyMap = null;

    public CreateView(String cvCmd) {
        parse(cvCmd);
    }
    
    private boolean parse(String cvCmd) {
        viewString = cvCmd;
        String[] result = Pattern.compile(CREATE_VIEW_REGEX).split(cvCmd);
        
        if (result.length >= 2) viewName = result[1].trim();
        if (result.length >= 3) {
            selectString = result[2].trim();
            SQLParser sqlP = new SQLParser(selectString);
            
            String[] columns = sqlP.getColumns();
            String[] phyTables = sqlP.getTables();
            phyMap = new TableMap[phyTables.length];
            
            for (int i=0; i<phyTables.length; i++) {
                String phyTable = phyTables[i];
                
                int colCnt = 0;
                for (String c : columns) {
                    if ((phyMap.length > 1 && c.startsWith(phyTable)) || (phyMap.length == 1) || (c.equals("*"))) {
                        colCnt++;
                    }
                }
                
                phyMap[i] = new TableMap(phyTable, colCnt);
                for (String c : columns) {
                    if ((phyMap.length == 1) && (c.equals("*"))) {
                        ViewTable v = DBConn.querySelectDB("SELECT * FROM " + phyTable);
                        phyMap[i] = new TableMap(phyTable, v.colNames.length);
                        for (int j = 0; j < v.colNames.length; j++) {
                            phyMap[i].addColumn(v.colNames[j].toLowerCase());
                        }
                        break;
                    } else if ((phyMap.length == 1) && !(c.equals("*"))) {
                        phyMap[i].addColumn(c.toLowerCase());
                    } else if ((phyMap.length > 1) && (c.equals("*"))) {
                        ViewTable v = DBConn.querySelectDB("SELECT * FROM " + phyTable);
                        phyMap[i] = new TableMap(phyTable, v.colNames.length);
                        for (int j = 0; j < v.colNames.length; j++) {
                            phyMap[i].addColumn(v.colNames[j].toLowerCase());//+"_"+i);
                            //phyMap[i].addColumn(v.colNames[j].toLowerCase()+"_"+i);
                        }
                        break;
                    } else if (phyMap.length > 1 && c.startsWith(phyTable)) {
                        // strip off prefix and prepend with _index.
                        StringBuffer sb = new StringBuffer(c);
                        //phyMap[i].addColumn(sb.delete(0, phyTable.length()+1).append("_" + i).toString().toLowerCase());
                        phyMap[i].addColumn(sb.delete(0, phyTable.length()+1).toString().toLowerCase());
                    }
                }
            }
            
            joinConstraints = sqlP.getJoinConstraint();
            condTree = sqlP.getCondTree();
            condString = sqlP.getConditionString();
            
            viewType = 0;
            if (condTree != null) viewType += RESTRICTION;
            if (!(columns.length==1 && columns[0].equals("*"))) viewType += PROJECTION;
            if (joinConstraints != null) viewType += JOIN;
        }
        return true;
    }

    public boolean isProjection() {
        return viewType >= PROJECTION;
    }
    
    public boolean isRestriction() {
        return viewType >= RESTRICTION;
    }
    
    public boolean isJoin() {
        return viewType >= JOIN;
    }
    
    public String getCreateViewSQL() {
        return viewString;
    }

    public String getCreateViewDefinitionSQL() {
        return selectString;
    }

    public String getCondString() {
        return condString;
    }

    public String getViewName() {
        return viewName;
    }

    public int getViewType() {
        return viewType;
    }

    public Node getCondTree() {
        return condTree;
    }

    public TableMap[] getPhyMap() {
        return phyMap;
    }
}
