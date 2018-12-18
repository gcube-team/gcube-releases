package it.eng.edison.usersurvey_portlet.server.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.server.entity.Survey;


/**
 * The Class GridquestionJpaController.
 */
public class GridquestionJpaController implements Serializable {
	
    /**
     * Instantiates a new gridquestion jpa controller.
     *
     * @param emf the emf
     */
    public GridquestionJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
    /** The emf. */
    private EntityManagerFactory emf = null;

    /**
     * Gets the entity manager.
     *
     * @return the entity manager
     */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public void insertRowColumn(Survey survey, SurveyQuestionModel surveyQuestionModel){
    	List<String> rowGridListTemp = surveyQuestionModel.getRowGridList();
    	List<String> columnGridListTemp = surveyQuestionModel.getColumnGridList();

    	Query query = null;
    	int queryExecuted;
        EntityManager em = getEntityManager();
        
        try {
	        em.getTransaction().begin();
	        for(int i=0; i<rowGridListTemp.size(); i++){
	       		query = em.createNativeQuery("INSERT INTO Gridquestion(id_survey, numberquestion, questiontype, gridlabel, roworcolumnlabel) VALUES(?,?,?,?,?)");
	       		query.setParameter(1, survey.getId());
	       		query.setParameter(2, surveyQuestionModel.getNumberquestion());
	       		query.setParameter(3, surveyQuestionModel.getQuestiontype());
	       		query.setParameter(4, rowGridListTemp.get(i));
	       		query.setParameter(5, "Row");
	       		queryExecuted = query.executeUpdate();
	        }
	        
	        
	        for(int i=0; i<columnGridListTemp.size(); i++){
	       		query = em.createNativeQuery("INSERT INTO Gridquestion(id_survey, numberquestion, questiontype, gridlabel, roworcolumnlabel) VALUES(?,?,?,?,?)");
	       		query.setParameter(1, survey.getId());
	       		query.setParameter(2, surveyQuestionModel.getNumberquestion());
	       		query.setParameter(3, surveyQuestionModel.getQuestiontype());
	       		query.setParameter(4, columnGridListTemp.get(i));
	       		query.setParameter(5, "Column");
	       		queryExecuted = query.executeUpdate();
	        }
	        
    	} catch (Exception e) {
    		e.printStackTrace();
        } finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
        }
    }
    
    
    public List<String> findSurveyQuestionRowColumnGridByIdSurveyAndNumberQuestion(int idSurvey, int numberQuestion, String roworcolumnlabel) {
    	Query query = null;
    	List<String> gridQuestionList = null;
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		query = em.createQuery("SELECT g.gridlabel FROM Gridquestion g WHERE g.idSurvey = :idSurvey AND g.numberquestion = :numberQuestion AND g.roworcolumnlabel = :roworcolumnlabel");
    		query.setParameter("idSurvey", idSurvey);
    		query.setParameter("numberQuestion", numberQuestion);
    		query.setParameter("roworcolumnlabel", roworcolumnlabel);
    		gridQuestionList = query.getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
    	}

    	return gridQuestionList;
    }
    
    public int deleteQuestions(int idSurveyQuestion){
    	int deletedCount = 0;
    	Query query = null;
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		query = em.createQuery("DELETE FROM Gridquestion where id_survey = :idSurveyQuestion");
    		deletedCount = query.setParameter("idSurveyQuestion", idSurveyQuestion).executeUpdate();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
    	}
    	return deletedCount;
    }
    
}
