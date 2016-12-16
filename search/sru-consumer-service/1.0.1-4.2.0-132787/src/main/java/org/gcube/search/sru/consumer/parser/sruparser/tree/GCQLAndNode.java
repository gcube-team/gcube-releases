package org.gcube.search.sru.consumer.parser.sruparser.tree;

public class GCQLAndNode extends GCQLBooleanNode {
	
	private static final long serialVersionUID = 5126343641586776174L;

	public GCQLAndNode() {
		ms = new ModifierSet(OperationTypeConstants.booleanAnd);
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
