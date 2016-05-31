/**
 * 
 */
package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.autoComplete;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author Spyros Boutsis, NKUA
 *
 */
public class ObjectNameAndPtr implements IsSerializable {

	private String name;
	private String id_ptr;
	
	public ObjectNameAndPtr() { }
	
	public ObjectNameAndPtr(String name, String id_ptr) {
		this.name = name;
		this.id_ptr = id_ptr;
	}
	
	public String getName() {
		return name;
	}
	
	public String getIDPtr() {
		return id_ptr;
	}
}
