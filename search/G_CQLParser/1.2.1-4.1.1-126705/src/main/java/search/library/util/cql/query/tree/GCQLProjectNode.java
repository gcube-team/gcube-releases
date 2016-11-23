package search.library.util.cql.query.tree;

import java.util.Vector;

public class GCQLProjectNode extends GCQLNode {
	
	public GCQLNode subtree; // the root of a subtree representing the query whose result 
							 // contains the attributes that will be projected

	Vector<ModifierSet> projectIndexes = new Vector<ModifierSet>();
	
	
	public void addProjectIndex(ModifierSet key) {
		projectIndexes.add(key);
	}
	
	public Vector<ModifierSet> getProjectIndexes() {
		return projectIndexes;
	}

	
	@Override
	public String toCQL() {
		String projectCQL = " project ";
		for (int i = 0; i < projectIndexes.size(); i++) {
			projectCQL += projectIndexes.get(i).toCQL() + " ";
		}
		
		String finalCQL = subtree.toCQL() + projectCQL;
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
