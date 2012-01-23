package parser.view;

public class TableMap {
    private String tableName;
    private int numColumns;
    private String[] columns;
    
    private int c;
    
    public TableMap (String tableName, int numColumns) {
        this.tableName = tableName;
        this.numColumns = numColumns;
        this.columns = new String[numColumns];
        this.c = 0;
    }
    
    public void addColumn(String colName) {
        if (c < numColumns) {
            columns[c] = colName;
            this.c++;
        }
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public String getColumnAtIndex(int index) {
        if (index >= numColumns) {
            return null;
        } else {
            return columns[index];
        }
    }
    
    public String getColString() {
        int i = 0;
        String colString = columns[i];
        i++;
        for (;i<columns.length;i++) {
            colString = colString + ", " + columns[i];
        }
        return colString;
    }
    
    public int getTotalColumns() {
        return this.numColumns;
    }
}