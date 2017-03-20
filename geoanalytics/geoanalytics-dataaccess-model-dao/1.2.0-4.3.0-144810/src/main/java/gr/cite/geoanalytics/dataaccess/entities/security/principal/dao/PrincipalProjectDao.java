package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalProject;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;

public interface PrincipalProjectDao  extends Dao<PrincipalProject, UUID> {
	public void deleteByProjectId(UUID id);
	public void deleteByPrincipalAndProjectId(Principal principal, UUID id);
	
	public List<Project> selectProjectByParticipant(Principal participant, String tenant);
	public List<Project> selectProjectByParticipantInGroup(Principal participant, String tenant);
	
	public PrincipalProject getByPrincipalAndProjectId(Principal principal, UUID projectId);
	
	public List<PrincipalProject> getByPrincipalUUIDsAndProjectId(List<UUID> principalIDs, UUID projectID);
}
