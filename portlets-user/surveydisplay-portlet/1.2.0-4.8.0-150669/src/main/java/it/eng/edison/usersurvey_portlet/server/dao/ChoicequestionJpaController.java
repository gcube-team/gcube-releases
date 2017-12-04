package it.eng.edison.usersurvey_portlet.server.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import it.eng.edison.usersurvey_portlet.client.model.SurveyQuestionModel;
import it.eng.edison.usersurvey_portlet.server.entity.Survey;


/**
 * The Class ChoicequestionJpaController.
 */
public class ChoicequestionJpaController implements Serializable {
	
    /**
     * Instantiates a new choicequestion jpa controller.
     *
     * @param emf the emf
     */
    public ChoicequestionJpaController(EntityManagerFactory emf) {
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
     * Insert multiple choice.
     *
     * @param survey the survey
     * @param surveyQuestionModel the survey question model
     */
    public void insertMultipleChoice(Survey survey, SurveyQuestionModel surveyQuestionModel){
    	List<String> multipleChoiceListTemp = surveyQuestionModel.getMultipleChoiceList();
    	
    	Query query = null;
    	int queryExecuted;
        EntityManager em = getEntityManager();
        
        try {
	        em.getTransaction().begin();
	        for(int i=0; i<multipleChoiceListTemp.size(); i++){
	       		query = em.createNativeQuery("INSERT INTO Choicequestion(id_survey, numberquestion, questiontype, choice) VALUES(?,?,?,?)");
	       		query.setParameter(1, survey.getId());
	       		query.setParameter(2, surveyQuestionModel.getNumberquestion());
	       		query.setParameter(3, surveyQuestionModel.getQuestiontype());
	       		query.setParameter(4, multipleChoiceListTemp.get(i));
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
    		query = em.createQuery("DELETE FROM Choicequestion where id_survey = :idSurveyQuestion");
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
    
    /**
     * Find survey question choice by id survey and number question.
     *
     * @param idSurvey the id survey
     * @param numberQuestion the number question
     * @return the list
     */
    public List<String> findSurveyQuestionChoiceByIdSurveyAndNumberQuestion(int idSurvey, int numberQuestion) {
    	Query query = null;
    	List<String> choiceQuestionList = null;
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		query = em.createQuery("SELECT c.choice FROM Choicequestion c WHERE c.idSurvey = :idSurvey AND c.numberquestion = :numberQuestion");
    		query.setParameter("idSurvey", idSurvey);
    		query.setParameter("numberQuestion", numberQuestion);
    		choiceQuestionList = query.getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
    	}

    	return choiceQuestionList;
    }
}
