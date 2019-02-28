package org.gcube.smartgears.utils.sweeper.test;

import org.gcube.smartgears.utils.sweeper.Sweeper;
import org.junit.Before;

public class SweeperTest {
	
	Sweeper sw = null;
	
	@Before
	public void setUp(){
		try {
			sw = new Sweeper();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
}
