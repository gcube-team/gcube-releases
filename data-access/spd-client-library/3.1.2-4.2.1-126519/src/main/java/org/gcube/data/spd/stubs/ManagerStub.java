package org.gcube.data.spd.stubs;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.data.spd.stubs.exceptions.QueryNotValidException;
import org.gcube.data.spd.stubs.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.stubs.exceptions.UnsupportedPluginException;
import org.gcube.data.spd.stubs.types.PluginDescriptions;

import static org.gcube.data.spd.client.Constants.*;

@WebService(name=manager_portType,targetNamespace=manager_target_namespace)
public interface ManagerStub {
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	String search(String query) throws QueryNotValidException, UnsupportedPluginException, UnsupportedCapabilityException;
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	PluginDescriptions getSupportedPlugins(Empty empty);
	
}
