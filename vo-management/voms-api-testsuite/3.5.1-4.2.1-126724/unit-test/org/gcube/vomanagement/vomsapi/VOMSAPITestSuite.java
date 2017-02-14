package org.gcube.vomanagement.vomsapi;

import org.gcube.vomanagement.vomsapi.impl.ExtendedVOMSAdminImplTest;
import org.gcube.vomanagement.vomsapi.impl.VOMSACLSynchronizationTest;
import org.gcube.vomanagement.vomsapi.impl.VOMSAdminSynchronizationTest;
import org.gcube.vomanagement.vomsapi.impl.VOMSAttributesSynchronizationTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test suite for unit tests of the VOMS-API library
 * 
 * @author roccetti
 *
 */
@RunWith(Suite.class)
@SuiteClasses( { ExtendedVOMSAdminImplTest.class })
public class VOMSAPITestSuite {
	
	//TODO: reinclude synchronization tests once fixed
	//VOMSAttributesSynchronizationTest.class,
	//VOMSAdminSynchronizationTest.class, 
	//VOMSACLSynchronizationTest.class,
}
