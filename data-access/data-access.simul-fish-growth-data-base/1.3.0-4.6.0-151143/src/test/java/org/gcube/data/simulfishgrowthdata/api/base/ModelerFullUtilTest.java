package org.gcube.data.simulfishgrowthdata.api.base;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.gcube.data.simulfishgrowthdata.model.GlobalModelWrapper;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.hibernate.Session;

import gr.i2s.fishgrowth.model.Fcr;
import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.ModelerFull;
import gr.i2s.fishgrowth.model.Mortality;
import gr.i2s.fishgrowth.model.Sfr;
import gr.i2s.fishgrowth.model.Site;
import gr.i2s.fishgrowth.model.TableEntity;
import junit.framework.TestCase;

public class ModelerFullUtilTest extends TestCase {

	long testId = 999999;

	protected void setUp() throws Exception {
		super.setUp();
		String dbEndpointName = "SimulFishGrowth";
		String scope = "/gcube/preprod/preECO";  

		HibernateUtil.configGently(dbEndpointName, scope);
	}

	public void testModeler() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmm");
		Session session = null;
		Long siteId = 21L;
		Long myHeroModelId = 1102L;
		Double twist = 1.05; // +5%
		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();
			ModelerFull toAdd = new ModelerFull();
			toAdd.setId(testId);
			toAdd.setDesignation("junit test" + " " + sdf.format(new Date()));
			toAdd.setSpeciesId(2);
			toAdd.setBroodstockQualityId(1);
			toAdd.setFeedQualityId(1);
			toAdd.setSiteId(siteId);
			toAdd.setStatusId(ModelerUtil.STATUS_FAILED_KPI);
			toAdd.setOwnerId("junit");

			ModelerFullUtil util = new ModelerFullUtil();
			ModelerUtil mutil = new ModelerUtil();

			Modeler pong = util.add(session, toAdd);
			assertNotNull("added entity", pong);
			if (myHeroModelId != null) {
				{
					Collection<Fcr> idx = new FcrUtil().getFcrs(session, myHeroModelId);
					for (Fcr cur : idx) {
						Fcr dupl = new Fcr();
						dupl.setModelerId(pong.getId());
						dupl.setFromWeight(cur.getFromWeight());
						dupl.setTemperature(cur.getTemperature());
						dupl.setValue(cur.getValue() * twist);
						session.save(dupl);
					}
				}
				{
					Collection<Sfr> idx = new SfrUtil().getSfrs(session, myHeroModelId);
					for (Sfr cur : idx) {
						Sfr dupl = new Sfr();
						dupl.setModelerId(pong.getId());
						dupl.setFromWeight(cur.getFromWeight());
						dupl.setTemperature(cur.getTemperature());
						dupl.setValue(cur.getValue() * twist);
						session.save(dupl);
					}
				}
				{
					Collection<Mortality> idx = new MortalityUtil().getMortalities(session, myHeroModelId);
					for (Mortality cur : idx) {
						Mortality dupl = new Mortality();
						dupl.setModelerId(pong.getId());
						dupl.setFromWeight(cur.getFromWeight());
						dupl.setTemperature(cur.getTemperature());
						dupl.setValue(cur.getValue() * twist);
						session.save(dupl);
					}
				}
				pong.setStatusId(ModelerUtil.STATUS_READY);
				mutil.update(session, pong);

				assertTrue("Fcr added", new FcrUtil().getFcrs(session, pong.getId()).size() > 0);
				assertTrue("Sfrr added", new SfrUtil().getSfrs(session, pong.getId()).size() > 0);
				assertTrue("Mortality added", new MortalityUtil().getMortalities(session, pong.getId()).size() > 0);
			}

			Modeler global = new ModelerUtil().getGlobal(session, pong.getId());
			assertNotNull("added entity global", global);
			Long globalId = global.getId();
			assertNotNull("retrieved global using id", mutil.getModeler(session, globalId));

			assertTrue("delete entity", util.delete(session, pong.getId()));
			assertNull("global entity also deleted", mutil.getModeler(session, globalId));

			session.getTransaction().commit();
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public void testGetCommonWeights() throws Exception {
		Session session = null;
		Collection<Long> modelIds = new ArrayList<>();
		modelIds.add(4759L); // site = 21; no kpis
		modelIds.add(5153L); // site = 1168
		modelIds.add(5284L); // site = 2
		modelIds.add(1102L); // site=2

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();
			Map<Integer, Collection<Double>> list = new ModelerUtil().getCommonWeightLimits(session, modelIds);
			session.getTransaction().rollback();
			assertNotNull("result exist", list);
			assertFalse("result exist", list.isEmpty());
			assertEquals("KPI count", 4, list.size()); // 4 KPIs
			assertEquals("common fcr categories", 13, list.get(1).size());
			assertEquals("common sfr categories", 26, list.get(2).size());
			assertEquals("common sgr categories", 13, list.get(3).size());
			assertEquals("common mortality categories", 13, list.get(4).size());
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	public void testGlobalModel() throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			long modelerId = prepareModelsInfrastructureFromScratch(session,
					prepareSitesInfrastructureFromScratch(session, null)).get(0).getId();

			long start = System.currentTimeMillis();
			Modeler modeler = (Modeler) session.get(Modeler.class, Long.valueOf(modelerId));
			assertNotNull("modeler exist", modeler);
			GlobalModelWrapper globalModel = new GlobalModelWrapper(session, modeler);
			assertNotNull("result exist", globalModel);
			globalModel.create();
			long end = System.currentTimeMillis();
			System.err.println("global model creation " + (end - start));
			assertNotNull("globalModel has fcrs", globalModel.fcrs);
			assertEquals("globalModel fcr count", 10, globalModel.fcrs.size());
			List<TableEntity> fcrsList = new ArrayList<>(globalModel.fcrs);
			int i = 0;
			assertEquals("globalModel fcr " + i + " fromWeight", Double.valueOf(0), fcrsList.get(i).getFromWeight());
			assertEquals("globalModel fcr " + i + " value", new Double((1.0 + 11.0) / 2.0), fcrsList.get(i).getValue());
			i++;
			assertEquals("globalModel fcr " + i + " fromWeight", Double.valueOf(1), fcrsList.get(i).getFromWeight());
			assertEquals("globalModel fcr " + i + " value", new Double((1.0 + 11.0 + 31.0) / 3.0),
					fcrsList.get(i).getValue());
			i++;
			assertEquals("globalModel fcr " + i + " fromWeight", Double.valueOf(5), fcrsList.get(i).getFromWeight());
			assertEquals("globalModel fcr " + i + " value", new Double((2.0 + 11.0 + 31.0) / 3.0),
					fcrsList.get(i).getValue());
			i++;
			assertEquals("globalModel fcr " + i + " fromWeight", Double.valueOf(7), fcrsList.get(i).getFromWeight());
			assertEquals("globalModel fcr " + i + " value", new Double((2.0 + 12.0 + 31.0) / 3.0),
					fcrsList.get(i).getValue());
			i++;
			assertEquals("globalModel fcr " + i + " fromWeight", Double.valueOf(8), fcrsList.get(i).getFromWeight());
			assertEquals("globalModel fcr " + i + " value", new Double((2.0 + 12.0 + 32.0) / 3.0),
					fcrsList.get(i).getValue());
			i++;
			assertEquals("globalModel fcr " + i + " fromWeight", Double.valueOf(10), fcrsList.get(i).getFromWeight());
			assertEquals("globalModel fcr " + i + " value", new Double((3.0 + 13.0 + 32.0) / 3.0),
					fcrsList.get(i).getValue());
			i++;
			assertEquals("globalModel fcr " + i + " fromWeight", Double.valueOf(12), fcrsList.get(i).getFromWeight());
			assertEquals("globalModel fcr " + i + " value", new Double((3.0 + 13.0 + 33.0) / 3.0),
					fcrsList.get(i).getValue());
			i++;
			assertEquals("globalModel fcr " + i + " fromWeight", Double.valueOf(15), fcrsList.get(i).getFromWeight());
			assertEquals("globalModel fcr " + i + " value", new Double((4.0 + 14.0 + 33.0) / 3.0),
					fcrsList.get(i).getValue());
			i++;
			assertEquals("globalModel fcr " + i + " fromWeight", Double.valueOf(17), fcrsList.get(i).getFromWeight());
			assertEquals("globalModel fcr " + i + " value", new Double((4.0 + 14.0 + 34.0) / 3.0),
					fcrsList.get(i).getValue());
			i++;
			assertEquals("globalModel fcr " + i + " fromWeight", Double.valueOf(21), fcrsList.get(i).getFromWeight());
			assertEquals("globalModel fcr " + i + " value", new Double((4.0 + 14.0 + 35.0) / 3.0),
					fcrsList.get(i).getValue());

			session.getTransaction().rollback();
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	public void testMarkOutliers() throws Exception {
		long modelid = 495L;

		Session session = null;

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();
			
			new ModelerUtil().markOutliers(session, modelid);
			
			session.getTransaction().commit();

		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	/**
	 * make sites infrastructure from scratch. Temperature is weird (eg 5o C) in
	 * order to avoid messing up with production-like sites.
	 * <p>
	 * The user either declares the desired annual site temperature or leave it
	 * to a predefined setup
	 * </p>
	 * 
	 * @param session
	 * @return
	 * @throws Exception
	 */
	static List<Site> prepareSitesInfrastructureFromScratch(final Session session, Integer forceTemp) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmmss");

		Site site1;
		Site site2;

		SiteUtil su = new SiteUtil();
		SimilarSiteUtil ssu = new SimilarSiteUtil();
		Site site;

		{ // site1
			site = new Site();
			site.setDesignation("junit gmodel " + sdf.format(new Date()));
			site.setOwnerId("junit_1");
			site.setLatitude("1");
			site.setLongitude("1");
			site.setCurrentRatingId(1L);
			site.setOxygenRatingId(1L);
			site.setRegionId(1L);
			site.setPeriodJanA(forceTemp != null ? forceTemp : 15);
			site.setPeriodJanB(forceTemp != null ? forceTemp : 15);
			site.setPeriodFebA(forceTemp != null ? forceTemp : 15);
			site.setPeriodFebB(forceTemp != null ? forceTemp : 15);
			site.setPeriodMarA(forceTemp != null ? forceTemp : 16);
			site.setPeriodMarB(forceTemp != null ? forceTemp : 16);
			site.setPeriodAprA(forceTemp != null ? forceTemp : 16);
			site.setPeriodAprB(forceTemp != null ? forceTemp : 16);
			site.setPeriodMayA(forceTemp != null ? forceTemp : 16);
			site.setPeriodMayB(forceTemp != null ? forceTemp : 16);
			site.setPeriodJunA(forceTemp != null ? forceTemp : 17);
			site.setPeriodJunB(forceTemp != null ? forceTemp : 17);
			site.setPeriodJulA(forceTemp != null ? forceTemp : 17);
			site.setPeriodJulB(forceTemp != null ? forceTemp : 17);
			site.setPeriodAugA(forceTemp != null ? forceTemp : 17);
			site.setPeriodAugB(forceTemp != null ? forceTemp : 17);
			site.setPeriodSepA(forceTemp != null ? forceTemp : 18);
			site.setPeriodSepB(forceTemp != null ? forceTemp : 18);
			site.setPeriodOctA(forceTemp != null ? forceTemp : 18);
			site.setPeriodOctB(forceTemp != null ? forceTemp : 18);
			site.setPeriodNovA(forceTemp != null ? forceTemp : 18);
			site.setPeriodNovB(forceTemp != null ? forceTemp : 18);
			site.setPeriodDecA(forceTemp != null ? forceTemp : 15);
			site.setPeriodDecB(forceTemp != null ? forceTemp : 15);
		}
		site1 = su.add(session, site);
		Long site1Id = site1.getId();
		Long site1GlobalId = ssu.getGlobal(session, site1Id);
		assertNotNull("Site1 has a global site id", site1GlobalId);
		Site site1Global = su.getSite(session, site1GlobalId);
		assertNotNull("Site1 has a global site", site1Global);
		assertTrue("site1 global contains site1", site1Global.getDesignation().contains("_" + site1Id + "_"));

		{// site2
			site = new Site();
			site.setDesignation("junit gmodel " + sdf.format(new Date()));
			site.setOwnerId("junit_2");
			site.setLatitude("1");
			site.setLongitude("1");
			site.setCurrentRatingId(1L);
			site.setOxygenRatingId(1L);
			site.setRegionId(1L);
			site.setPeriodJanA(forceTemp != null ? forceTemp : 15);
			site.setPeriodJanB(forceTemp != null ? forceTemp : 15);
			site.setPeriodFebA(forceTemp != null ? forceTemp : 15);
			site.setPeriodFebB(forceTemp != null ? forceTemp : 15 + 1);
			site.setPeriodMarA(forceTemp != null ? forceTemp : 16);
			site.setPeriodMarB(forceTemp != null ? forceTemp : 16);
			site.setPeriodAprA(forceTemp != null ? forceTemp : 16);
			site.setPeriodAprB(forceTemp != null ? forceTemp : 16);
			site.setPeriodMayA(forceTemp != null ? forceTemp : 16);
			site.setPeriodMayB(forceTemp != null ? forceTemp : 16 - 1);
			site.setPeriodJunA(forceTemp != null ? forceTemp : 17);
			site.setPeriodJunB(forceTemp != null ? forceTemp : 17);
			site.setPeriodJulA(forceTemp != null ? forceTemp : 17);
			site.setPeriodJulB(forceTemp != null ? forceTemp : 17);
			site.setPeriodAugA(forceTemp != null ? forceTemp : 17);
			site.setPeriodAugB(forceTemp != null ? forceTemp : 17 + 1);
			site.setPeriodSepA(forceTemp != null ? forceTemp : 18);
			site.setPeriodSepB(forceTemp != null ? forceTemp : 18);
			site.setPeriodOctA(forceTemp != null ? forceTemp : 18);
			site.setPeriodOctB(forceTemp != null ? forceTemp : 18);
			site.setPeriodNovA(forceTemp != null ? forceTemp : 18);
			site.setPeriodNovB(forceTemp != null ? forceTemp : 18);
			site.setPeriodDecA(forceTemp != null ? forceTemp : 15);
			site.setPeriodDecB(forceTemp != null ? forceTemp : 15);
		}
		site2 = su.add(session, site);
		Long site2Id = site2.getId();
		Long site2GlobalId = ssu.getGlobal(session, site2Id);
		assertNotNull("Site2 has a global site id", site2GlobalId);
		Site site2Global = su.getSite(session, site2GlobalId);
		assertNotNull("Site2 has a global site", site2Global);
		assertTrue("site2 global contains site1", site2Global.getDesignation().contains("_" + site1Id + "_"));
		assertTrue("site2 global contains site2", site2Global.getDesignation().contains("_" + site2Id + "_"));

		// again because it changed due to site2
		site1Global = su.getSite(session, site1GlobalId);

		assertTrue("site1 global contains site1", site1Global.getDesignation().contains("_" + site1Id + "_"));
		assertTrue("site1 global contains site2", site1Global.getDesignation().contains("_" + site2Id + "_"));

		List<Site> toRet = new ArrayList<Site>();
		toRet.add(site1);
		toRet.add(site2);
		return toRet;
	}

	static List<Modeler> prepareModelsInfrastructureFromScratch(final Session session, List<Site> sites)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmmss");
		List<Modeler> toRet = new ArrayList<>();

		Site site1 = sites.get(0);
		// modelers for site1
		Modeler modeler_1_1, modeler_2_1;

		Site site2 = sites.get(1);
		// modelers for site1
		Modeler modeler_1_2;

		{ // modelers
			ModelerUtil mu = new ModelerUtil();
			FcrUtil fu = new FcrUtil();
			SfrUtil su = new SfrUtil();
			MortalityUtil mrtu = new MortalityUtil();
			Modeler modeler;
			List<Fcr> fcrs;
			List<Sfr> sfrs;
			List<Mortality> mortalities;
			{ // modeler 1 1
				{
					modeler = new Modeler();
					modeler.setDesignation("junit Mdl 1-1 " + sdf.format(new Date()));
					modeler.setOwnerId("junit_1");
					modeler.setSpeciesId(2L);
					modeler.setBroodstockQualityId(1L);
					modeler.setFeedQualityId(1L);
					modeler.setSiteId(site1.getId());
					modeler.setStatusId(ModelerUtil.STATUS_PENDING_KPI);
				}
				mu.add(session, modeler);
				modeler_1_1 = mu.getModeler(session, modeler.getId()); // pong
				assertNotNull("Modeler inserted", modeler_1_1);
				assertEquals(modeler_1_1.getDesignation() + " connected to site1", modeler_1_1.getSiteId(),
						site1.getId());
				{
					Fcr fcr;

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(0);
					fcr.setValue(1);
					session.save(fcr);

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(5);
					fcr.setValue(2);
					session.save(fcr);

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(10);
					fcr.setValue(3);
					session.save(fcr);

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(15);
					fcr.setValue(4);
					session.save(fcr);
				}
				fcrs = fu.getFcrs(session, modeler_1_1.getId());
				assertNotNull(modeler_1_1.getDesignation() + " has fcr table", fcrs);
				assertEquals(modeler_1_1.getDesignation() + " fcr count", 4, fcrs.size());
				{
					Sfr sfr;

					sfr = new Sfr();
					sfr.setModelerId(modeler.getId());
					sfr.setTemperature(5);
					sfr.setFromWeight(0);
					sfr.setValue(1);
					session.save(sfr);
				}
				sfrs = su.getSfrs(session, modeler_1_1.getId());
				assertNotNull(modeler_1_1.getDesignation() + " has sfr table", sfrs);
				assertEquals(modeler_1_1.getDesignation() + " sfr count", 1, sfrs.size());
				{
					Mortality mortality;

					mortality = new Mortality();
					mortality.setModelerId(modeler.getId());
					mortality.setTemperature(5);
					mortality.setFromWeight(0);
					mortality.setValue(1);
					session.save(mortality);
				}
				mortalities = mrtu.getMortalities(session, modeler_1_1.getId());
				assertNotNull(modeler_1_1.getDesignation() + " has mortality table", mortalities);
				assertEquals(modeler_1_1.getDesignation() + " mortality count", 1, mortalities.size());
				modeler.setStatusId(ModelerUtil.STATUS_READY);
				mu.update(session, modeler);
			}
			toRet.add(modeler_1_1);
			{ // modeler 2 1
				{
					modeler = new Modeler();
					modeler.setDesignation("junit gmodel 2-1" + sdf.format(new Date()));
					modeler.setOwnerId("junit_1");
					modeler.setSpeciesId(2L);
					modeler.setBroodstockQualityId(1L);
					modeler.setFeedQualityId(1L);
					modeler.setSiteId(site1.getId());
					modeler.setStatusId(ModelerUtil.STATUS_PENDING_KPI);

				}
				mu.add(session, modeler);
				modeler_2_1 = mu.getModeler(session, modeler.getId()); // pong
				assertNotNull("Modeler 2-1 inserted", modeler_2_1);
				assertEquals(modeler_2_1.getDesignation() + " connected to site1", modeler_2_1.getSiteId(),
						site1.getId());
				{
					Fcr fcr;

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(0);
					fcr.setValue(11);
					session.save(fcr);

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(7);
					fcr.setValue(12);
					session.save(fcr);

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(10);
					fcr.setValue(13);
					session.save(fcr);

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(15);
					fcr.setValue(14);
					session.save(fcr);
				}
				fcrs = fu.getFcrs(session, modeler_1_1.getId());
				assertNotNull(modeler_2_1.getDesignation() + "has fcr table", fcrs);
				assertEquals(modeler_2_1.getDesignation() + "fcr count", 4, fcrs.size());
				{
					Sfr sfr;

					sfr = new Sfr();
					sfr.setModelerId(modeler.getId());
					sfr.setTemperature(5);
					sfr.setFromWeight(0);
					sfr.setValue(1);
					session.save(sfr);
				}
				sfrs = su.getSfrs(session, modeler_2_1.getId());
				assertNotNull(modeler_2_1.getDesignation() + "has sfr table", sfrs);
				assertEquals(modeler_2_1.getDesignation() + "sfr count", 1, sfrs.size());
				{
					Mortality mortality;

					mortality = new Mortality();
					mortality.setModelerId(modeler.getId());
					mortality.setTemperature(5);
					mortality.setFromWeight(0);
					mortality.setValue(1);
					session.save(mortality);
				}
				mortalities = mrtu.getMortalities(session, modeler_2_1.getId());
				assertNotNull(modeler_2_1.getDesignation() + "has mortality table", mortalities);
				assertEquals(modeler_2_1.getDesignation() + "mortality count", 1, mortalities.size());
				modeler.setStatusId(ModelerUtil.STATUS_READY);
				mu.update(session, modeler);
			}
			toRet.add(modeler_2_1);
			{ // modeler 1 2
				{
					modeler = new Modeler();
					modeler.setDesignation("junit gmodel 1-2" + sdf.format(new Date()));
					modeler.setOwnerId("junit_2");
					modeler.setSpeciesId(2L);
					modeler.setBroodstockQualityId(1L);
					modeler.setFeedQualityId(1L);
					modeler.setSiteId(site2.getId());
					modeler.setStatusId(ModelerUtil.STATUS_PENDING_KPI);

				}
				mu.add(session, modeler);
				modeler_1_2 = mu.getModeler(session, modeler.getId()); // pong
				assertNotNull("Modeler 1-2 inserted", modeler_1_2);
				assertEquals(modeler_1_2.getDesignation() + " connected to site2", modeler_1_2.getSiteId(),
						site2.getId());
				{
					Fcr fcr;

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(1);
					fcr.setValue(31);
					session.save(fcr);

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(8);
					fcr.setValue(32);
					session.save(fcr);

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(12);
					fcr.setValue(33);
					session.save(fcr);

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(17);
					fcr.setValue(34);
					session.save(fcr);

					fcr = new Fcr();
					fcr.setModelerId(modeler.getId());
					fcr.setTemperature(5);
					fcr.setFromWeight(21);
					fcr.setValue(35);
					session.save(fcr);
				}
				fcrs = fu.getFcrs(session, modeler_1_2.getId());
				assertNotNull(modeler_1_2.getDesignation() + "has fcr table", fcrs);
				assertEquals(modeler_1_2.getDesignation() + "fcr count", 5, fcrs.size());
				{
					Sfr sfr;

					sfr = new Sfr();
					sfr.setModelerId(modeler.getId());
					sfr.setTemperature(5);
					sfr.setFromWeight(0);
					sfr.setValue(1);
					session.save(sfr);
				}
				sfrs = su.getSfrs(session, modeler_1_2.getId());
				assertNotNull(modeler_1_2.getDesignation() + "has sfr table", sfrs);
				assertEquals(modeler_1_2.getDesignation() + "sfr count", 1, sfrs.size());
				{
					Mortality mortality;

					mortality = new Mortality();
					mortality.setModelerId(modeler.getId());
					mortality.setTemperature(5);
					mortality.setFromWeight(0);
					mortality.setValue(1);
					session.save(mortality);
				}
				mortalities = mrtu.getMortalities(session, modeler_1_2.getId());
				assertNotNull(modeler_1_2.getDesignation() + "has mortality table", mortalities);
				assertEquals(modeler_1_2.getDesignation() + "mortality count", 1, mortalities.size());
				modeler.setStatusId(ModelerUtil.STATUS_READY);
				mu.update(session, modeler);
			}
		}
		toRet.add(modeler_1_2);

		return toRet;

	}

	public void testGetCustomGlobalKPIs() throws Exception {
		Session session = null;
		Collection<Long> modelIds = new ArrayList<>();
		modelIds.add(4759L); // site = 21; no kpis
		modelIds.add(5153L); // site = 1168
		modelIds.add(5284L); // site = 2
		modelIds.add(1102L); // site = 2

		try {
			session = HibernateUtil.openSession();
			session.beginTransaction();

			Map<Integer, Collection<Double>> commonWeights = new ModelerUtil().getCommonWeightLimits(session, modelIds);
			assertNotNull("result exist", commonWeights);
			assertFalse("result exist", commonWeights.isEmpty());

			{
				Collection<Fcr> fcr = new ModelerUtil().getCommonKPIValues(session, 1, commonWeights.get(1), modelIds);
				assertNotNull("fcr exist", fcr);
				assertEquals("fcr count", 13 * 17, fcr.size()); // weight
																// categories *
																// temperatures
			}

			{
				Collection<Sfr> sfr = new ModelerUtil().getCommonKPIValues(session, 2, commonWeights.get(2), modelIds);
				assertNotNull("sfr exist", sfr);
				assertEquals("sfr count", 26 * 17, sfr.size()); // weight
																// categories *
																// temperatures
			}

			{
				Collection<Mortality> mortality = new ModelerUtil().getCommonKPIValues(session, 4, commonWeights.get(4),
						modelIds);
				assertNotNull("mortality exist", mortality);
				assertEquals("mortality count", 13 * 17, mortality.size()); // weight
																			// categories
																			// *
																			// temps
			}

			session.getTransaction().rollback();
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

}
