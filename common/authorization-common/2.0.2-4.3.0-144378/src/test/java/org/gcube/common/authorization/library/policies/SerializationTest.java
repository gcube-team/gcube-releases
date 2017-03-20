package org.gcube.common.authorization.library.policies;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class SerializationTest {

	static JAXBContext context;
	
	@BeforeClass
	public static void before() throws Exception{
		 context = JAXBContext.newInstance(User2ServicePolicy.class, Service2ServicePolicy.class);
	}
	
	@Test
	public void serializeUserPolicy() throws Exception{
		User2ServicePolicy up = new User2ServicePolicy("/gcube", new ServiceAccess("ServiceName", "ServiceClass","serviceID"), Roles.allExcept("VREManager", "VOManager"));
		StringWriter sw = new StringWriter();
		context.createMarshaller().marshal(up, sw);
		User2ServicePolicy upCopy = (User2ServicePolicy)context.createUnmarshaller().unmarshal(new StringReader(sw.toString()));
		System.out.println(upCopy.getPolicyAsString());
		Assert.assertEquals(up, upCopy);
	}
	
	@Test
	public void serializeServicePolicy() throws Exception{
		Service2ServicePolicy sp = new Service2ServicePolicy("/gcube", new ServiceAccess("ServiceName","ServiceClass"),
				Services.allExcept(new ServiceAccess("ServiceName2", "ServiceClass2"),new ServiceAccess("ServiceClass2")));
		StringWriter sw = new StringWriter();
		context.createMarshaller().marshal(sp, sw);
		Service2ServicePolicy spCopy = (Service2ServicePolicy)context.createUnmarshaller().unmarshal(new StringReader(sw.toString()));
		System.out.println(spCopy.getPolicyAsString());
		Assert.assertEquals(sp, spCopy);
	}
}
