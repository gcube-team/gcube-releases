package org.gcube.data.analysis.tabulardata.commons.templates.model.actions.finals;

public enum DuplicateBehaviour {

	Older("keeps older entries"), 
	Newer("keeps newer entries"),
	None("keeps all entries");
	
	private String description;
	
	private DuplicateBehaviour(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return description;
	}
}
