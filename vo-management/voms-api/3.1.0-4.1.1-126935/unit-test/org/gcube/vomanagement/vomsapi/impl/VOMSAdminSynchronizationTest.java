package org.gcube.vomanagement.vomsapi.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.gcube.vomanagement.vomsapi.VOMSAPITest;
import org.glite.wsdl.services.org_glite_security_voms_service_admin.VOMSAdmin;
import org.junit.Test;

/**
 * This test class verifies that methods of
 * {@link org.gcube.vomanagement.vomsapi.VOMSAdmin} sub-classes properly enforce
 * synchronization of VOMS calls. See the {@link VOMSAPIFactory} code for a
 * description of the synchronization mechanism.
 * 
 * @author Paolo Roccetti
 */
public class VOMSAdminSynchronizationTest extends VOMSAPITest {

	private static Logger logger = Logger
			.getLogger(VOMSAdminSynchronizationTest.class.getName());

	private VOMSAPIFactory factoryMock;

	private VOMSAdmin vomsAdminMock;

	private VOMSAdminImpl vomsAdminImpl;

	public void setUp() throws Exception {

		// create mock factory
		this.factoryMock = createStrictMock(VOMSAPIFactory.class);

		// create mock VOMSAdmin
		this.vomsAdminMock = createMock(VOMSAdmin.class);

		// create VOMSAdminImpl
		this.vomsAdminImpl = new VOMSAdminImpl(this.vomsAdminMock,
				this.factoryMock);

		// configure factory with expected call
		expect(this.factoryMock.getLock()).andReturn(new Object());
		this.factoryMock.prepareForCall(this.vomsAdminImpl);
		this.factoryMock.exitFromCall();

		// replay factoryMock
		replay(this.factoryMock);

	}

	public void tearDown() throws Exception {

		// verify factory and VOMSAdmin
		verify(this.factoryMock, this.vomsAdminMock);

		// clean objects
		this.factoryMock = null;
		this.vomsAdminMock = null;
		this.vomsAdminImpl = null;

	}

	// test the synchronization of a method using reflection
	private void testMethod(Method method) throws Exception {

		// log the method under test
		logMethod(method, logger);

		// get arguments for the call
		Object[] args = getArguments(method);

		// set expectation on vomsAdminMock
		if (method.getReturnType().equals(void.class)) {
			// set for methods returning void
			this.vomsAdminMock.getClass().getMethod(method.getName(),
				method.getParameterTypes()).invoke(this.vomsAdminMock, args);
		} else if (method.getReturnType().equals(int.class)) {
			// set for primitive int type
			expect(
				this.vomsAdminMock.getClass().getMethod(method.getName(),
					method.getParameterTypes())
						.invoke(this.vomsAdminMock, args)).andReturn(0);
		} else {
			// set for Objects
			expect(
				this.vomsAdminMock.getClass().getMethod(method.getName(),
					method.getParameterTypes())
						.invoke(this.vomsAdminMock, args)).andReturn(null);
		}

		// replay vomsAdminMock
		replay(this.vomsAdminMock);

		// invoke the method
		this.vomsAdminImpl.getClass().getMethod(method.getName(),
			method.getParameterTypes()).invoke(this.vomsAdminImpl, args);

	}

	@Test
	public void testVOMSAdminSynchronization() throws Exception {

		//TODO: rewrite these tests to avoid reflection
		
		// get methods to test
		Method[] methods = org.gcube.vomanagement.vomsapi.VOMSAdmin.class
				.getMethods();

		// iterate over methods to test
		for (Method method : methods) {

			// setUp
			setUp();

			// test method
			testMethod(method);

			// tearDown
			tearDown();
		}

	}

}
