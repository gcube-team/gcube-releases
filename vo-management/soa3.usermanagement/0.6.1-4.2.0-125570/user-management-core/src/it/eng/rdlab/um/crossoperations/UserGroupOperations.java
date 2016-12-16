package it.eng.rdlab.um.crossoperations;

import it.eng.rdlab.um.exceptions.GroupRetrievalException;
import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.exceptions.UserRetrievalException;
import it.eng.rdlab.um.group.beans.GroupModel;
import it.eng.rdlab.um.user.beans.UserModel;

import java.util.List;

public interface UserGroupOperations 
{

	public boolean assignUserToGroup(String groupId, String userId) throws UserManagementSystemException, GroupRetrievalException, UserRetrievalException;
	public boolean dismissUserFromGroup(String groupId, String userId) throws UserManagementSystemException, GroupRetrievalException, UserRetrievalException;
	public List<GroupModel> listGroupsByUser(String userId) throws UserManagementSystemException, GroupRetrievalException, UserRetrievalException;
	public List<UserModel> listUsersByGroup(String groupId) throws UserManagementSystemException, GroupRetrievalException, UserRetrievalException;
	public void close ();
}
