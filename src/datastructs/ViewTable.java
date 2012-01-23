package datastructs;

import java.util.ArrayList;



public class ViewTable extends ArrayList<Tuple> {

	
	private static final long serialVersionUID = 38377955508183810L;
	public ArrayList<Tuple> newTL = new ArrayList<Tuple>();
	public String[] colNames;
	public int numColumns;
	private int c = 0;
	
	public ViewTable(){
		
	}
	public ViewTable (int numColumns) {
		this.numColumns = numColumns;
		colNames = new String[numColumns];
	}
	
	public boolean addTuple(Tuple t) {
		try  {
			newTL.add(t);
			return true;
		}
		catch(Exception e) {
			System.err.println("Failed to add tuple to some QueryResult instance.");
			return false;
		}
	}
	
	public void addColName(String attrName) {
		colNames[c] = attrName;
		this.c++;
	}
}
