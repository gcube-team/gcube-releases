package org.gcube.portlets.user.td.gwtservice.shared.rule;


/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public enum RuleScopeType {
	TABLE("Table"), COLUMN("Column");
	/**
	 * @param text
	 */
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
