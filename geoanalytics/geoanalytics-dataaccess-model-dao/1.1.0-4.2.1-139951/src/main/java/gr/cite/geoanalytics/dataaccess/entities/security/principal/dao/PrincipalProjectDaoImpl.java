package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Repository;

import gr.cite.geoanalytics.dataaccess.dao.JpaDao;
import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.dataaccess.entities.project.Project;

@Repository
public class PrincipalProjectDaoImpl extends JpaDao<Principal, UUID> implements PrincipalProjectDao {

	@Override
	public Principal loadDetails(Principal t) {
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
	public List<Project> selectProjectByParticipantInGroup(Principal participant, String tenant) {
		List<Project> result = new ArrayList<Project>();
		StringBuilder queryB = new StringBuilder("SELECT pp.project FROM PrincipalProject pp inner join pp.participant.groupsPrincipal gp");
		queryB.append(" WHERE gp.member=:participant");
		queryB.append(" AND gp.member.tenant.name = :tenant");
		
		try{
			TypedQuery<Project> query = entityManager.createQuery(queryB.toString(), Project.class);
			query.setParameter("participant", participant);
			query.setParameter("tenant", tenant);
			
			result = query.getResultList();
			
			initializePrincipalProject(result);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}


	@Override
	public List<Project> selectProjectByParticipant(Principal participant, String tenant) {
		List<Project> result = new ArrayList<Project>();
		StringBuilder queryB = new StringBuilder("SELECT pp.project FROM PrincipalProject pp");
		queryB.append(" WHERE pp.participant=:participant");
		queryB.append(" AND pp.participant.tenant.name = :tenant");
		
		try{
			TypedQuery<Project> query = entityManager.createQuery(queryB.toString(), Project.class);
			query.setParameter("participant", participant);
			query.setParameter("tenant", tenant);
			
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
}