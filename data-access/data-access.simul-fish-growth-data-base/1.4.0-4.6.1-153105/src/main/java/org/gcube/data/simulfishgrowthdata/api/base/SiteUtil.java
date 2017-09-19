package org.gcube.data.simulfishgrowthdata.api.base;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.gcube.data.simulfishgrowthdata.util.DatabaseUtil;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.cite.geoanalytics.environmental.data.retriever.OxygenRetriever;
import gr.cite.geoanalytics.environmental.data.retriever.model.Unit;
import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.SimilarSite;
import gr.i2s.fishgrowth.model.Site;
import gr.i2s.fishgrowth.model.Usage;

public class SiteUtil extends BaseUtil {
	public Site add(Site site) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			add(session, site);

			session.getTransaction().commit();

			return site;
		} catch (Exception e) {
			logger.info(String.format("Could not add site [%s]", site), e);
			throw new Exception(String.format("Could not add site [%s]", site), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public void loadOxygen(final Site site) {
		int[] oxygenBiMonthly;
		try {
			oxygenBiMonthly = ArrayUtils.toPrimitive(new OxygenRetriever().getByLatLongAsArray(
					Double.valueOf(site.getLatitude()), Double.valueOf(site.getLongitude()), Unit.CELCIUS));
			if (oxygenBiMonthly == null || oxygenBiMonthly.length < 25)
				throw new Exception(
						"No proper oxygen values for (" + site.getLatitude() + ", " + site.getLongitude() + ")");
		} catch (Exception e) {
			logger.warn("Problem retrieving oxygen. I will fill with defaults", e);
			oxygenBiMonthly = new int[24];
			for (int i = 0; i < 24; i++) {
				oxygenBiMonthly[i] = 0;
			}
		}

		int i = 0;
		site.setOxygenPeriodJanA(oxygenBiMonthly[i++]);
		site.setOxygenPeriodJanB(oxygenBiMonthly[i++]);
		site.setOxygenPeriodFebA(oxygenBiMonthly[i++]);
		site.setOxygenPeriodFebB(oxygenBiMonthly[i++]);
		site.setOxygenPeriodMarA(oxygenBiMonthly[i++]);
		site.setOxygenPeriodMarB(oxygenBiMonthly[i++]);
		site.setOxygenPeriodAprA(oxygenBiMonthly[i++]);
		site.setOxygenPeriodAprB(oxygenBiMonthly[i++]);
		site.setOxygenPeriodMayA(oxygenBiMonthly[i++]);
		site.setOxygenPeriodMayB(oxygenBiMonthly[i++]);
		site.setOxygenPeriodJunA(oxygenBiMonthly[i++]);
		site.setOxygenPeriodJunB(oxygenBiMonthly[i++]);
		site.setOxygenPeriodJulA(oxygenBiMonthly[i++]);
		site.setOxygenPeriodJulB(oxygenBiMonthly[i++]);
		site.setOxygenPeriodAugA(oxygenBiMonthly[i++]);
		site.setOxygenPeriodAugB(oxygenBiMonthly[i++]);
		site.setOxygenPeriodSepA(oxygenBiMonthly[i++]);
		site.setOxygenPeriodSepB(oxygenBiMonthly[i++]);
		site.setOxygenPeriodOctA(oxygenBiMonthly[i++]);
		site.setOxygenPeriodOctB(oxygenBiMonthly[i++]);
		site.setOxygenPeriodNovA(oxygenBiMonthly[i++]);
		site.setOxygenPeriodNovB(oxygenBiMonthly[i++]);
		site.setOxygenPeriodDecA(oxygenBiMonthly[i++]);
		site.setOxygenPeriodDecB(oxygenBiMonthly[i++]);
		int sum = 0;
		for (i = 0; i < 12; i++)
			sum += oxygenBiMonthly[i];
		site.setOxygenPeriodYear(sum / 12);
	}

	public Site add(final Session session, final Site site) throws Exception {
		loadOxygen(site);
		session.save(site);
		manageSimilars(session, site, new HashSet<>());
		session.flush();
		return site;
	}

	public Site update(final Session session, final Site site) throws Exception {
		return update(session, site, new HashSet<>());
	}

	public Site update(final Session session, final Site site, final Set<Long> similarsAlreadyUpdated)
			throws Exception {
		loadOxygen(site);
		session.update(site);
		// session.flush();
		manageSimilars(session, site, similarsAlreadyUpdated);
		// session.flush();
		return site;
	}

	public Site update(final Site site) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			update(session, site, new HashSet<>());

			session.flush();

			session.getTransaction().commit();

			return site;
		} catch (Exception e) {
			logger.info(String.format("Could not update site [%s]", site), e);
			throw new Exception(String.format("Could not update site [%s]", site), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public boolean delete(Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			if (delete(session, id))
				session.flush();

			session.getTransaction().commit();

			return true;
		} catch (Exception e) {
			logger.info(String.format("Could not delete site [%s]", id), e);
			throw new Exception(String.format("Could not delete site [%s]", id), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public Site getSite(final Session session, final Long id) throws Exception {
		Site site = (Site) session.get(Site.class, Long.valueOf(id));
		return site;
	}

	public Site getSite(Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			Site site = getSite(session, id);

			session.getTransaction().commit();

			return site;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve site [%s]", id), e);
			throw new Exception(String.format("Could not retrieve site [%s]", id), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<Site> getSites(String ownerId) throws Exception {
		Session session = null;

		try {
			if (logger.isTraceEnabled())
				logger.trace(String.format("start getSites"));
			session = HibernateUtil.openSession();
			if (logger.isTraceEnabled())
				logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			List<Site> list = getSites(session, ownerId);

			session.getTransaction().commit();

			if (logger.isTraceEnabled())
				logger.trace(String.format("return SiteFulls %s", list));
			return list;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve sites for [%s]", ownerId), e);
			throw new Exception(String.format("Could not retrieve sites for [%s]", ownerId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public List<Site> getSites(final Session session, final String ownerId) throws Exception {
		Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId);

		List<Site> list = q.list();
		return list;
	}

	public List<Usage> getUsage(String ownerId) throws Exception {
		Session session = null;

		try {
			if (logger.isTraceEnabled())
				logger.trace(String.format("start getUsage"));
			session = HibernateUtil.openSession();
			if (logger.isTraceEnabled())
				logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createSQLQuery(_GET_USAGE_ON_OWNERID).addEntity(Usage.class).setParameter("ownerid",
					ownerId);

			List<Usage> list = q.list();

			session.getTransaction().commit();

			if (logger.isTraceEnabled())
				logger.trace(String.format("return site usage %s", list));
			return list;
		} catch (Exception e) {
			logger.info(String.format("Could not get site usage for [%s]", ownerId), e);
			throw new Exception(String.format("Could not get site usage for [%s]", ownerId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/**
	 * strict on the similarity, relaxed on the amount
	 * 
	 * @param session
	 * @param me
	 * @return
	 */
	public Set<Long> findSitesSimilarToMe(final Session session, final Site me) {
		return findSitesSimilarToMe(session, me, 1, 1); // only fully similar
	}

	public Set<Long> findSitesSimilarToMe(final Session session, final Site me, final Integer acceptableCount,
			final Integer upToGrade) {
		Set<Long> toRet = null;
		Integer currentGrade = 1;
		Integer curCount = toRet == null ? 0 : toRet.size();
		if (curCount < acceptableCount && currentGrade <= upToGrade) {
			toRet = findSitesSimilarToMeGradeA(session, me);
			curCount = toRet == null ? 0 : toRet.size();
			currentGrade++;
			if (curCount < acceptableCount && currentGrade <= upToGrade) {
				toRet = findSitesSimilarToMeGradeB(session, me);
				curCount = toRet == null ? 0 : toRet.size();
				currentGrade++;
				if (curCount < acceptableCount && currentGrade <= upToGrade) {
					toRet = findSitesSimilarToMeGradeC(session, me);
					curCount = toRet == null ? 0 : toRet.size();
					currentGrade++;
					if (curCount < acceptableCount && currentGrade <= upToGrade) {
						toRet = findSitesSimilarToMeGradeD(session, me);
						curCount = toRet == null ? 0 : toRet.size();
						currentGrade++;
						if (curCount < acceptableCount && currentGrade <= upToGrade) {
							toRet = findSitesSimilarToMeGradeE(session, me);
							// nothing more I can do
						}
					}
				}
			}
		}

		return toRet == null ? new HashSet<>() : toRet;
	}

	/**
	 * grade A
	 * 
	 * @param session
	 * @param me
	 * @return
	 */
	protected Set<Long> findSitesSimilarToMeGradeA(final Session session, final Site me) {
		session.flush();
		int dtemp = 1; // +- 1 degree
		int dAnnualTemp = 1; // +- 1 degree
		int doxygen = 20;
		Set<Long> toRet = new TreeSet<>(
				new SiteFullUtil().getSiteFullSimilar(session, me, dtemp, dAnnualTemp, doxygen));
		return toRet;
	}

	protected Set<Long> findSitesSimilarToMeGradeB(final Session session, final Site me) {
		session.flush();
		int dtemp = 1; // +- 1 degree
		int dAnnualTemp = 1; // +- 1 degree
		int doxygen = 100000; // ignore it
		Set<Long> toRet = new TreeSet<>(
				new SiteFullUtil().getSiteFullSimilar(session, me, dtemp, dAnnualTemp, doxygen));
		return toRet;
	}

	protected Set<Long> findSitesSimilarToMeGradeC(final Session session, final Site me) {
		session.flush();
		int dtemp = 2; // relaxed by 1
		int dAnnualTemp = 1; // +- 1 degree
		int doxygen = 100000; // ignore it
		Set<Long> toRet = new TreeSet<>(
				new SiteFullUtil().getSiteFullSimilar(session, me, dtemp, dAnnualTemp, doxygen));
		return toRet;
	}

	protected Set<Long> findSitesSimilarToMeGradeD(final Session session, final Site me) {
		session.flush();
		int dtemp = 100000; // ignore it
		int dAnnualTemp = 2; // relaxed by 1
		int doxygen = 100000; // ignore it
		Set<Long> toRet = new TreeSet<>(
				new SiteFullUtil().getSiteFullSimilar(session, me, dtemp, dAnnualTemp, doxygen));
		return toRet;
	}

	protected Set<Long> findSitesSimilarToMeGradeE(final Session session, final Site me) {
		session.flush();
		int dtemp = 100000; // ignore it
		int dAnnualTemp = 100000; // ignore it
		int doxygen = 100000; // ignore it
		Set<Long> toRet = new TreeSet<>(
				new SiteFullUtil().getSiteFullSimilar(session, me, dtemp, dAnnualTemp, doxygen));
		return toRet;
	}

	/**
	 * 
	 * @param site
	 * @param similarsAlreadyUpdated
	 * @throws Exception
	 */
	private void manageSimilars(Session session, Site site, Set<Long> similarsAlreadyUpdated) throws Exception {
		Long id = site.getId();
		// get similars to me as it is before these changes.
		// If the site is new then the set is empty.
		// If it is old then it is included in the set
		Set<Long> existingSimilarSites = getExistingSimilarSites(session, site);
		// I am keeping the other sites, without me
		existingSimilarSites.remove(site.getId());
		if (logger.isTraceEnabled())
			logger.trace(String.format("Existing similar sites [%s]", existingSimilarSites));
		// get my new similars, according to the current values.
		// This site is not included yet
		int grade = 1;
		Set<Long> sitesSimilarToMe = findSitesSimilarToMe(session, site);
		if (logger.isTraceEnabled())
			logger.trace(String.format("New similar sites [%s]", sitesSimilarToMe));
		// TODO if empty, change grade and retry
		/*-
		while (!satisfied) {
			grade++;
			sitesSimilarToMe = callAlgorithmThatFindsSimilar(session, site);
			if (logger.isTraceEnabled()) {
				logger.trace(String.format("New similar sites [%s]", sitesSimilarToMe));
			}
		}
		*/

		// do I need to reestablish the connections?
		if (sitesSimilarToMe.equals(existingSimilarSites)) {
			if (sitesSimilarToMe.isEmpty()) {
				// this is new and it doesn't have similars. Put it anyway
				establisSimilars(session, id, sitesSimilarToMe, grade);
			} else {
				if (logger.isTraceEnabled())
					logger.trace("this relations are already wired regarding the site similarity, leaving as is");
			}
		} else {
			removeFromSimilars(session, id, existingSimilarSites);
			establisSimilars(session, id, sitesSimilarToMe, grade);
			similarsAlreadyUpdated.add(id);
			cascadeUpdates(session, sitesSimilarToMe, similarsAlreadyUpdated);

		}

	}

	private Set<Long> getExistingSimilarSites(final Session session, final Site site) throws Exception {
		Set<Long> toRet = new TreeSet<>();
		Long globalSiteId = new SimilarSiteUtil().getGlobal(session, site.getId());
		if (globalSiteId != null) {
			Site global = getSite(session, globalSiteId);
			if (global != null)
				toRet = DatabaseUtil.explodeGlobalName(global.getDesignation());
		}
		return toRet;
	}

	private void establisSimilars(final Session session, final Long id, final Set<Long> sitesSimilarToMe, int grade)
			throws Exception {
		// these are my new similars, including me
		Set<Long> currentSimilarSites = new TreeSet<>(sitesSimilarToMe);
		currentSimilarSites.add(id);

		// establish in the database
		for (Long newId : currentSimilarSites) {
			SimilarSite similarSite = new SimilarSite(id, newId, grade);
			session.save(similarSite);
			if (newId != id) {
				// ... and vice versa
				similarSite = new SimilarSite(newId, id, grade);
				session.save(similarSite);
			}
		}
		// session.flush();

		Site globalFound = null;
		Long globalExistingId = new SimilarSiteUtil().getGlobal(session, id);
		if (globalExistingId != null) {
			Site globalExisting = getSite(session, globalExistingId);
			if (globalExisting != null) {
				// update
				Site newGlobalValues = getSiteAsGlobal(session, currentSimilarSites);
				globalExisting.setPeriodJanA(newGlobalValues.getPeriodJanA());
				globalExisting.setPeriodJanB(newGlobalValues.getPeriodJanB());
				globalExisting.setPeriodFebA(newGlobalValues.getPeriodFebA());
				globalExisting.setPeriodFebB(newGlobalValues.getPeriodFebB());
				globalExisting.setPeriodMarA(newGlobalValues.getPeriodMarA());
				globalExisting.setPeriodMarB(newGlobalValues.getPeriodMarB());
				globalExisting.setPeriodAprA(newGlobalValues.getPeriodAprA());
				globalExisting.setPeriodAprB(newGlobalValues.getPeriodAprB());
				globalExisting.setPeriodMayA(newGlobalValues.getPeriodMayA());
				globalExisting.setPeriodMayB(newGlobalValues.getPeriodMayB());
				globalExisting.setPeriodJunA(newGlobalValues.getPeriodJunA());
				globalExisting.setPeriodJunB(newGlobalValues.getPeriodJunB());
				globalExisting.setPeriodJulA(newGlobalValues.getPeriodJulA());
				globalExisting.setPeriodJulB(newGlobalValues.getPeriodJulB());
				globalExisting.setPeriodAugA(newGlobalValues.getPeriodAugA());
				globalExisting.setPeriodAugB(newGlobalValues.getPeriodAugB());
				globalExisting.setPeriodSepA(newGlobalValues.getPeriodSepA());
				globalExisting.setPeriodSepB(newGlobalValues.getPeriodSepB());
				globalExisting.setPeriodOctA(newGlobalValues.getPeriodOctA());
				globalExisting.setPeriodOctB(newGlobalValues.getPeriodOctB());
				globalExisting.setPeriodNovA(newGlobalValues.getPeriodNovA());
				globalExisting.setPeriodNovB(newGlobalValues.getPeriodNovB());
				globalExisting.setPeriodDecA(newGlobalValues.getPeriodDecA());
				globalExisting.setPeriodDecB(newGlobalValues.getPeriodDecB());
				globalExisting.setOxygenRatingId(newGlobalValues.getOxygenRatingId());
				globalExisting.setCurrentRatingId(newGlobalValues.getCurrentRatingId());
				globalExisting.setDesignation(DatabaseUtil.implodeGlobalName(currentSimilarSites));
				session.update(globalExisting);

				globalFound = globalExisting; // flag it
			}
		}
		if (globalFound == null) {
			globalFound = generateGlobal(session, id, currentSimilarSites);
		}

		batchUpdateStatuses(session, globalFound.getId(), ModelerUtil.STATUS_FAILED_KPI);
	}

	void batchUpdateStatuses(Session session, Long id, Long statusId) {
		new ModelerUtil().updateStatusOnSite(session, id, statusId);
		new ScenarioUtil().updateStatusOnSite(session, id, statusId);
	}

	/**
	 * trigger chain update the rest, from id forward
	 * 
	 * @param session
	 * @param candidates
	 * @param alreadyProcessed
	 * @throws Exception
	 */
	private void cascadeUpdates(final Session session, final Set<Long> candidates, final Set<Long> alreadyProcessed)
			throws Exception {
		for (Long siteId : candidates) {
			if (alreadyProcessed.contains(siteId))
				continue;
			Site site = getSite(session, siteId);
			if (site != null)
				update(session, site, alreadyProcessed);
		}
	}

	/**
	 * break any similarity between id the the set. The set does should not
	 * include id
	 * 
	 * @param session
	 * @param id
	 * @param existingSimilarSites
	 * @throws Exception
	 */
	private void removeFromSimilars(final Session session, final Long id, final Set<Long> existingSimilarSites)
			throws Exception {
		for (Long similarId : existingSimilarSites) {
			deleteFromSimilars(session, id, similarId);
		}

	}

	private Site generateGlobal(final Session session, final Long originalSiteId, final Set<Long> similarSites) {
		if (similarSites == null || similarSites.isEmpty()) {
			return null;
		}
		Site global = getSiteAsGlobal(session, similarSites);
		global.setDesignation(DatabaseUtil.implodeGlobalName(similarSites));
		global.setOwnerId(DatabaseUtil.GLOBAL_OWNER);
		session.save(global);
		Long id = global.getId();
		SimilarSite similarSite = new SimilarSite(originalSiteId, id, -1);
		session.save(similarSite);
		// session.flush();
		return global;
	}

	Site getSiteAsGlobal(Session session, Set<Long> ids) {
		Query q = session.createSQLQuery(_GET_AS_GLOBAL).setParameterList("siteids", ids)
				.setResultTransformer(new AliasToBeanResultTransformer(Site.class));
		List<Site> list = q.list();
		return list.isEmpty() ? null : list.get(0);
	}

	public boolean delete(Session session, Long id) {
		try {
			return delete(session, (Site) session.get(Site.class, Long.valueOf(id)));
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete Site with id [%s]", id), e);
		}
	}

	public boolean delete(Session session, Site site) {
		try {
			if (site == null) {
				return false;
			}
			if (!isGlobal(site)) {
				// I have to take care of the similariries first
				Long id = site.getId();
				// get similars to me as it is before these changes.
				// If the site is new then the set is empty.
				// If it is old then it is included in the set
				Set<Long> existingSimilarSites = getExistingSimilarSites(session, site);
				// I ma keeping the other sites, without me
				existingSimilarSites.remove(site.getId());
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("Existing similar sites [%s]", existingSimilarSites));
				}
				removeFromSimilars(session, id, existingSimilarSites);

				deleteGlobal(session, id);

				// finally :)
				session.delete(site);
				// trigger chain update for ex-similars
				cascadeUpdates(session, existingSimilarSites, new HashSet<>());
			} else {
				// global is simpler regarding similarities: just delete
				deleteFromSimilars(session, site.getId());
				session.delete(site);
			}

			// the related models are orphans, no point to stay alive
			List<Modeler> models = new ModelerUtil().getModelersForSite(session, site.getId());
			for (Modeler modeler : models) {
				new ModelerUtil().delete(session, modeler);
			}
			return true;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete Site  [%s]", site), e);
		}
	}

	private void deleteGlobal(Session session, Long id) throws Exception {
		// take care of the global site
		Long globalExistingId = new SimilarSiteUtil().getGlobal(session, id);
		if (globalExistingId != null) {
			Site globalExisting = getSite(session, globalExistingId);
			if (globalExisting != null) {
				delete(session, globalExisting);
			}
		}
	}

	// TODO move in model.Site
	static synchronized boolean isGlobal(Site site) {
		return DatabaseUtil.GLOBAL_OWNER.equalsIgnoreCase(site.getOwnerId());
	}

	public int deleteFromSimilars(Session session, Long id, Long similarId) {
		final SQLQuery q = session.createSQLQuery(_DELETE_PAIR_FROM_SIMILARS);
		q.setParameter("siteId", id);
		q.setParameter("similarId", similarId);
		return q.executeUpdate();
	}

	public int deleteFromSimilars(Session session, Long id) {
		final SQLQuery q = session.createSQLQuery(_DELETE_FROM_SIMILARS);
		q.setParameter("siteId", id);
		return q.executeUpdate();
	}

	private static final String _GET_ON_OWNERID_DESIGNATION = "FROM gr.i2s.fishgrowth.model.Site s WHERE s.ownerId = :ownerid AND s.designation = :designation";
	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.Site s WHERE s.ownerId = :ownerid ORDER BY s.designation ASC";
	private static final String _GET_USAGE_ON_OWNERID = "SELECT us.id as id, us.simulcount as usage FROM siteusageview us inner join site e on (us.id=e.id) WHERE e.ownerId = :ownerid ORDER BY us.id ASC";
	private static final String _DELETE_FROM_SIMILARS = "DELETE FROM SimilarSite s WHERE s.siteId=:siteId OR similarId = :siteId";
	private static final String _DELETE_PAIR_FROM_SIMILARS = "DELETE FROM SimilarSite s WHERE (s.siteId=:siteId AND s.similarId = :similarId) OR (s.siteId=:similarId AND s.similarId = :siteId)";
	private static final String _GET_AS_GLOBAL = "Select 0 as id, cast ('ownerid' as VARCHAR) as \"ownerId\", cast ('designation' as VARCHAR) as \"designation\","
			+ " cast (round(avg(periodJana)) as INTEGER) as \"periodJanA\", cast (round(avg(periodJanb)) as INTEGER) as \"periodJanB\", cast (round(avg(periodFeba)) as INTEGER) as \"periodFebA\", cast (round(avg(periodFebb)) as INTEGER) as \"periodFebB\", cast (round(avg(periodMara)) as INTEGER) as \"periodMarA\", cast (round(avg(periodMarb)) as INTEGER) as \"periodMarB\", cast (round(avg(periodapra)) as INTEGER) as \"periodAprA\", cast (round(avg(periodaprb)) as INTEGER) as \"periodAprB\", cast (round(avg(periodMaya)) as INTEGER) as \"periodMayA\", cast (round(avg(periodMayb)) as INTEGER) as \"periodMayB\", cast (round(avg(periodJuna)) as INTEGER) as \"periodJunA\", cast (round(avg(periodJunb)) as INTEGER) as \"periodJunB\", cast (round(avg(periodJula)) as INTEGER) as \"periodJulA\", cast (round(avg(periodJulb)) as INTEGER) as \"periodJulB\", cast (round(avg(periodauga)) as INTEGER) as \"periodAugA\", cast (round(avg(periodaugb)) as INTEGER) as \"periodAugB\", cast (round(avg(periodSepa)) as INTEGER) as \"periodSepA\", cast (round(avg(periodSepb)) as INTEGER) as \"periodSepB\", cast (round(avg(periodOcta)) as INTEGER) as \"periodOctA\", cast (round(avg(periodOctb)) as INTEGER) as \"periodOctB\", cast (round(avg(periodNova)) as INTEGER) as \"periodNovA\", cast (round(avg(periodNovb)) as INTEGER) as \"periodNovB\", cast (round(avg(periodDeca)) as INTEGER) as \"periodDecA\", cast (round(avg(periodDecb)) as INTEGER) as \"periodDecB\","
			+ " cast (round(avg(oxygenPeriodJana)) as INTEGER) as \"oxygenPeriodJanA\", cast (round(avg(oxygenPeriodJanb)) as INTEGER) as \"oxygenPeriodJanB\", cast (round(avg(oxygenPeriodFeba)) as INTEGER) as \"oxygenPeriodFebA\", cast (round(avg(oxygenPeriodFebb)) as INTEGER) as \"oxygenPeriodFebB\", cast (round(avg(oxygenPeriodMara)) as INTEGER) as \"oxygenPeriodMarA\", cast (round(avg(oxygenPeriodMarb)) as INTEGER) as \"oxygenPeriodMarB\", cast (round(avg(oxygenPeriodapra)) as INTEGER) as \"oxygenPeriodAprA\", cast (round(avg(oxygenPeriodaprb)) as INTEGER) as \"oxygenPeriodAprB\", cast (round(avg(oxygenPeriodMaya)) as INTEGER) as \"oxygenPeriodMayA\", cast (round(avg(oxygenPeriodMayb)) as INTEGER) as \"oxygenPeriodMayB\", cast (round(avg(oxygenPeriodJuna)) as INTEGER) as \"oxygenPeriodJunA\", cast (round(avg(oxygenPeriodJunb)) as INTEGER) as \"oxygenPeriodJunB\", cast (round(avg(oxygenPeriodJula)) as INTEGER) as \"oxygenPeriodJulA\", cast (round(avg(oxygenPeriodJulb)) as INTEGER) as \"oxygenPeriodJulB\", cast (round(avg(oxygenPeriodauga)) as INTEGER) as \"oxygenPeriodAugA\", cast (round(avg(oxygenPeriodaugb)) as INTEGER) as \"oxygenPeriodAugB\", cast (round(avg(oxygenPeriodSepa)) as INTEGER) as \"oxygenPeriodSepA\", cast (round(avg(oxygenPeriodSepb)) as INTEGER) as \"oxygenPeriodSepB\", cast (round(avg(oxygenPeriodOcta)) as INTEGER) as \"oxygenPeriodOctA\", cast (round(avg(oxygenPeriodOctb)) as INTEGER) as \"oxygenPeriodOctB\", cast (round(avg(oxygenPeriodNova)) as INTEGER) as \"oxygenPeriodNovA\", cast (round(avg(oxygenPeriodNovb)) as INTEGER) as \"oxygenPeriodNovB\", cast (round(avg(oxygenPeriodDeca)) as INTEGER) as \"oxygenPeriodDecA\", cast (round(avg(oxygenPeriodDecb)) as INTEGER) as \"oxygenPeriodDecB\","
			+ " 1 as \"oxygenRatingId\", 1 as \"currentRatingId\", 1 as \"regionId\", cast ('' as VARCHAR) as \"latitude\", cast ('' as VARCHAR) as \"longitude\", cast (round(avg(periodyear)) as INTEGER) as \"periodYear\", cast (round(avg(oxygenPeriodyear)) as INTEGER) as \"oxygenPeriodYear\"  FROM Site where id in (:siteids)";
	private static final Logger logger = LoggerFactory.getLogger(SiteUtil.class);
}
