package it.eng.edison.usersurvey_portlet.server.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import it.eng.edison.usersurvey_portlet.client.model.SurveyAnswerModel;
import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.server.entity.Survey;


/**
 * The Class GridquestionJpaController.
 */
public class GridanswerJpaController implements Serializable {
	
    /**
     * Instantiates a new gridanswer jpa controller.
     *
     * @param emf the emf
     */
    public GridanswerJpaController(EntityManagerFactory emf) {
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
    
    
    public int deleteQuestions(int idSurveyQuestion){
    	int deletedCount = 0;
    	Query query = null;
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		query = em.createQuery("DELETE FROM Gridanswer where id_survey = :idSurveyQuestion");
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

    public List<String> findGridAnswersByIdSurveyAndNumberQuestion(int idSurvey, int idUserAnswer, int numberQuestion) {
    	Query query = null;
    	List<String> choiceAnswerList = null;
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		query = em.createQuery("SELECT g.answer FROM Gridanswer g WHERE g.idSurvey = :idSurvey AND g.idUserAnswer = :idUserAnswer AND g.numberquestion = :numberQuestion");
    		query.setParameter("idSurvey", idSurvey);
    		query.setParameter("idUserAnswer", idUserAnswer);
    		query.setParameter("numberQuestion", numberQuestion);
    		choiceAnswerList = query.getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
    	}

    	return choiceAnswerList;
    }

}
