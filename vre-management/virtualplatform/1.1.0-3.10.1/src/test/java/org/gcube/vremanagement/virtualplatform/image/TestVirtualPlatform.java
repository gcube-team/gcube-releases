package org.gcube.vremanagement.virtualplatform.image;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.gcube.vremanagement.virtualplatform.image.PlatformConfiguration;
import org.gcube.vremanagement.virtualplatform.image.Platforms;
import org.gcube.vremanagement.virtualplatform.image.VirtualPlatform;
import org.gcube.vremanagement.virtualplatform.model.DeployedPackage;
import org.gcube.vremanagement.virtualplatform.model.PackageSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for {@link VirtualPlatform}
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class TestVirtualPlatform {

	VirtualPlatform platform;
	org.gcube.vremanagement.virtualplatform.model.Package app;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		List<PlatformConfiguration> platforms = Platforms.listAvailablePlatforms(new File(TestPlatforms.configFolder));
		platform = new VirtualPlatform(platforms.iterator().next());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}



	/**
	 * Test method for {@link org.gcube.vremanagement.virtualplatform.image.VirtualPlatform#getNewAppInstance()}.
	 */
//	@Test
	public void testGetNewAppInstance() {
		try {
			this.app = platform.getNewAppInstance();
			System.out.println("Got a new instance of " +this.app.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			fail("Fail to get a new app instance from the virtual platform");
		}
		
	}
	
	/**
	 * Test method for {@link org.gcube.vremanagement.virtualplatform.image.VirtualPlatform#getNewAppInstance()}.
	 */
//	@Test
	public void testIsAvailable() {
		try {
			platform.isAvailable();
		} catch (Exception e) {
			e.printStackTrace();
			fail("Fail to test availability of the virtual platform");
		}
		
	}

	/**
	 * Test method for {@link org.gcube.vremanagement.virtualplatform.image.VirtualPlatform#deploy(org.gcube.vremanagement.virtualplatform.model.PackageSet)}.
	 */
//	@Test
	public void testDeploy() {
		this.testGetNewAppInstance();
		app.setTargetPath("/whnmanager");
		app.setFile(new File("/home/rcirillo/whnmanager.war"));
		app.setName("whnmanager");
		PackageSet<DeployedPackage> deployed;
		PackageSet<org.gcube.vremanagement.virtualplatform.model.Package> packages = new PackageSet<org.gcube.vremanagement.virtualplatform.model.Package>();
		packages.add(this.app);
		try {
			deployed = this.platform.deploy(packages);
			System.out.println("Applications deployed");
			for (DeployedPackage p : deployed) {
				System.out.println("Application endpoints " + Arrays.toString(p.getEndpoints()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to test the deployment");
		}
	}


	/**
	 * Test method for {@link org.gcube.vremanagement.virtualplatform.image.VirtualPlatform#deactivate(org.gcube.vremanagement.virtualplatform.model.PackageSet)}.
	 */
//	@Test
	public void testActivate() {
		this.testGetNewAppInstance();
		app.setTargetPath("/whnmanager");
		app.setName("whnmanager");
		PackageSet<org.gcube.vremanagement.virtualplatform.model.Package> packages = new PackageSet<org.gcube.vremanagement.virtualplatform.model.Package>();
		packages.add(this.app);
		try {
			this.platform.activate(packages);
			System.out.println("Application activated");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to test activation");
		}
	}
	/**
	 * Test method for {@link org.gcube.vremanagement.virtualplatform.image.VirtualPlatform#deactivate(org.gcube.vremanagement.virtualplatform.model.PackageSet)}.
	 */
//	@Test
	public void testDeactivate() {
		this.testGetNewAppInstance();
		app.setTargetPath("/whnmanager");
		app.setName("whnmanager");
		PackageSet<org.gcube.vremanagement.virtualplatform.model.Package> packages = new PackageSet<org.gcube.vremanagement.virtualplatform.model.Package>();
		packages.add(this.app);
		try {
			this.platform.deactivate(packages);
			System.out.println("Application deactivated");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to test deactivation");
		}
	}
	
	/**
	 * Test method for {@link org.gcube.vremanagement.virtualplatform.image.VirtualPlatform#undeploy(org.gcube.vremanagement.virtualplatform.model.PackageSet)}.
	 */
//	@Test
	public void testUndeploy() {
		this.testGetNewAppInstance();
		app.setTargetPath("/whnmanager");
		app.setName("whnmanager");
		PackageSet<org.gcube.vremanagement.virtualplatform.model.Package> packages = new PackageSet<org.gcube.vremanagement.virtualplatform.model.Package>();
		packages.add(this.app);
		try {
			this.platform.undeploy(packages);
			System.out.println("Application undeployed");
		} catch (Exception e) {
			e.printStackTrace();
			fail("Failed to test the undeployment");
		}
	}

}
