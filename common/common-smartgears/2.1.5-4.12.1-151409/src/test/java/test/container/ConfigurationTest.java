package test.container;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

import org.gcube.smartgears.configuration.Mode;
import org.gcube.smartgears.configuration.application.ApplicationConfiguration;
import org.gcube.smartgears.configuration.application.DefaultApplicationConfiguration;
import org.gcube.smartgears.configuration.container.ContainerConfiguration;
import org.gcube.smartgears.configuration.container.ContainerConfigurationBinder;
import org.gcube.smartgears.configuration.container.Site;
import org.gcube.smartgears.persistence.DefaultPersistence;
import org.junit.Test;

public class ConfigurationTest {

	@Test
	public void containerConfigurationBinds() throws Exception {

		String appXml = "<application mode='offline'>" + "<name>name</name>" + "<group>class</group>"
				+ "<version>version</version>" + "<description>desc</description>" + "<persistence location='target'/>"
				+ "</application>";

		String xml = "<container mode='offline'>"
				+ "<hostname>localhost</hostname>"
				+ "<port>8080</port>"
				+ "<secure-port>8484</secure-port>"
				+ "<infrastructure>gcube</infrastructure>"
				+ "<authorizeChildrenContext>true</authorizeChildrenContext> "
				+"<token>token1</token>" + "<token>token2</token>" + "<persistence location='target'/>" + appXml + "<site>"
				+ "<country>it</country>" + "<location>rome</location>" + "<latitude>41.9000</latitude>"
				+ "<longitude>12.5000</longitude>" + "</site>" + "<property name='prop1' value='val1' />"
				+ "<property name='prop2' value='val2' />" + "<publication-frequency>30</publication-frequency>"
				+ "</container>";

		ContainerConfigurationBinder binder = new ContainerConfigurationBinder();

		ContainerConfiguration bound = binder.bind(new ByteArrayInputStream(xml.getBytes()));

		bound.validate();

		List<String> scopes = bound.startTokens();

		assertTrue(scopes.contains("token1"));
		assertTrue(scopes.contains("token2"));

		assertEquals(sampleContainerConfiguration(), bound);

	}

	private ContainerConfiguration sampleContainerConfiguration() {

		return new ContainerConfiguration().mode(Mode.offline).hostname("localhost").port(8080).securePort(8484).infrastructure("gcube")
				.startTokens(Arrays.asList("token1", "token2"))
				.site(new Site().country("it").location("rome").latitude("41.9000").longitude("12.5000"))
				.property("prop1", "val1").property("prop2", "val2").publicationFrequency(30)
				.app(sampleAppConfiguration()).authorizeChildrenContext(true)
				.persistence(new DefaultPersistence("target"));

	}

	private ApplicationConfiguration sampleAppConfiguration() {

		return new DefaultApplicationConfiguration().mode(Mode.offline).name("name").serviceClass("class")
				.version("version").description("desc").persistence(new DefaultPersistence("target"));

	}
}
