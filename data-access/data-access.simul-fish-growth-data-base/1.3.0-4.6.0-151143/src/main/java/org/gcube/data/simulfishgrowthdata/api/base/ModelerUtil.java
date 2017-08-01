package org.gcube.data.simulfishgrowthdata.api.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.gcube.data.simulfishgrowthdata.model.UnivariateOutlierDetector;
import org.gcube.data.simulfishgrowthdata.model.UnivariateOutlierDetector.IValue;
import org.gcube.data.simulfishgrowthdata.model.UnivariateOutlierDetector.SimpleValue;
import org.gcube.data.simulfishgrowthdata.util.DatabaseUtil;
import org.gcube.data.simulfishgrowthdata.util.ExcelReader;
import org.gcube.data.simulfishgrowthdata.util.HibernateUtil;
import org.gcube.data.simulfishgrowthdata.util.UserFriendlyException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import gr.i2s.fishgrowth.model.Fcr;
import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.Mortality;
import gr.i2s.fishgrowth.model.SampleData;
import gr.i2s.fishgrowth.model.Sfr;
import gr.i2s.fishgrowth.model.Sgr;
import gr.i2s.fishgrowth.model.TableEntity;
import gr.i2s.fishgrowth.model.Usage;
import gr.i2s.fishgrowth.model.WeightLimit;

public class ModelerUtil extends BaseUtil {

	public static final Long STATUS_READY = 1L;
	public static final Long STATUS_PENDING_KPI = 2L;
	public static final Long STATUS_FAILED_KPI = 3L;

	public Modeler add(Modeler modeler) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			add(session, modeler);

			session.getTransaction().commit();
			return modeler;
		} catch (Exception e) {
			logger.info(String.format("Could not add modeler [%s]", modeler), e);
			throw new Exception(String.format("Could not add modeler [%s]", modeler), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public Modeler add(Session session, Modeler modeler) throws Exception {
		session.save(modeler);

		manageUploadFiles(session, modeler);

		Modeler global = globalBaseCopy(modeler);
		session.save(global);

		session.flush();

		return modeler;
	}

	Modeler globalBaseCopy(final Modeler copy) {
		Modeler toRet = new Modeler();
		toRet.setOwnerId(DatabaseUtil.GLOBAL_OWNER);
		toRet.setDesignation(String.valueOf(copy.getId()));
		toRet.setSpeciesId(copy.getSpeciesId());
		toRet.setBroodstockQualityId(copy.getBroodstockQualityId());
		toRet.setBroodstockGeneticImprovement(copy.isBroodstockGeneticImprovement());
		toRet.setFeedQualityId(copy.getFeedQualityId());
		toRet.setStatusId(ModelerUtil.STATUS_FAILED_KPI);
		return toRet;
	}

	public Modeler update(Modeler modeler) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			update(session, modeler);

			session.getTransaction().commit();
			return modeler;
		} catch (Exception e) {
			logger.info(String.format("Could not update modeler [%s]", modeler), e);
			throw new Exception(String.format("Could not update modeler [%s]", modeler), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public Modeler update(final Session session, final Modeler modeler) throws Exception {
		session.update(modeler);
		manageUploadFiles(session, modeler);
		session.flush();
		return modeler;
	}

	public boolean delete(Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			Modeler modeler = getModeler(session, id);
			if (modeler != null) {
				if (delete(session, modeler)) {
					session.flush();
				}
			}

			session.getTransaction().commit();
			return true;
		} catch (Exception e) {
			logger.info(String.format("Could not delete modeler [%s]", id), e);
			throw new Exception(String.format("Could not delete modeler [%s]", id), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	// TODO move in model.Site
	static synchronized boolean isGlobal(Modeler modeler) {
		return DatabaseUtil.GLOBAL_OWNER.equalsIgnoreCase(modeler.getOwnerId());
	}

	public Modeler getModeler(Long id) throws Exception {
		Session session = null;

		try {
			session = HibernateUtil.openSession();

			session.beginTransaction();

			Modeler modeler = getModeler(session, id);

			session.getTransaction().commit();

			return modeler;
		} catch (Exception e) {
			logger.info(String.format("Modeler not found [%s]", id), e);
			throw new Exception(String.format("Modeler not found [%s]", id), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public Modeler getModeler(Session session, Long id) throws Exception {
		Modeler modeler = (Modeler) session.get(Modeler.class, Long.valueOf(id));
		return modeler;
	}

	public List<Modeler> getModelers(String ownerId, List<Long> statuses) throws Exception {
		Session session = null;

		try {
			logger.trace(String.format("start getModelers"));
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();

			Query q = session.createQuery(_GET_ALL_ON_OWNERID).setParameter("ownerid", ownerId);

			List<Modeler> list = q.list();
			if (statuses != null && !statuses.isEmpty())
				for (ListIterator<Modeler> iter = list.listIterator(); iter.hasNext();) {
					Modeler m = (Modeler) iter.next();
					if (!statuses.contains(m.getStatusId())) {
						iter.remove();
					}
				}

			session.getTransaction().commit();

			logger.trace(String.format("return Modelers %s", list));
			return list;
		} catch (Exception e) {
			logger.info(String.format("Could not retrieve modelers for [%s]", ownerId), e);
			throw new Exception(String.format("Could not retrieve modelers for [%s]", ownerId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	public void cleanKPIs(Long id) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("cleaning KPIs for model %s", id));
		}
		Session session = null;

		try {
			session = HibernateUtil.openSession();
			logger.trace(String.format("session [%s]", session));

			session.beginTransaction();
			cleanKPIs(session, id);
			session.getTransaction().commit();
		} catch (Exception e) {
			logger.info(String.format("Could not clean KPIs for model [%s]", id), e);
			throw new Exception(String.format("Could not clean KPIs for model [%s]", id), e);
		} finally {
			HibernateUtil.closeSession(session);
		}

	}

	public void cleanKPIs(Session session, Long id) {
		new SfrUtil().deleteAll(session, id);
		new FcrUtil().deleteAll(session, id);
		new MortalityUtil().deleteAll(session, id);
	}

	/**
	 * 
	 * @param session
	 * @param id
	 * @param uploadFileType
	 * @param uploadFileLocation
	 * @return the fileLocation because it will be used in the array
	 * @throws Exception
	 */
	private String importRemote(Session session, int kind, long id, String uploadFileType, String uploadFileLocation)
			throws Exception {
		if (kind == ExcelReader.KIND_SAMPLE) {
			String toRet = null;
			if (!Strings.isNullOrEmpty(uploadFileType)) {
				if ("xls".equalsIgnoreCase(uploadFileType) || "xlsx".equalsIgnoreCase(uploadFileType)) {
					ExcelReader.instance(ExcelReader.KIND_SAMPLE).importRemote(session, id, uploadFileLocation);
					toRet = uploadFileLocation;
				}
			}
			if (toRet != null) {
				markOutliers(session, id);
				return toRet;
			}
			logger.error(String.format("uknown type [%s] for id [%s]", uploadFileType, id));
		} else if (kind == ExcelReader.KIND_LIMITS) {
			if (!Strings.isNullOrEmpty(uploadFileType)) {
				if ("xls".equalsIgnoreCase(uploadFileType) || "xlsx".equalsIgnoreCase(uploadFileType)) {
					ExcelReader.instance(ExcelReader.KIND_LIMITS).importRemote(session, id, uploadFileLocation);
					return uploadFileLocation;
				}
				logger.error(String.format("uknown type [%s] for id [%s]", uploadFileType, id));
			}
		}
		return null;
	}

	static class SampleDatabase extends SimpleValue {
		Long idx;

		public SampleDatabase(final Long idx, final Double value) {
			super(value);
			this.idx = idx;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("SampleValue at [").append(idx).append("] = [").append(value).append("]");
			return builder.toString();
		}

	}

	/**
	 * 
	 * @param session
	 * @param modelid
	 * @return message concerning the process
	 * @throws Exception
	 */
	public String markOutliers(Session session, long modelid) throws Exception {
		String toRet = "";

		Double lowerperc = 25.0;
		Double upperperc = 75.0;

		Set<Long> toMark = new HashSet<Long>();

		List<SampleData> samples = getSampleData(session, modelid);

		List<IValue> mortality = new ArrayList<IValue>();
		List<IValue> sfr = new ArrayList<IValue>();
		List<IValue> fcr = new ArrayList<IValue>();

		for (SampleData sample : samples) {
			mortality.add(new SampleDatabase(sample.getId(), sample.getMortalityRate()));
			sfr.add(new SampleDatabase(sample.getId(), sample.getSfr()));
			fcr.add(new SampleDatabase(sample.getId(), sample.getFcr()));
		}
		UnivariateOutlierDetector detector = new UnivariateOutlierDetector().addValues(mortality)
				.defineLowerPercentage(lowerperc).defineUpperPercentage(upperperc).execute();
		detector.cleanFromOutliers();
		for (IValue value : detector.getOutliers()) {
			SampleDatabase sampleDatabase = (SampleDatabase) value;
			toMark.add(sampleDatabase.idx);
		}

		detector.cleanOutliers().cleanValues().addValues(sfr).execute().cleanFromOutliers();
		for (IValue value : detector.getOutliers()) {
			SampleDatabase sampleDatabase = (SampleDatabase) value;
			toMark.add(sampleDatabase.idx);
		}

		detector.cleanOutliers().cleanValues().addValues(fcr).execute().cleanFromOutliers();
		for (IValue value : detector.getOutliers()) {
			SampleDatabase sampleDatabase = (SampleDatabase) value;
			toMark.add(sampleDatabase.idx);
		}

		if (!toMark.isEmpty()) {
			final SQLQuery q = session.createSQLQuery(_UPDATE_SAMPLE_DATA_OUTLIERS);
			q.setParameterList("ids", toMark);
			q.setParameter("inclusion", 0);
			q.executeUpdate();
		}

		if (toMark.size() > (samples.size() / 2)) {
			toRet = String.format("Too many outliers (%.2f)", toMark.size() / samples.size());
		}
		return toRet;

	}

	private void manageUploadFiles(Session session, Modeler modeler) throws UserFriendlyException {
		try {
			manageUploadFile(session, modeler.getId(), ExcelReader.KIND_SAMPLE, modeler.getUploadFileTypeData(),
					modeler.getUploadFileLocationData());
		} catch (Exception e) {
			throw new UserFriendlyException("Could not upload SampleData file", e);
		}
		try {
			manageUploadFile(session, modeler.getId(), ExcelReader.KIND_LIMITS, modeler.getUploadFileTypeWeights(),
					modeler.getUploadFileLocationWeights());
		} catch (Exception e) {
			throw new UserFriendlyException("Could not upload WeightCategories file", e);
		}
	}

	private void manageUploadFile(Session session, long id, int kind, String type, String location) throws Exception {
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("managing upload data file for [%s], of type [%s], located at [%s]", id, type,
					location));
		}
		String uploadData = Strings.nullToEmpty(location);
		// clean existing and unused
		String existingSQL = kind == ExcelReader.KIND_SAMPLE ? _GET_EXISTING_UPLOAD_SOURCE_SAMPLE
				: _GET_EXISTING_UPLOAD_SOURCE_LIMITS;
		Query qUploadSource = session.createQuery(existingSQL).setParameter("simulModelId", id);
		List<String> existingUploadSource = qUploadSource.list();
		if (logger.isTraceEnabled()) {
			logger.trace(String.format("existing uploads for %s are [%s]", id, existingUploadSource));
		}
		if (!existingUploadSource.isEmpty())
			if (uploadData.equals(existingUploadSource.get(0))) {
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("upload data file for %s set to same value; bypassing", id));
				}
				return;
			} else {
				// clean previous
				int recsCleaned;
				if (kind == ExcelReader.KIND_SAMPLE)
					recsCleaned = cleanSampleData(session, id);
				else
					recsCleaned = cleanWeightLimits(session, id);
				if (logger.isTraceEnabled()) {
					logger.trace(String.format("upload data file for [%s] changed; erased [%s] mismatching records", id,
							recsCleaned));
				}
			}
		if (!Strings.isNullOrEmpty(uploadData))
			importRemote(session, kind, id, type, uploadData);
	}

	public List<Modeler> getModelersForSite(Session session, Long siteid) {
		try {
			logger.trace(String.format("start getModelersForSites"));

			Query q = session.createQuery(_GET_ALL_ON_SITE).setParameter("siteid", siteid);

			List<Modeler> list = q.list();

			logger.trace(String.format("return Modelers %s", list));
			return list;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not retrieve modelers for site [%s]", siteid), e);
		}
	}

	public List<Long> getModelerIdsForSites(Session session, Long speciesId, List<Long> siteids) {
		List<Long> toRet = new ArrayList<>();
		if (!siteids.isEmpty()) {
			try {
				logger.trace(String.format("start getModelersForSites"));

				Query q = session.createSQLQuery(_GET_ALL_IDS_ON_SITES).setParameterList("siteids", siteids)
						.setParameter("speciesId", speciesId);

				toRet = q.list();

				logger.trace(String.format("return Modeler ids %s", toRet));
			} catch (Exception e) {
				throw new RuntimeException(String.format("Could not retrieve modeler ids for sites [%s]", siteids), e);
			}
		}
		return toRet;
	}

	public List<WeightLimit> getWeightLimits(Session session, Long simulModelId) {
		try {
			logger.trace(String.format("start getModelersForSites"));

			Query q = session.createQuery(_GET_WEIGHT_LIMITS).setParameter("simulModelId", simulModelId);

			List<WeightLimit> list = q.list();

			logger.trace(String.format("return Modelers %s", list));
			return list;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not retrieve modelers for site [%s]", simulModelId), e);
		}
	}

	public int cleanSampleData(Session session, Long id) {
		final SQLQuery q = session.createSQLQuery(_DELETE_ALL_SAMPLE_DATA);
		q.setParameter("simulModelId", id);
		return q.executeUpdate();
	}

	public int cleanWeightLimits(Session session, Long id) {
		final SQLQuery q = session.createSQLQuery(_DELETE_ALL_LIMITS_DATA);
		q.setParameter("simulModelId", id);
		return q.executeUpdate();
	}

	public boolean delete(Session session, Modeler modeler) {
		try {
			if (modeler == null) {
				return false;
			}
			if (!isGlobal(modeler))
				delete(session, getGlobal(session, modeler.getId()));

			cleanSampleData(session, modeler.getId());
			cleanWeightLimits(session, modeler.getId());
			cleanKPIs(session, modeler.getId());
			session.delete(modeler);

			return true;
		} catch (Exception e) {
			throw new RuntimeException(String.format("Could not delete Modeler  [%s]", modeler), e);
		}
	}

	public List<Usage> getUsage(String ownerId) throws Exception {
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
			throw new Exception(String.format("Could not retrieve site usage for ownerid [%s]", ownerId), e);
		} finally {
			HibernateUtil.closeSession(session);
		}
	}

	static public class CommonWeightLimitsType {
		Integer kpiKind;
		Double fromWeight;

		public Integer getKpiKind() {
			return kpiKind;
		}

		public void setKpiKind(Integer kpiKind) {
			this.kpiKind = kpiKind;
		}

		public Double getFromWeight() {
			return fromWeight;
		}

		public void setFromWeight(Double fromWeight) {
			this.fromWeight = fromWeight;
		}

		public CommonWeightLimitsType() {

		}

		public CommonWeightLimitsType(Integer kpiKind, Double fromWeight) {
			this.kpiKind = kpiKind;
			this.fromWeight = fromWeight;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("CommonWeightLimitsType [kpiKind=").append(kpiKind).append(", fromWeight=")
					.append(fromWeight).append("]");
			return builder.toString();
		}

	}

	/**
	 * 
	 * @param session
	 * @param modelIds
	 * @return Map<kpiKind, List<fromWeight>>
	 * @throws Exception
	 */
	public Map<Integer, Collection<Double>> getCommonWeightLimits(Session session, Collection<Long> modelIds)
			throws Exception {
		try {
			Query q = session.createSQLQuery(_GET_COMMON_WEIGHT_LIMITS).addScalar("kpiKind", new IntegerType())
					.addScalar("fromWeight", new DoubleType()).setParameter("kindFcr", WeightLimit.KPI_KIND_FCR)
					.setParameter("kindSfr", WeightLimit.KPI_KIND_SFR)
					.setParameter("kindMortality", WeightLimit.KPI_KIND_MORTALITY)
					.setParameterList("modelIds", modelIds)

					.setResultTransformer(Transformers.aliasToBean(CommonWeightLimitsType.class));

			Collection<CommonWeightLimitsType> listQuery = q.list();

			logger.trace(String.format("got from db commonWeightLimits %s", listQuery));

			Map<Integer, Collection<Double>> toRet = new HashMap<>();

			Integer curKpiKind = Integer.MIN_VALUE;
			Collection<Double> weights = null;
			for (CommonWeightLimitsType item : listQuery) {
				Integer kpiKind = item.kpiKind;
				if (!kpiKind.equals(curKpiKind)) {
					if (weights != null && !weights.isEmpty()) {
						toRet.put(curKpiKind, weights);
					}
					curKpiKind = kpiKind;
					weights = new ArrayList<>();
				}
				weights.add(item.fromWeight);

			}
			if (weights != null && !weights.isEmpty()) {
				toRet.put(curKpiKind, weights);
			}

			logger.trace(String.format("return commonWeightLimits %s", toRet));
			return toRet;
		} catch (Exception e) {
			logger.error(String.format("Could not getCommonWeightLimits for modelIds [%s]", modelIds), e);
			throw new Exception(String.format("Could not getCommonWeightLimits for modelIds [%s]", modelIds), e);
		}
	}

	static public class KPIValuesType {
		Long simulModelId;
		Integer temperature;
		Double fromWeight;
		Double value;

		public Long getSimulModelId() {
			return simulModelId;
		}

		public void setSimulModelId(Long simulModelId) {
			this.simulModelId = simulModelId;
		}

		public Integer getTemperature() {
			return temperature;
		}

		public void setTemperature(Integer temperature) {
			this.temperature = temperature;
		}

		public Double getFromWeight() {
			return fromWeight;
		}

		public void setFromWeight(Double fromWeight) {
			this.fromWeight = fromWeight;
		}

		public Double getValue() {
			return value;
		}

		public void setValue(Double value) {
			this.value = value;
		}

		public KPIValuesType() {

		}

		public KPIValuesType(Long simulModelId, Integer temperature, Double fromWeight, Double value) {
			this.simulModelId = simulModelId;
			this.temperature = temperature;
			this.fromWeight = fromWeight;
			this.value = value;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("KPIValuesType [simulModelId=").append(simulModelId).append(", temperature=")
					.append(temperature).append(", fromWeight=").append(fromWeight).append(", value=").append(value)
					.append("]");
			return builder.toString();
		}
	}

	/**
	 * 
	 * @param session
	 * @param kpiKind
	 * @param modelIds
	 * @return List<? Extends TableEntity: Fcr/Sfr/Sgr/Mortality>
	 * @throws Exception
	 */
	public Collection getCommonKPIValues(Session session, Integer kpiKind, Collection<Double> commonWeightLimits,
			Collection<Long> modelIds) throws Exception {
		try {
			String kpiTableName;
			if (kpiKind == WeightLimit.KPI_KIND_FCR)
				kpiTableName = "Fcr";
			else if (kpiKind == WeightLimit.KPI_KIND_SFR)
				kpiTableName = "Sfr";
			else if (kpiKind == WeightLimit.KPI_KIND_SGR)
				kpiTableName = "Sgr";
			else if (kpiKind == WeightLimit.KPI_KIND_MORTALITY)
				kpiTableName = "Mortality";
			else
				throw new Exception(String.format("Unknown kpiKind [%s]", kpiKind));

			long start = System.currentTimeMillis();
			Query q = session.createSQLQuery(_GET_ALL_KPI_VALUES.replace(":kpiTable", kpiTableName))
					.addScalar("simulModelId", new LongType()).addScalar("temperature", new IntegerType())
					.addScalar("fromWeight", new DoubleType()).addScalar("value", new DoubleType())
					.setParameterList("modelIds", modelIds)
					.setResultTransformer(Transformers.aliasToBean(KPIValuesType.class));

			Collection<KPIValuesType> listQuery = q.list();
			long end = System.currentTimeMillis();

			logger.trace("global model kpi retrieve  " + (end - start));

			logger.trace(String.format("got from db getCommonKPIValues %s", listQuery));

			start = System.currentTimeMillis();
			List<TableEntity> toRet = new ArrayList<>();

			Map<Long, Map<Integer, Map<Double, Double>>> simulModels = new TreeMap<>();
			{
				Long curSimulModelId = Long.MIN_VALUE;
				Integer curTemperature = Integer.MIN_VALUE;
				Map<Double, Double> values = null;
				Map<Integer, Map<Double, Double>> temps = null;
				for (Iterator<KPIValuesType> iterQuery = listQuery.iterator(); iterQuery.hasNext();) {
					KPIValuesType item = iterQuery.next();

					Long simulModelId = item.simulModelId;
					if (!simulModelId.equals(curSimulModelId)) {
						if (values != null) { // leftovers
							Map<Double, Double> expandedValues = expandAccordingToCommonWeights(commonWeightLimits,
									values);
							temps.put(curTemperature, expandedValues); // replace
																		// it
						}

						temps = new TreeMap<>();
						curTemperature = Integer.MIN_VALUE;
						values = null;
						simulModels.put(simulModelId, temps);
						curSimulModelId = simulModelId;
					}

					Integer temperature = item.temperature;
					if (!temperature.equals(curTemperature)) {
						if (values != null) {
							Map<Double, Double> expandedValues = expandAccordingToCommonWeights(commonWeightLimits,
									values);
							temps.put(curTemperature, expandedValues); // replace it
						}

						values = new TreeMap<>();
						temps.put(temperature, values);
						curTemperature = temperature;
					}

					values.put(item.fromWeight, item.value);

					if (!iterQuery.hasNext()) {
						// wrap it up
						if (values != null) {
							Map<Double, Double> expandedValues = expandAccordingToCommonWeights(commonWeightLimits,
									values);
							temps.put(curTemperature, expandedValues); // replace
																		// it
						}
					}
				}
			}
			end = System.currentTimeMillis();
			if (logger.isTraceEnabled())
				logger.trace("global model kpi step 1  " + (end - start));
			logger.trace(String.format("retrieved %s", simulModels));

			start = System.currentTimeMillis();
			{
				// build the kpi list, at last!

				// not all models have the same temperatures so I parse from
				// very low to very high :}
				for (int temperature = 0; temperature <= 50; temperature++) {
					int countAdded = 0;
					for (Double weight : commonWeightLimits) {
						Double sum = 0.0;
						Integer count = 0;
						for (Long simulModel : simulModels.keySet()) {
							Map<Double, Double> values = simulModels.get(simulModel).get(temperature);
							if (values != null) {
								Double value = values.get(weight);
								if (value != null) {
									sum += value;
									count++;
								}
							}
						}
						if (count > 0) {
							TableEntity toAdd = createTableEntity(kpiKind);
							toAdd.setTemperature(temperature);
							toAdd.setFromWeight(weight);
							toAdd.setValue(sum / (double) count);
							toRet.add(toAdd);
							countAdded++;
						}
					}
					logger.trace(String.format("for temperature [%s] I have [%s] KPIs of kind [%s]", temperature,
							countAdded, kpiKind));
				}
			}
			end = System.currentTimeMillis();
			if (logger.isTraceEnabled())
				logger.trace("global model kpi step 2  " + (end - start));

			logger.trace(String.format("return getCommonKPIValues %s", toRet));
			return toRet;
		} catch (Exception e) {
			logger.error(String.format("Could not getCommonKPIValues for modelIds [%s]", modelIds), e);
			throw new Exception(String.format("Could not getCommonKPIValues for modelIds [%s]", modelIds), e);
		}
	}

	TableEntity createTableEntity(int kpiKind) {
		if (kpiKind == WeightLimit.KPI_KIND_FCR)
			return new Fcr();
		else if (kpiKind == WeightLimit.KPI_KIND_SFR)
			return new Sfr();
		else if (kpiKind == WeightLimit.KPI_KIND_SGR)
			return new Sgr();
		else if (kpiKind == WeightLimit.KPI_KIND_MORTALITY)
			return new Mortality();
		else
			throw new RuntimeException(String.format("Unknown kpiKind [%s]", kpiKind));

	}

	private Map<Double, Double> expandAccordingToCommonWeights(Collection<Double> commonWeightLimits,
			Map<Double, Double> values) {
		// expand value according to commonWeights
		Map<Double, Double> expandedValues = new TreeMap<>();
		// start with the 1st value
		Double lastValue = null;
		for (Iterator<Double> iterCommon = commonWeightLimits.iterator(); iterCommon.hasNext();) {
			Double weight = iterCommon.next();
			if (values.containsKey(weight)) {
				lastValue = values.get(weight);
			}
			expandedValues.put(weight, lastValue);
		}
		return expandedValues;
	}

	public int updateStatusOnSite(Session session, Long siteId, Long statusId) {
		final SQLQuery q = session.createSQLQuery(_UPDATE_STATUS_ON_SITEID);
		q.setParameter("siteId", siteId);
		q.setParameter("statusId", statusId);
		return q.executeUpdate();
	}

	public Modeler getGlobal(Session session, Long id) {
		Modeler toRet = null;
		final Query q = session.createQuery(_GET_GLOBAL).setParameter("designation", String.valueOf(id))
				.setParameter("ownerId", DatabaseUtil.GLOBAL_OWNER);
		List<Modeler> results = q.list();
		if (results != null && !results.isEmpty()) {
			toRet = results.get(0);
		}
		return toRet;
	}

	public List<SampleData> getSampleData(Session session, Long simulModelId) {
		final Query q = session.createQuery(_GET_ALL_SAMPLE_DATA_ON_SIMULMODELID).setParameter("simulModelId",
				simulModelId);
		List<SampleData> toRet = q.list();
		if (toRet == null) {
			toRet = new ArrayList<>();
		}
		return toRet;
	}

	private static final String _UPDATE_STATUS_ON_SITEID = "UPDATE SimulModel SET statusId=:statusId WHERE siteId = :siteId";
	private static final String _GET_GLOBAL = "FROM gr.i2s.fishgrowth.model.Modeler s WHERE s.designation = :designation AND s.ownerId = :ownerId";
	private static final String _GET_ALL_ON_OWNERID = "FROM gr.i2s.fishgrowth.model.Modeler s WHERE s.ownerId = :ownerid ORDER BY s.designation ASC";
	private static final String _GET_EXISTING_UPLOAD_SOURCE_SAMPLE = "SELECT DISTINCT uploadSource FROM gr.i2s.fishgrowth.model.SampleData s WHERE s.simulModelId = :simulModelId";
	private static final String _GET_EXISTING_UPLOAD_SOURCE_LIMITS = "SELECT DISTINCT uploadSource FROM gr.i2s.fishgrowth.model.WeightLimit s WHERE s.simulModelId = :simulModelId";
	private static final String _DELETE_ALL_SAMPLE_DATA = "DELETE FROM SampleData s WHERE s.simulModelId = :simulModelId";
	private static final String _DELETE_ALL_LIMITS_DATA = "DELETE FROM WeightLimit s WHERE s.simulModelId = :simulModelId";
	private static final String _GET_ALL_ON_SITE = "FROM gr.i2s.fishgrowth.model.Modeler s WHERE s.siteId = :siteid ORDER BY s.id ASC";
	private static final String _GET_ALL_IDS_ON_SITES = "SELECT id FROM SimulModel WHERE speciesId=:speciesId AND siteId IN (:siteids) ORDER BY id ASC";
	private static final String _GET_USAGE_ON_OWNERID = "SELECT us.id as id, us.scenariocount as usage FROM simulusageview us inner join simulmodel e on (us.id=e.id) WHERE e.ownerId = :ownerid ORDER BY us.id ASC";
	private static final String _GET_WEIGHT_LIMITS = "FROM gr.i2s.fishgrowth.model.WeightLimit s WHERE s.simulModelId = :simulModelId";
	private static final String _GET_COMMON_WEIGHT_LIMITS = "(SELECT DISTINCT :kindFcr AS \"kpiKind\", fromWeight AS \"fromWeight\" FROM fcr WHERE simulModelId IN (:modelIds) UNION SELECT DISTINCT :kindSfr AS \"kpiKind\", fromWeight AS \"fromWeight\" FROM sfr WHERE simulModelId IN (:modelIds) UNION SELECT DISTINCT :kindMortality AS \"kpiKind\", fromWeight AS \"fromWeight\" FROM mortality WHERE simulModelId IN (:modelIds)) ORDER BY \"kpiKind\", \"fromWeight\"";
	/*-
	private static final String _GET_COMMON_WEIGHT_LIMITS = "SELECT DISTINCT kpiKind AS \"kpiKind\", toWeight AS \"toWeight\" FROM WeightLimit WHERE simulModelId IN (:modelIds) ORDER BY kpiKind, toWeight";
	*/
	private static final String _GET_ALL_KPI_VALUES = "SELECT DISTINCT simulModelId AS \"simulModelId\", temperature AS \"temperature\", fromWeight AS \"fromWeight\", value AS \"value\" FROM :kpiTable WHERE simulModelId IN (:modelIds) ORDER BY simulModelId ASC, temperature ASC, fromWeight ASC";
	private static final String _GET_ALL_SAMPLE_DATA_ON_SIMULMODELID = "FROM gr.i2s.fishgrowth.model.SampleData s WHERE s.simulModelId = :simulModelId";
	private static final String _UPDATE_SAMPLE_DATA_OUTLIERS = "UPDATE SampleData s SET inclusion=:inclusion WHERE s.id in (:ids)";

	private static final Logger logger = LoggerFactory.getLogger(ModelerUtil.class);
}
