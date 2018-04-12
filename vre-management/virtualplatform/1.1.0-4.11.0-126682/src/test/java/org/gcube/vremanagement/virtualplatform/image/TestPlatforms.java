/**
 * 
 */
package org.gcube.vremanagement.virtualplatform.image;


import java.io.File;
import java.util.List;

import org.gcube.vremanagement.virtualplatform.image.PlatformConfiguration;
import org.gcube.vremanagement.virtualplatform.image.Platforms;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author manuele
 *
 */
public class TestPlatforms {
	
	protected static String configFolder = "/Users/manuele/work/workspace/VREManagement/TomcatClientPlatform/TomcatClientPlatform.TRUNK/lib";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAvailablePlatforms() {
		List<PlatformConfiguration> platforms = Platforms.listAvailablePlatforms(new File(configFolder));
		for (PlatformConfiguration platform : platforms) {
			System.out.println("Found platform " + platform.getName());
			System.out.println("Found platform class " + platform.getPlatformClass());
			System.out.println("Classloader?  " + platform.requireDedicatedClassloader());
			System.out.println("Manager  " + platform.getBaseURL().toString());

		}
	}
	
	
}
