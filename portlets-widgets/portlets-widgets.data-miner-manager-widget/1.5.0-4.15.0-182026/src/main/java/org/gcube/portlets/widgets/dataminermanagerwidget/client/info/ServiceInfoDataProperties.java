package org.gcube.portlets.widgets.dataminermanagerwidget.client.info;

import org.gcube.data.analysis.dataminermanagercl.shared.service.ServiceInfoData;

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
public interface ServiceInfoDataProperties extends PropertyAccess<ServiceInfoData> {
	@Path("key")
	ModelKeyProvider<ServiceInfoData> id();

	ValueProvider<ServiceInfoData, String> key();
	ValueProvider<ServiceInfoData, String> value();
	ValueProvider<ServiceInfoData, String> category();
}

