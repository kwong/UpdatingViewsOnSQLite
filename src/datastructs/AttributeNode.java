package datastructs;

public class AttributeNode extends Node {
	String val;

	public AttributeNode(String val, Node LNode, Node RNode) {
		super(LNode, RNode);
		this.val = val;
	}
	// Look for actual values in the set
	public String eval() {
		return AttributeToValues.get(val);
	}

	public boolean isAttributeNode() {
		return true;
	}
}