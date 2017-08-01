/**
 * 
 */
package org.gcube.common.mycontainer;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

/**
 * @author Fabio Simeoni
 *
 */
public class ConfigurationTest {


	@Test
	public void findAndConfigure() {
		
		MyContainer container = new MyContainer();
		
		assertNotNull(container.location());
		assertEquals(Utils.DEFAULT_PORT,container.port);
		assertNotNull(container.storageLocation());
	}
	
	@Test
	public void locateAndConfigure() {
		
		MyContainer container = new MyContainer("test-installation");
		assertEquals(Utils.DEFAULT_PORT,container.port);
		assertNotNull(container.location());
	}
	
	@Test
	public void mergeConfigure() {
		
		Properties props = new Properties();
		props.put(Utils.PORT_PROPERTY, "8888");
		props.put(Utils.STARTUP_TIMEOUT_PROPERTY,"30000");
		
		MyContainer container = new MyContainer(props,true);
		
		assertEquals(new File("test-installation").getAbsoluteFile(),container.location());
		assertEquals(8888,container.port);
		assertEquals(30000,container.startupTimeout);
	}
	
}
