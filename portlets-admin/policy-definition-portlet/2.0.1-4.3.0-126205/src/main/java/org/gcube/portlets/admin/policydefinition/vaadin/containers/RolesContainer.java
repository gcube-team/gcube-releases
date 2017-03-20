package org.gcube.portlets.admin.policydefinition.vaadin.containers;

import java.util.List;

import org.gcube.portlets.admin.policydefinition.common.util.RoleHelper;
import org.gcube.portlets.admin.policydefinition.services.restful.RestfulClient;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

public class RolesContainer {
	
	public static Container getOnlyRoles() throws Exception{
		// Create a container
		Container container = new IndexedContainer();
		container.addContainerProperty("role", String.class, "none");
		
		// add items
		List<String> roles = RestfulClient.getInstance().getRoles();
		for (String role : roles) {
			if(RoleHelper.getRole(role) != null){
				Item item = container.addItem(RoleHelper.viewRole(role));
				item.getItemProperty("role").setValue(role);
			}
		}
		return container;
	}
	
	public static Container getOnlyServiceCategory() throws Exception{
		// Create a container
		Container container = new IndexedContainer();
		container.addContainerProperty("role", String.class, "none");
		
		// add items
		List<String> roles = RestfulClient.getInstance().getRoles();
		for (String role : roles) {
			if(RoleHelper.getServiceCategory(role) != null){
				Item item = container.addItem(RoleHelper.viewRole(role));
				item.getItemProperty("role").setValue(role);
			}
		}
		return container;
	}

}
