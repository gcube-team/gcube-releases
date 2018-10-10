package org.gcube.vremanagement.virtualplatform.image;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import org.gcube.vremanagement.virtualplatform.image.PlatformLoader;
import org.gcube.vremanagement.virtualplatform.model.Package;
import org.gcube.vremanagement.virtualplatform.model.TargetPlatform;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link TargetPlatform}
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class TestTargetPlatform {

	TargetPlatform<?> container;
	Package app;
	
	@Before
	public void setUp() throws Exception {
		PlatformLoader cl = new PlatformLoader(new File[]{
				new File("/usr/share/java/ant.jar"),
				new File("/home/rcirillo/apache-tomcat-6.0.36/lib/catalina-ant.jar")/*,
				new File("/Users/manuele/work/workspace/VREManagement/Branches/WARDeployer.Private/lib/org.gcube.vremanagement.deployer.client.tomcat.jar")*/});
		this.container =  (TargetPlatform<?>) cl.getInstanceOf("org.gcube.vremanagement.deployer.client.tomcat.Container");
		this.app = (Package) cl.getInstanceOf(container.getResourceClass().getName());
		this.app.setTargetPath("/fisheryresources-web-0.0.5");
		this.app.setFile(new File("/Users/manuele/work/ext/Tomcat-6.0.32/fisheryresources-web-0.0.5.war"));
		
	}

	@After
	public void tearDown() throws Exception {
	}

//	@Test
	public void testGetPlatform() {
		System.out.println("Platform: " + container.getPlatform());
	}

//	@Test
	public void testGetPlatformVersion() {
		System.out.println("Platform version: " + container.getPlatformVersion());
	}

//	@Test
	public void testGetPlatformMinorVersion() {
		System.out.println("Platform minor version: " + container.getPlatformMinorVersion());
	}

//	@Test
	public void testGetResourceClass() {
		System.out.println("Managed resource: " + container.getResourceClass().getName());
	}

//	@Test
	public void testDeploy() {
		System.out.println("Loader for TargetPlatform is: " +container.getClass().getClassLoader());
 		try {
 			this.testSetBaseURL();
 			this.testSetPassword();
 			this.testSetUser();
 			Method m = container.getClass().getMethod("deploy", container.getResourceClass());
 			m.invoke(this.container, app);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to deploy");
		}
	}

//	@Test
	public void testUndeploy() {
		try {
 			this.testSetBaseURL();
 			this.testSetPassword();
 			this.testSetUser();
 			Method m = container.getClass().getMethod("undeploy", container.getResourceClass());
 			m.invoke(this.container, app);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to undeploy");
		}
	}

//	@Test
	public void testSetBaseURL() {
		try {
			container.setBaseURL(new URL("http://localhost:8080"));
		} catch (MalformedURLException e) {
			fail("Failed to set the URL");
		}
	}

//	@Test
	public void testSetPassword() {
		container.setPassword("manuele");
	}

//	@Test
	public void testSetUser() {
		container.setUser("manuele");
	}

}
