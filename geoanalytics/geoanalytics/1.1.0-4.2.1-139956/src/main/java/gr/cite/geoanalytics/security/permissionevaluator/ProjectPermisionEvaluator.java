package gr.cite.geoanalytics.security.permissionevaluator;

import java.util.UUID;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.security.accesscontrol.dao.AccessControlDao;
import gr.cite.geoanalytics.dataaccess.entities.workflow.dao.WorkflowTaskDao;
import gr.cite.geoanalytics.security.SecurityContextAccessor;

@Service( value="projectPermissionEvaluator")
public class ProjectPermisionEvaluator {
	
	private static final Logger log = LoggerFactory.getLogger(ProjectPermisionEvaluator.class);
	
	private SecurityContextAccessor securityContextAccessor;
	private AccessControlDao accessControlDao;
	private WorkflowTaskDao wokrflowTaskDao;
	
	@Inject
	public void setWokrflowTaskDao(WorkflowTaskDao wokrflowTaskDao) {
		this.wokrflowTaskDao = wokrflowTaskDao;
	}
	
	@Inject
	public void setSecurityContextAccessor(SecurityContextAccessor securityContextAccessor) {
		this.securityContextAccessor = securityContextAccessor;
	}
	
	@Inject
	public void setAccessControlDao(AccessControlDao accessControlDao) {
		this.accessControlDao = accessControlDao;
	}
	
	public Boolean hasPermissionProject(Object projectId){
		Boolean hasPermission = false;
		try{
			Principal principal = securityContextAccessor.getPrincipal();
			hasPermission = this.accessControlDao.hasPrincipalPermissionForEntity(principal, UUID.fromString(projectId.toString()));
		}catch (Exception e) {
			log.error("Error during project permission check", e);
			hasPermission = false;
		}
		return hasPermission;
		
	}
	
	public Boolean hasPermissionTask(Object taskId){
		Boolean hasPermission = false;
		try{
			Principal principal = securityContextAccessor.getPrincipal();
			UUID projectIdFromTask = this.wokrflowTaskDao.read(UUID.fromString((String) taskId)).getWorkflow().getProject().getId();
			hasPermission = this.accessControlDao.hasPrincipalPermissionForEntity(principal, UUID.fromString(projectIdFromTask.toString()));
		}catch (Exception e) {
			log.error("Error during task of project permission check", e);
			hasPermission = false;
		}
		return hasPermission;
		
	}
	
	public Boolean hasPermissionDocument(Object documentId){
		Boolean hasPermission = false;
		try{
			Principal principal = securityContextAccessor.getPrincipal();
			UUID projectIdFromDocument = this.wokrflowTaskDao.read(UUID.fromString((String)documentId)).getWorkflow().getProject().getId();
			hasPermission = this.accessControlDao.hasPrincipalPermissionForEntity(principal, UUID.fromString(projectIdFromDocument.toString()));
		}catch (Exception e) {
			log.error("Error during document of project permission check", e);
			hasPermission = false;
		}
		return hasPermission;
		
	}
	
	public Boolean hasPermissionTaskDocument(Object taskId, Object documentId){
		
		Boolean hasPermissionTask = this.hasPermissionTask(taskId);
		Boolean hasPermissionDocument = this.hasPermissionDocument(documentId);
			
		return hasPermissionTask && hasPermissionDocument;
	}
}
