package org.gcube.vremanagement.softwaregateway.client.fws;


import java.util.List;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import static org.gcube.vremanagement.softwaregateway.client.Constants.*;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.DependenciesCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.PackageCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.PluginCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.SACoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.ServiceCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.getPackagesResponse;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.getPluginResponse;


	/**
	 * 
	 * @author Roberto Cirillo (ISTI -CNR)
	 *
	 */
@WebService(name=porttypeAccessLocalName,targetNamespace=NAMESPACE_ACCESS)
public interface SGAccessServiceJAXWSStubs {
			
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String getLocation(PackageCoordinates packageCoordinates);
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String getSALocation(SACoordinates saCoordinates);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String getDependencies(DependenciesCoordinates packageCoordinates);
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public getPackagesResponse getPackages(ServiceCoordinates serviceCoordinates);
	
// there are 2 plugin response defined in the wsdl
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public getPluginResponse getPlugins(PluginCoordinates serviceCoordinates);
	
}
