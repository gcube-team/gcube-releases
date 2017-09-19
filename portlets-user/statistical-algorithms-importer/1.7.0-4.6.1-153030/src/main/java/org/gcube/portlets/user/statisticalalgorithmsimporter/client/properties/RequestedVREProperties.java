package org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.RequestedVRE;

import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public interface RequestedVREProperties extends PropertyAccess<RequestedVRE> {

	ModelKeyProvider<RequestedVRE> id();

	ValueProvider<RequestedVRE, String> name();

	ValueProvider<RequestedVRE, String> description();

}
