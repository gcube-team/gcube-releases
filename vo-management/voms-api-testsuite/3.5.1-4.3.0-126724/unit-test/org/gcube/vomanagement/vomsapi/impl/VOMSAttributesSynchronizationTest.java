package org.gcube.vomanagement.vomsapi.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.gcube.vomanagement.vomsapi.VOMSAPITest;
import org.glite.wsdl.services.org_glite_security_voms_service_attributes.VOMSAttributes;
import org.junit.Test;

/**
 * This test class verifies that methods of
 * {@link org.gcube.vomanagement.vomsapi.VOMSAttributes} sub-classes properly
 * enforce synchronization of VOMS calls. See the {@link VOMSAPIFactory} code
 * for a description of the synchronization mechanism.
 * 
 * @author Paolo Roccetti
 */
public class VOMSAttributesSynchronizationTest extends VOMSAPITest {

	private static Logger logger = Logger
			.getLogger(VOMSAttributesSynchronizationTest.class.getName());

	private VOMSAPIFactory factoryMock;

	private VOMSAttributes vomsAttributesMock;

	private VOMSAttributesImpl vomsAttributesImpl;

	public void setUp() throws Exception {

		// create mock factory
		this.factoryMock = createStrictMock(VOMSAPIFactory.class);

		// create mock VOMSAdmin
		this.vomsAttributesMock = createMock(VOMSAttributes.class);

		// create VOMSAdminImpl
		this.vomsAttributesImpl = new VOMSAttributesImpl(
				this.vomsAttributesMock, this.factoryMock);

		// configure factory with expected call
		expect(this.factoryMock.getLock()).andReturn(new Object());
		this.factoryMock.prepareForCall(this.vomsAttributesImpl);
		this.factoryMock.exitFromCall();

		// replay factoryMock
		replay(this.factoryMock);

	}

	public void tearDown() throws Exception {

		// verify factory and VOMSAdmin
		verify(this.factoryMock, this.vomsAttributesMock);

		// clean objects
		this.factoryMock = null;
		this.vomsAttributesMock = null;
		this.vomsAttributesImpl = null;

	}

	// test the synchronization of a method using reflection
	private void testMethod(Method method) throws Exception {

		// log the method under test
		logMethod(method, logger);

		// get arguments for the call
		Object[] args = getArguments(method);

		// set expectation on vomsAttributesMock
		if (method.getReturnType().equals(void.class)) {
			// set for methods returning void
			this.vomsAttributesMock.getClass().getMethod(method.getName(),
				method.getParameterTypes()).invoke(this.vomsAttributesMock,
				args);
		} else if (method.getReturnType().equals(int.class)) {
			// set for primitive int type
			expect(
				this.vomsAttributesMock.getClass().getMethod(method.getName(),
					method.getParameterTypes()).invoke(this.vomsAttributesMock,
					args)).andReturn(0);
		} else if (method.getReturnType().equals(boolean.class)) {
			// set for primitive boolean type
			expect(
				this.vomsAttributesMock.getClass().getMethod(method.getName(),
					method.getParameterTypes()).invoke(this.vomsAttributesMock,
					args)).andReturn(false);
		} else {
			// set for Objects
			expect(
				this.vomsAttributesMock.getClass().getMethod(method.getName(),
					method.getParameterTypes()).invoke(this.vomsAttributesMock,
					args)).andReturn(null);
		}

		// replay vomsAdminMock
		replay(this.vomsAttributesMock);

		// invoke the method
		this.vomsAttributesImpl.getClass().getMethod(method.getName(),
			method.getParameterTypes()).invoke(this.vomsAttributesImpl, args);

	}

	@Test
	public void testVOMSAttributesSynchronization() throws Exception {

		//TODO: rewrite these tests to avoid reflection
		
		// get methods to test
		Method[] methods = org.gcube.vomanagement.vomsapi.VOMSAttributes.class
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
