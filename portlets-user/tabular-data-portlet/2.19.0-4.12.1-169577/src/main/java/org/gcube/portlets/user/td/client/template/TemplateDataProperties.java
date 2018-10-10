package org.gcube.portlets.user.td.client.template;



import java.util.Date;

import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateData;

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
public interface TemplateDataProperties  extends PropertyAccess<TemplateData> {
	
	@Path("id")
	ModelKeyProvider<TemplateData> id();
	
	ValueProvider<TemplateData, String> name();
	ValueProvider<TemplateData, String> category();
	ValueProvider<TemplateData, String> ownerLogin();
	ValueProvider<TemplateData, String> agency();
	ValueProvider<TemplateData, String> description();
	ValueProvider<TemplateData, Date> creationDate();
	
	
	
}

