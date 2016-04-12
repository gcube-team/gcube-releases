package it.eng.rdlab.um.user.service;

import it.eng.rdlab.um.exceptions.UserManagementSystemException;
import it.eng.rdlab.um.exceptions.UserRetrievalException;
import it.eng.rdlab.um.user.beans.UserModel;

import java.util.List;


/**
 * This interface defines the class that manages the users.
 * 
 * @author Ciro Formisano
 *
 */
public interface UserManager 
{
	public boolean createUser(UserModel usermodel) throws UserManagementSystemException;
	public boolean deleteUser(String userId) throws UserManagementSystemException, UserRetrievalException;
	public boolean updateUser(UserModel user) throws UserManagementSystemException, UserRetrievalException;
	public UserModel getUser(String userId) throws UserManagementSystemException, UserRetrievalException ;
	public List<UserModel> listUsers() throws UserManagementSystemException, UserRetrievalException;
	public List<UserModel> listUsers (UserModel filter)  throws UserManagementSystemException, UserRetrievalException;
	public void close ();
}
