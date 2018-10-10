package org.gcube.vremanagement.vremodel.cl.proxy;

import java.util.List;
import javax.xml.ws.wsaddressing.W3CEndpointReference;
import org.gcube.vremanagement.vremodel.cl.stubs.types.Report;


public interface Factory {

	W3CEndpointReference createResource();
	
	List<Report> getAllVREs();
	
	List<String> getExistingNamesVREs();
	
	void removeVRE(String id);
	
	void initDB();
	
	W3CEndpointReference getEPRbyId(String id);
	
}
