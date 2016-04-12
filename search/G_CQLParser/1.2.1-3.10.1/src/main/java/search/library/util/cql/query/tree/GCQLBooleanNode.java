package search.library.util.cql.query.tree;

public class GCQLBooleanNode extends GCQLNode {
	
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
