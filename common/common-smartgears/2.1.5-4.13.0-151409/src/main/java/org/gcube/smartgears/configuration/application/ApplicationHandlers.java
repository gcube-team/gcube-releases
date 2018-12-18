package org.gcube.smartgears.configuration.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.ValidationError;
import org.gcube.common.validator.Validator;
import org.gcube.common.validator.ValidatorFactory;
import org.gcube.common.validator.annotations.IsValid;
import org.gcube.smartgears.handlers.application.ApplicationHandler;
import org.gcube.smartgears.handlers.application.ApplicationLifecycleHandler;
import org.gcube.smartgears.handlers.application.RequestHandler;
import org.w3c.dom.Element;

/**
 * The {@link ApplicationHandler}s that manage the application.
 *  
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name="handlers")
public class ApplicationHandlers {

	
	@XmlElement(name="lifecycle") @IsValid
	private LifecycleHandlers lifecycleHandlers = new LifecycleHandlers();
	
	@XmlElement(name="request") @IsValid
	private RequestHandlers requestHandlers = new RequestHandlers();
	
	public ApplicationHandlers() {}
	
	/**
	 * Returns the {@link ApplicationLifecycleHandler}s for the service.
	 * @return the lifecycle handlers
	 */
	public List<ApplicationLifecycleHandler> lifecycleHandlers() {
		return lifecycleHandlers.values;
	}
	
	/**
	 * Sets the {@link ApplicationLifecycleHandler}s for the service.
	 * @param handlers the lifecycle handlers
	 * @return this configuration
	 */
	public ApplicationHandlers set(ApplicationLifecycleHandler ... handlers) {
		this.lifecycleHandlers = new LifecycleHandlers(Arrays.asList(handlers));
		return this;
	}
	
	/**
	 * Returns the {@link RequestHandler}s for the service.
	 * @return the lifetime handlers
	 */
	public List<RequestHandler> requestHandlers() {
		return requestHandlers.values;
	}
	
	/**
	 * Sets the {@link RequestHandler}s for the service.
	 * @param handlers the request handlers
	 * @return this configuration
	 */
	public ApplicationHandlers set(RequestHandler ... handlers) {
		this.requestHandlers = new RequestHandlers(Arrays.asList(handlers));
		return this;
	}
	
	public void validate() {
		
		List<String> msgs = new ArrayList<String>();
		
		Validator validator = ValidatorFactory.validator();
		
		for (ValidationError error : validator.validate(this))
			msgs.add(error.toString());
		
		if (!msgs.isEmpty())
			throw new IllegalStateException("invalid configuration: "+msgs);
		
	}
    
   //////////////// HELPER BINDING CLASSES
   
   //used internally to introduce level of nesting in JAXB whilst preserving arbitrary extension
 	
    private static class LifecycleHandlers {
 	
 		@SuppressWarnings("all")
 		LifecycleHandlers() { //needed for deserialisation
 		}
 		
 		LifecycleHandlers(List<ApplicationLifecycleHandler> handlers) {
 			this.values=handlers;
 		}
 		
 		@XmlAnyElement(lax=true)
 		List<ApplicationLifecycleHandler> values = new ArrayList<ApplicationLifecycleHandler>();
 		

 		//since we use @AnyElement, after deserialisation, we check there are no DOM elements
 		@SuppressWarnings("unused")
 	    void afterUnmarshal(Unmarshaller u, Object parent) {
 	    	for (Object o : values)
 	    		if (o instanceof Element)
 	    			throw new RuntimeException("invalid handler detected in configuration: "+Element.class.cast(o).getLocalName());
 	    }
 		
 	}
 	
 	//used internally to introduce level of nesting in JAXB whilst preserving arbitrary extension
 	private static class RequestHandlers {
 	
 		@SuppressWarnings("all")
 		RequestHandlers() { //needed for deserialisation
 		}
 		
 		RequestHandlers(List<RequestHandler> handlers) {
 			this.values=handlers;
 		}
 		
 		@XmlAnyElement(lax=true)
 		List<RequestHandler> values = new ArrayList<RequestHandler>();
 		
 		//since we use @AnyElement, after deserialisation, we check there are no DOM elements
 		@SuppressWarnings("unused")
 	    void afterUnmarshal(Unmarshaller u, Object parent) {
 	    	for (Object o : values)
 	    		if (o instanceof Element)
 	    			throw new RuntimeException("invalid handler detected in configuration: "+Element.class.cast(o).getLocalName());
 	    }
 	}
 	
 	public void mergeWith(ApplicationHandlers other){
 		List<ApplicationLifecycleHandler> lifecycles = other.lifecycleHandlers();
 		for (ApplicationLifecycleHandler handler : lifecycles)
 			if (!this.lifecycleHandlers().contains(handler))
 				this.lifecycleHandlers().add(handler);
		
 		List<RequestHandler> requests = other.requestHandlers();
 		for (RequestHandler handler : requests)
 			if (!this.requestHandlers().contains(handler))
 				this.requestHandlers().add(handler);
 
 	}
   
}