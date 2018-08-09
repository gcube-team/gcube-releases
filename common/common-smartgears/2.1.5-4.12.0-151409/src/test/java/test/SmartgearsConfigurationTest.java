package test;

import static junit.framework.Assert.*;

import java.io.ByteArrayInputStream;

import org.gcube.smartgears.configuration.library.SmartGearsConfiguration;
import org.gcube.smartgears.configuration.library.SmartGearsConfigurationBinder;
import org.junit.Test;

public class SmartgearsConfigurationTest {

	@Test
	public void configurationBinds() throws Exception {

		String xml = "<smartgears version='1.0.0-SNAPSHOT'/>";
		
		SmartGearsConfigurationBinder binder = new SmartGearsConfigurationBinder();
		
		SmartGearsConfiguration bound = binder.bind(new ByteArrayInputStream(xml.getBytes()));
		
		bound.validate();
		
		String version = bound.version();
		assertEquals("1.0.0-SNAPSHOT",version);

		assertEquals(sampleSmartgearsConfiguration(),bound);
		
		
	}
	
	private SmartGearsConfiguration sampleSmartgearsConfiguration() {

		return new SmartGearsConfiguration().version("1.0.0-SNAPSHOT");

	}
}
