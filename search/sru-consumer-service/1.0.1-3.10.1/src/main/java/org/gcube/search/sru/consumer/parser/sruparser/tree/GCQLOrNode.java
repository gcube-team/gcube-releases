package org.gcube.search.sru.consumer.parser.sruparser.tree;

public class GCQLOrNode extends GCQLBooleanNode {
	
	private static final long serialVersionUID = 6417253969708223616L;

	public GCQLOrNode() {
		ms = new ModifierSet(OperationTypeConstants.booleanOr);
	}
	
	@Override
	public void printNode(int numStars) {
		System.out.println();
		for (int i = 0; i < numStars; i++) {
			System.out.print("*");
		}
		System.out.println(this.getClass().getName() + " ---- " + toCQL() + " ---- ");
		
		int newNum = numStars + 1;
		left.printNode(newNum);
		right.printNode(newNum);
	}

}
