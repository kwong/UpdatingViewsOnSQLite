package datastructs;

public class ModNode extends Node {
	Node lnode;
	Node rnode;

	public ModNode(Node lnode, Node rnode) {
		super(lnode, rnode);
		this.lnode = lnode;
		this.rnode = rnode;
	}

	public IntNode eval() {
		int left, right;
		IntNode lnew, rnew;
		if (lnode instanceof IntNode) {
			left = ((IntNode) lnode).eval();
		} else if (lnode instanceof AttributeNode) {
			if (lnode.eval() == null) return null;
			try {
				left = Integer.parseInt(lnode.eval().toString().trim());
			} catch (NumberFormatException nfe) { return null; }
		} else {
			lnew = (IntNode) (lnode.eval());
			left = ((IntNode) lnew).eval();
		}

		if (rnode instanceof IntNode) {
			right = ((IntNode) rnode).eval();
		} else if (rnode instanceof AttributeNode) {
			if (rnode.eval() == null) return null;
			try {
				right = Integer.parseInt(rnode.eval().toString().trim());
			} catch (NumberFormatException nfe) { return null; }
		} else {
			rnew = (IntNode) (rnode.eval());
			right = ((IntNode) rnew).eval();
		}

		return new IntNode(left % right, null, null);
	}

}