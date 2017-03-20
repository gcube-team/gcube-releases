package org.gcube.vomanagement.vomsapi.impl;

import java.rmi.RemoteException;

import javax.xml.rpc.Stub;

import org.apache.log4j.Logger;
import org.glite.wsdl.services.org_glite_security_voms.User;
import org.glite.wsdl.services.org_glite_security_voms.VOMSException;
import org.glite.wsdl.services.org_glite_security_voms_service_admin.VOMSAdmin;

/**
 * Implementation of the {@link VOMSAdmin} interface providing synchronization
 * for VOMS calls. Method implementation is delegated to the {@link VOMSAdmin}
 * object passed to the constructor.
 * 
 * @author Paolo Roccetti
 */
class VOMSAdminImpl extends VOMSAPIStub implements
		org.gcube.vomanagement.vomsapi.VOMSAdmin {

	/**
	 * The log4j instance
	 */
	private static Logger logger = Logger.getLogger(VOMSAdminImpl.class
			.getName());

	private VOMSAdmin vomsAdmin;

	/**
	 * Constructor.
	 * 
	 * @param vomsAdmin
	 *            the {@link VOMSAdmin} object to actually perform VOMS calls.
	 * 
	 * @param factory
	 *            the {@link VOMSAPIFactory} that created this object
	 */
	public VOMSAdminImpl(VOMSAdmin vomsAdmin, VOMSAPIFactory factory) {
		super(factory);
		this.vomsAdmin = vomsAdmin;
	}

	@Override
	void configureVOMSAPIStubForCall() {
		// configure the inner stubs for the call
		configureSecurity((Stub) this.vomsAdmin);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#addMember(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void addMember(String groupName, String userDN, String userCA)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAdmin.addMember(groupName, userDN, userCA);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#assignRole(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public void assignRole(String groupName, String roleName, String userDN,
			String userCA) throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAdmin.assignRole(groupName, addRolePrefix(roleName),
					userDN, userCA);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#createGroup(java.lang.String,
	 *      java.lang.String)
	 */
	public void createGroup(String groupName, String parentGroup)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAdmin.createGroup(groupName, parentGroup);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#createRole(java.lang.String)
	 */
	public void createRole(String roleName) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAdmin.createRole(addRolePrefix(roleName));

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#createUser(org.glite.wsdl.services.org_glite_security_voms.User)
	 */
	public void createUser(User user) throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAdmin.createUser(user);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#deleteGroup(java.lang.String)
	 */
	public void deleteGroup(String groupName) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAdmin.deleteGroup(groupName);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#deleteRole(java.lang.String)
	 */
	public void deleteRole(String roleName) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAdmin.deleteRole(addRolePrefix(roleName));

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#deleteUser(java.lang.String,
	 *      java.lang.String)
	 */
	public void deleteUser(String userDN, String userCA)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAdmin.deleteUser(userDN, userCA);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#dismissRole(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String)
	 */
	public void dismissRole(String groupName, String roleName, String userDN,
			String userCA) throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAdmin.dismissRole(groupName, addRolePrefix(roleName),
					userDN, userCA);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#getGroupPath(java.lang.String)
	 */
	public String[] getGroupPath(String groupName) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException {

		String[] groupPath;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				groupPath = this.vomsAdmin.getGroupPath(groupName);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		return groupPath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#getMajorVersionNumber()
	 */
	public int getMajorVersionNumber() throws RemoteException, VOMSAPIConfigurationException {

		int i;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				i = this.vomsAdmin.getMajorVersionNumber();

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		return i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#getMinorVersionNumber()
	 */
	public int getMinorVersionNumber() throws RemoteException, VOMSAPIConfigurationException {

		int i;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				i = this.vomsAdmin.getMinorVersionNumber();

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		return i;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#getPatchVersionNumber()
	 */
	public int getPatchVersionNumber() throws RemoteException, VOMSAPIConfigurationException {

		int i;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				i = this.vomsAdmin.getPatchVersionNumber();

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		return i;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#getUser(java.lang.String,
	 *      java.lang.String)
	 */
	public User getUser(String userDN, String userCA) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException {

		User user;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				user = this.vomsAdmin.getUser(userDN, userCA);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		return user;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#getVOName()
	 */
	public String getVOName() throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		String voName;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				voName = this.vomsAdmin.getVOName();

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		return voName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#listCAs()
	 */
	public String[] listCAs() throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		String[] cas;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				cas = this.vomsAdmin.listCAs();

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		return cas;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#listGroups(java.lang.String,
	 *      java.lang.String)
	 */
	public String[] listGroups(String userDN, String userCA)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		String[] groups;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				groups = this.vomsAdmin.listGroups(userDN, userCA);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		return groups;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#listMembers(java.lang.String)
	 */
	public User[] listMembers(String groupName) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException {

		User[] users;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				users = this.vomsAdmin.listMembers(groupName);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		return users;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#listRoles()
	 */
	public String[] listRoles() throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		String[] roles;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				roles = this.vomsAdmin.listRoles();

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		if (roles == null) {
			return new String[] {};
		} else {
			return discardRolePrefix(roles);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#listRoles(java.lang.String,
	 *      java.lang.String)
	 */
	public String[] listRoles(String userDN, String userCA)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		String[] roles;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				roles = this.vomsAdmin.listRoles(userDN, userCA);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		if (roles == null) {
			return new String[] {};
		} else {
			return discardRolePrefix(roles);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#listSubGroups(java.lang.String)
	 */
	public String[] listSubGroups(String groupName) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException {

		String[] groups;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				groups = this.vomsAdmin.listSubGroups(groupName);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		if (groups == null) {
			return new String[] {};
		} else {
			return groups;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#listUsersWithRole(java.lang.String,
	 *      java.lang.String)
	 */
	public User[] listUsersWithRole(String groupName, String roleName)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		User[] users;

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				users = this.vomsAdmin.listUsersWithRole(groupName,
					addRolePrefix(roleName));

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

		if (users == null) {
			return new User[] {};
		} else {
			return users;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#removeMember(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void removeMember(String groupName, String userDN, String userCA)
			throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAdmin.removeMember(groupName, userDN, userCA);

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
	 * @see org.gcube.vomanagement.vomsapi.impl.VOMSAdmin#setUser(org.glite.wsdl.services.org_glite_security_voms.User)
	 */
	public void setUser(User user) throws RemoteException, VOMSException, VOMSAPIConfigurationException {

		try {
			synchronized (getFactory().getLock()) {

				getFactory().prepareForCall(this);

				this.vomsAdmin.setUser(user);

				getFactory().exitFromCall();
			}
		} catch (RemoteException e) {
			handleException(e, logger);
			throw e;
		}

	}

}
