package org.gcube.search.sru.consumer.parser.sruparser.tree;

public class GCQLProxNode extends GCQLBooleanNode {
	
	private static final long serialVersionUID = -4621681679636534988L;

	public GCQLProxNode() {
		ms = new ModifierSet(OperationTypeConstants.booleanProx);
	}
	
//	@Override
//	public String toCQL(){
//		String operator;
//		operator = ms.toCQL();
//		String booleanCQL = left.toCQL() + " " + operator + " " + right.toCQL();
//		return booleanCQL;
//	}
	
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
