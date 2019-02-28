package org.gcube.portlets.user.speciesdiscovery.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class QueryModel extends BaseModelData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public QueryModel(String name, String description, String queryString) {
		set("name", name);
	    set("description", description);
	    set("queryString", queryString);
	}

}
