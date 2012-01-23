package logic;

import java.util.ArrayList;

public class IndexAssoc {

	public String tablename;
	public ArrayList<Integer> indices; // to improve
	//public int size = 0;

	public IndexAssoc(String tablename) {
		this.tablename = tablename;
		indices = new ArrayList<Integer>();
		// this.indices = indices;
	}


	/*public void addIndex(int i) {
		System.out.println("adding "+i+" at " + indices.size());
		this.indices.add(i);
		//indices[size] = i;
		
		//size = size + 1;
	}*/

}
