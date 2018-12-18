package it.eng.edison.usersurvey_portlet.server.dao;

import it.eng.edison.usersurvey_portlet.server.dao.exceptions.NonexistentEntityException;
import it.eng.edison.usersurvey_portlet.server.entity.Invitationtoken;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * The Class InvitationtokenJpaController.
 *
 */
public class InvitationtokenJpaController implements Serializable {

    /**
     * Instantiates a new invitationtoken jpa controller.
     *
     * @param emf the emf
     */
    public InvitationtokenJpaController(EntityManagerFactory emf) {
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
     * @param invitationtoken the invitationtoken
     */
    public void create(Invitationtoken invitationtoken) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(invitationtoken);
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
     * @param invitationtoken the invitationtoken
     * @throws NonexistentEntityException the nonexistent entity exception
     * @throws Exception the exception
     */
    public void edit(Invitationtoken invitationtoken) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            invitationtoken = em.merge(invitationtoken);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = invitationtoken.getId();
                if (findInvitationtoken(id) == null) {
                    throw new NonexistentEntityException("The invitationtoken with id " + id + " no longer exists.");
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
            Invitationtoken invitationtoken;
            try {
                invitationtoken = em.getReference(Invitationtoken.class, id);
                invitationtoken.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The invitationtoken with id " + id + " no longer exists.", enfe);
            }
            em.remove(invitationtoken);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Find invitationtoken entities.
     *
     * @return the list
     */
    public List<Invitationtoken> findInvitationtokenEntities() {
        return findInvitationtokenEntities(true, -1, -1);
    }

    /**
     * Find invitationtoken entities.
     *
     * @param maxResults the max results
     * @param firstResult the first result
     * @return the list
     */
    public List<Invitationtoken> findInvitationtokenEntities(int maxResults, int firstResult) {
        return findInvitationtokenEntities(false, maxResults, firstResult);
    }

    /**
     * Find invitationtoken entities.
     *
     * @param all the all
     * @param maxResults the max results
     * @param firstResult the first result
     * @return the list
     */
    private List<Invitationtoken> findInvitationtokenEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Invitationtoken.class));
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
     * Find invitationtoken.
     *
     * @param id the id
     * @return the invitationtoken
     */
    public Invitationtoken findInvitationtoken(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Invitationtoken.class, id);
        } finally {
            em.close();
            em = null;
        }
    }

    /**
     * Gets the invitationtoken count.
     *
     * @return the invitationtoken count
     */
    public int getInvitationtokenCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Invitationtoken> rt = cq.from(Invitationtoken.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
            em = null;
        }
    }
    
    /**
     * Find invitationtoken by user answer id.
     *
     * @param iduseranswer the iduseranswer
     * @return the list
     */
    public List<Invitationtoken> findInvitationtokenByUserAnswerId(int iduseranswer) {
    	Query query = null;
    	List<Invitationtoken> invitationTokenList = null;
        EntityManager em = getEntityManager();
        try {
       		em.getTransaction().begin();
       		query = em.createQuery("SELECT i FROM Invitationtoken i WHERE i.iduseranswer = :iduseranswer");
       		query.setParameter("iduseranswer", iduseranswer);
       		invitationTokenList = query.getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
        } finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
        }
        return invitationTokenList;
    }
    
    /**
     * Gets the list user answered survey.
     *
     * @param idSurveySelected the id survey selected
     * @return the list user answered survey
     */
    public List<Integer> getListUserAnsweredSurvey(int idSurveySelected) {
    	Query query = null;
    	List<Integer> idUserAnsweredList = null;
        EntityManager em = getEntityManager();
        try {
       		em.getTransaction().begin();
       		query = em.createQuery("SELECT iduseranswer FROM Invitationtoken i WHERE (i.field3 = :burned OR i.uuid = :burned) AND i.idSurvey = :idSurveySelected");
			query.setParameter("burned", "BURNED");
			query.setParameter("idSurveySelected", idSurveySelected);
			idUserAnsweredList = query.getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
        } finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
        }
        return idUserAnsweredList;
    } 
    
    /**
     * Gets the user answered survey count.
     *
     * @return the user answered survey count
     */
    public List<Integer> getUserAnsweredSurveyCount() {
    	Query query = null;
    	List<Integer> idSurveyAnsweredList = null;
        EntityManager em = getEntityManager();
        try {
       		em.getTransaction().begin();
       		query = em.createQuery("SELECT i.idSurvey FROM Invitationtoken i WHERE i.field3 = :burned OR i.uuid = :burned");
       		query.setParameter("burned", "BURNED");
       		idSurveyAnsweredList = query.getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
        } finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
        }
        return idSurveyAnsweredList;
    } 
    
    /**
     * Burn token.
     *
     * @param tokenToBurn the token to burn
     * @param fillOutSurveyDate the fill out survey date
     * @param idUserAnswer the id user answer
     * @param idSurvey the id survey
     */
    public void burnToken(String tokenToBurn, String fillOutSurveyDate, int idUserAnswer, int idSurvey){
        EntityManager em = getEntityManager();
        try {
       		em.getTransaction().begin();
       		em.createQuery("UPDATE Invitationtoken i SET i.uuid = '' WHERE i.uuid = :tokenToBurn").setParameter("tokenToBurn", tokenToBurn).executeUpdate();
       		em.createQuery("UPDATE Invitationtoken i SET i.field2 = :fillOutSurveyDate WHERE i.iduseranswer = :iduseranswer AND i.idSurvey = :idSurvey").setParameter("fillOutSurveyDate", fillOutSurveyDate).setParameter("iduseranswer", idUserAnswer).setParameter("idSurvey", idSurvey).executeUpdate();
        } catch (Exception e) {
    		e.printStackTrace();
        } finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
        }
        
    }
}
