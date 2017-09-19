package it.eng.edison.usersurvey_portlet.server.dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


import it.eng.edison.usersurvey_portlet.server.entity.Survey;
import it.eng.edison.usersurvey_portlet.server.entity.Surveyquestion;
import it.eng.edison.usersurvey_portlet.server.dao.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * The Class SurveyquestionJpaController.
 *
 */
public class SurveyquestionJpaController implements Serializable {

    /**
     * Instantiates a new surveyquestion jpa controller.
     *
     * @param emf the emf
     */
    public SurveyquestionJpaController(EntityManagerFactory emf) {
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
     * Creates the.
     *
     * @param surveyquestion the surveyquestion
     */
    public void create(Surveyquestion surveyquestion) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Survey idSurvey = surveyquestion.getIdSurvey();
            if (idSurvey != null) {
                idSurvey = em.getReference(idSurvey.getClass(), idSurvey.getId());
                surveyquestion.setIdSurvey(idSurvey);
            }
            em.persist(surveyquestion);
            if (idSurvey != null) {
                idSurvey.getSurveyquestionCollection().add(surveyquestion);
                idSurvey = em.merge(idSurvey);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Edits the.
     *
     * @param surveyquestion the surveyquestion
     * @throws NonexistentEntityException the nonexistent entity exception
     * @throws Exception the exception
     */
    public void edit(Surveyquestion surveyquestion) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Surveyquestion persistentSurveyquestion = em.find(Surveyquestion.class, surveyquestion.getId());
            Survey idSurveyOld = persistentSurveyquestion.getIdSurvey();
            Survey idSurveyNew = surveyquestion.getIdSurvey();
            if (idSurveyNew != null) {
                idSurveyNew = em.getReference(idSurveyNew.getClass(), idSurveyNew.getId());
                surveyquestion.setIdSurvey(idSurveyNew);
            }
            surveyquestion = em.merge(surveyquestion);
            if (idSurveyOld != null && !idSurveyOld.equals(idSurveyNew)) {
                idSurveyOld.getSurveyquestionCollection().remove(surveyquestion);
                idSurveyOld = em.merge(idSurveyOld);
            }
            if (idSurveyNew != null && !idSurveyNew.equals(idSurveyOld)) {
                idSurveyNew.getSurveyquestionCollection().add(surveyquestion);
                idSurveyNew = em.merge(idSurveyNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = surveyquestion.getId();
                if (findSurveyquestion(id) == null) {
                    throw new NonexistentEntityException("The surveyquestion with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Destroy.
     *
     * @param id the id
     * @throws NonexistentEntityException the nonexistent entity exception
     */
    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Surveyquestion surveyquestion;
            try {
                surveyquestion = em.getReference(Surveyquestion.class, id);
                surveyquestion.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The surveyquestion with id " + id + " no longer exists.", enfe);
            }
            Survey idSurvey = surveyquestion.getIdSurvey();
            if (idSurvey != null) {
                idSurvey.getSurveyquestionCollection().remove(surveyquestion);
                idSurvey = em.merge(idSurvey);
            }
            em.remove(surveyquestion);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Find surveyquestion entities.
     *
     * @return the list
     */
    public List<Surveyquestion> findSurveyquestionEntities() {
        return findSurveyquestionEntities(true, -1, -1);
    }

    /**
     * Find surveyquestion entities.
     *
     * @param maxResults the max results
     * @param firstResult the first result
     * @return the list
     */
    public List<Surveyquestion> findSurveyquestionEntities(int maxResults, int firstResult) {
        return findSurveyquestionEntities(false, maxResults, firstResult);
    }

    /**
     * Find surveyquestion entities.
     *
     * @param all the all
     * @param maxResults the max results
     * @param firstResult the first result
     * @return the list
     */
    private List<Surveyquestion> findSurveyquestionEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Surveyquestion.class));
            
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
            em = null;
        }
    }

    /**
     * Find surveyquestion.
     *
     * @param id the id
     * @return the surveyquestion
     */
    public Surveyquestion findSurveyquestion(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Surveyquestion.class, id);
        } finally {
            em.close();
            em = null;
        }
    }

    /**
     * Gets the surveyquestion count.
     *
     * @return the surveyquestion count
     */
    public int getSurveyquestionCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Surveyquestion> rt = cq.from(Surveyquestion.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
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
    		query = em.createQuery("DELETE FROM Surveyquestion where id_survey = :idSurveyQuestion");
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
     * Find survey question by id survey.
     *
     * @param idSurvey the id survey
     * @return the list
     */
    public List<Surveyquestion> findSurveyQuestionByIdSurvey(int idSurvey) {
    	Query query = null;
    	List<Surveyquestion> surveyQuestionList = null;
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		query = em.createQuery("SELECT s FROM Surveyquestion s WHERE s.idSurvey.id = :idSurvey");
    		query.setParameter("idSurvey", idSurvey);
    		surveyQuestionList = query.getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
    	}
    	return surveyQuestionList;
    }
    
}
