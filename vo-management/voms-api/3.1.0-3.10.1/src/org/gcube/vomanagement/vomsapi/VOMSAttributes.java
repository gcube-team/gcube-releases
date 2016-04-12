package org.gcube.vomanagement.vomsapi;

import java.rmi.RemoteException;

import org.gcube.vomanagement.vomsapi.impl.VOMSAPIConfigurationException;
import org.glite.wsdl.services.org_glite_security_voms.User;
import org.glite.wsdl.services.org_glite_security_voms.VOMSException;
import org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeClass;
import org.glite.wsdl.services.org_glite_security_voms_service_attributes.AttributeValue;

/**
 * 
 * This interface contains operations to manage Attributes in a VOMS VO.
 * Attributes are name-value couples assigned to users, groups and roles of a
 * VOMS VO.
 * 
 * <p>
 * Attributes in a VOMS VO belongs to attribute classes, that defines the
 * attribute name, and, in case, a constraint on the attribute assignment in the
 * VO (see the
 * <code>createAttributeClass(String className, String classDescription, boolean uniqueness)</code>
 * method). <br>
 * Attribute classes can be defined in the VO by means of the
 * <code>createAttributeClass(...)</code> methods.<br>
 * Once an {@link AttributeClass} has been defined, the corresponding object can
 * be retrieved using the <code>getAttributeClass(...)</code> method.
 * </p>
 * 
 * <p>
 * The {@link AttributeClass} object are used to build the
 * {@link AttributeValue} objects, that represents attribute values.<br>
 * In particular, once created, {@link AttributeValue} objects can be assigned
 * to VO entities, namely: users, groups and roles.<br>
 * This can be done by means of the <code>setUserAttribute(...)</code>,
 * <code>setGroupAttribute(...)</code> and <code>setRoleAttribute(...)</code>
 * methods.
 * </p>
 * 
 * <p>
 * It is worth notice that VOMS attributes are single value, i.e. only one value
 * can be assigned for the given attribute class to a VO entity (user, group or
 * role).
 * </p>
 * 
 * <p>
 * Unless differently noted, group names are absolute to the VO, e.g.
 * <code>/testVO/testGroup1/testGroup2</code>. In addition, roleName
 * arguments does not requires the <code>"Role="</code> prefix, as well as
 * role returned from {@link VOMSAdmin} methods are already cleaned from the
 * <code>"Role="</code> prefix.
 * </p>
 * 
 * <p>
 * Lastly, in all methods returning an array, if the return contains no
 * elements, an empty array is returned, instead of the null value.
 * </p>
 * 
 * @author Paolo Roccetti
 */
public interface VOMSAttributes {

	/**
	 * Creates a new attribute class with the given name. If an attribute class
	 * with the given name already exists, a {@link VOMSException} will be
	 * thrown.
	 * 
	 * @param className
	 *            the name of the new attribute class.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void createAttributeClass(String className) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	/**
	 * Creates a new attribute class with the given name and description. If an
	 * attribute class with the given name already exists, a
	 * {@link VOMSException} will be thrown.
	 * 
	 * @param className
	 *            the name of the new attribute class.
	 * 
	 * @param classDescription
	 *            the description of the new attribute class.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void createAttributeClass(String className, String classDescription)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Creates a new attribute class with the given name and description. If an
	 * attribute class with the given name already exists, a
	 * {@link VOMSException} will be thrown.<br>
	 * The uniqueness parameter controls the assignment of values for this
	 * attributes to VO users, roles and groups. In particular, setting the
	 * uniqueness parameter to true, ensures the same value is never
	 * assigned for this attribute to two different users, roles, or groups of the VO.
	 * 
	 * @param className
	 *            the name of the new attribute class.
	 * 
	 * @param classDescription
	 *            the description of the new attribute class.
	 * 
	 * @param uniqueness
	 *            if values of this attributes must have a unique assignment in
	 *            the VO
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void createAttributeClass(String className, String classDescription,
			boolean uniqueness) throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Deletes an existing attribute class with the given name. If an attribute
	 * class with the given name does not exists, a {@link VOMSException} will
	 * be thrown.
	 * 
	 * @param className
	 *            the name of the attribute class to delete.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void deleteAttributeClass(String className) throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	/**
	 * Deletes the given attribute class. If the given attribute class has been
	 * already deleted, a {@link VOMSException} will be thrown.
	 * 
	 * @param attributeClass
	 *            the attribute class to delete.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void deleteAttributeClass(AttributeClass attributeClass)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Deletes the attribute with the given class from the given group. If the
	 * attribute with the given class is not assigned to the given group, a
	 * {@link VOMSException} will be thrown.
	 * 
	 * @param groupName
	 *            the group name
	 * 
	 * @param className
	 *            the class name of the attribute to delete
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void deleteGroupAttribute(String groupName, String className)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Deletes the given attribute from the given group. If the attribute is not
	 * assigned to the given group, a {@link VOMSException} will be thrown.
	 * 
	 * @param groupName
	 *            the group name
	 * 
	 * @param value
	 *            the attribute value to delete. The attribute class will only
	 *            be taken into account to determine the attribute to delete,
	 *            the supplied attribute value will be ignored.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void deleteGroupAttribute(String groupName, AttributeValue value)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Deletes the given attribute from the given role in the given group. If
	 * the attribute with the given class is not assigned to the given role in
	 * the group, a {@link VOMSException} will be thrown.
	 * 
	 * @param groupName
	 *            the group name
	 * 
	 * @param roleName
	 *            the role name
	 * 
	 * @param className
	 *            the class name of the attribute to delete
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void deleteRoleAttribute(String groupName, String roleName,
			String className) throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Deletes the given attribute from the given role in the given group. If
	 * the attribute with the given class is not assigned to the given role in
	 * the group, a {@link VOMSException} will be thrown.
	 * 
	 * @param groupName
	 *            the group name
	 * 
	 * @param roleName
	 *            the role name
	 * 
	 * @param value
	 *            the attribute value to delete. The attribute class will only
	 *            be taken into account to determine the attribute to delete,
	 *            the supplied attribute value will be ignored.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void deleteRoleAttribute(String groupName, String roleName,
			AttributeValue value) throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Deletes the given attribute from the given user. If the attribute with
	 * the given class is not assigned to the given user, a
	 * {@link VOMSException} will be thrown.
	 * 
	 * @param user
	 *            the {@link User} object to identify the user
	 * 
	 * @param className
	 *            the class name of the attribute to delete.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void deleteUserAttribute(User user, String className)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Deletes the given attribute from the given user. If the attribute with
	 * the given class is not assigned to the given user, a
	 * {@link VOMSException} will be thrown.
	 * 
	 * 
	 * @param user
	 *            the {@link User} object to identify the user
	 * 
	 * @param value
	 *            the attribute value to delete. The attribute class will only
	 *            be taken into account to determine the attribute to delete,
	 *            the supplied attribute value will be ignored.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void deleteUserAttribute(User user, AttributeValue value)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Get the {@link AttributeClass} associated with the given class name, if
	 * any. If a class with the given name does not exists, a
	 * {@link NullPointerException} will be thrown.
	 * 
	 * @param className
	 *            the class name
	 * 
	 * @return the {@link AttributeClass} object associated with the given class
	 *         name.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public AttributeClass getAttributeClass(String className)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * List attribute classes defined in the VO.
	 * 
	 * @return a {@link AttributeClass} array containing the attribute classes
	 *         defined in the VO. If no attribute classes have been defined for
	 *         the VO, an empty array will be returned.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public AttributeClass[] listAttributeClasses() throws RemoteException,
			VOMSException, VOMSAPIConfigurationException;

	/**
	 * List attribute values set for the given group.
	 * 
	 * @param groupName
	 *            the group name
	 * 
	 * @return a {@link AttributeClass} array containing the attribute values
	 *         set for the given group. If no attribute values have been defined
	 *         for the given group, an empty array will be returned.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public AttributeValue[] listGroupAttributes(String groupName)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * List attribute values set for the given role in the given group.
	 * 
	 * @param groupName
	 *            the group name
	 * 
	 * @param roleName
	 *            the role name
	 * 
	 * @return a {@link AttributeClass} array containing the attribute values
	 *         set for the given role in the given group. If no attribute values
	 *         have been defined for the given role in the given group, an empty
	 *         array will be returned.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public AttributeValue[] listRoleAttributes(String groupName, String roleName)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * List attribute values set for the given user.
	 * 
	 * @param user
	 *            the {@link User} object to identify the user
	 * 
	 * @return a {@link AttributeClass} array containing the attribute values
	 *         set for the given user. If no attribute values have been defined
	 *         for the given user, an empty array will be returned.
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public AttributeValue[] listUserAttributes(User user)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	// This operation is not yet implemented in VOMS, thus it has been
	// temporarily removed
	// public void saveAttributeClass(AttributeClass attributeClass) throws
	// RemoteException, VOMSException, VOMSAPIConfigurationException;

	/**
	 * Set the given attribute value for the given group.
	 * 
	 * @param groupName
	 *            the group name
	 * 
	 * @param value
	 *            the attribute value to set
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void setGroupAttribute(String groupName, AttributeValue value)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Set the given attribute value for the given role in the given group.
	 * 
	 * @param groupName
	 *            the group name
	 * 
	 * @param roleName
	 *            the role name
	 * 
	 * @param value
	 *            the attribute value to set
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void setRoleAttribute(String groupName, String roleName,
			AttributeValue value) throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

	/**
	 * Set the given attribute value for the given user.
	 * 
	 * @param user
	 *            the {@link User} object to identify the user
	 * 
	 * @param value
	 *            the attribute value to set
	 * 
	 * @throws RemoteException
	 *             when a remote exception occurs in the communication with VOMS
	 * @throws VOMSException
	 *             when a VOMS related exception occurs on the server side.
	 * @throws VOMSAPIConfigurationException
	 *             when a local exception occurs configuring the VOMS-API stubs
	 *             for the remote call
	 */
	public void setUserAttribute(User user, AttributeValue value)
			throws RemoteException, VOMSException,
			VOMSAPIConfigurationException;

}