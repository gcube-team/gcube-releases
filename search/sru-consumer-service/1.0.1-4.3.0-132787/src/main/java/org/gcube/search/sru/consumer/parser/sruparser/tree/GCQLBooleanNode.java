package org.gcube.search.sru.consumer.parser.sruparser.tree;

public class GCQLBooleanNode extends GCQLNode {
	
	private static final long serialVersionUID = -1980549695568487090L;

	public GCQLNode left;
	
	public ModifierSet ms;

	public GCQLNode right;
	
	
	@Override
	public String toCQL() {
		return ("((" + left.toCQL() + ")" +
				" " + ms.toCQL() + " " +
				"(" + right.toCQL() + "))");
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
