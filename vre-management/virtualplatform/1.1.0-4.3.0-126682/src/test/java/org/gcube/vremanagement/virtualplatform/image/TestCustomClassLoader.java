package org.gcube.vremanagement.virtualplatform.image;

import static org.junit.Assert.*;

import java.io.File;
import java.net.MalformedURLException;


import org.gcube.vremanagement.virtualplatform.image.PlatformLoader;
import org.gcube.vremanagement.virtualplatform.model.TargetPlatform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestCustomClassLoader {

	PlatformLoader cl;
	
	public static PlatformLoader getLoader() throws MalformedURLException {
//		PlatformLoader cl = new PlatformLoader(new File[]{ new File("/usr/share/java/ant-1.8.2/lib/ant.jar"),
//				new File("/Users/manuele/work/ext/Tomcat-6.0.32/apache-tomcat-6.0.32-deployer/lib/catalina-ant.jar")});
		PlatformLoader cl = new PlatformLoader(new File[]{ new File("/usr/share/java/ant.jar"),
		new File("/home/rcirillo/apache-tomcat-6.0.36/lib/catalina-ant.jar")});
	
		return cl;
	}
	@Before
	public void setUp() throws Exception {
		cl = getLoader();
	}

	@After
	public void tearDown() throws Exception {
		cl = null;
	}

//	@Test
	public void testGetInstanceOf() {
		try {
			TargetPlatform<?> container =  (TargetPlatform<?>) cl.getInstanceOf("org.gcube.vremanagement.virtualplatform.tomcat.Container");
			Object app = cl.getInstanceOf(container.getResourceClass().getName());
			System.out.println("Platform: " + container.getPlatform());
			System.out.println("Managed resource: " + container.getResourceClass().getName());
			System.out.println("Resource: " + app.getClass().getName());

		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to invoke a method on container class");
		}
	}

//	@Test
	public void testLoad() {
		try {
			cl.load("org.gcube.vremanagement.deployer.client.tomcat.Container");
			cl.load("org.gcube.vremanagement.deployer.client.tomcat.Webapp");
		} catch (ClassNotFoundException e) {}
	}

}
