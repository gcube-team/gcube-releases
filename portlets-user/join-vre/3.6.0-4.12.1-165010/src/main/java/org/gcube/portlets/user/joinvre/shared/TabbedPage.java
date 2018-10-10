package org.gcube.portlets.user.joinvre.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class TabbedPage implements IsSerializable, Comparable<TabbedPage> {

	protected String name;
	protected String description;	
	
	public TabbedPage() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TabbedPage(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	/** {@inheritDoc} */
	@Override
	public int compareTo(TabbedPage tabName) {
		return name.compareTo(tabName.name);
	}
}
