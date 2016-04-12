package org.gcube.search.sru.consumer.parser.sruparser.tree;

public class GCQLPrefixNode extends GCQLNode{
	
	private static final long serialVersionUID = 7448975732947287754L;

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
