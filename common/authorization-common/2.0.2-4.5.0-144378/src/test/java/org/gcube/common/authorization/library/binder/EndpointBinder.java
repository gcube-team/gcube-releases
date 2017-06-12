package org.gcube.common.authorization.library.binder;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gcube.common.authorization.library.enpoints.AuthorizationEndpoint;
import org.gcube.common.authorization.library.enpoints.AuthorizationEndpointScanner;
import org.junit.Assert;
import org.junit.Test;

public class EndpointBinder {

	
	
	public static JAXBContext getContext() throws JAXBException{
		return JAXBContext.newInstance(AuthorizationEndpoint.class);
	}
	
	@Test
	public void bind() throws Exception{
		JAXBContext context = getContext();
		StringWriter sw = new StringWriter();
		AuthorizationEndpoint ae1 = new AuthorizationEndpoint("myInfra", 2, "146.48.85.179", 8080);
		ae1.setSecureConnection(true);
		context.createMarshaller().marshal(ae1, sw);
		System.out.println(sw);
		AuthorizationEndpoint ae2 = (AuthorizationEndpoint)context.createUnmarshaller().unmarshal(new StringReader(sw.toString()));
		Assert.assertEquals(ae1, ae2);
	}
	
	@Test
	public void compare(){
		AuthorizationEndpoint ae1 = new AuthorizationEndpoint("myInfra", 2, "146.48.85.179", 8080);
		AuthorizationEndpoint ae2 = new AuthorizationEndpoint("myInfra", 1, "146.48.85.179", 8080);
		
		Assert.assertTrue(ae1.compareTo(ae2)>0);
	}
	
	@Test
	public void order(){
		AuthorizationEndpoint ae1 = new AuthorizationEndpoint("myInfra" ,2, "146.48.85.179", 8080);
		AuthorizationEndpoint ae2 = new AuthorizationEndpoint("myInfra" ,1, "146.48.85.179", 8080);
		AuthorizationEndpoint ae3 = new AuthorizationEndpoint("myInfra", 3, "146.48.85.179", 8080);
		
		AuthorizationEndpoint[] arr1 = new AuthorizationEndpoint[]{ae1, ae2, ae3};
		
		Arrays.sort(arr1);
		
		AuthorizationEndpoint[] arr2 = new AuthorizationEndpoint[]{ae2, ae1, ae3};
		
		Assert.assertArrayEquals(arr1,  arr2);
		
	}
	
	@Test
	public void scan(){
		Map<Integer, AuthorizationEndpoint> endpoints = AuthorizationEndpointScanner.endpoints().getEndpoints();
		System.out.println(endpoints);
	}
	
}
