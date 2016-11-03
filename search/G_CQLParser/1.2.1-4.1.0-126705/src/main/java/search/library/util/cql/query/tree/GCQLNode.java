package search.library.util.cql.query.tree;

public class GCQLNode {
	
	/**
	 * Decompiles a parse-tree into a CQL query.
	 * @return
	 */
	public String toCQL() {
		return "";
	}
	
	/**
	 * prints the node as part of the tree to which it belongs
	 * @param numSpaces
	 */
	public void printNode(int numStars) {
		System.out.println();
		for (int i = 0; i < numStars; i++)
			System.out.print("*");
		System.out.println(this.toString());
	}

}
