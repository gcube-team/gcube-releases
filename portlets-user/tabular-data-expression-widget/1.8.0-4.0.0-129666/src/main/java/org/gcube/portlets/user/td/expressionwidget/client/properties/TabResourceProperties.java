package org.gcube.portlets.user.td.expressionwidget.client.properties;

import java.util.Date;

import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface TabResourceProperties extends PropertyAccess<TabResource> {
	
	@Path("id")
	ModelKeyProvider<TabResource> id();
	
	ValueProvider<TabResource, String> name();
	ValueProvider<TabResource, String> tabResourceType();
	ValueProvider<TabResource, String> agency();
	ValueProvider<TabResource, Date> date();
	ValueProvider<TabResource, String> tableTypeName();
	ValueProvider<TabResource, String> ownerLogin();

}