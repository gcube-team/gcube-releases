package org.gcube.vomanagement.vomsapi.impl;

import java.rmi.RemoteException;

import javax.xml.rpc.Stub;

import org.apache.log4j.Logger;
import org.glite.wsdl.services.org_glite_security_voms.User;
import org.glite.wsdl.services.org_glite_security_voms.VOMSException;
import org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeClass;
import org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue;
import org.glite.wsdl.services.org_glite_security_voms_service_attributes.VOMSAttributes;

/**
 * Implementation of the {@link VOMSAttributes} interface providing
 * synchronization for VOMS calls. Method implementation is delegated to the
 * {@link VOMSAttributes} object passed to the constructor.
 * 
 * @author Paolo Roccetti
 */
class VOMSAttributesImpl extends VOMSAPIStub implements
		org.gcube.vomanagement.vomsapi.VOMSAttributes {

	/**
	 * The log4j instance
	 */
	private static Logger logger = Logger
			.getLogger(VOMSAttributesImpl.class.getName());

	private VOMSAttributes vomsAttributes;

	/**
	 * Constructor.
	 * 
	 * @param vomsAttributes
	 *            the {@link VOMSAttributes} object to actually perform VOMS
	 *            calls.
	 */
	VOMSAttributesImpl(VOMSAttributes vomsAttributes, VOMSAPIFactory factory) {
		super(factory);
		this.vomsAttributes = vomsAttributes;
	}

	@Override
	void configureVOMSAPIStubForCall() {

		// configure the inner stubs for the call
		configureSecurity((Stub) this.vomsAttributes);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#createAttributeClass(java.lang.String)
	 */
	public void createAttributeClass(String className) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.createAttributeClass(className);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#createAttributeClass(java.lang.String,
	 *      java.lang.String)
	 */
	public void createAttributeClass(String className, String classDescription)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.createAttributeClass(className, classDescription);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#createAttributeClass(java.lang.String,
	 *      java.lang.String, boolean)
	 */
	public void createAttributeClass(String className, String classDescription,
			boolean uniqueness)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.createAttributeClass(className, classDescription, uniqueness);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#deleteAttributeClass(java.lang.String)
	 */
	public void deleteAttributeClass(String className) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.deleteAttributeClass(className);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#deleteAttributeClass(org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeClass)
	 */
	public void deleteAttributeClass(AttributeClass attributeClass)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.deleteAttributeClass(attributeClass);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#deleteGroupAttribute(java.lang.String,
	 *      java.lang.String)
	 */
	public void deleteGroupAttribute(String groupName, String className)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.deleteGroupAttribute(groupName, className);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#deleteGroupAttribute(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue)
	 */
	public void deleteGroupAttribute(String groupName, AttributeValue value)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.deleteGroupAttribute(groupName, value);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#deleteRoleAttribute(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void deleteRoleAttribute(String groupName, String roleName,
			String className)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.deleteRoleAttribute(groupName, addRolePrefix(roleName), className);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#deleteRoleAttribute(java.lang.String,
	 *      java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue)
	 */
	public void deleteRoleAttribute(String groupName, String roleName,
			AttributeValue value) throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.deleteRoleAttribute(groupName, addRolePrefix(roleName), value);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#deleteUserAttribute(org.glite.wsdl.services.org_glite_security_voms.User,
	 *      java.lang.String)
	 */
	public void deleteUserAttribute(User user, String className)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.deleteUserAttribute(user, className);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#deleteUserAttribute(org.glite.wsdl.services.org_glite_security_voms.User,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue)
	 */
	public void deleteUserAttribute(User user, AttributeValue value)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.deleteUserAttribute(user, value);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#getAttributeClass(java.lang.String)
	 */
	public AttributeClass getAttributeClass(String className)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		AttributeClass ac = null;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				ac = this.vomsAttributes.getAttributeClass(className);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		return ac;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#listAttributeClasses()
	 */
	public AttributeClass[] listAttributeClasses() throws RemoteException,
			VOMSException, VOMSAPIConfigurationException {

		AttributeClass[] acs;
		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				acs = this.vomsAttributes.listAttributeClasses();

				getFactory().exitFromCall();
			}

		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}
		
		if (acs == null) {
			return new AttributeClass[] {};
		} else {
			return acs;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#listGroupAttributes(java.lang.String)
	 */
	public AttributeValue[] listGroupAttributes(String groupName)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		AttributeValue[] avs;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				avs = this.vomsAttributes.listGroupAttributes(groupName);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		if (avs == null) {
			return new AttributeValue[] {};
		} else {
			return avs;
		}


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#listRoleAttributes(java.lang.String,
	 *      java.lang.String)
	 */
	public AttributeValue[] listRoleAttributes(String groupName, String roleName)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		AttributeValue[] avs;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				avs = this.vomsAttributes.listRoleAttributes(groupName, addRolePrefix(roleName));

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		if (avs == null) {
			return new AttributeValue[] {};
		} else {
			return avs;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#listUserAttributes(org.glite.wsdl.services.org_glite_security_voms.User)
	 */
	public AttributeValue[] listUserAttributes(User user)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		AttributeValue[] avs;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				avs = this.vomsAttributes.listUserAttributes(user);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		if (avs == null) {
			return new AttributeValue[] {};
		} else {
			return avs;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#setGroupAttribute(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue)
	 */
	public void setGroupAttribute(String groupName, AttributeValue value)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.setGroupAttribute(groupName, value);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#setRoleAttribute(java.lang.String,
	 *      java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue)
	 */
	public void setRoleAttribute(String groupName, String roleName,
			AttributeValue value)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.setRoleAttribute(groupName, addRolePrefix(roleName), value);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAttributes#setUserAttribute(org.glite.wsdl.services.org_glite_security_voms.User,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue)
	 */
	public void setUserAttribute(User user, AttributeValue value)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAttributes.setUserAttribute(user, value);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

}
