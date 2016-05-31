package org.gcube.portlets.admin.policydefinition.vaadin.containers;

import java.util.List;

import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.portlets.admin.policydefinition.common.util.PresentationHelper;
import org.gcube.portlets.admin.policydefinition.services.informationsystem.InformationSystemClient;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.IndexedContainer;

/**
 * @deprecated
 * @see {@link ServicesQuery} and {@link ServicesQueryFactory} for pagination functionality.
 */
public class ServicesContainer {

	public static final String SERVICE_NAME = "Service Name";
	public static final String SERVICE_CLASS = "Service Class";
	
	public static Container getServicesContainer(){// throws IOException{
		// Create a container
		Container container = new IndexedContainer();
		 
		// Define the properties (columns) if required by container
		container.addContainerProperty(SERVICE_NAME, String.class, "none");
		container.addContainerProperty(SERVICE_CLASS, String.class, "none");
		
		// add items
		List<GCoreEndpoint> retrieveServiceEndpoints = InformationSystemClient.getInstance().retrieveServices();
		for (GCoreEndpoint gCoreEndpoint : retrieveServiceEndpoints) {
			Item item = container.addItem(PresentationHelper.buildNameHelper(gCoreEndpoint.profile().serviceName(), gCoreEndpoint.profile().serviceClass()));
			item.getItemProperty(SERVICE_NAME).setValue(gCoreEndpoint.profile().serviceName());
			item.getItemProperty(SERVICE_CLASS).setValue(gCoreEndpoint.profile().serviceClass());
		}
		return container;
	}
	
	
}
