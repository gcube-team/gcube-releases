package org.gcube.common.authorization.library.binder;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.QualifiersList;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.junit.Assert;
import org.junit.Test;

public class AuthorizationEntryBinder {
	
	public static JAXBContext getContext() throws JAXBException{
		return JAXBContext.newInstance(QualifiersList.class, AuthorizationEntry.class);
	}
	
	@Test
	public void bind() throws Exception{
		JAXBContext context = getContext();
		StringWriter sw = new StringWriter();
		AuthorizationEntry ae1 = new AuthorizationEntry(new UserInfo("lucio.lelii", new ArrayList<String>()), "scope", new ArrayList<Policy>(), "TOKEN");
		Map<String, String> services = new HashMap<String, String>();
		services.put("service", "endpoint");
		context.createMarshaller().marshal(ae1, sw);
		System.out.println(sw.toString());
		AuthorizationEntry ae2 = (AuthorizationEntry)context.createUnmarshaller().unmarshal(new StringReader(sw.toString()));
		System.out.println(ae2.toString());
		Assert.assertEquals(ae1, ae2);
		
		QualifiersList entries = new QualifiersList(Collections.singletonMap("qualifier", "token"));
		System.out.println(entries);
		sw = new StringWriter();
		context.createMarshaller().marshal(entries, sw);
		System.out.println(sw);
		
		//QualifiersList entries2 = (QualifiersList)context.createUnmarshaller().unmarshal(new StringReader(sw.toString()));
		
	}
}
