/**
 * 
 */
package org.gcube.common.mycontainer;

import static org.gcube.common.mycontainer.TestUtils.existingGar;
import static org.gcube.common.mycontainer.TestUtils.libOnly;

import org.junit.Test;

/**
 * @author Fabio Simeoni
 *
 */
public class DeployGarFileTest {


		@Test
	public void deployFile() {
		
		MyContainer container = new MyContainer(libOnly(),existingGar());
		
		container.start();
				
		container.stop();
		
		GarDeployer deployer = new GarDeployer(container);
		
		deployer.undeploy(libOnly().id());
		deployer.undeploy(existingGar().id());
		
	}
	
}
