package search.library.util.cql.query.tree;

public class GCQLPrefixNode extends GCQLNode{
	
	GCQLPrefix prefix; // the prefix definition that governs the subree
	
	GCQLNode subtree; // the root of a parse-tree representing the part of the query that is governed by this prefix definition
	
	
	public GCQLPrefixNode(String name, String identifier, GCQLNode subree) {
		this.subtree = subree;
		prefix = new GCQLPrefix(name, identifier);
	}
	
	@Override
	public String toCQL(){
		String prefixCQL = prefix.toCQL() + " " + subtree.toCQL();
		return prefixCQL;
	}
	
	@Override
	public void printNode(int numStars) {
		System.out.println();
		for (int i = 0; i < numStars; i++) {
			System.out.print("*");
		}
		System.out.println(this.getClass().getName() + " ---- " + toCQL() + " ---- ");
		
		int newNum = numStars + 1;
		subtree.printNode(newNum);
	}
	

}
