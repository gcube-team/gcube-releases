package org.gcube.portlets.admin.resourcemanagement.client;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

@SuppressWarnings("serial")
public class ContexClientModel extends BaseModelData implements IsSerializable {
	 
	public ContexClientModel() {
	}
	public ContexClientModel(final String id, final String name) {
		set("id", id);
		set("name", name);
	}
	public String getId() {
		return get("id");
	}
	public String getName() {
		return get("name");
	}
	public void setId(final String id) {
		set("id", id);
	}
	public void setName(final String name) {
		set("name", name);
	}
}
