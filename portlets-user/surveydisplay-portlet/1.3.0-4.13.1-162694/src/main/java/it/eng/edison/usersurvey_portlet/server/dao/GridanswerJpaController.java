package it.eng.edison.usersurvey_portlet.server.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;

import it.eng.edison.usersurvey_portlet.client.model.SurveyAnswerModel;
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
    
    
    /**
     * Insert grid answer.
     *
     * @param survey the survey
     * @param surveyAnswerModel the survey answer model
     */
    public void insertGridAnswer(Survey survey, SurveyAnswerModel surveyAnswerModel){
    	List<String> gridAnswerListTemp = surveyAnswerModel.getGridAnswerList();
    	
    	Query query = null;
    	int queryExecuted;
        EntityManager em = getEntityManager();
        
        try {
	        em.getTransaction().begin();
	        for(int i=0; i<gridAnswerListTemp.size(); i++){
	       		query = em.createNativeQuery("INSERT INTO Gridanswer(id_survey, iduseranswer, numberquestion, questiontype, answer) VALUES(?,?,?,?,?)");

	       		query.setParameter(1, survey.getId());
	       		query.setParameter(2, surveyAnswerModel.getIdUserAnswer());
	       		query.setParameter(3, surveyAnswerModel.getNumberquestion());
	       		query.setParameter(4, surveyAnswerModel.getQuestiontype());
	       		query.setParameter(5, gridAnswerListTemp.get(i));
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
