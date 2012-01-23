package datastructs;

import parser.SQLParser;

public class GTNode extends Node {
	Node lnode;
	Node rnode;

	public GTNode(Node lnode, Node rnode) {
		super(lnode, rnode);
		this.lnode = lnode;
		this.rnode = rnode;
	}

	public BoolNode eval() {
		int left, right;
		IntNode lnew, rnew;
		
		if (lnode instanceof IntNode) {
			left = ((IntNode) lnode).eval();
		} else if (lnode instanceof AttributeNode) {
			if (lnode.eval() == null) return new BoolNode((evalType == SQLParser.INSERT) ? false : true, null, null);
			try {
				left = Integer.parseInt(lnode.eval().toString().trim());
			} catch (NumberFormatException nfe) { return new BoolNode((evalType == SQLParser.INSERT) ? false : true, null, null); }
		} else {
			lnew = (IntNode) (lnode.eval());
			left = ((IntNode) lnew).eval();
		}

		if (rnode instanceof IntNode) {
			right = ((IntNode) rnode).eval();
		} else if (rnode instanceof AttributeNode) {
			if (rnode.eval() == null) return new BoolNode(false, null, null);
			try {
				right = Integer.parseInt(rnode.eval().toString().trim());
			} catch (NumberFormatException nfe) { return new BoolNode((evalType == SQLParser.INSERT) ? false : true, null, null); }
		} else {
			rnew = (IntNode) (rnode.eval());
			right = ((IntNode) rnew).eval();
		}

		return new BoolNode(left > right, null, null);
	}

}