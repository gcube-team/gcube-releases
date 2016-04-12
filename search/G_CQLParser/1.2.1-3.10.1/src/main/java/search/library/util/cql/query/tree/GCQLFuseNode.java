package search.library.util.cql.query.tree;

public class GCQLFuseNode extends GCQLNode {
	
	public GCQLNode subtree; // the root of a subtree representing the query whose result 
							 // contains the attributes that will be projected

	ModifierSet fuseMode;
	
	
	public void setFuseMode(ModifierSet key)
	{
		fuseMode = key;
	}
	
	public ModifierSet getFuseMode() {
		return fuseMode;
	}

	
	@Override
	public String toCQL() {
		String fuseCQL = " fuse ";
		fuseCQL += fuseMode.toCQL() + " ";
		
		String finalCQL = subtree.toCQL() + fuseCQL;
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
