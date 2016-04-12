package org.gcube.portlets.user.gisviewer.client.commons.beans;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CQLQueryObject implements IsSerializable {

	private String layerName;
	private String cqlQuery;
	
	public CQLQueryObject() {
		super();
	}

	public CQLQueryObject(String layerName, String cqlQuery) {
		super();
		this.layerName = layerName;
		this.cqlQuery = cqlQuery;
	}

	public String getLayerName() {
		return layerName;
	}

	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}

	public String getCqlQuery() {
		return cqlQuery;
	}

	public void setCqlQuery(String cqlQuery) {
		this.cqlQuery = cqlQuery;
	}
}
