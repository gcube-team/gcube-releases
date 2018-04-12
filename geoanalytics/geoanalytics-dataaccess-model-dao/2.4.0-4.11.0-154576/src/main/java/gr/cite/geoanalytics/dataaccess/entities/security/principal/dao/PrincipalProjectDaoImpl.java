package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.principal.PrincipalProject;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;

@Repository
public class PrincipalProjectDaoImpl extends JpaDao<PrincipalProject, UUID> implements PrincipalProjectDao {

	@Override
	public PrincipalProject loadDetails(PrincipalProject t) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteByProjectId(UUID id) {
		Query query = entityManager.createQuery("delete PrincipalProject pp where pp.project.id = :id");
		query.setParameter("id", id);
		query.executeUpdate();
	}

	@Override
	public void deleteByPrincipalAndProjectId(Principal principal, UUID id) {
		Query query = entityManager.createQuery("DELETE PrincipalProject pp WHERE pp.project.id = :id AND pp.participant = :participant");
		query.setParameter("id", id);
		query.setParameter("participant", principal);
		query.executeUpdate();
		
	}

	@Override
	public void deleteByPrincipalProjectIDs(Collection<UUID> principalProjectUUIDs) {
		Query query = entityManager.createQuery("delete PrincipalProject pp where pp.participant.id IN :principalProjectUUIDs");
		query.setParameter("principalProjectUUIDs", principalProjectUUIDs);
		query.executeUpdate();
	}

	@Override
	public List<Project> selectProjectByParticipantInGroup(Principal participant) {
		List<Project> result = new ArrayList<Project>();
		StringBuilder queryB = new StringBuilder("SELECT pp.project FROM PrincipalProject pp inner join pp.participant.groupsPrincipal gp");
		queryB.append(" WHERE gp.member=:participant");
//		queryB.append(" AND gp.member.tenant.name = :tenant");
		
		try{
			TypedQuery<Project> query = entityManager.createQuery(queryB.toString(), Project.class);
			query.setParameter("participant", participant);
//			query.setParameter("tenant", tenant);
			
			result = query.getResultList();
			
			initializePrincipalProject(result);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}


	@Override
	public List<Project> selectProjectByParticipant(Principal participant) {
		List<Project> result = new ArrayList<Project>();
		StringBuilder queryB = new StringBuilder("SELECT pp.project FROM PrincipalProject pp");
		queryB.append(" WHERE pp.participant=:participant");
//		queryB.append(" AND pp.participant.tenant.name = :tenant");
		
		try{
			TypedQuery<Project> query = entityManager.createQuery(queryB.toString(), Project.class);
			query.setParameter("participant", participant);
//			query.setParameter("tenant", tenant);
			
			result = query.getResultList();
			
			initializePrincipalProject(result);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void initializePrincipalProject(List<Project> projects){
		for(Project project : projects){
			Hibernate.initialize(project.getPrincipalProject());			
		}
	}

	@Override
	public PrincipalProject getByPrincipalAndProjectId(Principal principal, UUID projectId) {

		PrincipalProject result = new PrincipalProject();
		StringBuilder queryB = new StringBuilder("FROM PrincipalProject pp");
		queryB.append(" WHERE pp.participant=:participant");
		queryB.append(" AND pp.project.id = :projectId");
		
		try{
			TypedQuery<PrincipalProject> query = entityManager.createQuery(queryB.toString(), PrincipalProject.class);
			query.setParameter("participant", principal);
			query.setParameter("projectId", projectId);
			
			result = query.getSingleResult();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	
	}

	@Override
	public List<PrincipalProject> getByPrincipalUUIDsAndProjectId(List<UUID> principalIDs, UUID projectID) {
		List<PrincipalProject> ppList = new ArrayList<PrincipalProject>();
		if(principalIDs.isEmpty()){
			return ppList;
		}
		
		StringBuilder queryStr = new StringBuilder("FROM PrincipalProject pp");
		queryStr.append(" WHERE pp.participant.id IN :principalIDs");
		queryStr.append(" AND pp.project.id = :projectID");
		
		TypedQuery<PrincipalProject> query = entityManager.createQuery(queryStr.toString(), PrincipalProject.class);
		query.setParameter("principalIDs", principalIDs);
		query.setParameter("projectID", projectID);

		try{
			ppList = query.getResultList(); 
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return ppList;
	}
}