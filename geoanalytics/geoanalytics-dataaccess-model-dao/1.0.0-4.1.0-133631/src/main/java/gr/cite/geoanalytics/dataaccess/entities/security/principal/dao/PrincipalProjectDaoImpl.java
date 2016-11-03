package gr.cite.geoanalytics.dataaccess.entities.security.principal.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

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
	public List<Project> selectProjectByParticipant(Principal participant) {
		List<Project> result = new ArrayList<Project>();
		try{
			TypedQuery<Project> query = entityManager.createQuery("select pp.project from PrincipalProject pp where pp.participant=:participant", Project.class);
			query.setParameter("participant", participant);
			
			result = query.getResultList();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	
}