package org.gcube.portlets.admin.vredeployer.client.model;


import com.extjs.gxt.ui.client.data.BaseTreeModel;

@SuppressWarnings("serial")
public class VREFunctionalityModel extends BaseTreeModel{
	
	
	public VREFunctionalityModel() {
		
	}
	/**
	 * @param node
	 * @param description
	 * @param icon
	 */
	public VREFunctionalityModel(String id, String node, String description, String icon, boolean isSelected) {
		set("id",id);
		set("node", node);
		set("name", node);
		set("description", description);
		set("icon", icon);
		set("isSelected", isSelected);
	}
	
	/**
	 * 
	 * @param children
	 */
	public void addChildren(VREFunctionalityModel[] children) {
		for (int i = 0; i < children.length; i++) {
			add(children[i]);
		}
	}
	
	public String getId() {
		return (String) get("id");
	}

	public String getName() {
		return (String) get("name");
	}

	public String getNode() {
		return (String) get("node");
	}
	
	public String getDescription() {
		return (String) get("description");
	}

	public String toString() {
		return getName();
	}
	
	public boolean isSelected() {
		return ((Boolean) get("isSelected")).booleanValue();
	}
	
	public void setSelected(boolean selected) {
		set("isSelected", selected);
	}

}
