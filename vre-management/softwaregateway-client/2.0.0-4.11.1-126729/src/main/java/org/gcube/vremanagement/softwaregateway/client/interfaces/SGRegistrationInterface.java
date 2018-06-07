package org.gcube.vremanagement.softwaregateway.client.interfaces;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.vremanagement.softwaregateway.client.exceptions.ServiceNotAvaiableException;
import org.gcube.vremanagement.softwaregateway.client.fws.Types.LocationCoordinates;


/**
 * 
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public interface SGRegistrationInterface {
	
	public String register(String registerRequest) throws ServiceNotAvaiableException;

	public Empty unregister(LocationCoordinates coordinates) throws ServiceNotAvaiableException;
}
