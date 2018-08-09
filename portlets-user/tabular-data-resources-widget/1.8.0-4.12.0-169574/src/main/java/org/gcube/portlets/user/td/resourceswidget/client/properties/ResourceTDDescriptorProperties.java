package org.gcube.portlets.user.td.resourceswidget.client.properties;

import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDDescriptor;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDType;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author Giancarlo Panichi 
 * 
 *
 */
public interface ResourceTDDescriptorProperties extends
		PropertyAccess<ResourceTDDescriptor> {
	
	@Path("id")
	ModelKeyProvider<ResourceTDDescriptor> id();
	
	ValueProvider<ResourceTDDescriptor,String> name();
	
	ValueProvider<ResourceTDDescriptor,String> creationDate();
 	
	ValueProvider<ResourceTDDescriptor,ResourceTDType> resourceType();

}
