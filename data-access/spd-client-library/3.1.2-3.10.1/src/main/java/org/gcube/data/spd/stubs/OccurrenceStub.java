package org.gcube.data.spd.stubs;

import static org.gcube.data.spd.client.Constants.*;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;


@WebService(name=occurrence_portType,targetNamespace=occurrence_target_namespace)
public interface OccurrenceStub {

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String getByIds(String idsLocator);
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String getByKeys(String keysLocator);
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String createLayer(String keysLocator);
	
}
