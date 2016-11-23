/**
 * 
 */
package org.gcube.common.mycontainer;

import org.junit.Test;

/**
 * @author Fabio Simeoni
 *
 */
public class LifetimeTest {


	@Test
	public void startAndStop() {
		
		MyContainer container = new MyContainer();
		
		container.start();
		
		assert container.isRunning();
		
		container.stop();
		
		assert !container.isRunning();
	}

}
