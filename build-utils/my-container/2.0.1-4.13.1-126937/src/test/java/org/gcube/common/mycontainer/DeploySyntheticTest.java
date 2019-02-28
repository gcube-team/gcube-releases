/**
 * 
 */
package org.gcube.common.mycontainer;

import static org.gcube.common.mycontainer.TestUtils.syntheticGar;

import org.junit.Test;

/**
 * @author Fabio Simeoni
 *
 */
public class DeploySyntheticTest {


	@Test
	public void deploySynthetic() {
		
		MyContainer container = new MyContainer(syntheticGar());
		
		container.start();
				
		container.stop();
		
		new GarDeployer(container).undeploy(syntheticGar().id());
		
	}
}
