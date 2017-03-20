package search.library.util.cql.query.tree;

public class GCQLAndNode extends GCQLBooleanNode {
	
	public GCQLAndNode() {
		ms = new ModifierSet(OperationTypeConstants.booleanAnd);
	}
	
//	@Override
//	public String toCQL(){
//		String operator;
//		operator = ms.toCQL();
//		String booleanCQL = left.toCQL() + " " + operator + " (" + right.toCQL() + ")";
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
