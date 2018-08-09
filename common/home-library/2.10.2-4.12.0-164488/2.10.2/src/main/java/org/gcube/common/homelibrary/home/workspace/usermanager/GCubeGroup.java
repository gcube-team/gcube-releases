/**
 * 
 */
package org.gcube.common.homelibrary.home.workspace.usermanager;

import java.util.List;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;


public interface GCubeGroup{
	
	public String getName();

    /**
     * Adds the specified member to the group.
     *
     * @param user the user to add to this group.
     *
     * @return true if the member was successfully added,
     * false if the user was already a member.
     * 
     * @throws InternalErrorException 
     */
    public boolean addMember(String user) throws InternalErrorException;

	/**
	 * Add a list of users
	 * @param users
	 * @throws InternalErrorException
	 */
	public boolean addMembers(List<String> users) throws InternalErrorException;
	
    /**
     * Removes the specified member from the group.
     *
     * @param user the user to remove from this group.
     *
     * @return true if the user was removed, or
     * false if the user was not a member.
     * @throws InternalErrorException 
     */
    public boolean removeMember(String user) throws InternalErrorException;

    
	/**
	 * Delete a list of users
	 * @param users
	 * @throws InternalErrorException
	 */
	public boolean removeMembers(List<String> users) throws InternalErrorException;
	
	
    /**
     * Returns true if the passed user is a member of the group.
     * This method does a recursive search, so if a user belongs to a
     * group which is a member of this group, true is returned.
     *
     * @param user the user whose membership is to be checked.
     *
     * @return true if the user is a member of this group,
     * false otherwise.
     * @throws InternalErrorException 
     */
    public boolean isMember(String user) throws InternalErrorException;


    /**
     * Returns a list of the members in the group.
     *
     * @return a list of the members in the group.
     * @throws InternalErrorException 
     */
    public List<String> getMembers() throws InternalErrorException;   
    
    /**
     * Return a diplayName for VRE group
     * @return a diplayName for VRE group
     * @throws InternalErrorException 
     */
    public String getDisplayName() throws InternalErrorException;

	/**
	 * Set a new diplayName for VRE group
	 * @param displayName
	 * @return true if the diplayName has been updated
	 * @throws InternalErrorException
	 */
    public boolean setDisplayName(String displayName) throws InternalErrorException;
	
}
