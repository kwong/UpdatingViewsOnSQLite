package datastructs;

public class BoolNode extends Node {
	public boolean val;

	public BoolNode(boolean val, Node LNode, Node RNode) {
		super(LNode, RNode);
		this.val = val;
	}

	public Boolean eval() {
		return val;
	}

	@Override
	public boolean isAttributeNode() {
		return false;
	}
}