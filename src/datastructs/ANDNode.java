package datastructs;

import parser.SQLParser;

public class ANDNode extends Node {
	Node lnode;
	Node rnode;

	public ANDNode(Node lnode, Node rnode) {
		super(lnode, rnode);
		this.lnode = lnode;
		this.rnode = rnode;
	}

	public BoolNode eval() {
		boolean left, right;
		BoolNode lnew, rnew;
		
		if (lnode instanceof BoolNode) {
			left = ((BoolNode) lnode).eval();
		} if (lnode == null) {
			left = (evalType == SQLParser.INSERT) ? false : true;
		} else {
			lnew = (BoolNode) (lnode.eval());
			left = ((BoolNode) lnew).eval();
		}

		if (rnode instanceof BoolNode) {
			right = ((BoolNode) rnode).eval();
		} if (rnode == null) {
			right = (evalType == SQLParser.INSERT) ? false : true;
		} else {
			rnew = (BoolNode) (rnode.eval());
			right = ((BoolNode) rnew).eval();
		}

		return new BoolNode(left && right, null, null);
	}

}