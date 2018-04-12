package org.gcube.portlets.user.geoexplorer.client.beans;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Property implements IsSerializable, Cloneable {
	
	private String name;
	private String type;	
	private boolean nillable=true;
	private int maxOccours=1;
	private int minOccours=0;
	
	public Property() {
		super();
	}
	
	public Property(String name, String type, boolean nillable,
			int maxOccours, int minOccours) {
		super();
		
		this.name = name;
		this.type = type;
		this.nillable = nillable;
		this.maxOccours = maxOccours;
		this.minOccours = minOccours;
	}

	public Property(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isNillable() {
		return nillable;
	}

	public void setNillable(boolean nillable) {
		this.nillable = nillable;
	}

	public int getMaxOccours() {
		return maxOccours;
	}

	public void setMaxOccours(int maxOccours) {
		this.maxOccours = maxOccours;
	}

	public int getMinOccours() {
		return minOccours;
	}

	public void setMinOccours(int minOccours) {
		this.minOccours = minOccours;
	}
}
