package org.gcube.vomanagement.vomsapi;

import java.rmi.RemoteException;

import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfigurationException;
import org.glite.wsdl.services.org_glite_security_voms.VOMSException;
import org.glite.wsdl.services.org_glite_security_voms_service_acl.ACLEntry;

/**
 * This interface contains operations to manage ACL of a VOMS VO.
 * 
 * <p>
 * <b>NOTE:</b> This interface is used internally by the VOMS-API library to
 * provide some functionalities of the {@link ExtendedVOMSAdmin} interface. It
 * is not intended to be used directly from external code, and for this reason
 * it has not been fully documented.
 * </p>
 * 
 * @author Paolo Roccetti
 */
public interface VOMSACL {

	public void addACLEntry(String arg0, ACLEntry arg1, boolean arg2)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	public void addDefaultACLEntry(String arg0, ACLEntry arg1)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	public ACLEntry[] getACL(String arg0) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	public ACLEntry[] getDefaultACL(String arg0) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	public void removeACLEntry(String arg0, ACLEntry arg1, boolean arg2)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	public void removeDefaultACLEntry(String arg0, ACLEntry arg1)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	public void setACL(String arg0, ACLEntry[] arg1) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	public void setDefaultACL(String arg0, ACLEntry[] arg1)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

}