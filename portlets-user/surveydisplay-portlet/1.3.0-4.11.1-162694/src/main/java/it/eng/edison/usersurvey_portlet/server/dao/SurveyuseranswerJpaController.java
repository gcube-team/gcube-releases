package it.eng.edison.usersurvey_portlet.server.dao;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import it.eng.edison.usersurvey_portlet.server.entity.Survey;
import it.eng.edison.usersurvey_portlet.server.entity.Surveyuseranswer;
import it.eng.edison.usersurvey_portlet.server.dao.exceptions.NonexistentEntityException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * The Class SurveyuseranswerJpaController.
 *
 */
public class SurveyuseranswerJpaController implements Serializable {

    /**
     * Instantiates a new surveyuseranswer jpa controller.
     *
     * @param emf the emf
     */
    public SurveyuseranswerJpaController(EntityManagerFactory emf) {
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
     * @param surveyuseranswer the surveyuseranswer
     */
    public void create(Surveyuseranswer surveyuseranswer) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Survey idSurvey = surveyuseranswer.getIdSurvey();
            if (idSurvey != null) {
                idSurvey = em.getReference(idSurvey.getClass(), idSurvey.getId());
                surveyuseranswer.setIdSurvey(idSurvey);
            }
            em.persist(surveyuseranswer);
            if (idSurvey != null) {
                idSurvey.getSurveyuseranswerCollection().add(surveyuseranswer);
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
     * @param surveyuseranswer the surveyuseranswer
     * @throws NonexistentEntityException the nonexistent entity exception
     * @throws Exception the exception
     */
    public void edit(Surveyuseranswer surveyuseranswer) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Surveyuseranswer persistentSurveyuseranswer = em.find(Surveyuseranswer.class, surveyuseranswer.getId());
            Survey idSurveyOld = persistentSurveyuseranswer.getIdSurvey();
            Survey idSurveyNew = surveyuseranswer.getIdSurvey();
            if (idSurveyNew != null) {
                idSurveyNew = em.getReference(idSurveyNew.getClass(), idSurveyNew.getId());
                surveyuseranswer.setIdSurvey(idSurveyNew);
            }
            surveyuseranswer = em.merge(surveyuseranswer);
            if (idSurveyOld != null && !idSurveyOld.equals(idSurveyNew)) {
                idSurveyOld.getSurveyuseranswerCollection().remove(surveyuseranswer);
                idSurveyOld = em.merge(idSurveyOld);
            }
            if (idSurveyNew != null && !idSurveyNew.equals(idSurveyOld)) {
                idSurveyNew.getSurveyuseranswerCollection().add(surveyuseranswer);
                idSurveyNew = em.merge(idSurveyNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = surveyuseranswer.getId();
                if (findSurveyuseranswer(id) == null) {
                    throw new NonexistentEntityException("The surveyuseranswer with id " + id + " no longer exists.");
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
            Surveyuseranswer surveyuseranswer;
            try {
                surveyuseranswer = em.getReference(Surveyuseranswer.class, id);
                surveyuseranswer.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The surveyuseranswer with id " + id + " no longer exists.", enfe);
            }
            Survey idSurvey = surveyuseranswer.getIdSurvey();
            if (idSurvey != null) {
                idSurvey.getSurveyuseranswerCollection().remove(surveyuseranswer);
                idSurvey = em.merge(idSurvey);
            }
            em.remove(surveyuseranswer);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Find surveyuseranswer entities.
     *
     * @return the list
     */
    public List<Surveyuseranswer> findSurveyuseranswerEntities() {
        return findSurveyuseranswerEntities(true, -1, -1);
    }

    /**
     * Find surveyuseranswer entities.
     *
     * @param maxResults the max results
     * @param firstResult the first result
     * @return the list
     */
    public List<Surveyuseranswer> findSurveyuseranswerEntities(int maxResults, int firstResult) {
        return findSurveyuseranswerEntities(false, maxResults, firstResult);
    }

    /**
     * Find surveyuseranswer entities.
     *
     * @param all the all
     * @param maxResults the max results
     * @param firstResult the first result
     * @return the list
     */
    private List<Surveyuseranswer> findSurveyuseranswerEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Surveyuseranswer.class));
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
     * Find surveyuseranswer.
     *
     * @param id the id
     * @return the surveyuseranswer
     */
    public Surveyuseranswer findSurveyuseranswer(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Surveyuseranswer.class, id);
        } finally {
            em.close();
            em = null;
        }
    }

    /**
     * Gets the surveyuseranswer count.
     *
     * @return the surveyuseranswer count
     */
    public int getSurveyuseranswerCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Surveyuseranswer> rt = cq.from(Surveyuseranswer.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
            em = null;
        }
    }
    
}
