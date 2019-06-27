package org.gcube.portlets.user.td.gwtservice.shared.rule;


/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public enum RuleScopeType {
	TABLE("Table"), COLUMN("Column");
	
	private RuleScopeType(final String id) {
		this.id = id;
	}

	private final String id;

	@Override
	public String toString() {
		return id;
	}
	
	public String getLabel() {
		return id;
	}
	
	
	public static RuleScopeType get(String ruleScopeType) {
		for(RuleScopeType ws:values()){
			if(ws.id.compareTo(ruleScopeType)==0){
				return ws;
			}
		}
		
		return null;
	}
	
}
