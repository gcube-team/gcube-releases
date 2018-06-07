package org.gcube.portlets.user.td.metadatawidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRLocalizedText;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
 
public interface TRLocalizedTextProperties extends
		PropertyAccess<TRLocalizedText> {
	
	@Path("id")
	ModelKeyProvider<TRLocalizedText> id();

	ValueProvider<TRLocalizedText, String> value();

	ValueProvider<TRLocalizedText, String> localeCode();

	

}
