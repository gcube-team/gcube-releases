package org.gcube.data.publishing.gCatFeeder.tests;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.data.publishing.gCatFeeder.model.EnvironmentConfiguration;
import org.gcube.data.publishing.gCatFeeder.utils.ISUtils;
import org.junit.BeforeClass;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class InfrastructureTests {

	private static String testContext=null; 

	static {
		testContext=System.getProperty("testContext");		
		System.out.println("TEST CONTEXT = "+testContext);
	}

	protected static boolean isTestInfrastructureEnabled() {
		return testContext!=null;
	}

	@BeforeClass
	public static void setTestContext() {

		if(isTestInfrastructureEnabled()) {
			Properties props=new Properties();
			try{
				props.load(BaseCollectorTest.class.getResourceAsStream("/tokens.properties"));
			}catch(IOException e) {throw new RuntimeException(e);}
			if(!props.containsKey(testContext)) throw new RuntimeException("No token found for scope : "+testContext);
			SecurityTokenProvider.instance.set(props.getProperty(testContext));
			ScopeProvider.instance.set(testContext);
		}
	}

	
	private static EnvironmentConfiguration env=new EnvironmentConfiguration() {
		
		@Override
		public Map<String, String> getCurrentConfiguration() {
			if(isTestInfrastructureEnabled()) {
				return ISUtils.loadConfiguration();
			}else return Collections.emptyMap();
		}
	};
	
	protected static EnvironmentConfiguration getEnvironmentConfiguration() {
		return env;
	}
}
