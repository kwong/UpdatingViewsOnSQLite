package datastructs;

public class IntNode extends Node {
	public int val;

	public IntNode(int val, Node LNode, Node RNode) {
		super(LNode, RNode);
		this.val = val;
	}

	public Integer eval() {
		return val;
	}
}