package org.gcube.portlets.user.workspace.client.model;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 *
 */
public class ScopeModel extends BaseModelData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScopeModel() {}

	public ScopeModel(String id, String name) {
		setId(id);
		setName(name);
	}
	
	public String getId() {
		return get("id");
	}

	public void setId(String id) {
		set("id", id);
	}

	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}
}