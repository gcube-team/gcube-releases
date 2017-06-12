package org.gcube.portlets.user.td.sdmxexportwidget.client.properties;



import java.util.Date;

import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateData;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
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

