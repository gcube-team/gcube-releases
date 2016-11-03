package org.gcube.search.sru.consumer.parser.sruparser.tree;

public class GCQLTermNode extends GCQLNode {

	private static final long serialVersionUID = 5434878196097016820L;
	String index;
	GCQLRelation relation;
	String term;
	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public GCQLRelation getRelation() {
		return relation;
	}
	public void setRelation(GCQLRelation relation) {
		this.relation = relation;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	
	@Override
	public String toCQL() {
		String cqlStr;
		if (index != null && !index.equals("")) {
			// we also have an index and a relation
			cqlStr = index + " " + relation.toCQL() + " " + term; 
		} else
			cqlStr = term;
		
		return cqlStr;
	}
	
	@Override
	public void printNode(int numStars) {
		System.out.println();
		for (int i = 0; i < numStars; i++) {
			System.out.print("*");
		}
		System.out.println(this.getClass().getName() + " ---- " + toCQL() + " ---- ");
	}
	
}
