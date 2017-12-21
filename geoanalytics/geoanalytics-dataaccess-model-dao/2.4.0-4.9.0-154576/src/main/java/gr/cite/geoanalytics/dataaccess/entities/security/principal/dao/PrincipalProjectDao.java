package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import gr.cite.geoanalytics.dataaccess.dao.Dao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalProject;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;

public interface PrincipalProjectDao  extends Dao<PrincipalProject, UUID> {
	public void deleteByProjectId(UUID id);
	public void deleteByPrincipalAndProjectId(Principal principal, UUID id);
	public void deleteByPrincipalProjectIDs(Collection<UUID> principalProjectUUIDs);
	
	public List<Project> selectProjectByParticipant(Principal participant);
	public List<Project> selectProjectByParticipantInGroup(Principal participant);
	
	public PrincipalProject getByPrincipalAndProjectId(Principal principal, UUID projectId);
	
	public List<PrincipalProject> getByPrincipalUUIDsAndProjectId(List<UUID> principalIDs, UUID projectID);
}
