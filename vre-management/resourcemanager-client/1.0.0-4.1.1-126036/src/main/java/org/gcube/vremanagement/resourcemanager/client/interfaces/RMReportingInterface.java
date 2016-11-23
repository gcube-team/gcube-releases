package org.gcube.vremanagement.resourcemanager.client.interfaces;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;
import org.gcube.vremanagement.resourcemanager.client.exceptions.InvalidScopeException;
import org.gcube.vremanagement.resourcemanager.client.exceptions.NoSuchReportException;
import org.gcube.vremanagement.resourcemanager.client.fws.Types.SendReportParameters;


/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public interface RMReportingInterface {
	
	public Empty sendReport(SendReportParameters params) throws  InvalidScopeException;

	public String getReport(String reportId) throws  InvalidScopeException, NoSuchReportException;
}
