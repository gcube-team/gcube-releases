package org.gcube.smartgears.configuration.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.smartgears.handlers.container.ContainerHandler;

/**
 * The {@link ContainerHandler}s that manage the application.
 *  
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name="handlers")
public class ContainerHandlers {
	
	@XmlAnyElement(lax=true)
	List<ContainerHandler> handlers = new ArrayList<ContainerHandler>();
	
	public ContainerHandlers() {}
	
	/**
	 * Returns the {@link ContainerHandler}s for the service.
	 * @return the lifecycle handlers
	 */
	public List<ContainerHandler> get() {
		return handlers;
	}
	
	/**
	 * Sets the {@link ContainerHandler}s for the service.
	 * @param handlers the lifecycle handlers
	 * @return this configuration
	 */
	public ContainerHandlers set(ContainerHandler ... handlers) {
		this.handlers = Arrays.asList(handlers);
		return this;
	}
   
	public void mergeWith(ContainerHandlers other){
 		List<ContainerHandler> handlers = other.get();
 		for (ContainerHandler handler : handlers)
 			if (!this.get().contains(handler))
 				this.get().add(handler);
 	}
}