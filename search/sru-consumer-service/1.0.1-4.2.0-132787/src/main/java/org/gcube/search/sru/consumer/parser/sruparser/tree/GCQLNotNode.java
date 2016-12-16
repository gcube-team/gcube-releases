package org.gcube.search.sru.consumer.parser.sruparser.tree;

public class GCQLNotNode extends GCQLBooleanNode {
	
	private static final long serialVersionUID = 8560234303786642514L;

	public GCQLNotNode() {
		ms = new ModifierSet(OperationTypeConstants.booleanNot);
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
