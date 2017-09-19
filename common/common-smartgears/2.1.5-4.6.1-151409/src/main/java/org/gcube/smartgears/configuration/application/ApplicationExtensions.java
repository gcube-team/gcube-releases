package org.gcube.smartgears.configuration.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.common.validator.ValidationError;
import org.gcube.common.validator.Validator;
import org.gcube.common.validator.ValidatorFactory;
import org.gcube.common.validator.annotations.IsValid;
import org.gcube.smartgears.extensions.ApplicationExtension;
import org.w3c.dom.Element;

/**
 * The {@link ApplicationExtension}s that manage the application.
 *  
 * @author Fabio Simeoni
 *
 */
@XmlRootElement(name="extensions")
public class ApplicationExtensions {
	
	@XmlAnyElement(lax=true) @IsValid
	List<ApplicationExtension> extensions = new ArrayList<ApplicationExtension>();
	
	public ApplicationExtensions() {}
	
	/**
	 * Returns the extensions for the application.
	 * @return the extensions
	 */
	public List<ApplicationExtension> extensions() {
		return extensions;
	}
	
	/**
	 * Sets the extensions for the application.
	 * @param extensions the extensions
	 * @return this configuration
	 */
	public ApplicationExtensions set(ApplicationExtension ... extensions) {
		this.extensions = Arrays.asList(extensions);
		return this;
	}
	
	@Override
	public String toString() {
		return extensions.toString();
	}
	
	public void validate() {
		
		List<String> msgs = new ArrayList<String>();
		
		Validator validator = ValidatorFactory.validator();
		
		for (ValidationError error : validator.validate(this))
			msgs.add(error.toString());
		
		if (!msgs.isEmpty())
			throw new IllegalStateException("invalid configuration: "+msgs);
		
	}
	
	//since we use @AnyElement, after deserialisation, we check there are no DOM elements
    void afterUnmarshal(Unmarshaller u, Object parent) {
    	for (Object o : extensions)
    		if (o instanceof Element)
    			throw new RuntimeException("invalid extensions detected: "+Element.class.cast(o).getLocalName());
    }
	
}