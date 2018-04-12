package it.eng.edison.usersurvey_portlet.server.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import it.eng.edison.usersurvey_portlet.client.model.SurveyAnswerModel;
import it.eng.edison.usersurvey_portlet.server.entity.Survey;


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
    		query = em.createQuery("SELECT c.choice FROM Choiceanswer c WHERE c.idSurvey = :idSurvey AND c.numberquestion = :numberQuestion");
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
    
    /**
     * Insert multiple choice.
     *
     * @param survey the survey
     * @param surveyAnswerModel the survey answer model
     */
    public void insertMultipleChoice(Survey survey, SurveyAnswerModel surveyAnswerModel){
    	List<String> multipleChoiceListTemp = surveyAnswerModel.getMultipleChoiceList();
    	
    	Query query = null;
    	int queryExecuted;
        EntityManager em = getEntityManager();
        
        try {
	        em.getTransaction().begin();
	        for(int i=0; i<multipleChoiceListTemp.size(); i++){
	       		query = em.createNativeQuery("INSERT INTO Choiceanswer(id_survey, iduseranswer, questiontype, numberquestion, choice, field1) VALUES(?,?,?,?,?,?)");
	       		query.setParameter(1, survey.getId());
	       		query.setParameter(2, surveyAnswerModel.getIdUserAnswer());
	       		query.setParameter(3, surveyAnswerModel.getQuestiontype());
	       		query.setParameter(4, surveyAnswerModel.getNumberquestion());
	       		query.setParameter(5, multipleChoiceListTemp.get(i));
	       		query.setParameter(6, surveyAnswerModel.getAnswer1());
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
}
