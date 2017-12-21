package org.gcube.data.simulfishgrowthdata.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil;
import org.gcube.data.simulfishgrowthdata.api.base.SimilarSiteUtil;
import org.gcube.data.simulfishgrowthdata.api.base.SiteUtil;
import org.gcube.data.simulfishgrowthdata.util.DatabaseUtil;
import org.gcube.data.simulfishgrowthdata.util.UserFriendlyException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

import gr.cite.geoanalytics.environmental.data.retriever.OxygenRetriever;
import gr.cite.geoanalytics.environmental.data.retriever.TemperatureRetriever;
import gr.cite.geoanalytics.environmental.data.retriever.model.Unit;
import gr.i2s.fishgrowth.model.Fcr;
import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.Mortality;
import gr.i2s.fishgrowth.model.Sfr;
import gr.i2s.fishgrowth.model.Site;
import gr.i2s.fishgrowth.model.TableEntity;
import gr.i2s.fishgrowth.model.WeightLimit;

public class GlobalModelWrapper {
	private static final Logger logger = LoggerFactory.getLogger(GlobalModelWrapper.class);

	final Session session;

	public Collection<Fcr> fcrs;
	public Collection<Sfr> sfrs;
	public Collection<Mortality> mortalities;
	public Integer temperatureBiMonthly[];
	public Integer oxygenBiMonthly[];

	Long speciesId;

	Set<Long> similarSites;
	// Set<Long> similarModels;

	IEnvValuesProvider temperatureProvider;
	IEnvValuesProvider oxygenProvider;

	String uniqueId = null;

	String additionalSimilarityConstraint = null;

	private Integer acceptableSiteCount;

	private Integer upToGrade;

	private String myId;

	private GlobalModelWrapper(final Session session) {
		this.session = session;

		acceptableSiteCount = 1; // lenient on the count
		upToGrade = 1; // strict on the similarity

		temperatureBiMonthly = null;
		oxygenBiMonthly = null;

		similarSites = null;

	}

	public GlobalModelWrapper(final Session session, final String latitude, final String longitude,
			final Long speciesId, final TemperatureRetriever temperatureRetriever,
			final OxygenRetriever oxygenRetriever, String additionalSimilarityConstraint) throws Exception {
		this(session, latitude, longitude, speciesId, temperatureRetriever, oxygenRetriever, 1, 1, additionalSimilarityConstraint);
	}

	public GlobalModelWrapper(final Session session, final String latitude, final String longitude,
			final Long speciesId, final TemperatureRetriever temperatureRetriever,
			final OxygenRetriever oxygenRetriever, int acceptableSiteCount, int upToGrade, String additionalSimilarityConstraint) throws Exception {
		this(session);

		setAdditionalSimilarityConstraint(additionalSimilarityConstraint);

		this.acceptableSiteCount = acceptableSiteCount;
		this.upToGrade = upToGrade;

		this.speciesId = speciesId;

		try {
			temperatureProvider = new IEnvValuesProvider() {

				@Override
				public Integer[] getValues(String latitude, String longitude) throws Exception {
					if (logger.isTraceEnabled())
						logger.trace(String.format("asking service for oxygen table, based on lat/long"));
					Integer[] toRet = (temperatureRetriever == null ? new TemperatureRetriever() : temperatureRetriever)
							.getByLatLongAsArray(Double.valueOf(latitude), Double.valueOf(longitude), Unit.CELCIUS);

					return toRet;
				}
			};
		} catch (Exception e) {
			throw new UserFriendlyException("Could not retrieve temperature values provider.", e);
		}

		try {
			oxygenProvider = new IEnvValuesProvider() {

				@Override
				public Integer[] getValues(String latitude, String longitude) throws Exception {
					if (logger.isTraceEnabled())
						logger.trace(String.format("asking service for oxygen table, based on lat/long"));

					Integer[] toRet = (oxygenRetriever == null ? new OxygenRetriever() : oxygenRetriever)
							.getByLatLongAsArray(Double.valueOf(latitude), Double.valueOf(longitude), Unit.CELCIUS);
					return toRet;

				}
			};
		} catch (Exception e) {
			throw new UserFriendlyException("Could not retrieve oxygen values provider.", e);
		}

		Site site = discoverVirtualSite(latitude, longitude);
		fillData(site);

		this.myId = String.valueOf(speciesId) + "_" + String.valueOf(site.getId());

	}

	public String getMyId() {
		return myId;
	}

	/**
	 * 
	 * @param latitude
	 * @param longitude
	 * @return
	 * @throws Exception
	 */
	private Site discoverVirtualSite(String latitude, String longitude) throws Exception {
		// gather values
		Integer[] temps = temperatureProvider.getValues(latitude, longitude);
		Integer[] oxy = oxygenProvider.getValues(latitude, longitude);
		Site toRet = makeSite(latitude, longitude, temps, oxy);
		Set<Long> similars = new SiteUtil().setAdditionalSimilarityConstraint(this.additionalSimilarityConstraint)
				.findSitesSimilarToMe(session, toRet, acceptableSiteCount, upToGrade);
		toRet.setDesignation(DatabaseUtil.implodeGlobalName(similars));
		return toRet;
	}

	/**
	 * Called when we have a virtual site i.e. a site created on the fly for the
	 * GlobalModeler process
	 * 
	 * @param session
	 * @param site
	 * @param speciesId
	 * @throws Exception
	 */
	public GlobalModelWrapper(final Session session, final Site site, final Long speciesId) throws Exception {
		this(session);

		fillData(site);

		this.speciesId = speciesId;
	}

	/**
	 * called when we have an actual Modeler i.e. something the user created and
	 * filled. We rely on data similar to the modeler.<br/>
	 * <li>From the modeler we get the site
	 * <li>from the site we get the existing global site
	 * <li>from the global we get the existing similar sites
	 * 
	 * @param session
	 * @param modeler
	 * @throws Exception
	 */
	public GlobalModelWrapper(final Session session, final Modeler modeler) throws Exception {
		this(session);

		Long siteId = modeler.getSiteId();
		// get similar sites: strict on the similarity, lenient on the count
		Set<Long> mySimilarSites = new SiteUtil().setAdditionalSimilarityConstraint(additionalSimilarityConstraint)
				.findSitesSimilarToMeIncludingMe(session, (new SiteUtil().getSite(session, siteId)), 1, 1);
		Site global = new SiteUtil().getSiteAsGlobal(session, mySimilarSites);
		global.setDesignation(DatabaseUtil.implodeGlobalName(mySimilarSites));
		fillData(global);
		fillData(modeler);
	}

	/**
	 * The site should be a global site ie it should be named as
	 * DatabaseUtil.implodeGlobalName(listOfSimilarIds. The site.designation is
	 * our source of similarsite ids
	 * 
	 * @param site
	 * @return
	 * @throws Exception
	 */
	GlobalModelWrapper fillData(final Site site) throws Exception {
		temperatureBiMonthly = new Integer[24];
		int i = 0;
		temperatureBiMonthly[i++] = site.getPeriodJanA();
		temperatureBiMonthly[i++] = site.getPeriodJanB();
		temperatureBiMonthly[i++] = site.getPeriodFebA();
		temperatureBiMonthly[i++] = site.getPeriodFebB();
		temperatureBiMonthly[i++] = site.getPeriodMarA();
		temperatureBiMonthly[i++] = site.getPeriodMarB();
		temperatureBiMonthly[i++] = site.getPeriodAprA();
		temperatureBiMonthly[i++] = site.getPeriodAprB();
		temperatureBiMonthly[i++] = site.getPeriodMayA();
		temperatureBiMonthly[i++] = site.getPeriodMayB();
		temperatureBiMonthly[i++] = site.getPeriodJunA();
		temperatureBiMonthly[i++] = site.getPeriodJunB();
		temperatureBiMonthly[i++] = site.getPeriodJulA();
		temperatureBiMonthly[i++] = site.getPeriodJulB();
		temperatureBiMonthly[i++] = site.getPeriodAugA();
		temperatureBiMonthly[i++] = site.getPeriodAugB();
		temperatureBiMonthly[i++] = site.getPeriodSepA();
		temperatureBiMonthly[i++] = site.getPeriodSepB();
		temperatureBiMonthly[i++] = site.getPeriodOctA();
		temperatureBiMonthly[i++] = site.getPeriodOctB();
		temperatureBiMonthly[i++] = site.getPeriodNovA();
		temperatureBiMonthly[i++] = site.getPeriodNovB();
		temperatureBiMonthly[i++] = site.getPeriodDecA();
		temperatureBiMonthly[i++] = site.getPeriodDecB();

		oxygenBiMonthly = new Integer[24];
		i = 0;
		oxygenBiMonthly[i++] = site.getOxygenPeriodJanA();
		oxygenBiMonthly[i++] = site.getOxygenPeriodJanB();
		oxygenBiMonthly[i++] = site.getOxygenPeriodFebA();
		oxygenBiMonthly[i++] = site.getOxygenPeriodFebB();
		oxygenBiMonthly[i++] = site.getOxygenPeriodMarA();
		oxygenBiMonthly[i++] = site.getOxygenPeriodMarB();
		oxygenBiMonthly[i++] = site.getOxygenPeriodAprA();
		oxygenBiMonthly[i++] = site.getOxygenPeriodAprB();
		oxygenBiMonthly[i++] = site.getOxygenPeriodMayA();
		oxygenBiMonthly[i++] = site.getOxygenPeriodMayB();
		oxygenBiMonthly[i++] = site.getOxygenPeriodJunA();
		oxygenBiMonthly[i++] = site.getOxygenPeriodJunB();
		oxygenBiMonthly[i++] = site.getOxygenPeriodJulA();
		oxygenBiMonthly[i++] = site.getOxygenPeriodJulB();
		oxygenBiMonthly[i++] = site.getOxygenPeriodAugA();
		oxygenBiMonthly[i++] = site.getOxygenPeriodAugB();
		oxygenBiMonthly[i++] = site.getOxygenPeriodSepA();
		oxygenBiMonthly[i++] = site.getOxygenPeriodSepB();
		oxygenBiMonthly[i++] = site.getOxygenPeriodOctA();
		oxygenBiMonthly[i++] = site.getOxygenPeriodOctB();
		oxygenBiMonthly[i++] = site.getOxygenPeriodNovA();
		oxygenBiMonthly[i++] = site.getOxygenPeriodNovB();
		oxygenBiMonthly[i++] = site.getOxygenPeriodDecA();
		oxygenBiMonthly[i++] = site.getOxygenPeriodDecB();

		if (site.getDesignation() != null && !site.getDesignation().isEmpty()) {
			similarSites = DatabaseUtil.explodeGlobalName(site.getDesignation());

			uniqueId = site.getDesignation();
		}

		if (similarSites == null || similarSites.isEmpty())
			throw new Exception("No similar sites! Nothing to work on");

		return this;
	}

	public GlobalModelWrapper setTemperatureProvider(IEnvValuesProvider temperatureProvider) {
		this.temperatureProvider = temperatureProvider;
		return this;
	}

	public GlobalModelWrapper setOxygenProvider(IEnvValuesProvider oxygenProvider) {
		this.oxygenProvider = oxygenProvider;
		return this;
	}

	GlobalModelWrapper fillData(final Modeler modeler) {
		speciesId = modeler.getSpeciesId();

		return this;
	}

	public GlobalModelWrapper setAdditionalSimilarityConstraint(String additionalSimilarityConstraint) {
		this.additionalSimilarityConstraint = additionalSimilarityConstraint;
		return this;
	}

	public String getUniqueId() {
		if (uniqueId == null) {
			uniqueId = DatabaseUtil.implodeGlobalName(similarSites);
		}
		return uniqueId;
	}

	public GlobalModelWrapper create() throws Exception {
		long start, end;

		if (canContinue()) {
			start = System.currentTimeMillis();
			constructGlobalModel(session, similarSites);
			end = System.currentTimeMillis();
			if (logger.isTraceEnabled())
				logger.trace("global model constructGlobalModel " + (end - start));
		}

		return this;
	}

	public Map<Integer, RangeMap<Double, Double>> fillKPITable(final Collection<? extends TableEntity> list) {
		if (logger.isTraceEnabled())
			logger.trace(String.format("fcr list from db [%s]", list));
		// TODO sort it first
		Map<Integer, RangeMap<Double, Double>> table = new HashMap<Integer, RangeMap<Double, Double>>();
		double lastLimit = 0;
		double prevMab = Double.MAX_VALUE;
		int curTemp = -1;
		RangeMap<Double, Double> tempColumn = null;
		// should be sorted to temperature asc, then weight desc
		for (TableEntity item : list) {
			if (item.getTemperature() != curTemp) {
				// temperature value changed; save previous
				if (tempColumn != null) {
					// take care of the lower limit
					if (lastLimit > 0) {
						tempColumn.put(Range.closedOpen(0.0, lastLimit), 0.0);
					}
					table.put(curTemp, tempColumn);
				}

				curTemp = item.getTemperature();
				// reset
				tempColumn = TreeRangeMap.create();
				prevMab = Double.MAX_VALUE;
			}
			double mab = item.getFromWeight();
			tempColumn.put(Range.closedOpen(mab, prevMab), item.getValue());
			lastLimit = mab;
			prevMab = mab;
		}
		// last temperature didn't get a chance to see a temperature value
		// change
		if (tempColumn != null) {
			if (lastLimit > 0) {
				tempColumn.put(Range.closedOpen(0.0, lastLimit), 0.0);
			}
			table.put(curTemp, tempColumn);
		}
		return table;
	}

	private void constructGlobalModel(Session session, Set<Long> similarSites) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("retrieving global model KPIs"));
		}

		Set<Long> similarModels = new TreeSet<>(
				new ModelerUtil().getModelerIdsForSites(session, speciesId, new ArrayList<Long>(similarSites)));

		if (similarModels == null || similarModels.isEmpty()) {
			throw new Exception("No similar models found! Nothing to do. SimilarSites are " + similarSites);
		}

		Collection<Long> modelIdsList = new ArrayList<>(similarModels);
		long start = System.currentTimeMillis();
		Map<Integer, Collection<Double>> commonWeights = new ModelerUtil().getCommonWeightLimits(session, modelIdsList);
		long end = System.currentTimeMillis();
		if (logger.isTraceEnabled())
			logger.trace("global model commonWeights " + (end - start));

		start = System.currentTimeMillis();
		fcrs = new ModelerUtil().getCommonKPIValues(session, WeightLimit.KPI_KIND_FCR,
				commonWeights.get(WeightLimit.KPI_KIND_FCR), modelIdsList);
		end = System.currentTimeMillis();
		if (logger.isTraceEnabled())
			logger.trace("global model fcrs " + (end - start));

		start = System.currentTimeMillis();
		sfrs = new ModelerUtil().getCommonKPIValues(session, WeightLimit.KPI_KIND_SFR,
				commonWeights.get(WeightLimit.KPI_KIND_SFR), modelIdsList);
		end = System.currentTimeMillis();
		if (logger.isTraceEnabled())
			logger.trace("global model sfrs " + (end - start));

		start = System.currentTimeMillis();
		mortalities = new ModelerUtil().getCommonKPIValues(session, WeightLimit.KPI_KIND_MORTALITY,
				commonWeights.get(WeightLimit.KPI_KIND_MORTALITY), modelIdsList);
		end = System.currentTimeMillis();
		if (logger.isTraceEnabled())
			logger.trace("global model mortalities " + (end - start));
	}

	/**
	 * allow or forbid based on certain rules like privacy, number of
	 * participant data etc
	 * 
	 * @return
	 */
	private boolean canContinue() {
		// lame sample
		if (similarSites.size() < 1)
			return false;

		return true;
	}

	private Site makeSite(String latitude, String longitude, Integer[] temp, Integer[] oxy) {
		Site site = new Site();

		site.setLatitude(latitude);
		site.setLongitude(longitude);

		int i = 0;
		site.setPeriodJanA(temp[i++]);
		site.setPeriodJanB(temp[i++]);
		site.setPeriodFebA(temp[i++]);
		site.setPeriodFebB(temp[i++]);
		site.setPeriodMarA(temp[i++]);
		site.setPeriodMarB(temp[i++]);
		site.setPeriodAprA(temp[i++]);
		site.setPeriodAprB(temp[i++]);
		site.setPeriodMayA(temp[i++]);
		site.setPeriodMayB(temp[i++]);
		site.setPeriodJunA(temp[i++]);
		site.setPeriodJunB(temp[i++]);
		site.setPeriodJulA(temp[i++]);
		site.setPeriodJulB(temp[i++]);
		site.setPeriodAugA(temp[i++]);
		site.setPeriodAugB(temp[i++]);
		site.setPeriodSepA(temp[i++]);
		site.setPeriodSepB(temp[i++]);
		site.setPeriodOctA(temp[i++]);
		site.setPeriodOctB(temp[i++]);
		site.setPeriodNovA(temp[i++]);
		site.setPeriodNovB(temp[i++]);
		site.setPeriodDecA(temp[i++]);
		site.setPeriodDecB(temp[i++]);
		int sum = 0;
		for (i = 0; i < temp.length; i++)
			sum += temp[i];
		site.setPeriodYear(sum / temp.length);

		i = 0;
		site.setOxygenPeriodJanA(oxy[i++]);
		site.setOxygenPeriodJanB(oxy[i++]);
		site.setOxygenPeriodFebA(oxy[i++]);
		site.setOxygenPeriodFebB(oxy[i++]);
		site.setOxygenPeriodMarA(oxy[i++]);
		site.setOxygenPeriodMarB(oxy[i++]);
		site.setOxygenPeriodAprA(oxy[i++]);
		site.setOxygenPeriodAprB(oxy[i++]);
		site.setOxygenPeriodMayA(oxy[i++]);
		site.setOxygenPeriodMayB(oxy[i++]);
		site.setOxygenPeriodJunA(oxy[i++]);
		site.setOxygenPeriodJunB(oxy[i++]);
		site.setOxygenPeriodJulA(oxy[i++]);
		site.setOxygenPeriodJulB(oxy[i++]);
		site.setOxygenPeriodAugA(oxy[i++]);
		site.setOxygenPeriodAugB(oxy[i++]);
		site.setOxygenPeriodSepA(oxy[i++]);
		site.setOxygenPeriodSepB(oxy[i++]);
		site.setOxygenPeriodOctA(oxy[i++]);
		site.setOxygenPeriodOctB(oxy[i++]);
		site.setOxygenPeriodNovA(oxy[i++]);
		site.setOxygenPeriodNovB(oxy[i++]);
		site.setOxygenPeriodDecA(oxy[i++]);
		site.setOxygenPeriodDecB(oxy[i++]);
		sum = 0;
		for (i = 0; i < oxy.length; i++)
			sum += oxy[i];
		site.setOxygenPeriodYear(sum / oxy.length);

		return site;
	}

	static public interface IEnvValuesProvider {
		Integer[] getValues(String latitude, String longitude) throws Exception;
	}

}
