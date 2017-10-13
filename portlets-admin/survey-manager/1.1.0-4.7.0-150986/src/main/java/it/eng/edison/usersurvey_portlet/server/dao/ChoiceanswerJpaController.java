package it.eng.edison.usersurvey_portlet.server.dao;

import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.server.dao.exceptions.NonexistentEntityException;
import it.eng.edison.usersurvey_portlet.server.entity.Choicequestion;
import it.eng.edison.usersurvey_portlet.server.entity.Survey;
import it.eng.edison.usersurvey_portlet.server.entity.Surveyquestion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;


/**
 * The Class ChoiceanswerJpaController.
 */
public class ChoiceanswerJpaController implements Serializable {
	
    /**
     * Instantiates a new choiceanswer jpa controller.
     *
     * @param emf the emf
     */
    public ChoiceanswerJpaController(EntityManagerFactory emf) {
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
    
    
    
    /**
     * Find survey choice answer by id survey and number question.
     *
     * @param idSurvey the id survey
     * @param idUserAnswer the id user answer
     * @param numberQuestion the number question
     * @return the list
     */
    public List<String> findSurveyChoiceAnswerByIdSurveyAndNumberQuestion(int idSurvey, int idUserAnswer, int numberQuestion) {
    	Query query = null;
    	List<String> choiceAnswerList = null;
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		query = em.createQuery("SELECT c.choice FROM Choiceanswer c WHERE c.idSurvey = :idSurvey AND c.idUserAnswer = :idUserAnswer AND c.numberquestion = :numberQuestion");
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
    
    /**
     * Delete questions.
     *
     * @param idSurveyQuestion the id survey question
     * @return the int
     */
    public int deleteQuestions(int idSurveyQuestion){
    	int deletedCount = 0;
    	Query query = null;
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		query = em.createQuery("DELETE FROM Choiceanswer where id_survey = :idSurveyQuestion");
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
