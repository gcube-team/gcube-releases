package org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.code.CodeData;

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
public interface CodeDataProperties extends PropertyAccess<CodeData> {
		
		@Path("id")
		ModelKeyProvider<CodeData> code();
		
		ValueProvider<CodeData, Integer> id();
		ValueProvider<CodeData, String> codeLine();
		
	}
