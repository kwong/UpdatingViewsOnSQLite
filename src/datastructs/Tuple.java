package datastructs;

public class Tuple {

	public String[] tuple;
	int maxCols;
	public Tuple(int maxCols) {
		this.maxCols = maxCols;
		tuple = new String[maxCols];
		
	}
	
	public String toString() {
		String s = "";
		for(int i = 0; i<this.maxCols; i++)
			s = s + "\t"+ tuple[i] ;
		s = s+ "\n";
		return s;
	}
}
