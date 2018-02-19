package gr.cite.regional.data.collection.dataaccess.services;

import gr.cite.regional.data.collection.dataaccess.daos.UserReferenceDao;
import gr.cite.regional.data.collection.dataaccess.entities.DataSubmission;
import gr.cite.regional.data.collection.dataaccess.entities.UserReference;
import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class UserReferenceServiceImpl implements UserReferenceService {
	private UserReferenceDao userReferenceDao;
	
	@Autowired
	public UserReferenceServiceImpl(UserReferenceDao userReferenceDao) {
		this.userReferenceDao = userReferenceDao;
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public UserReference addUserReference(UserReference userReference) throws ServiceException {
		userReference.setRegistrationDate(Date.from(Instant.now()));
		try {
			return this.userReferenceDao.create(userReference);
		} catch (Exception e) {
			throw new ServiceException("Persistence error on UserReference creation", e);
		}
	}

	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public UserReference createUserReferenceIfNotExists(UserReference userReference) throws ServiceException {
		UserReference user = this.getUserReferenceByLabel(userReference.getLabel());
		if (user == null)
			return this.addUserReference(userReference);
		else
			return this.updateUserReference(user);
	}

	private void setManagedReferences(UserReference userReference) {
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public UserReference updateUserReference(UserReference userReference) throws ServiceException {
		try {
			UserReference currentUserReference = this.userReferenceDao.read(userReference.getId());
			replaceModifiedFields(userReference, currentUserReference);
			
			return this.userReferenceDao.update(userReference);
		} catch (Exception e) {
			throw new ServiceException("Persistence error on UserReference [" + userReference.getId() + "] update", e);
		}
	}
	
	private void replaceModifiedFields(UserReference userReference, UserReference currentUserReference) {
		if (userReference.getLabel() != null) {
			currentUserReference.setLabel(userReference.getLabel());
		}
		if (userReference.getEmail() != null) {
			currentUserReference.setEmail(userReference.getEmail());
		}
		if (userReference.getFullName() != null) {
			currentUserReference.setFullName(userReference.getFullName());
		}
		if (userReference.getUri() != null) {
			currentUserReference.setUri(userReference.getUri());
		}
		if (userReference.getAnnotations() != null) {
			currentUserReference.setAnnotations(userReference.getAnnotations());
		}
		if (userReference.getAttributes() != null) {
			currentUserReference.setAttributes(userReference.getAttributes());
		}
	}
	
	@Override
	public UserReference getUserReference(Integer id) {
		return this.userReferenceDao.read(id);
	}

	@Override
	public UserReference getUserReferenceByLabel(String username) throws ServiceException {
		List<UserReference> users =  this.userReferenceDao.getUserReferenceByUsername(username);
		if(users.size() > 1)
			throw new ServiceException("More than one users with the same name appear to exist in the database");
		else if(users.size() == 1)
			return users.get(0);

		return null;
	}

	@Override
	public List<UserReference> getAllUserReferences() {
		return this.userReferenceDao.getAll();
	}
	
	@Override
	@Transactional(rollbackOn = ServiceException.class)
	public void deleteUserReference(Integer id) throws ServiceException {
		try {
			UserReference userReference = this.userReferenceDao.read(id);
			this.userReferenceDao.delete(userReference);
		} catch (Exception e) {
			throw new ServiceException("Persistence error on UserReference deletion", e);
		}
	}
}
