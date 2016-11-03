package search.library.util.cql.query.tree;

import java.util.Vector;

public class GCQLSortNode extends GCQLNode {
	
	public GCQLNode subtree; // the root of a subtree representing the query whose result is to be sorted

	Vector<ModifierSet> sortIndexes = new Vector<ModifierSet>();
	
	
	public void addSortIndex(ModifierSet key) {
		sortIndexes.add(key);
	}
	
	public Vector<ModifierSet> getSortIndexes() {
		return sortIndexes;
	}
	
	public String toCQL() {
		String sortCQL = "sortBy ";
		for (int i = 0; i < sortIndexes.size(); i++) {
			sortCQL += sortIndexes.get(i).toCQL() + " ";
		}
		
		String finalCQL = subtree.toCQL() + " " + sortCQL;
		return finalCQL;
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
