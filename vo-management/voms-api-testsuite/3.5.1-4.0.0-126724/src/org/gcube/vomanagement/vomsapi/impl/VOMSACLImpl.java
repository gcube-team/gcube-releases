package org.gcube.vomanagement.vomsapi.impl;

import java.rmi.RemoteException;

import javax.xml.rpc.Stub;

import org.apache.log4j.Logger;
import org.glite.wsdl.services.org_glite_security_voms.VOMSException;
import org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry;
import org.glite.wsdl.services.org_glite_security_voms_service_acl.VOMSACL;

/**
 * Implementation of the {@link VOMSACL} interface providing synchronization for
 * VOMS calls. Method implementation is delegated to the {@link VOMSACL} object
 * passed to the constructor.
 * 
 * @author Paolo Roccetti
 */
class VOMSACLImpl extends VOMSAPIStub implements
		org.gcube.vomanagement.vomsapi.VOMSACL {

	/**
	 * The log4j instance
	 */
	private static Logger logger = Logger
			.getLogger(VOMSACLImpl.class.getName());

	private VOMSACL vomsACL;

	/**
	 * Constructor.
	 * 
	 * @param vomsACL
	 *            the {@link VOMSACL} object to actually perform VOMS calls.
	 */
	public VOMSACLImpl(VOMSACL vomsACL, VOMSAPIFactory factory) {
		super(factory);
		this.vomsACL = vomsACL;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAPIStub#configureVOMSAPIStubForCall()
	 */
	@Override
	void configureVOMSAPIStubForCall() {
		// configure the inner stubs for the call
		configureSecurity((Stub) this.vomsACL);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.glite.wsdl.services.org_glite_security_voms_service_acl.VOMSACL#addACLEntry(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry,
	 *      boolean)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSACL#addACLEntry(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry,
	 *      boolean)
	 */
	public void addACLEntry(String arg0, ACLEntry arg1, boolean arg2)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsACL.addACLEntry(arg0, arg1, arg2);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#addDefaultACLEntry(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms.ACLEntry)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSACL#addDefaultACLEntry(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry)
	 */
	public void addDefaultACLEntry(String arg0, ACLEntry arg1)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsACL.addDefaultACLEntry(arg0, arg1);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#getACL(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSACL#getACL(java.lang.String)
	 */
	public ACLEntry[] getACL(String arg0) throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		ACLEntry[] aclArray;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				aclArray = this.vomsACL.getACL(arg0);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		return aclArray;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#getDefaultACL(java.lang.String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSACL#getDefaultACL(java.lang.String)
	 */
	public ACLEntry[] getDefaultACL(String arg0) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException {

		ACLEntry[] aclArray;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				aclArray = this.vomsACL.getDefaultACL(arg0);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		return aclArray;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.glite.wsdl.services.org_glite_security_voms_service_acl.VOMSACL#removeACLEntry(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry,
	 *      boolean)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSACL#removeACLEntry(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry,
	 *      boolean)
	 */
	public void removeACLEntry(String arg0, ACLEntry arg1, boolean arg2)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsACL.removeACLEntry(arg0, arg1, arg2);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#removeDefaultACLEntry(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms.ACLEntry)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSACL#removeDefaultACLEntry(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry)
	 */
	public void removeDefaultACLEntry(String arg0, ACLEntry arg1)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsACL.removeDefaultACLEntry(arg0, arg1);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#setACL(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms.ACLEntry[])
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSACL#setACL(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry[])
	 */
	public void setACL(String arg0, ACLEntry[] arg1) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsACL.setACL(arg0, arg1);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#setDefaultACL(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms.ACLEntry[])
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSACL#setDefaultACL(java.lang.String,
	 *      org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry[])
	 */
	public void setDefaultACL(String arg0, ACLEntry[] arg1)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsACL.setDefaultACL(arg0, arg1);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

}
