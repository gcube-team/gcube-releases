package org.gcube.resourcemanagement.support.shared.types.datamodel;

import java.io.Serializable;

import org.gcube.common.scope.impl.ScopeBean;

@SuppressWarnings("serial")
public class D4SEnvironment implements Serializable{
	private ScopeBean context;
	private String uuid;
	
	public D4SEnvironment() {
		super();
		// TODO Auto-generated constructor stub
	}
	public D4SEnvironment(ScopeBean context, String uuid) {
		super();
		this.context = context;
		this.uuid = uuid;
	}
	public ScopeBean getContext() {
		return context;
	}
	public void setContext(ScopeBean context) {
		this.context = context;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("D4SEnvironment [context=");
		builder.append(context);
		builder.append(", uuid=");
		builder.append(uuid);
		builder.append("]");
		return builder.toString();
	}
	
	

}
