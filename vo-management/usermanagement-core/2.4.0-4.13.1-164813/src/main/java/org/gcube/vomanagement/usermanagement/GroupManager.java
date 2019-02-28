package org.gcube.vomanagement.usermanagement;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementNameException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.exception.UserRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.VirtualGroupNotExistingException;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.VirtualGroup;


/**
 * This interface defines the class that manages the groups.
 * 
 * @author Massimiliano Assante, CNR-ISTI
 *
 */
public interface GroupManager {
	/**
	 * This method only creates the Group (site), no private or public pages are associated
	 * @param rootVOName the name
	 * @param description the description
	 * @return the instance of the yet created root VO
	 * @throws UserManagementNameException
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 * @throws GroupRetrievalFault
	 * @throws UserManagementPortalException
	 */
	GCubeGroup createRootVO(String rootVOName, String description)throws UserManagementNameException, UserManagementSystemException, UserRetrievalFault ,GroupRetrievalFault, UserManagementPortalException;
	/**
	 * This method only creates the Group (site), no private or public pages are associated
	 * 
	 * @param virtualOrgName the name you wish
	 * @param rootVOGroupId
	 * @param description
	 * @return the instance of the yet created VO
	 * @throws UserManagementNameException
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 * @throws GroupRetrievalFault
	 * @throws UserManagementPortalException
	 */
	GCubeGroup createVO(String virtualOrgName, long rootVOGroupId, String description)throws UserManagementNameException, UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault, UserManagementPortalException;
	/**
	 * This method only creates the Group (site), no private or public pages are associated
	 * 
	 * @param virtualResearchEnvName the name you wish for this VRE
	 * @param virtualOrgGroupId 
	 * @param description the description
	 * @return the instance of the yet created root VRE
	 * @throws UserManagementNameException
	 * @throws UserManagementSystemException
	 * @throws UserRetrievalFault
	 * @throws GroupRetrievalFault
	 * @throws UserManagementPortalException
	 */
	GCubeGroup createVRE(String virtualResearchEnvName, long virtualOrgGroupId, String description)throws UserManagementNameException, UserManagementSystemException, UserRetrievalFault, GroupRetrievalFault, UserManagementPortalException;
	/**
	 * 
	 * @param groupId the LR groupId
	 * @return the parent groupId if exists, -1 otherwise
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	long  getGroupParentId(long groupId) throws UserManagementSystemException, GroupRetrievalFault;
	/**
	 * 
	 * @param groupName the VO/VRE name
	 * @return the LR groupId if exists, -1 otherwise
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	long getGroupId(String groupName) throws UserManagementSystemException, GroupRetrievalFault;
	/**
	 * 
	 * @param groupId the LR groupId
	 * @return an instance of @see {@link GCubeGroup} if exists, null otherwise
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	GCubeGroup getGroup(long groupId) throws UserManagementSystemException, GroupRetrievalFault;
	/**
	 * Virtual Groups are handled as Custom attributes in Liferay Sites table
	 * @return the virtual group list available
	 * @throws GroupRetrievalFault
	 */
	List<VirtualGroup> getVirtualGroups() throws VirtualGroupNotExistingException;
	/**
	 * Virtual Groups are handled as Custom attributes in Liferay Sites table
	 * @param actualGroupId the LR groupId
	 * @return the virtual group name associated to this group
	 * @throws GroupRetrievalFault
	 */
	List<VirtualGroup> getVirtualGroups(long actualGroupId) throws GroupRetrievalFault, VirtualGroupNotExistingException;
	/*
	 * 
	 * @param scope the infrastructure scope e.g. /gcube/devsec
	 * @return the LR groupId, -1 otherwise
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	long getGroupIdFromInfrastructureScope(String scope) throws IllegalArgumentException, UserManagementSystemException, GroupRetrievalFault;
	/**
	 * 
	 * @return an instance of @see {@link GCubeGroup} filled with the RootVO metadata
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	GCubeGroup getRootVO() throws UserManagementSystemException, GroupRetrievalFault;
	/**
	 * 
	 * @return the RootVO name
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	String getRootVOName() throws UserManagementSystemException, GroupRetrievalFault;
	/**
	 * 
	 * @param groupId the LR groupId
	 * @return the infrastructure scope given the groupId e.g. for devsec VO would return /gcube/devsec
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	String getInfrastructureScope(long groupId) throws UserManagementSystemException, GroupRetrievalFault;
	/**
	 * @deprecated 
	 * please use getInfrastructureScope(long groupId)
	 * @param groupId the LR groupId
	 * @return the infrastructure scope given the groupId e.g. for devsec VO would return /gcube/devsec
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	String getScope(long groupId) throws UserManagementSystemException, GroupRetrievalFault;
	/**
	 * * @return a flat list of VOs and VREs present in this gateway mapped as @see {@link GCubeGroup}
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault if the rootVO does not exists
	 */
	List<GCubeGroup> listGroups()  throws UserManagementSystemException, GroupRetrievalFault ;
	/**
	 * @param userId the LR userId
	 * @return a flat list of VOs and VREs where the user is registered as @see {@link GCubeGroup} 
	 * @throws UserRetrievalFault
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault 
	 */
	List<GCubeGroup> listGroupsByUser(long userId) throws UserRetrievalFault, UserManagementSystemException, GroupRetrievalFault;
	/**
	 * * @return the list of user VREs
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault if the rootVO does not exists
	 */
	List<GCubeGroup> listVresByUser(long userId)  throws UserManagementSystemException, GroupRetrievalFault, UserRetrievalFault ;
	/**
	 * @param userId the LR userId
	 * @param  serverName the host name of the server that is sending the request (e.g. i-marine.d4science.org)
	 * @return a set of VREs where the user is registered as @see {@link GCubeGroup} on the current Site (e.g i-marine.d4science.org)
	 * @throws UserRetrievalFault
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault 
	 */
	Set<GCubeGroup> listGroupsByUserAndSite(long userId, final String serverName) throws UserRetrievalFault, UserManagementSystemException, GroupRetrievalFault, VirtualGroupNotExistingException;
	/**
	 * @param userId the LR userId
	 * @param siteGroupId the LR groupId from which is sending the request
	 * @return a set of VREs where the user is registered as @see {@link GCubeGroup} on the current Site (e.g i-marine.d4science.org)
	 * @throws UserRetrievalFault
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault 
	 */
	Set<GCubeGroup> listGroupsByUserAndSiteGroupId(long userId, long siteGroupId) throws UserRetrievalFault, UserManagementSystemException, GroupRetrievalFault, VirtualGroupNotExistingException;
	/**
	 * 
	 * @param userId the LR userId
	 * @return a map contaiting the users groups and his roles in the group
	 * @throws UserManagementSystemException
	 */
	Map<GCubeGroup, List<GCubeRole>> listGroupsAndRolesByUser(long userId) throws UserManagementSystemException;
	/**
	 * 
	 * @param groupId the LR groupId
	 * @return true if the groupId is the RootVO, false otherwise
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	Boolean isRootVO(long groupId) throws UserManagementSystemException, GroupRetrievalFault;
	/**
	 * 
	 * @param groupId the LR groupId
	 * @return true if the groupId is a VO, false otherwise
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	Boolean isVO(long groupId) throws UserManagementSystemException, GroupRetrievalFault;
	/**
	 * 
	 * @param groupId the LR groupId
	 * @return true if the groupId is a VRE, false otherwise
	 * @throws UserManagementSystemException
	 * @throws GroupRetrievalFault
	 */
	Boolean isVRE(long groupId) throws UserManagementSystemException, GroupRetrievalFault;
	/**
	 * 
	 * @param groupId the LR groupId
	 * @param attributeKey the name of the attribute you want to read its value
	 * @return the attributeKey value if existing, null otherwise
	 */
	Serializable readCustomAttr(long groupId, String attributeKey) throws GroupRetrievalFault;
	/**
	 * 
	 * @param groupId
	 * @param attributeKey the name of the attribute you want to save
	 * @param value the value
	 * @throws GroupRetrievalFault
	 */
	void saveCustomAttr(long groupId, String attributeKey, Serializable value) throws GroupRetrievalFault;
	/**
	 * 
	 * @param groupId
	 * @param description text to update
	 * @return the updated description
	 */
	String updateGroupDescription(long groupId, String description) throws GroupRetrievalFault;
	/**
	 * 
	 * @param logoId the logo identifier of the group @see {GCubeGroup#logoId}
	 * @return the absolute path on server for the logo (e.g. /image/layout_set_logo?img_id ... )
	 * @throws GroupRetrievalFault
	 */
	String getGroupLogoURL(long logoId);
	
	/**
	 * Retrieve the list of GCubeGroups that are Gateways (i.e. groups with no father and children).
	 * @return a list of gateways
	 */
	List<GCubeGroup> getGateways();
}