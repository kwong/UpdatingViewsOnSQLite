package datastructs;

import parser.SQLParser;

public class NotEqualsNode extends Node {
	Node lnode;
	Node rnode;

	public NotEqualsNode(Node lnode, Node rnode) {
		super(lnode, rnode);
		this.lnode = lnode;
		this.rnode = rnode;
	}

	public BoolNode eval() {
		String left, right;
		Node lnew, rnew;
		
		/*
		
		if (rnode.eval() == null) return null;
			try {
				right = Integer.parseInt(rnode.eval().toString().trim());
			} catch (NumberFormatException nfe) { return null; }
		
		 */
		
		if (lnode instanceof StrNode || lnode instanceof IntNode || lnode instanceof BoolNode) {
			left = lnode.eval().toString();
		} else if (lnode instanceof AttributeNode) {
			if (lnode.eval() == null) return new BoolNode((evalType == SQLParser.INSERT) ? false : true, null, null);
			try {
				left = lnode.eval().toString().trim();
			} catch (NumberFormatException nfe) { return new BoolNode((evalType == SQLParser.INSERT) ? false : true, null, null); }
		} else {
			lnew = (Node) lnode.eval();
			left = ((StrNode) lnew).eval();
		}

		if (rnode instanceof StrNode || rnode instanceof IntNode || rnode instanceof BoolNode) {
			right = rnode.eval().toString();
		} else if (rnode instanceof AttributeNode) {
			if (rnode.eval() == null) return new BoolNode((evalType == SQLParser.INSERT) ? false : true, null, null);
			try {
				right = rnode.eval().toString().trim();
			} catch (NumberFormatException nfe) { return new BoolNode((evalType == SQLParser.INSERT) ? false : true, null, null); }
		} else {
			rnew = (Node) (rnode.eval());
			right = ((StrNode) rnew).eval();
		}

		return new BoolNode(!left.equalsIgnoreCase(right), null, null);
	}

}