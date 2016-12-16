package org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.IOType;
import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InputOutputVariables;
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
public interface InputOutputVariablesProperties extends
		PropertyAccess<InputOutputVariables> {

	ModelKeyProvider<InputOutputVariables> id();

	ValueProvider<InputOutputVariables, String> name();

	ValueProvider<InputOutputVariables, String> description();

	ValueProvider<InputOutputVariables, DataType> dataType();
	
	ValueProvider<InputOutputVariables, String> defaultValue();
	
	ValueProvider<InputOutputVariables, IOType> ioType();
	
	ValueProvider<InputOutputVariables, String> sourceSelection();
	
	
}
