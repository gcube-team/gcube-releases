package org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InterpreterPackageInfo;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface InterpreterPackageInfoProperties extends
		PropertyAccess<InterpreterPackageInfo> {

	ModelKeyProvider<InterpreterPackageInfo> id();

	ValueProvider<InterpreterPackageInfo, String> name();

	ValueProvider<InterpreterPackageInfo, String> version();

	ValueProvider<InterpreterPackageInfo, String> details();

}
