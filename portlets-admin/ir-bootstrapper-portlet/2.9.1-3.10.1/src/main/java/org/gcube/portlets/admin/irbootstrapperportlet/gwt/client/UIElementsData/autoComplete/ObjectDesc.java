/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class ObjectDesc implements IsSerializable {
	private String ID;
	private List<ObjectNameAndPtr> children;
	
	public ObjectDesc() { }
	
	public ObjectDesc(String ID) {
		this.ID = ID;
		this.children = new LinkedList<ObjectNameAndPtr>();
	}
	
	public String getID() {
		return this.ID;
	}
	
	public void addChild(String name, String childID_ptr) {
		this.children.add(new ObjectNameAndPtr(name, childID_ptr));
	}
	
	public void addChild(ObjectNameAndPtr child) {
		this.children.add(child);
	}
	
	public List<ObjectNameAndPtr> getChildren() {
		return this.children;
	}
}
