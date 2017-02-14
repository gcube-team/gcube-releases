package it.eng.rdlab.um.group.service;

import it.eng.rdlab.um.exceptions.GroupRetrievalException;
import it.eng.rdlab.um.exceptions.SubGroupException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.exceptions.UserRetrievalException;
import it.eng.rdlab.um.group.beans.GroupModel;

import java.util.List;


/**
 * This interface defines the class that manages the groups.
 * 
 * @author Ciro Formisano
 *
 */
public interface GroupManager 
{
	
	public boolean createGroup(GroupModel groupModel) throws UserManagementSystemException, UserRetrievalException;
	public boolean deleteGroup(String groupId,boolean checkSubgroups) throws UserManagementSystemException, GroupRetrievalException,SubGroupException;
	public boolean updateGroup(GroupModel group) throws UserManagementSystemException, GroupRetrievalException;
	public GroupModel getGroup(String groupId) throws UserManagementSystemException, GroupRetrievalException;
	public List<GroupModel> listGroups() throws UserManagementSystemException, GroupRetrievalException;
	public List<GroupModel> listGroups (GroupModel filter)  throws UserManagementSystemException, GroupRetrievalException;
	public long  getGroupParentId(String groupId) throws UserManagementSystemException, GroupRetrievalException;
	public List<GroupModel> listSubGroupsByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalException;
	public boolean createSubGroup(String parentGroupId,GroupModel groupModel) throws UserManagementSystemException,GroupRetrievalException;
	public void close ();
}
