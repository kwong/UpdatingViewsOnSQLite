package datastructs;

import java.util.Hashtable;

public class Node extends Object {
	Node LNode;
	Node RNode;
	public Object val;
	
	protected static int evalType;
	protected static Hashtable<String,String> AttributeToValues; 
	
	public Node(Node LNode, Node RNode) {
		this.LNode = LNode;
		this.RNode = RNode;
	}
	
	public void setEvalType(int type) {
		evalType = type;
	}
	
	public void setAttributeToValues(Hashtable<String,String> atv){
		AttributeToValues = atv;
	}
	public boolean isAttributeNode() {
		return false;
	}

	public Object eval() {
		return val;
	}
}