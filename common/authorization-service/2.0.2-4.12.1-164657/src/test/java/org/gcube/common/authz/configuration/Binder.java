package org.gcube.common.authz.configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;

import org.gcube.common.authorization.library.ClientType;
import org.gcube.common.authorizationservice.configuration.AllowedEntity;
import org.gcube.common.authorizationservice.configuration.AllowedEntity.EntityType;
import org.gcube.common.authorizationservice.configuration.AuthorizationConfiguration;
import org.gcube.common.authorizationservice.configuration.AuthorizationRule;
import org.gcube.common.authorizationservice.configuration.ConfigurationBuilder;
import org.gcube.common.authorizationservice.configuration.RuleBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class Binder {

	private static JAXBContext context;

	@BeforeClass
	public static void init() throws Exception{
		context = JAXBContext.newInstance(AuthorizationConfiguration.class);
	}

	@Test
	public void binder() throws Exception{
		AuthorizationRule firtRule = new RuleBuilder().path("/*").entity(new AllowedEntity(EntityType.IP, "192.168.0.1")).entity(new AllowedEntity(EntityType.IP, "192.168.0.2")).needsToken(ClientType.USER, ClientType.CONTAINER).build();
		AuthorizationRule secondRule = new RuleBuilder().path("/newPath").entity(new AllowedEntity(EntityType.ROLE, "ContextManager")).entity(new AllowedEntity(EntityType.IP, "192.168.0.3")).build();
		AuthorizationRule thirdRule = new RuleBuilder().path("/anotherPath").entity(new AllowedEntity(EntityType.USER, "user")).build();

		AuthorizationConfiguration authConf = ConfigurationBuilder.getBuilder().rule(firtRule).rule(secondRule).rule(thirdRule).build();		
		StringWriter sw = new StringWriter();
		context.createMarshaller().marshal(authConf, sw);
		System.out.println(sw);
		AuthorizationConfiguration extractedRule = (AuthorizationConfiguration)context.createUnmarshaller().unmarshal(new StringReader(sw.toString()));
		System.out.println(extractedRule);

		Assert.assertTrue(authConf.equals(extractedRule));
	}

	@Test
	public void loadBinder() throws Exception{

		try( BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("AuthorizationConfiguration.xml")))){
			String line = null;
			while ((line=reader.readLine())!=null)
				System.out.println(line);
		}


		AuthorizationConfiguration authConf = (AuthorizationConfiguration)context.createUnmarshaller().unmarshal(
				new BufferedReader(new InputStreamReader(ClassLoader.getSystemClassLoader().getResourceAsStream("AuthorizationConfiguration.xml"))));
		System.out.println(authConf);

		StringWriter sw = new StringWriter();
		context.createMarshaller().marshal(authConf, sw);
		System.out.println(sw);
		AuthorizationConfiguration extractedRule = (AuthorizationConfiguration)context.createUnmarshaller().unmarshal(new StringReader(sw.toString()));
		Assert.assertTrue(authConf.equals(extractedRule));

	}
	
	

}
