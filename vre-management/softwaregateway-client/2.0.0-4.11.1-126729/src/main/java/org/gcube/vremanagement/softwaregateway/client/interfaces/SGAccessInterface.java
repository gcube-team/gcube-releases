package org.gcube.vremanagement.softwaregateway.client.interfaces;

import java.util.List;

import org.gcube.vremanagement.softwaregateway.client.exceptions.ServiceNotAvaiableException;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.DependenciesCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.LocationItem;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.PackageCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.PluginCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.SACoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.ServiceCoordinates;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.getPackagesResponse;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.getPluginResponse;

/**
 * 
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public interface SGAccessInterface {
	
	
	public String getLocation(PackageCoordinates packageCoordinates) throws ServiceNotAvaiableException;
	
	public String getSALocation(SACoordinates saCoordinates) throws ServiceNotAvaiableException;

	public String getDependencies(DependenciesCoordinates packageCoordinates) throws ServiceNotAvaiableException;
	
	public getPackagesResponse getPackages(ServiceCoordinates serviceCoordinates) throws ServiceNotAvaiableException;
	
// there are 2 plugin response defined in the wsdl	
	public getPluginResponse getPlugins(PluginCoordinates serviceCoordinates) throws ServiceNotAvaiableException;
	

}
