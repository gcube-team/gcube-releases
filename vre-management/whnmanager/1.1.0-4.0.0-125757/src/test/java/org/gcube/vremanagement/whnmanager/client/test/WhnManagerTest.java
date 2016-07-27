package org.gcube.vremanagement.whnmanager.client.test;

import static org.junit.Assert.*;

import java.net.URL;
import javax.xml.namespace.QName;

import org.gcube.common.calls.jaxws.GcubeService;
import org.gcube.common.scope.api.ScopeProvider;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.MalformedURLException;
import javax.xml.ws.Service;

import org.gcube.resourcemanagement.whnmanager.api.WhnManager;
import org.gcube.resourcemanagement.whnmanager.api.types.AddScopeInputParams;


public class WhnManagerTest {

	private Logger logger = LoggerFactory.getLogger(WhnManagerTest.class);
	private final static String address="http://localhost:8080/whn-manager/gcube/vremanagement/ws/whnmanager";
	private URL url;
	private WhnManager whn;
	
	@Before
	public void setup() throws MalformedURLException{
		ScopeProvider.instance.set("/gcube/devsec");
		url = new URL(address);
		QName qname = new QName(WhnManager.TNS, WhnManager.SERVICE_NAME);
		GcubeService<WhnManager> serviceManager = GcubeService.service().withName(qname).andInterface(WhnManager.class);
	    whn=org.gcube.common.calls.jaxws.StubFactory.stubFor(serviceManager).at(address);
		
	}
//	@Test
	public void addScopeTest() throws Exception{
        AddScopeInputParams params = new AddScopeInputParams("/gcube/devsec", "");
        assertTrue(whn.addScope(params));

	}

//	@Test
	public void removeScopeTest() throws Exception{
        assertTrue(whn.removeScope("/gcube/devsec"));
	}

	
}
