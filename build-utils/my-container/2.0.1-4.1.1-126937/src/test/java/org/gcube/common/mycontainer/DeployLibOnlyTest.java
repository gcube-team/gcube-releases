/**
 * 
 */
package org.gcube.common.mycontainer;

import static org.gcube.common.mycontainer.TestUtils.libOnly;

import org.junit.Test;

/**
 * @author Fabio Simeoni
 *
 */
public class DeployLibOnlyTest {


		@Test
	public void deployFile() {
		
		MyContainer container = new MyContainer(libOnly());
		
		container.start();
				
		container.stop();
		
		GarDeployer deployer = new GarDeployer(container);
		
		deployer.undeploy(libOnly().id());
		
	}
	
}
