package it.eng.edison.usersurvey_portlet.server.dao;

import it.eng.edison.usersurvey_portlet.server.entity.Survey;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import it.eng.edison.usersurvey_portlet.server.entity.Surveyuseranswer;
import java.util.ArrayList;
import java.util.Collection;
import it.eng.edison.usersurvey_portlet.server.entity.Surveyquestion;
import it.eng.edison.usersurvey_portlet.server.dao.exceptions.IllegalOrphanException;
import it.eng.edison.usersurvey_portlet.server.dao.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * The Class SurveyJpaController.
 *
 */
public class SurveyJpaController implements Serializable {

    /**
     * Instantiates a new survey jpa controller.
     *
     * @param emf the emf
     */
    public SurveyJpaController(EntityManagerFactory emf) {
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
     * @param survey the survey
     */
    public void create(Survey survey) {
        if (survey.getSurveyuseranswerCollection() == null) {
            survey.setSurveyuseranswerCollection(new ArrayList<Surveyuseranswer>());
        }
        if (survey.getSurveyquestionCollection() == null) {
            survey.setSurveyquestionCollection(new ArrayList<Surveyquestion>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Surveyuseranswer> attachedSurveyuseranswerCollection = new ArrayList<Surveyuseranswer>();
            for (Surveyuseranswer surveyuseranswerCollectionSurveyuseranswerToAttach : survey.getSurveyuseranswerCollection()) {
                surveyuseranswerCollectionSurveyuseranswerToAttach = em.getReference(surveyuseranswerCollectionSurveyuseranswerToAttach.getClass(), surveyuseranswerCollectionSurveyuseranswerToAttach.getId());
                attachedSurveyuseranswerCollection.add(surveyuseranswerCollectionSurveyuseranswerToAttach);
            }
            survey.setSurveyuseranswerCollection(attachedSurveyuseranswerCollection);
            Collection<Surveyquestion> attachedSurveyquestionCollection = new ArrayList<Surveyquestion>();
            for (Surveyquestion surveyquestionCollectionSurveyquestionToAttach : survey.getSurveyquestionCollection()) {
                surveyquestionCollectionSurveyquestionToAttach = em.getReference(surveyquestionCollectionSurveyquestionToAttach.getClass(), surveyquestionCollectionSurveyquestionToAttach.getId());
                attachedSurveyquestionCollection.add(surveyquestionCollectionSurveyquestionToAttach);
            }
            survey.setSurveyquestionCollection(attachedSurveyquestionCollection);
            em.persist(survey);
            for (Surveyuseranswer surveyuseranswerCollectionSurveyuseranswer : survey.getSurveyuseranswerCollection()) {
                Survey oldIdSurveyOfSurveyuseranswerCollectionSurveyuseranswer = surveyuseranswerCollectionSurveyuseranswer.getIdSurvey();
                surveyuseranswerCollectionSurveyuseranswer.setIdSurvey(survey);
                surveyuseranswerCollectionSurveyuseranswer = em.merge(surveyuseranswerCollectionSurveyuseranswer);
                if (oldIdSurveyOfSurveyuseranswerCollectionSurveyuseranswer != null) {
                    oldIdSurveyOfSurveyuseranswerCollectionSurveyuseranswer.getSurveyuseranswerCollection().remove(surveyuseranswerCollectionSurveyuseranswer);
                    oldIdSurveyOfSurveyuseranswerCollectionSurveyuseranswer = em.merge(oldIdSurveyOfSurveyuseranswerCollectionSurveyuseranswer);
                }
            }
            for (Surveyquestion surveyquestionCollectionSurveyquestion : survey.getSurveyquestionCollection()) {
                Survey oldIdSurveyOfSurveyquestionCollectionSurveyquestion = surveyquestionCollectionSurveyquestion.getIdSurvey();
                surveyquestionCollectionSurveyquestion.setIdSurvey(survey);
                surveyquestionCollectionSurveyquestion = em.merge(surveyquestionCollectionSurveyquestion);
                if (oldIdSurveyOfSurveyquestionCollectionSurveyquestion != null) {
                    oldIdSurveyOfSurveyquestionCollectionSurveyquestion.getSurveyquestionCollection().remove(surveyquestionCollectionSurveyquestion);
                    oldIdSurveyOfSurveyquestionCollectionSurveyquestion = em.merge(oldIdSurveyOfSurveyquestionCollectionSurveyquestion);
                }
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
     * @param survey the survey
     * @throws IllegalOrphanException the illegal orphan exception
     * @throws NonexistentEntityException the nonexistent entity exception
     * @throws Exception the exception
     */
    public void edit(Survey survey) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Survey persistentSurvey = em.find(Survey.class, survey.getId());
            Collection<Surveyuseranswer> surveyuseranswerCollectionOld = persistentSurvey.getSurveyuseranswerCollection();
            Collection<Surveyuseranswer> surveyuseranswerCollectionNew = survey.getSurveyuseranswerCollection();
            Collection<Surveyquestion> surveyquestionCollectionOld = persistentSurvey.getSurveyquestionCollection();
            Collection<Surveyquestion> surveyquestionCollectionNew = survey.getSurveyquestionCollection();
            List<String> illegalOrphanMessages = null;
            for (Surveyuseranswer surveyuseranswerCollectionOldSurveyuseranswer : surveyuseranswerCollectionOld) {
                if (!surveyuseranswerCollectionNew.contains(surveyuseranswerCollectionOldSurveyuseranswer)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Surveyuseranswer " + surveyuseranswerCollectionOldSurveyuseranswer + " since its idSurvey field is not nullable.");
                }
            }
            for (Surveyquestion surveyquestionCollectionOldSurveyquestion : surveyquestionCollectionOld) {
                if (!surveyquestionCollectionNew.contains(surveyquestionCollectionOldSurveyquestion)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Surveyquestion " + surveyquestionCollectionOldSurveyquestion + " since its idSurvey field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Surveyuseranswer> attachedSurveyuseranswerCollectionNew = new ArrayList<Surveyuseranswer>();
            for (Surveyuseranswer surveyuseranswerCollectionNewSurveyuseranswerToAttach : surveyuseranswerCollectionNew) {
                surveyuseranswerCollectionNewSurveyuseranswerToAttach = em.getReference(surveyuseranswerCollectionNewSurveyuseranswerToAttach.getClass(), surveyuseranswerCollectionNewSurveyuseranswerToAttach.getId());
                attachedSurveyuseranswerCollectionNew.add(surveyuseranswerCollectionNewSurveyuseranswerToAttach);
            }
            surveyuseranswerCollectionNew = attachedSurveyuseranswerCollectionNew;
            survey.setSurveyuseranswerCollection(surveyuseranswerCollectionNew);
            Collection<Surveyquestion> attachedSurveyquestionCollectionNew = new ArrayList<Surveyquestion>();
            for (Surveyquestion surveyquestionCollectionNewSurveyquestionToAttach : surveyquestionCollectionNew) {
                surveyquestionCollectionNewSurveyquestionToAttach = em.getReference(surveyquestionCollectionNewSurveyquestionToAttach.getClass(), surveyquestionCollectionNewSurveyquestionToAttach.getId());
                attachedSurveyquestionCollectionNew.add(surveyquestionCollectionNewSurveyquestionToAttach);
            }
            surveyquestionCollectionNew = attachedSurveyquestionCollectionNew;
            survey.setSurveyquestionCollection(surveyquestionCollectionNew);
            survey = em.merge(survey);
            for (Surveyuseranswer surveyuseranswerCollectionNewSurveyuseranswer : surveyuseranswerCollectionNew) {
                if (!surveyuseranswerCollectionOld.contains(surveyuseranswerCollectionNewSurveyuseranswer)) {
                    Survey oldIdSurveyOfSurveyuseranswerCollectionNewSurveyuseranswer = surveyuseranswerCollectionNewSurveyuseranswer.getIdSurvey();
                    surveyuseranswerCollectionNewSurveyuseranswer.setIdSurvey(survey);
                    surveyuseranswerCollectionNewSurveyuseranswer = em.merge(surveyuseranswerCollectionNewSurveyuseranswer);
                    if (oldIdSurveyOfSurveyuseranswerCollectionNewSurveyuseranswer != null && !oldIdSurveyOfSurveyuseranswerCollectionNewSurveyuseranswer.equals(survey)) {
                        oldIdSurveyOfSurveyuseranswerCollectionNewSurveyuseranswer.getSurveyuseranswerCollection().remove(surveyuseranswerCollectionNewSurveyuseranswer);
                        oldIdSurveyOfSurveyuseranswerCollectionNewSurveyuseranswer = em.merge(oldIdSurveyOfSurveyuseranswerCollectionNewSurveyuseranswer);
                    }
                }
            }
            for (Surveyquestion surveyquestionCollectionNewSurveyquestion : surveyquestionCollectionNew) {
                if (!surveyquestionCollectionOld.contains(surveyquestionCollectionNewSurveyquestion)) {
                    Survey oldIdSurveyOfSurveyquestionCollectionNewSurveyquestion = surveyquestionCollectionNewSurveyquestion.getIdSurvey();
                    surveyquestionCollectionNewSurveyquestion.setIdSurvey(survey);
                    surveyquestionCollectionNewSurveyquestion = em.merge(surveyquestionCollectionNewSurveyquestion);
                    if (oldIdSurveyOfSurveyquestionCollectionNewSurveyquestion != null && !oldIdSurveyOfSurveyquestionCollectionNewSurveyquestion.equals(survey)) {
                        oldIdSurveyOfSurveyquestionCollectionNewSurveyquestion.getSurveyquestionCollection().remove(surveyquestionCollectionNewSurveyquestion);
                        oldIdSurveyOfSurveyquestionCollectionNewSurveyquestion = em.merge(oldIdSurveyOfSurveyquestionCollectionNewSurveyquestion);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = survey.getId();
                if (findSurvey(id) == null) {
                    throw new NonexistentEntityException("The survey with id " + id + " no longer exists.");
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
     * @throws IllegalOrphanException the illegal orphan exception
     * @throws NonexistentEntityException the nonexistent entity exception
     */
    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Survey survey;
            try {
                survey = em.getReference(Survey.class, id);
                survey.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The survey with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Surveyuseranswer> surveyuseranswerCollectionOrphanCheck = survey.getSurveyuseranswerCollection();
            for (Surveyuseranswer surveyuseranswerCollectionOrphanCheckSurveyuseranswer : surveyuseranswerCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Survey (" + survey + ") cannot be destroyed since the Surveyuseranswer " + surveyuseranswerCollectionOrphanCheckSurveyuseranswer + " in its surveyuseranswerCollection field has a non-nullable idSurvey field.");
            }
            Collection<Surveyquestion> surveyquestionCollectionOrphanCheck = survey.getSurveyquestionCollection();
            for (Surveyquestion surveyquestionCollectionOrphanCheckSurveyquestion : surveyquestionCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Survey (" + survey + ") cannot be destroyed since the Surveyquestion " + surveyquestionCollectionOrphanCheckSurveyquestion + " in its surveyquestionCollection field has a non-nullable idSurvey field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(survey);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Find survey entities.
     *
     * @return the list
     */
    public List<Survey> findSurveyEntities() {
        return findSurveyEntities(true, -1, -1);
    }

    /**
     * Find survey entities.
     *
     * @param maxResults the max results
     * @param firstResult the first result
     * @return the list
     */
    public List<Survey> findSurveyEntities(int maxResults, int firstResult) {
        return findSurveyEntities(false, maxResults, firstResult);
    }

    /**
     * Find survey entities.
     *
     * @param all the all
     * @param maxResults the max results
     * @param firstResult the first result
     * @return the list
     */
    private List<Survey> findSurveyEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Survey.class));
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
     * Find survey.
     *
     * @param id the id
     * @return the survey
     */
    public Survey findSurvey(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Survey.class, id);
        } finally {
            em.close();
            em = null;
        }
    }

    /**
     * Gets the survey count.
     *
     * @return the survey count
     */
    public int getSurveyCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Survey> rt = cq.from(Survey.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
            em = null;
        }
    }
    
    /**
     * Find surveys by user id creator.
     *
     * @param idusercreator the idusercreator
     * @return the list
     */
    public List<Survey> findSurveysByUserIdCreator(int idusercreator){
    	Query query = null;
    	List<Survey> surveyList = null;
        EntityManager em = getEntityManager();
        try {
       		em.getTransaction().begin();
       		query = em.createQuery("SELECT s FROM Survey s WHERE s.idusercreator = :idusercreator ORDER BY datesurvay");
       		query.setParameter("idusercreator", idusercreator);
       		surveyList = query.getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
        } finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
        }
        return surveyList;
    }
    
    
    /**
     * Find surveys by user.
     *
     * @param idusercreator the idusercreator
     * @param groupid the groupid
     * @return the list
     */
    public List<Survey> findSurveysByUser(int idusercreator, long groupid){
    	Query query = null;
    	List<Survey> surveyList = null;
        EntityManager em = getEntityManager();
        try {
       		em.getTransaction().begin();
       		query = em.createQuery("SELECT s FROM Survey s WHERE s.idusercreator = :idusercreator AND s.groupid = :groupid ORDER BY datesurvay");
       		query.setParameter("idusercreator", idusercreator);
       		query.setParameter("groupid", groupid);
       		query.setParameter("idusercreator", idusercreator);
       		surveyList = query.getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
        } finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
        }
        return surveyList;
    }
    
    /**
     * Find all surveys.
     *
     * @param groupid the groupid
     * @return the list
     */
    public List<Survey> findAllSurveys(long groupid){
    	Query query = null;
    	List<Survey> surveyList = null;
        EntityManager em = getEntityManager();
        try {
       		em.getTransaction().begin();
       		query = em.createQuery("SELECT s FROM Survey s WHERE s.groupid = :groupid ORDER BY datesurvay");
       		query.setParameter("groupid", groupid);
       		surveyList = query.getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
        } finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
        }

        return surveyList;
    }
    
}
