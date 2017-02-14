package org.gcube.vremanagement.resourcemanager.client.fws;

import static org.gcube.vremanagement.resourcemanager.client.Constants.*;
import static org.gcube.vremanagement.resourcemanager.client.fws.Types.*;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;

import org.gcube.common.clients.stubs.jaxws.JAXWSUtils.Empty;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
@WebService(name=porttypeReportingLocalName,targetNamespace=NAMESPACE_REPORTING)
public interface RMReportingServiceJAXWSStubs {
	
	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public Empty SendReport(SendReportParameters params);

	@SOAPBinding(parameterStyle=ParameterStyle.BARE)
	public String  GetReport(String reportId);

	
}


