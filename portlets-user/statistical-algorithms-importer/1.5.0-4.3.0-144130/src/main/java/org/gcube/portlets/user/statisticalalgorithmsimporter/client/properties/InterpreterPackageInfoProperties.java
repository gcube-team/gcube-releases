package org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.InterpreterPackageInfo;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface InterpreterPackageInfoProperties extends
		PropertyAccess<InterpreterPackageInfo> {

	ModelKeyProvider<InterpreterPackageInfo> id();

	ValueProvider<InterpreterPackageInfo, String> name();

	ValueProvider<InterpreterPackageInfo, String> version();

}
