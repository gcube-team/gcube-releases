package org.gcube.data.simulfishgrowthdata.api;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.gcube.data.simulfishgrowthdata.util.ConnectionUtil;
import org.gcube.data.simulfishgrowthdata.util.DatabaseUtil;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.SimilarSite;
import gr.i2s.fishgrowth.model.Site;
import gr.i2s.fishgrowth.model.Usage;

@Path("/Site")
public class SiteUtil extends BaseUtil {
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response add(Site site) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			session.save(site);

			session.flush();

			session.getTransaction().commit();

			return Response.status(Response.Status.OK).entity(site.getId()).build();
		} catch (Exception e) {
			logger.error(String.format("Could not add site [%s]", site), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Site site) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			session.update(site);

			session.flush();

			session.getTransaction().commit();

			return Response.status(Response.Status.OK).entity(site).build();
		} catch (Exception e) {
			logger.error(String.format("Could not update site [%s]", site), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@DELETE
	@Path("/{id}")
	public Response delete(@PathParam("id") Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			if (doDelete(session, id))
				session.flush();

			session.getTransaction().commit();
			return Response.status(Response.Status.OK).build();
		} catch (Exception e) {
			logger.error(String.format("Could not delete site [%s]", id), e);
			return Response.status(ConnectionUtil.Status.UNPROCESSABLE_ENTITY).entity(e).build();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Site getSite(@PathParam("id") Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			Site site = (Site) session.get(Site.class, Long.valueOf(id));

			session.getTransaction().commit();

			return site;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve site [%s]", id), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/all/{ownerId}")
	public List<Site> getSites(@PathParam("ownerId") String ownerId) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getSites"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId);

			List<Site> list = q.list();

			session.getTransaction().commit();

			logger.trace(String.format("return SiteFulls %s", list));
			return list;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve sites for ownerid [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	@GET
	@Path("/usage/{ownerId}")
	public List<Usage> getUsage(@PathParam("ownerId") String ownerId) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getUsage"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createSQLQuery(_GET_USAGE_ON_OWNERID).addEntity(Usage.class).setParameter("ownerid",
					ownerId);

			List<Usage> list = q.list();

			session.getTransaction().commit();

			logger.trace(String.format("return site usage %s", list));
			return list;
		} catch (Exception e) {
			logger.error(String.format("Could not retrieve site usage for ownerid [%s]", ownerId), e);
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * we assume we are already in a hibernate transaction
	 * 
	 * @param site
	 * @throws Exception
	 */
	private void manageSimilars(Session session, Site site) throws Exception {
		Long id = site.getId();
		// get similars to me as it is before these changes.
		// If the site is new the set is empty.
		// If it is old it is included in the set
		Set<Long> existingSimilarSites = new TreeSet<>(new SimilarSiteUtil().doGetSimilarSites(session, id));
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("Existing similars [%s]", existingSimilarSites));
		}
		// get my new similars, according to the current values.
		// This site is not included yet
		int dtemp = 1; // +- 1 degree
		Set<Long> newSimilarSites = new TreeSet<>(new SiteFullUtil().doGetSiteFullSimilar(session, site, dtemp));
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("New similars [%s]", newSimilarSites));
		}
		// these are my new similars, including me
		Set<Long> currentSimilarSites= new TreeSet<>(newSimilarSites);
		currentSimilarSites.add(id);
		// do I need to reestablish the connections?
		if (newSimilarSites.equals(existingSimilarSites)) {
			logger.trace("this relations are already wired regarding the similarity");
		} else {
			Site globalPreviousExisting = getGlobalSiteForTheseSites(session, existingSimilarSites);
			Site globalPreviousNew = getGlobalSiteForTheseSites(session, newSimilarSites);

			doDelete(session, globalPreviousExisting);
			doDelete(session, globalPreviousNew);

			// remove from the previous,
			doDeleteFromSimilars(session, id);
			if (existingSimilarSites.contains(id)) {
				existingSimilarSites.remove(id);
			}
			// establish in the database
			for (Long newId : currentSimilarSites) {
				SimilarSite similarSite = new SimilarSite(id, newId, -1);
				session.save(similarSite);
				if (newId != id) {
					// ... and vice versa
					similarSite = new SimilarSite(newId, id, -1);
					session.save(similarSite);
				}
			}
			Site globalCurrentExisting = generateGlobal(session, existingSimilarSites);
			Site globalCurrentNew = generateGlobal(session, currentSimilarSites);
			
			//TODO update models.siteid from globalPreviousExisting to globalCurrentExisting
			//TODO update models.siteid from globalPreviousNew to globalCurrentNew
			// TODO keep the updated ids in order to trigger calculations
		}

	}

	private Site generateGlobal(final Session session, final Set<Long> similarSites) {
		if (similarSites == null || similarSites.isEmpty()) {
			return null;
		}
		Site global = doGetSiteAsGlobal(session, similarSites);
		global.setDesignation(DatabaseUtil.getGlobalName(similarSites));
		global.setOwnerId(DatabaseUtil.GLOBAL_OWNER);
		session.save(global);
		// establish in the database
		Long id = global.getId();
		SimilarSite similarSite = new SimilarSite(id, id, -1);
		session.save(similarSite);
		for (Long simId : similarSites) {
			similarSite = new SimilarSite(id, simId, -1);
			session.save(similarSite);
			// ... and vice versa
			similarSite = new SimilarSite(simId, id, -1);
			session.save(similarSite);
		}
		return global;
	}

	/**
	 * find the global site for this lsit and
	 * 
	 * @param session
	 * @param sites
	 */
	private Site getGlobalSiteForTheseSites(Session session, Set<Long> sites) {
		if (sites == null || sites.isEmpty()) {
			return null;
		}
		String globalSiteName = DatabaseUtil.getGlobalName(sites);
		Site site = doGetSite(session, DatabaseUtil.GLOBAL_OWNER, globalSiteName);
		return site;
	}

	private Site doGetSite(Session session, String ownerId, String designation) {
		Query q = session.createQuery(_GET_ON_OWNERID_DESIGNATION).setParameter("ownerid", ownerId)
				.setParameter("designation", designation);
		List<Site> list = q.list();
		return list.get(0);
	}

	private Site doGetSiteAsGlobal(Session session, @Nonnull Set<Long> ids) {
		Query q = session.createSQLQuery(_GET_AS_GLOBAL).setParameterList("siteids", ids)
				.setResultTransformer(new AliasToBeanResultTransformer(Site.class));
		List<Site> list = q.list();
		return list.get(0);
	}

	public boolean doDelete(Session session, Long id) {
		try {
			return doDelete(session, (Site) session.get(Site.class, Long.valueOf(id)));
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete Site with id [%s]", id), e);
		}
	}

	public boolean doDelete(Session session, Site site) {
		try {
			if (site == null) {
				return false;
			}
			if (!isGlobal(site)) {
				// get similar site -> get the global site -> delete it
				Set<Long> existingSimilarSites = new TreeSet<>(
						new SimilarSiteUtil().doGetSimilarSites(session, site.getId()));
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("Existing similars [%s]", existingSimilarSites));
				}
				// clean previous global
				Site globalExisting = getGlobalSiteForTheseSites(session, existingSimilarSites);
				doDelete(session, globalExisting);
			}
			List<Modeler> models = new ModelerUtil().getModelersForSite(session, site.getId());
			for (Modeler modeler : models) {
				new ModelerUtil().doDelete(session, modeler);
			}
			doDeleteFromSimilars(session, site.getId());
			session.delete(site);
			return true;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete Site  [%s]", site), e);
		}
	}

	// TODO move in model.Site
	static synchronized boolean isGlobal(Site site) {
		return DatabaseUtil.GLOBAL_OWNER.equalsIgnoreCase(site.getOwnerId());
	}

	public int doDeleteFromSimilars(Session session, Long id) {
		final SQLQuery q = session.createSQLQuery(_DELETE_FROM_SIMILARS);
		q.setParameter("siteId", id);
		return q.executeUpdate();
	}

	private static final String _GET_ON_OWNERID_DESIGNATION = "FROM gr.i2s.fishgrowth.model.Site s WHERE s.ownerId = :ownerid AND s.designation = :designation";
	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.Site s WHERE s.ownerId = :ownerid ORDER BY s.designation ASC";
	private static final String _GET_USAGE_ON_OWNERID = "SELECT us.id as id, us.simulcount as usage FROM siteusageview us inner join site e on (us.id=e.id) WHERE e.ownerId = :ownerid ORDER BY us.id ASC";
	private static final String _DELETE_FROM_SIMILARS = "DELETE FROM SimilarSite s WHERE s.siteId=:siteId OR similarId = :siteId";
	private static final String _GET_AS_GLOBAL = "Select 0 as id, cast ('ownerid' as VARCHAR) as \"ownerId\", cast ('designation' as VARCHAR) as \"designation\", cast (round(avg(periodJana)) as INTEGER) as \"periodJanA\", cast (round(avg(periodJanb)) as INTEGER) as \"periodJanB\", cast (round(avg(periodFeba)) as INTEGER) as \"periodFebA\", cast (round(avg(periodFebb)) as INTEGER) as \"periodFebB\", cast (round(avg(periodMara)) as INTEGER) as \"periodMarA\", cast (round(avg(periodMarb)) as INTEGER) as \"periodMarB\", cast (round(avg(periodapra)) as INTEGER) as \"periodAprA\", cast (round(avg(periodaprb)) as INTEGER) as \"periodAprB\", cast (round(avg(periodMaya)) as INTEGER) as \"periodMayA\", cast (round(avg(periodMayb)) as INTEGER) as \"periodMayB\", cast (round(avg(periodJuna)) as INTEGER) as \"periodJunA\", cast (round(avg(periodJunb)) as INTEGER) as \"periodJunB\", cast (round(avg(periodJula)) as INTEGER) as \"periodJulA\", cast (round(avg(periodJulb)) as INTEGER) as \"periodJulB\", cast (round(avg(periodauga)) as INTEGER) as \"periodAugA\", cast (round(avg(periodaugb)) as INTEGER) as \"periodAugB\", cast (round(avg(periodSepa)) as INTEGER) as \"periodSepA\", cast (round(avg(periodSepb)) as INTEGER) as \"periodSepB\", cast (round(avg(periodOcta)) as INTEGER) as \"periodOctA\", cast (round(avg(periodOctb)) as INTEGER) as \"periodOctB\", cast (round(avg(periodNova)) as INTEGER) as \"periodNovA\", cast (round(avg(periodNovb)) as INTEGER) as \"periodNovB\", cast (round(avg(periodDeca)) as INTEGER) as \"periodDecA\", cast (round(avg(periodDecb)) as INTEGER) as \"periodDecB\", 1 as \"oxygenRatingId\", 1 as \"currentRatingId\", 1 as \"regionId\", cast ('' as VARCHAR) as \"latitude\", cast ('' as VARCHAR) as \"longitude\", cast (round(avg(periodyear)) as INTEGER) as \"periodYear\" FROM Site where id in (:siteids)";
	private static final Logger logger = LoggerFactory.getLogger(SiteUtil.class);
}
