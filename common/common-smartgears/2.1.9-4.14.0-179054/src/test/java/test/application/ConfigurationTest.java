package test.application;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.ByteArrayInputStream;

import org.gcube.smartgears.configuration.Mode;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.configuration.application.ApplicationConfigurationBinder;
import org.gcube.smartgears.configuration.application.ApplicationExtensions;
import org.gcube.smartgears.configuration.application.DefaultApplicationConfiguration;
import org.gcube.smartgears.configuration.application.Include;
import org.gcube.smartgears.extensions.ApplicationExtension;
import org.gcube.smartgears.persistence.DefaultPersistence;
import org.junit.Test;

public class ConfigurationTest {

	@Test
	public void configurationBinds() throws Exception {

		String xml = "<application mode='offline' context='ctx' isSecure='true'>" +
							"<name>name</name>" +
							"<group>class</group>" +
							"<version>version</version>" +
							"<description>desc</description>" +
							"<scope>start/scope</scope>"+
							"<scope>another/start/scope</scope>"+
							"<include>/pathBis</include>" +
							"<persistence location='target'/>" +
					"</application>";

		
		ApplicationConfigurationBinder binder = new ApplicationConfigurationBinder();
		
		ApplicationConfiguration bound = binder.bind(new ByteArrayInputStream(xml.getBytes()));
		
		
		System.out.println(bound);
		
		assertEquals(sampleConfiguration(),bound);
		
	}
	
	
	@Test
	public void extensionsBind() throws Exception {

		String xml = "<extensions>" +
							"<remote-management name='custom' mapping='custom' />" +
					"</extensions>";

		
		ApplicationConfigurationBinder binder = new ApplicationConfigurationBinder();
		
		ApplicationExtensions bound = binder.bindExtensions(new ByteArrayInputStream(xml.getBytes()));

		assertNotNull(bound.extensions());
		assertEquals(1,bound.extensions().size());
		
		ApplicationExtension ext = bound.extensions().get(0);
		assertEquals("custom",ext.name());
		assertEquals("custom",ext.mapping());
		
		
	}
	
	
	/*@Test
	public void configurationsMerge() throws Exception {
	
		ApplicationConfiguration original = sampleConfiguration();
		
		ApplicationConfiguration one = sampleConfiguration();
		
		ApplicationConfiguration two = new DefaultApplicationConfiguration();
		two.mode(Mode.online);
		two.persistence(new DefaultPersistence(new File(".").getAbsolutePath()));
		two.startScopes("yet/another/one");
		
		one.merge(two);
		
		assertEquals(one.mode(), two.mode());
		assertEquals(one.name(), original.name());
		assertEquals(one.persistence(), two.persistence());
		
		Set<String> merged = new HashSet<>(original.startScopes());
		
		merged.addAll(two.startScopes());
		
		assertEquals(merged,one.startScopes());
		
	}*/
	
	//helpers
	
	private ApplicationConfiguration sampleConfiguration() {
		
		
		return new DefaultApplicationConfiguration()
						.mode(Mode.offline)
						.context("ctx")
						.name("name")
						.serviceClass("class")
						.includes(new Include("/pathBis"))
						.version("version")
						.description("desc")
						.persistence(new DefaultPersistence("target"));

	}

	
}
