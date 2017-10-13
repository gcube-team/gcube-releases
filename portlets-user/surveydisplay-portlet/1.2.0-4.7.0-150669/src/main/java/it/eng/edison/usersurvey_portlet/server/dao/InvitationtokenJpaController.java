package it.eng.edison.usersurvey_portlet.server.dao;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import it.eng.edison.usersurvey_portlet.server.dao.exceptions.NonexistentEntityException;
import it.eng.edison.usersurvey_portlet.server.entity.Invitationtoken;

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
     * Find id survey by UUID.
     *
     * @param UUID the uuid
     * @return the int
     */
    public int findIdSurveyByUUID(String UUID){
    	
    	Query query = null;
    	int idSurveybyUUID = 0;
        EntityManager em = getEntityManager();
        try {
       		em.getTransaction().begin();
       		query = em.createQuery("SELECT i.idSurvey FROM Invitationtoken i WHERE i.uuid = :UUID");
       		query.setParameter("UUID", UUID);
       		idSurveybyUUID = (int) query.getSingleResult();
    	} catch (Exception e) {
    		idSurveybyUUID = -2;
    		e.printStackTrace();
        } finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
    		return idSurveybyUUID;
        }
        
    }
    
    /**
     * Find id survey by UUID and user id.
     *
     * @param UUID the uuid
     * @param userId the user id
     * @return the int
     */
    public int findIdSurveyByUUIDAndUserId(String UUID, int userId){
    	
    	Query query = null;
    	int idSurveybyUUID = 0;
        EntityManager em = getEntityManager();
        try {
       		em.getTransaction().begin();
       		query = em.createQuery("SELECT i.idSurvey FROM Invitationtoken i WHERE i.uuid = :UUID AND i.iduseranswer = :iduseranswer");
       		query.setParameter("UUID", UUID);
       		query.setParameter("iduseranswer", userId);
       		idSurveybyUUID = (int) query.getSingleResult();
    	} catch (Exception e) {
    		e.printStackTrace();
    		idSurveybyUUID = -1;
        } finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
    		return idSurveybyUUID;
        }
        
    }
    
    /**
     * Find invitationtoken by UUID.
     *
     * @param UUID the uuid
     * @return the list
     */
    public List<Invitationtoken> findInvitationtokenByUUID(String UUID) {
    	Query query = null;
    	List<Invitationtoken> invitationTokenList = null;
        EntityManager em = getEntityManager();
        try {
       		em.getTransaction().begin();
       		query = em.createQuery("SELECT i FROM Invitationtoken i WHERE i.uuid = :UUID");
       		query.setParameter("UUID", UUID);
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
     * Find email users by id survey.
     *
     * @param idSurvey the id survey
     * @return the list
     */
    public List<String> findEmailUsersByIdSurvey(int idSurvey){
    	Query query = null;
    	List<String> invitationTokenEmailUsersList = null;
        EntityManager em = getEntityManager();
        try {
       		em.getTransaction().begin();
       		query = em.createQuery("SELECT i.field1 FROM Invitationtoken i WHERE i.idSurvey = :idSurvey");
       		query.setParameter("idSurvey", idSurvey);
       		invitationTokenEmailUsersList = query.getResultList();
    	} catch (Exception e) {
    		e.printStackTrace();
        } finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
        }
        return invitationTokenEmailUsersList;
    }
    
    /**
     * Burn token.
     *
     * @param fillOutSurveyDate the fill out survey date
     * @param idUserAnswer the id user answer
     * @param idSurvey the id survey
     */
    public void burnToken(String fillOutSurveyDate, int idUserAnswer, int idSurvey){
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		em.createQuery("UPDATE Invitationtoken i SET i.field3 = 'BURNED' WHERE i.iduseranswer = :idUserAnswer AND i.idSurvey = :idSurvey").setParameter("idUserAnswer", idUserAnswer).setParameter("idSurvey", idSurvey).executeUpdate();
    		em.createQuery("UPDATE Invitationtoken i SET i.field2 = :fillOutSurveyDate WHERE i.iduseranswer = :iduseranswer AND i.idSurvey = :idSurvey").setParameter("fillOutSurveyDate", fillOutSurveyDate).setParameter("iduseranswer", idUserAnswer).setParameter("idSurvey", idSurvey).executeUpdate();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
    	}
    }
    
    /**
     * Burn token anonymous survey.
     *
     * @param fillOutSurveyDate the fill out survey date
     * @param UUID the uuid
     * @param idSurvey the id survey
     */
    public void burnTokenAnonymousSurvey(String fillOutSurveyDate, String UUID, int idSurvey){
    	EntityManager em = getEntityManager();
    	try {
    		em.getTransaction().begin();
    		em.createQuery("UPDATE Invitationtoken i SET i.field3 = 'BURNED' WHERE i.uuid = :UUID AND i.idSurvey = :idSurvey").setParameter("UUID", UUID).setParameter("idSurvey", idSurvey).executeUpdate();
    		em.createQuery("UPDATE Invitationtoken i SET i.field2 = :fillOutSurveyDate WHERE i.uuid = :UUID AND i.idSurvey = :idSurvey").setParameter("fillOutSurveyDate", fillOutSurveyDate).setParameter("UUID", UUID).setParameter("idSurvey", idSurvey).executeUpdate();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		em.getTransaction().commit();
    		em.close();
    		em = null;
    	}
    }
}
