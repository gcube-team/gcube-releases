package org.gcube.portlets.admin.policydefinition.vaadin.containers;

import java.util.List;

import org.gcube.portlets.admin.policydefinition.services.informationsystem.InformationSystemClient;

import com.vaadin.data.Container;
import com.vaadin.data.util.IndexedContainer;

public class HostsContainer {
	
	public static Container getHostsContainer(String serviceName, String serviceClass){
		// Create a container
		Container container = new IndexedContainer();
		
		// add items
		List<String> retrieveHosts = InformationSystemClient.getInstance().retrieveHosts(serviceName, serviceClass);
		for (String hostName : retrieveHosts) {
			container.addItem(retrieveHost(hostName));
		}

		return container;
	}
	
	/**
	 * Retrieve the host-name from URL.
	 * @param hostURI
	 * @return Host Name
	 */
	public static String retrieveHost(String hostURI){
		String temp = hostURI.substring(hostURI.indexOf("://")+3);
//		if(temp.indexOf(":") > 0)
//			return temp.substring(0, temp.indexOf(":"));
		if(temp.indexOf("/") > 0)
			return temp.substring(0, temp.indexOf("/"));
		else 
			return temp;
	}
}
