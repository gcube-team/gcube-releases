package gr.cite.geoanalytics.security.permissionevaluator;

import java.io.Serializable;
import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import gr.cite.gaap.datatransferobjects.ProjectInfoMessenger;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;
import gr.cite.geoanalytics.dataaccess.entities.security.accesscontrol.dao.AccessControlDao;
import gr.cite.geoanalytics.security.SecurityContextAccessor;

public class GeoanalyticsPermissionEvaluator implements PermissionEvaluator{
	
	private static final Logger log = LoggerFactory.getLogger(GeoanalyticsPermissionEvaluator.class);
	
	private AccessControlDao accessControlDao;
	private SecurityContextAccessor securityContextAccessor;
	
	@Inject
	public void setSecurityContextAccessor(SecurityContextAccessor securityContextAccessor) {
		this.securityContextAccessor = securityContextAccessor;
	}
	
	@Inject
	public void setAccessControlDao(AccessControlDao accessControlDao) {
		this.accessControlDao = accessControlDao;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		
		Boolean hasPermission = false;
		try{
			Principal principal = securityContextAccessor.getPrincipal();
			if ("access".equals(permission.toString()) && targetDomainObject instanceof Project) {
				Project project = ((Project)targetDomainObject);
				hasPermission = this.accessControlDao.hasPrincipalPermissionForEntity(principal, project.getId());
		    }else if("access".equals(permission.toString()) && targetDomainObject instanceof ProjectInfoMessenger){
		    	ProjectInfoMessenger projectInfoMessenger = ((ProjectInfoMessenger)targetDomainObject);
		    	hasPermission = (projectInfoMessenger.getProjectMessenger() == null) ? false : this.accessControlDao.hasPrincipalPermissionForEntity(principal,
						UUID.fromString(projectInfoMessenger.getProjectMessenger().getId()));
				
		    }else if("access".equals(permission.toString()) && targetDomainObject instanceof Project){
		    	Project project = ((Project)targetDomainObject);
		    	hasPermission = (project == null) ? false : this.accessControlDao.hasPrincipalPermissionForEntity(principal, project.getId());
		    }else if("access".equals(permission.toString()) && targetDomainObject instanceof String){
		    	String projectId = targetDomainObject.toString();
		    	hasPermission = this.accessControlDao.hasPrincipalPermissionForEntity(principal,UUID.fromString(projectId));
		    }
		}catch (Exception e) {
	    	log.error("Error during user entity authrization link", e);
			e.printStackTrace();
			hasPermission = false;
		}
		return hasPermission;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
			Object permission) {
		throw new UnsupportedOperationException();
	}

}