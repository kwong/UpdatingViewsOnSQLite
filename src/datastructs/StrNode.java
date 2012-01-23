package datastructs;

public class StrNode extends Node {
	public String val;

	public StrNode(String val, Node LNode, Node RNode) {
		super(LNode, RNode);
		this.val = val;
	}

	public String eval() {
		return val;
	}
}