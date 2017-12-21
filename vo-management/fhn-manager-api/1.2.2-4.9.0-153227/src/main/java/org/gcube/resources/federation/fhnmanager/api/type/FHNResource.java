package org.gcube.resources.federation.fhnmanager.api.type;

import java.io.Serializable;

public class FHNResource implements Serializable {

	protected String id;

	public FHNResource() {
	}

	public FHNResource(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
