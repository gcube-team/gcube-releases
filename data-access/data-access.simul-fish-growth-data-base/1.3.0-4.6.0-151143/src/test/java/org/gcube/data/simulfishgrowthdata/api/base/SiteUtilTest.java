package org.gcube.data.simulfishgrowthdata.api.base;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Session;

import gr.i2s.fishgrowth.model.Site;
import junit.framework.TestCase;

public class SiteUtilTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
		String dbEndpointName = "SimulFishGrowth";
		String scope = "/gcube/preprod/preECO";

		HibernateUtil.configGently(dbEndpointName, scope);
	}

	public void testSite() throws Exception {
		Long id = 4L;
		Site site = new SiteUtil().getSite(id);
		assertNotNull("got entity", site);

		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();
			{
				// very relaxed!
				int diff = 1;
				int oxydiff = 100;
				List<Long> similars = new SiteFullUtil().getSiteFullSimilar(session, site, diff, diff, oxydiff);
				assertNotNull("got similars", similars);
				assertFalse("got similars", similars.isEmpty());
			}
			{
				Set<Long> similars = new SiteUtil().findSitesSimilarToMe(session, site);
				assertNotNull("got similars", similars);
				assertFalse("got similars", similars.isEmpty());
			}

			session.getTransaction().commit();
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	public void debugging() throws Exception {
		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			new ScenarioUtil().executeScenario(session, 30L);

			session.getTransaction().commit();
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	public void testSiteSimilarity() throws Exception {
		Session session = null;
		Long myHeroId = 3L;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			Site myHeroSite = new SiteUtil().getSite(session, myHeroId);
			assertNotNull("hero site", myHeroSite);
			Long myHeroGlobalId = new SimilarSiteUtil().getGlobal(session, myHeroId);
			assertNotNull("hero globalId", myHeroGlobalId);

			Long newId = null;
			Long globalId = null;

			{
				// create sample site, based on ana existing one
				Set<Long> baseSites = new SiteUtil().findSitesSimilarToMe(session, myHeroSite);
				Site site = new SiteUtil().getSiteAsGlobal(session, baseSites);
				session.save(site);
				newId = site.getId();
				site.setDesignation("junit test site");
				site.setOwnerId(myHeroSite.getOwnerId());
				site.setPeriodJunA(site.getPeriodJunA() - 1);
				new SiteUtil().update(session, site, new HashSet<>());
				assertNotNull("new site created", newId);
			}
			{
				// verify global site creation and proper wiring
				globalId = new SimilarSiteUtil().getGlobal(session, newId);
				assertNotNull("new globalId", globalId);
				Site global = new SiteUtil().getSite(session, globalId);
				assertNotNull("new global site", global);
				assertTrue("global contains hero", global.getDesignation().contains("_" + myHeroId + "_"));
			}
			{
				//
				Long currentGlobalHero = new SimilarSiteUtil().getGlobal(session, myHeroId);
				assertEquals("GlobalId should be updated, not recreated", myHeroGlobalId, currentGlobalHero);
				// verify wiring between the new and the existing
				Site globalHero = new SiteUtil().getSite(session, myHeroGlobalId);
				assertNotNull("hero global site", globalHero);
				assertTrue("hero global contains new", globalHero.getDesignation().contains("_" + newId + "_"));
			}
			{
				// delete and verify wiring is broken
				new SiteUtil().delete(session, newId);
				Site site = new SiteUtil().getSite(session, newId);
				assertNull("new deleted", site);
				Site global = new SiteUtil().getSite(session, globalId);
				assertNull("new global deleted", global);
				Long heroGlobalId = new SimilarSiteUtil().getGlobal(session, myHeroId);
				global = new SiteUtil().getSite(session, heroGlobalId);
				assertFalse("hero global notified regarding neww's removal",
						global.getDesignation().contains("_" + newId + "_"));
			}

			session.getTransaction().commit();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	/*-
	public void testActuallyDebugging() throws Exception {
		// List<Site> sites = new
		// SiteUtil().getSites("_gcube_devNext_NextNext");
		Session session = null;
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();
			Site site = new SiteUtil().getSite(session, 2L);
			new SiteUtil().update(session, site);
			// for (Site site : sites) {
			// Set<Long> similars = new SiteUtil().whoAreMySimilarSites(session,
			// site);
			// System.out.println(String.format("[%s] similars: [%s]",
			// site.getId(), similars));
			// }
			session.getTransaction().commit();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}
	*/

}
