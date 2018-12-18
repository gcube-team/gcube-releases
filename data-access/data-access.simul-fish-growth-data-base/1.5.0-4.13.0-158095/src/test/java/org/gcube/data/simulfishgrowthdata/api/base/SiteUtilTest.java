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
				Set<Long> similars = new SiteUtil().findSitesSimilarToMe(session, site, 1, 1);
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

			new ScenarioUtil().setAdditionalSimilarityConstraint(ScenarioUtilTest.additionalSimilarityConstraint).executeScenario(session, 30L);

			session.getTransaction().commit();
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	public void testSiteLookForSimilars() throws Exception {
		Session session = null;
		Long myHeroId = 4L;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();
			
			SiteUtil su = new SiteUtil().setAdditionalSimilarityConstraint(ScenarioUtilTest.additionalSimilarityConstraint);

			Site myHeroSite = su.getSite(session, myHeroId);
			assertNotNull("hero site", myHeroSite);

			Long newId = null;

			{
				// create sample site, based on an existing one
				Set<Long> baseSites = su.findSitesSimilarToMe(session, myHeroSite, 10, 5);
				Site site = su.getSiteAsGlobal(session, baseSites);
				session.save(site);
				newId = site.getId();
				site.setDesignation("junit test site");
				site.setOwnerId(myHeroSite.getOwnerId());
				//site.setPeriodJunA(site.getPeriodJunA() - 1);
				su.update(session, site, new HashSet<>());
				assertNotNull("new site created", newId);
				
			}
			
			Set<Long> similars = su.findSitesSimilarToMe(session, myHeroSite, 10, 5);
			assertFalse("myHeroSite similars does not contain self", similars.contains(myHeroId));
			assertTrue("myHeroSite similars contains newSite", similars.contains(newId));

			Set<Long> allSimilars = su.findSitesSimilarToMeIncludingMe(session, myHeroSite, 10, 5);
			assertTrue("myHeroSite similars contains self", allSimilars.contains(myHeroId));
			assertTrue("myHeroSite similars contains newSite", allSimilars.contains(newId));


			session.getTransaction().rollback();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

}
