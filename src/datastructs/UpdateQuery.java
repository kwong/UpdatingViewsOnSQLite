package datastructs;

import parser.SQLParser;
import parser.view.CreateView;

public class UpdateQuery {
	private CreateView cv;
	private SQLParser sp;
	private String[] pkval; 
	public UpdateQuery(){
		
	}
	public UpdateQuery(CreateView cv, SQLParser sp, String[] pkval){
		this.cv = cv;
		this.sp = sp;
		this.pkval = pkval;
	}
	public CreateView getView() {
		return this.cv;
	}
	public SQLParser getSp(){
		return this.sp;
	}
	public String[] getPkVal(){
		return this.pkval;
	}
}
