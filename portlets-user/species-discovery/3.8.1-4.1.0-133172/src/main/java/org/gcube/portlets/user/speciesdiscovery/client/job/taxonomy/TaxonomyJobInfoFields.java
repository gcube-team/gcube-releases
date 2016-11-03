package org.gcube.portlets.user.speciesdiscovery.client.job.taxonomy;

import org.gcube.portlets.user.speciesdiscovery.client.util.GridField;

public enum TaxonomyJobInfoFields implements GridField{
	NAME("name","Name"),
	LOADING("loading","Loading");
	
	private String id;
	private String name;
	
	private TaxonomyJobInfoFields(String id, String name) {
		this.id=id;
		this.name=name;
	}
	
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isSortable() {
		return false;
	}

}
