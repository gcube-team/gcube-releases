package gr.cite.geoanalytics.manager;

import java.util.UUID;

import javax.inject.Inject;

import gr.cite.geoanalytics.dataaccess.entities.principal.AccessControl;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.security.accesscontrol.dao.AccessControlDao;

public class AccessControlManager {
	
	private AccessControlDao accessControlDao;
	
	@Inject
	public void setAccessControlDao(AccessControlDao accessControlDao) {
		this.accessControlDao = accessControlDao;
	}
	
	public AccessControl findByPrincipalAndEntity(Principal principal, UUID entityId){
		return this.accessControlDao.findByPrincipalAndEntity(principal, entityId);
	}
}
