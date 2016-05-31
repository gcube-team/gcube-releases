package org.gcube.data.spd.stubs;

import static org.gcube.data.spd.client.Constants.*;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import org.gcube.data.spd.stubs.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.stubs.exceptions.UnsupportedCapabilityException;
import org.gcube.data.spd.stubs.exceptions.UnsupportedPluginException;

@WebService(name=classification_portType,targetNamespace=classification_target_namespace)
@SOAPBinding(parameterStyle=ParameterStyle.BARE)
public interface ClassificationStub {

	public String retrieveTaxonChildrenByTaxonId(String id) throws UnsupportedPluginException,UnsupportedCapabilityException, InvalidIdentifierException;
	
	public String retrieveChildrenTreeById(String id) throws UnsupportedPluginException,UnsupportedCapabilityException, InvalidIdentifierException; 
	
	public String retrieveSynonymsById(String id) throws UnsupportedPluginException,UnsupportedCapabilityException, InvalidIdentifierException; 
	
	public String getTaxaByIds(String ids); 
}
