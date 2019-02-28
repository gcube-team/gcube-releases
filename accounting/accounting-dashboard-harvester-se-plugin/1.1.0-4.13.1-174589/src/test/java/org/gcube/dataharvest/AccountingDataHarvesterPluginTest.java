package org.gcube.dataharvest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.gcube.accounting.accounting.summary.access.AccountingDao;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.internal.Dimension;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.dataharvest.datamodel.HarvestedDataKey;
import org.gcube.dataharvest.harvester.MethodInvocationHarvester;
import org.gcube.dataharvest.harvester.SocialInteractionsHarvester;
import org.gcube.dataharvest.harvester.VREAccessesHarvester;
import org.gcube.dataharvest.harvester.VREUsersHarvester;
import org.gcube.dataharvest.harvester.sobigdata.DataMethodDownloadHarvester;
import org.gcube.dataharvest.harvester.sobigdata.ResourceCatalogueHarvester;
import org.gcube.dataharvest.harvester.sobigdata.TagMeMethodInvocationHarvester;
import org.gcube.dataharvest.utils.AggregationType;
import org.gcube.dataharvest.utils.ContextAuthorization;
import org.gcube.dataharvest.utils.ContextTest;
import org.gcube.dataharvest.utils.DateUtils;
import org.gcube.dataharvest.utils.Utils;
import org.gcube.resourcemanagement.support.server.managers.context.ContextManager;
import org.gcube.vremanagement.executor.api.rest.SmartExecutor;
import org.gcube.vremanagement.executor.api.types.LaunchParameter;
import org.gcube.vremanagement.executor.api.types.Scheduling;
import org.gcube.vremanagement.executor.client.SmartExecutorClientFactory;
import org.junit.Assert;
import org.junit.Test;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AccountingDataHarvesterPluginTest extends ContextTest {
	
	private static Logger logger = LoggerFactory.getLogger(AccountingDataHarvesterPluginTest.class);
	
	public static final String SO_BIG_DATA_CONTEXT = "/d4science.research-infrastructures.eu/SoBigData";
	
	
	public static SortedSet<String> getContexts() throws Exception {
		SortedSet<String> contexts = new TreeSet<>();
		LinkedHashMap<String,ScopeBean> map = ContextManager.readContexts();
		for(String scope : map.keySet()) {
			try {
				String context = map.get(scope).toString();
				contexts.add(context);
			} catch(Exception e) {
				throw e;
			}
		}
		return contexts;
	}
	
	// @Test
	public void getDimensions() {
		try {
			
			Utils.setContext(ROOT);
			
			AccountingDao dao = AccountingDao.get();
			
			Set<Dimension> dimensionSet = dao.getDimensions();
			for(Dimension d : dimensionSet) {
				logger.debug("{} - {} - {} - {}", d.getId(), d.getGroup(), d.getAggregatedMeasure(), d.getLabel());
			}
			
			logger.info("End.");
			
		} catch(Exception e) {
			logger.error("", e);
		}
	}
	
	// @Test
	public void launch() {
		try {
			
			Utils.setContext(ROOT);
			
			DataHarvestPluginDeclaration dataHarvestPluginDeclaration = new DataHarvestPluginDeclaration();
			
			AccountingDataHarvesterPlugin accountingDataHarvesterPlugin = new AccountingDataHarvesterPlugin(
					dataHarvestPluginDeclaration);
			
			Map<String,Object> inputs = new HashMap<>();
			
			AggregationType aggregationType = AggregationType.MONTHLY;
			
			inputs.put(AccountingDataHarvesterPlugin.MEASURE_TYPE_INPUT_PARAMETER, aggregationType.name());
			inputs.put(AccountingDataHarvesterPlugin.GET_VRE_USERS_INPUT_PARAMETER, true);
			inputs.put(AccountingDataHarvesterPlugin.RERUN_INPUT_PARAMETER, true);
			inputs.put(AccountingDataHarvesterPlugin.DRY_RUN_INPUT_PARAMETER, true);
			
			/*
			Calendar from = DateUtils.getStartCalendar(2016, Calendar.SEPTEMBER, 1);
			String fromDate = DateUtils.LAUNCH_DATE_FORMAT.format(from.getTime());
			logger.trace("{} is {}", AccountingDataHarvesterPlugin.START_DATE_INPUT_PARAMETER, fromDate);
			inputs.put(AccountingDataHarvesterPlugin.START_DATE_INPUT_PARAMETER, fromDate);
			*/
			
			accountingDataHarvesterPlugin.launch(inputs);
			
			logger.info("End.");
			
		} catch(Exception e) {
			logger.error("", e);
		}
	}
	
	
	// @Test
	public void launchPluginOnSmartExecutor() {
		try {
			
			Utils.setContext(ROOT);
			
			SmartExecutor smartExecutor = SmartExecutorClientFactory.create(DataHarvestPluginDeclaration.NAME);
			Assert.assertNotNull(smartExecutor);
			
			Map<String,Object> inputs = new HashMap<>();
			
			AggregationType aggregationType = AggregationType.MONTHLY;
			
			inputs.put(AccountingDataHarvesterPlugin.MEASURE_TYPE_INPUT_PARAMETER, aggregationType.name());
			inputs.put(AccountingDataHarvesterPlugin.GET_VRE_USERS_INPUT_PARAMETER, true);
			inputs.put(AccountingDataHarvesterPlugin.RERUN_INPUT_PARAMETER, true);
			inputs.put(AccountingDataHarvesterPlugin.DRY_RUN_INPUT_PARAMETER, false);
			
			/*
			Calendar from = DateUtils.getStartCalendar(2016, Calendar.SEPTEMBER, 1);
			String fromDate = DateUtils.LAUNCH_DATE_FORMAT.format(from.getTime());
			logger.trace("{} is {}", AccountingDataHarvesterPlugin.START_DATE_INPUT_PARAMETER, fromDate);
			inputs.put(AccountingDataHarvesterPlugin.START_DATE_INPUT_PARAMETER, fromDate);
			*/
			
			// 
			CronExpression cronExpression = new CronExpression("0 0 10 3 1/1 ? *");
			Scheduling scheduling = new Scheduling(cronExpression);
			scheduling.setGlobal(false);
			LaunchParameter launchParameter = new LaunchParameter(DataHarvestPluginDeclaration.NAME, inputs, scheduling);
			smartExecutor.launch(launchParameter);
			
			logger.info("End.");
			
		} catch(Exception e) {
			logger.error("", e);
		}
	}
	
	// @Test
	public void launchOldData() {
		try {
			
			Utils.setContext(ROOT);
			
			DataHarvestPluginDeclaration dataHarvestPluginDeclaration = new DataHarvestPluginDeclaration();
			
			AccountingDataHarvesterPlugin accountingDataHarvesterPlugin = new AccountingDataHarvesterPlugin(
					dataHarvestPluginDeclaration);
			
			Map<String,Object> inputs = new HashMap<>();
			
			AggregationType aggregationType = AggregationType.MONTHLY;
			
			inputs.put(AccountingDataHarvesterPlugin.MEASURE_TYPE_INPUT_PARAMETER, aggregationType.name());
			inputs.put(AccountingDataHarvesterPlugin.GET_VRE_USERS_INPUT_PARAMETER, true);
			inputs.put(AccountingDataHarvesterPlugin.RERUN_INPUT_PARAMETER, true);
			inputs.put(AccountingDataHarvesterPlugin.DRY_RUN_INPUT_PARAMETER, false);
			
			Calendar from = DateUtils.getStartCalendar(2016, Calendar.SEPTEMBER, 1);
			
			Calendar runbeforeDate = DateUtils.getStartCalendar(2018, Calendar.JUNE, 1);
			
			while(from.before(runbeforeDate)) {
				String fromDate = DateUtils.LAUNCH_DATE_FORMAT.format(from.getTime());
				logger.trace("{} is {}", AccountingDataHarvesterPlugin.START_DATE_INPUT_PARAMETER, fromDate);
				inputs.put(AccountingDataHarvesterPlugin.START_DATE_INPUT_PARAMETER, fromDate);
				accountingDataHarvesterPlugin.launch(inputs);
				from.add(aggregationType.getCalendarField(), 1);
			}
			
			logger.info("End.");
			
		} catch(Exception e) {
			logger.error("", e);
		}
	}
	
	// @Test
	public void launchOldDataVREAccessesHarvester() {
		try {
			
			Utils.setContext(ROOT);
			// AccountingDao dao = AccountingDao.get();
			
			DataHarvestPluginDeclaration dataHarvestPluginDeclaration = new DataHarvestPluginDeclaration();
			
			AccountingDataHarvesterPlugin accountingDataHarvesterPlugin = new AccountingDataHarvesterPlugin(
					dataHarvestPluginDeclaration);
			Properties properties = accountingDataHarvesterPlugin.getConfigParameters();
			AccountingDataHarvesterPlugin.getProperties().set(properties);
			
			ContextAuthorization contextAuthorization = new ContextAuthorization();
			SortedSet<String> contexts = contextAuthorization.getContexts();
			
			AggregationType aggregationType = AggregationType.MONTHLY;
			
			Calendar from = DateUtils.getStartCalendar(2018, Calendar.APRIL, 1);
			
			Calendar runbeforeDate = DateUtils.getStartCalendar(2018, Calendar.JUNE, 1);
			
			while(from.before(runbeforeDate)) {
				Date start = from.getTime();
				Date end = DateUtils.getEndDateFromStartDate(aggregationType, start, 1);
				
				logger.debug("Harvesting from {} to {}", DateUtils.format(start), DateUtils.format(end));
				
				ArrayList<AccountingRecord> accountingRecords = new ArrayList<>();
				
				VREAccessesHarvester vreAccessesHarvester = null;
				
				for(String context : contexts) {
					// Setting the token for the context
					Utils.setContext(contextAuthorization.getTokenForContext(context));
					
					ScopeBean scopeBean = new ScopeBean(context);
					
					if(vreAccessesHarvester == null) {
						
						if(scopeBean.is(Type.INFRASTRUCTURE)) {
							vreAccessesHarvester = new VREAccessesHarvester(start, end);
						} else {
							// This code should be never used because the scopes are sorted by fullname
							
							ScopeBean parent = scopeBean.enclosingScope();
							while(!parent.is(Type.INFRASTRUCTURE)) {
								parent = scopeBean.enclosingScope();
							}
							
							// Setting back token for the context
							Utils.setContext(contextAuthorization.getTokenForContext(parent.toString()));
							
							vreAccessesHarvester = new VREAccessesHarvester(start, end);
							
							// Setting back token for the context
							Utils.setContext(contextAuthorization.getTokenForContext(context));
						}
						
					}
					
					try {
						if(context.startsWith(AccountingDataHarvesterPlugin.SO_BIG_DATA_VO)
								&& start.before(DateUtils.getStartCalendar(2018, Calendar.APRIL, 1).getTime())) {
							logger.info("Not Harvesting VREs Accesses for {} from {} to {}", context,
									DateUtils.format(start), DateUtils.format(end));
						} else {
							// Collecting Google Analytics Data for VREs Accesses
							List<AccountingRecord> harvested = vreAccessesHarvester.getAccountingRecords();
							accountingRecords.addAll(harvested);
							
						}
					} catch(Exception e) {
						logger.error("Error harvesting Social Interactions for {}", context, e);
					}
				}
				
				logger.debug("Harvest Measures from {} to {} are {}", DateUtils.format(start), DateUtils.format(end),
						accountingRecords);
				Utils.setContext(ROOT);
				
				// dao.insertRecords(accountingRecords.toArray(new AccountingRecord[1]));
				
				Thread.sleep(TimeUnit.SECONDS.toMillis(10));
				
				from.add(aggregationType.getCalendarField(), 1);
				
			}
			
			Utils.setContext(ROOT);
			
		} catch(Exception e) {
			logger.error("", e);
		}
		
		logger.info("End.");
		
	}
	
	// @Test
	public void testScopeBean() throws Exception {
		Utils.setContext(ROOT);
		SortedSet<String> contexts = getContexts();
		
		AggregationType aggregationType = AggregationType.MONTHLY;
		
		Date start = DateUtils.getStartCalendar(2018, Calendar.MARCH, 1).getTime();
		// start = DateUtils.getPreviousPeriod(measureType).getTime();
		Date end = DateUtils.getEndDateFromStartDate(aggregationType, start, 1);
		
		logger.info("\n\n\n");
		
		for(String context : contexts) {
			ScopeBean scopeBean = new ScopeBean(context);
			// logger.debug("FullName {} - Name {}", scopeBean.toString(), scopeBean.name());
			
			try {
				
				if(scopeBean.is(Type.VRE) && start.equals(DateUtils.getPreviousPeriod(aggregationType).getTime())) {
					logger.info("Harvesting (VRE Users) for {} from {} to {}", context, DateUtils.format(start),
							DateUtils.format(end));
				} else {
					logger.info("--- Not Harvesting (VRE Users) for {} from {} to {}", context, DateUtils.format(start),
							DateUtils.format(end));
				}
				
				if((context.startsWith(AccountingDataHarvesterPlugin.SO_BIG_DATA_VO)
						|| context.startsWith(AccountingDataHarvesterPlugin.SO_BIG_DATA_EU_VRE)
						|| context.startsWith(AccountingDataHarvesterPlugin.SO_BIG_DATA_IT_VRE))
						&& start.before(DateUtils.getStartCalendar(2018, Calendar.APRIL, 1).getTime())) {
					logger.info("--- Not Harvesting (SoBigData Check) for {} from {} to {}", context,
							DateUtils.format(start), DateUtils.format(end));
				} else {
					logger.info("Harvesting (SoBigData Check) for {} from {} to {}", context, DateUtils.format(start),
							DateUtils.format(end));
				}
				
			} catch(Exception e) {
				logger.error("Error harvesting Social Interactions for {}", context, e);
			}
			
		}
		
	}
	
	// @Test
	public void testVREAccessesHarvester() {
		try {
			
			Utils.setContext(ROOT);
			
			AggregationType measureType = AggregationType.MONTHLY;
			
			// Date start = DateUtils.getStartCalendar(2015, Calendar.FEBRUARY, 1).getTime();
			// Date end = DateUtils.getStartCalendar(2019, Calendar.FEBRUARY, 1).getTime();
			
			Date start = DateUtils.getPreviousPeriod(measureType).getTime();
			Date end = DateUtils.getEndDateFromStartDate(measureType, start, 1);
			
			AccountingDataHarvesterPlugin accountingDataHarvesterPlugin = new AccountingDataHarvesterPlugin(null);
			accountingDataHarvesterPlugin.getConfigParameters();
			
			ContextAuthorization contextAuthorization = new ContextAuthorization();
			SortedSet<String> contexts = contextAuthorization.getContexts();
			
			VREAccessesHarvester vreAccessesHarvester = null;
			
			ArrayList<AccountingRecord> accountingRecords = new ArrayList<>();
			
			for(String context : contexts) {
				// Setting the token for the context
				Utils.setContext(contextAuthorization.getTokenForContext(context));
				
				ScopeBean scopeBean = new ScopeBean(context);
				
				if(vreAccessesHarvester == null) {
					
					if(scopeBean.is(Type.INFRASTRUCTURE)) {
						vreAccessesHarvester = new VREAccessesHarvester(start, end);
					} else {
						// This code should be never used because the scopes are sorted by fullname
						
						ScopeBean parent = scopeBean.enclosingScope();
						while(!parent.is(Type.INFRASTRUCTURE)) {
							parent = scopeBean.enclosingScope();
						}
						
						// Setting back token for the context
						Utils.setContext(contextAuthorization.getTokenForContext(parent.toString()));
						
						vreAccessesHarvester = new VREAccessesHarvester(start, end);
						
						// Setting back token for the context
						Utils.setContext(contextAuthorization.getTokenForContext(context));
					}
					
				}
				
				try {
					if(context.startsWith(AccountingDataHarvesterPlugin.SO_BIG_DATA_VO)
							&& start.before(DateUtils.getStartCalendar(2018, Calendar.APRIL, 1).getTime())) {
						logger.info("Not Harvesting VREs Accesses for {} from {} to {}", context,
								DateUtils.format(start), DateUtils.format(end));
					} else {
						// Collecting Google Analytics Data for VREs Accesses
						List<AccountingRecord> harvested = vreAccessesHarvester.getAccountingRecords();
						accountingRecords.addAll(harvested);
					}
				} catch(Exception e) {
					logger.error("Error harvesting Social Interactions for {}", context, e);
				}
			}
			
			logger.debug("{}", accountingRecords);
			
		} catch(Exception e) {
			logger.error("", e);
		}
	}
	
	// @Test
	public void testSocialInteraction() {
		try {
			
			Utils.setContext(ROOT);
			// AccountingDao dao = AccountingDao.get();
			
			DataHarvestPluginDeclaration dataHarvestPluginDeclaration = new DataHarvestPluginDeclaration();
			
			AccountingDataHarvesterPlugin accountingDataHarvesterPlugin = new AccountingDataHarvesterPlugin(
					dataHarvestPluginDeclaration);
			Properties properties = accountingDataHarvesterPlugin.getConfigParameters();
			AccountingDataHarvesterPlugin.getProperties().set(properties);
			
			ContextAuthorization contextAuthorization = new ContextAuthorization();
			
			SortedSet<String> contexts = new TreeSet<>();
			contexts.add("/d4science.research-infrastructures.eu/D4Research");
			contexts.add("/d4science.research-infrastructures.eu/FARM/WECAFC-FIRMS");
			contexts.add("/d4science.research-infrastructures.eu/gCubeApps/BlueBridgeProject");
			contexts.add("/d4science.research-infrastructures.eu/gCubeApps/Parthenos");
			contexts.add("/d4science.research-infrastructures.eu/gCubeApps/ScalableDataMining");
			contexts.add("/d4science.research-infrastructures.eu/gCubeApps/gCube");
			
			AggregationType aggregationType = AggregationType.MONTHLY;
			
			Calendar from = DateUtils.getStartCalendar(2018, Calendar.JUNE, 1);
			Date start = from.getTime();
			Date end = DateUtils.getEndDateFromStartDate(aggregationType, start, 1);
			
			logger.debug("Harvesting Social Interaction from {} to {}", DateUtils.format(start), DateUtils.format(end));
			
			ArrayList<AccountingRecord> accountingRecords = new ArrayList<>();
			
			for(String context : contexts) {
				// Setting the token for the context
				Utils.setContext(contextAuthorization.getTokenForContext(context));
				try {
					// Collecting info on social (posts, replies and likes)
					logger.info("Going to harvest Social Interactions for {}", context);
					SocialInteractionsHarvester socialHarvester = new SocialInteractionsHarvester(start, end);
					List<AccountingRecord> harvested = socialHarvester.getAccountingRecords();
					accountingRecords.addAll(harvested);
				} catch(Exception e) {
					logger.error("Error harvesting Social Interactions for {}", context, e);
				}
			}
			
			logger.debug("Harvest Measures from {} to {} are {}", DateUtils.format(start), DateUtils.format(end),
					accountingRecords);
			Utils.setContext(ROOT);
			// dao.insertRecords(accountingRecords.toArray(new AccountingRecord[1]));
			
		} catch(Exception e) {
			logger.error("", e);
		}
		
		logger.info("End.");
		
	}
	
	// @Test
	public void testMethodInvocation() {
		try {
			
			Utils.setContext(StockAssessment);
			
			AggregationType measureType = AggregationType.MONTHLY;
			
			Date start = DateUtils.getPreviousPeriod(measureType).getTime();
			Date end = DateUtils.getEndDateFromStartDate(measureType, start, 1);
			
			MethodInvocationHarvester methodInvocationHarvester = new MethodInvocationHarvester(start, end);
			List<AccountingRecord> accountingRecords = methodInvocationHarvester.getAccountingRecords();
			
			logger.debug("{}", accountingRecords);
			
		} catch(Exception e) {
			logger.error("", e);
		}
	}
	
	// @Test
	public void testTagMeMethodInvocation() {
		try {
			
			Utils.setContext(TAGME);
			
			AggregationType measureType = AggregationType.MONTHLY;
			
			Date start = DateUtils.getPreviousPeriod(measureType).getTime();
			Date end = DateUtils.getEndDateFromStartDate(measureType, start, 1);
			
			TagMeMethodInvocationHarvester methodInvocationHarvester = new TagMeMethodInvocationHarvester(start, end);
			List<AccountingRecord> accountingRecords = methodInvocationHarvester.getAccountingRecords();
			
			logger.debug("{}", accountingRecords);
			
		} catch(Exception e) {
			logger.error("", e);
		}
	}
	
	// @Test
	public void testGetVREUsersForSpecificVRE() {
		try {
			Utils.setContext(ROOT);
			
			DataHarvestPluginDeclaration dataHarvestPluginDeclaration = new DataHarvestPluginDeclaration();
			AccountingDataHarvesterPlugin accountingDataHarvesterPlugin = new AccountingDataHarvesterPlugin(
					dataHarvestPluginDeclaration);
			Properties properties = accountingDataHarvesterPlugin.getConfigParameters();
			AccountingDataHarvesterPlugin.getProperties().set(properties);
			
			// AccountingDao dao = AccountingDao.get();
			
			ContextAuthorization contextAuthorization = new ContextAuthorization();
			Utils.setContext(contextAuthorization
					.getTokenForContext("/d4science.research-infrastructures.eu/SoBigData/SportsDataScience"));
			
			AggregationType measureType = AggregationType.MONTHLY;
			
			Date start = DateUtils.getPreviousPeriod(measureType).getTime();
			Date end = DateUtils.getEndDateFromStartDate(measureType, start, 1);
			
			VREUsersHarvester vreUsersHarvester = new VREUsersHarvester(start, end);
			List<AccountingRecord> harvested = vreUsersHarvester.getAccountingRecords();
			
			logger.info("Harvested Data from {} to {} : {}", DateUtils.format(start), DateUtils.format(end), harvested);
			
			org.gcube.dataharvest.utils.Utils.setContext(ROOT);
			// dao.insertRecords(accountingRecords.toArray(new AccountingRecord[1]));
			
		} catch(Exception e) {
			logger.error("", e);
		}
		
	}
	
	// @Test
	public void testFilteringGenericResource() {
		try {
			Utils.setContext(RESOURCE_CATALOGUE);
			
			AggregationType measureType = AggregationType.MONTHLY;
			
			Date start = DateUtils.getPreviousPeriod(measureType).getTime();
			Date end = DateUtils.getEndDateFromStartDate(measureType, start, 1);
			
			SortedSet<String> contexts = getContexts();
			
			AccountingDataHarvesterPlugin accountingDataHarvesterPlugin = new AccountingDataHarvesterPlugin(null);
			accountingDataHarvesterPlugin.getConfigParameters();
			
			ResourceCatalogueHarvester resourceCatalogueHarvester = new ResourceCatalogueHarvester(start, end,
					contexts);
			SortedSet<String> validContexts = resourceCatalogueHarvester.getValidContexts(contexts,
					SO_BIG_DATA_CONTEXT + "/");
			logger.info("Valid Contexts {}", validContexts);
			
		} catch(Exception e) {
			logger.error("", e);
		}
		
	}
	
	// @Test
	public void testResourceCatalogueHarvester() {
		try {
			
			Utils.setContext(RESOURCE_CATALOGUE);
			
			AggregationType measureType = AggregationType.MONTHLY;
			
			// Date start = DateUtils.getStartCalendar(2015, Calendar.FEBRUARY, 1).getTime();
			// Date end = DateUtils.getStartCalendar(2019, Calendar.FEBRUARY, 1).getTime();
			
			Date start = DateUtils.getPreviousPeriod(measureType).getTime();
			Date end = DateUtils.getEndDateFromStartDate(measureType, start, 1);
			
			AccountingDataHarvesterPlugin accountingDataHarvesterPlugin = new AccountingDataHarvesterPlugin(null);
			accountingDataHarvesterPlugin.getConfigParameters();
			
			SortedSet<String> contexts = getContexts();
			
			ResourceCatalogueHarvester resourceCatalogueHarvester = new ResourceCatalogueHarvester(start, end,
					contexts);
			List<AccountingRecord> data = resourceCatalogueHarvester.getAccountingRecords();
			
			logger.debug("{}", data);
			
		} catch(Exception e) {
			logger.error("", e);
		}
	}
	
	// @Test
	public void testDataMethodDownloadHarvester() {
		try {
			
			Utils.setContext(RESOURCE_CATALOGUE);
			
			AggregationType measureType = AggregationType.MONTHLY;
			
			// Date start = DateUtils.getStartCalendar(2015, Calendar.FEBRUARY, 1).getTime();
			// Date end = DateUtils.getStartCalendar(2019, Calendar.FEBRUARY, 1).getTime();
			
			Date start = DateUtils.getPreviousPeriod(measureType).getTime();
			Date end = DateUtils.getEndDateFromStartDate(measureType, start, 1);
			
			AccountingDataHarvesterPlugin accountingDataHarvesterPlugin = new AccountingDataHarvesterPlugin(null);
			accountingDataHarvesterPlugin.getConfigParameters();
			
			SortedSet<String> contexts = getContexts();
			
			DataMethodDownloadHarvester dataMethodDownloadHarvester = new DataMethodDownloadHarvester(start, end,
					contexts);
			List<AccountingRecord> data = dataMethodDownloadHarvester.getAccountingRecords();
			
			logger.debug("{}", data);
			
		} catch(Exception e) {
			logger.error("", e);
		}
	}
	
	
	public static final String E_LEARNING_AREA_VRE = "/d4science.research-infrastructures.eu/SoBigData/E-Learning_Area";
	
	// @Test
	public void addMissingVREAccesses() {
		try {
			
			Utils.setContext(ROOT);
			
			DataHarvestPluginDeclaration dataHarvestPluginDeclaration = new DataHarvestPluginDeclaration();
			AccountingDataHarvesterPlugin adhp = new AccountingDataHarvesterPlugin(dataHarvestPluginDeclaration);
			Properties properties = adhp.getConfigParameters();
			AccountingDataHarvesterPlugin.getProperties().set(properties);
			
			ContextAuthorization contextAuthorization = new ContextAuthorization();
			
			// DatabaseManager dbaseManager = new DatabaseManager();
			AccountingDao dao = AccountingDao.get();
			
			Set<ScopeDescriptor> scopeDescriptorSet = dao.getContexts();
			Map<String,ScopeDescriptor> scopeDescriptorMap = new HashMap<>();
			for(ScopeDescriptor scopeDescriptor : scopeDescriptorSet) {
				scopeDescriptorMap.put(scopeDescriptor.getId(), scopeDescriptor);
			}
			AccountingDataHarvesterPlugin.scopeDescriptors.set(scopeDescriptorMap);
			
			
			Set<Dimension> dimensionSet = dao.getDimensions();
			Map<String,Dimension> dimensionMap = new HashMap<>();
			for(Dimension dimension : dimensionSet) {
				dimensionMap.put(dimension.getId(), dimension);
			}
			
			AccountingDataHarvesterPlugin.dimensions.set(dimensionMap);
			
			// ArrayList<HarvestedData> data = new ArrayList<HarvestedData>();
			ArrayList<AccountingRecord> accountingRecords = new ArrayList<AccountingRecord>();
			
			String context = E_LEARNING_AREA_VRE;
			
			// Setting the token for the context
			Utils.setContext(contextAuthorization.getTokenForContext(context));
			
			
			ScopeBean scopeBean = new ScopeBean(context);
			ScopeDescriptor scopeDescriptor = new ScopeDescriptor(scopeBean.name(), context);
			
			Dimension dimension = AccountingDataHarvesterPlugin.getDimension(HarvestedDataKey.ACCESSES.getKey());
			
			Calendar calendar = DateUtils.getStartCalendar(2018, Calendar.JULY, 1);
			calendar.set(Calendar.DAY_OF_MONTH, 15);
			
			Map<Integer, Integer> monthValues = new HashMap<>();
			monthValues.put(Calendar.JULY, 54);
			monthValues.put(Calendar.AUGUST, 23);
			monthValues.put(Calendar.SEPTEMBER, 127);
			monthValues.put(Calendar.OCTOBER, 192);
			
			for(Integer month : monthValues.keySet()) {
				calendar.set(Calendar.MONTH, month);
				Instant instant = calendar.toInstant();
				
				AccountingRecord ar = new AccountingRecord(scopeDescriptor, instant, dimension, (long) monthValues.get(month));
				logger.debug("{} : {}", ar.getDimension().getId(), ar.getMeasure());
				accountingRecords.add(ar);
			}
			
			logger.trace("{}", accountingRecords);
			dao.insertRecords(accountingRecords.toArray(new AccountingRecord[1]));
			
		} catch(Exception e) {
			logger.error("", e);
		}
	}
}
