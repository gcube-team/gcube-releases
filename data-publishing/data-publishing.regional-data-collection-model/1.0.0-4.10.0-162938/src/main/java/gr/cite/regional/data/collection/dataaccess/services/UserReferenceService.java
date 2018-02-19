package gr.cite.regional.data.collection.dataaccess.services;

import gr.cite.regional.data.collection.dataaccess.entities.UserReference;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;

import java.util.List;

public interface UserReferenceService {
	public UserReference addUserReference(UserReference userReference) throws ServiceException;
	public UserReference createUserReferenceIfNotExists(UserReference userReference) throws ServiceException;
	public UserReference updateUserReference(UserReference userReference) throws ServiceException;
	public UserReference getUserReference(Integer id);
	public UserReference getUserReferenceByLabel(String username) throws ServiceException;
	public List<UserReference> getAllUserReferences();
	public void deleteUserReference(Integer id) throws ServiceException;
}
