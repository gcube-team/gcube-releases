package org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.GlobalVariables;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.DataType;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface GlobalVariablesProperties extends
		PropertyAccess<GlobalVariables> {

	ModelKeyProvider<GlobalVariables> id();

	ValueProvider<GlobalVariables, String> name();

	ValueProvider<GlobalVariables, String> description();

	ValueProvider<GlobalVariables, DataType> dataType();
	
	ValueProvider<GlobalVariables, String> defaultValue();
}
