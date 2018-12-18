package org.gcube.dataharvest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import org.gcube.accounting.accounting.summary.access.AccountingDao;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;
import org.gcube.accounting.accounting.summary.access.model.internal.Dimension;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.dataharvest.harvester.MethodInvocationHarvester;
import org.gcube.dataharvest.harvester.SocialInteractionsHarvester;
import org.gcube.dataharvest.harvester.VREAccessesHarvester;
import org.gcube.dataharvest.harvester.VREUsersHarvester;
import org.gcube.dataharvest.harvester.sobigdata.DataMethodDownloadHarvester;
import org.gcube.dataharvest.harvester.sobigdata.ResourceCatalogueHarvester;
import org.gcube.dataharvest.harvester.sobigdata.TagMeMethodInvocationHarvester;
import org.gcube.dataharvest.utils.AggregationType;
import org.gcube.dataharvest.utils.ContextAuthorization;
import org.gcube.dataharvest.utils.DateUtils;
import org.gcube.dataharvest.utils.Utils;
import org.gcube.vremanagement.executor.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Eric Perrone (ISTI - CNR)
 * @author Luca Frosini (ISTI - CNR)
 */
public class AccountingDataHarvesterPlugin extends Plugin<DataHarvestPluginDeclaration> {
	
	private static Logger logger = LoggerFactory.getLogger(AccountingDataHarvesterPlugin.class);
	
	private static final String PROPERTY_FILENAME = "config.properties";
	
	public static final String START_DATE_INPUT_PARAMETER = "startDate";
	public static final String MEASURE_TYPE_INPUT_PARAMETER = "measureType";
	public static final String RERUN_INPUT_PARAMETER = "reRun";
	public static final String GET_VRE_USERS_INPUT_PARAMETER = "getVREUsers";
	public static final String DRY_RUN_INPUT_PARAMETER = "dryRun";
	
	public static final String SO_BIG_DATA_VO = "/d4science.research-infrastructures.eu/SoBigData";
	public static final String SO_BIG_DATA_EU_VRE = "/d4science.research-infrastructures.eu/gCubeApps/SoBigData.eu";
	public static final String SO_BIG_DATA_IT_VRE = "/d4science.research-infrastructures.eu/gCubeApps/SoBigData.it";
	public static final String SO_BIG_DATA_CATALOGUE_CONTEXT = "/d4science.research-infrastructures.eu/SoBigData/ResourceCatalogue";
	
	public static final String TAGME_CONTEXT = "/d4science.research-infrastructures.eu/SoBigData/TagMe";
	
	public static final String TO_BE_SET = "TO BE SET";
	
	protected Date start;
	protected Date end;
	
	public AccountingDataHarvesterPlugin(DataHarvestPluginDeclaration pluginDeclaration) {
		super(pluginDeclaration);
	}
	
	private static final InheritableThreadLocal<Properties> properties = new InheritableThreadLocal<Properties>() {
		
		@Override
		protected Properties initialValue() {
			return new Properties();
		}
		
	};
	
	
	public static InheritableThreadLocal<Properties> getProperties() {
		return properties;
	}
	
	public static Dimension getDimension(String key) {
		Dimension dimension = dimensions.get().get(key);
		if(dimension == null) {
			dimension = new Dimension(key, key, null, key);
		}
		return dimension;
	}
	
	private static final InheritableThreadLocal<Map<String, Dimension>> dimensions = new InheritableThreadLocal<Map<String, Dimension>>() {
		
		@Override
		protected Map<String, Dimension> initialValue() {
			return new HashMap<>();
		}
		
	};
	
	
	public static ScopeDescriptor getScopeDescriptor(String context) {
		return scopeDescriptors.get().get(context);
	}
	
	private static final InheritableThreadLocal<Map<String, ScopeDescriptor>> scopeDescriptors = new InheritableThreadLocal<Map<String, ScopeDescriptor>>() {
		
		@Override
		protected Map<String, ScopeDescriptor> initialValue() {
			return new HashMap<>();
		}
		
	};
	
	public static ScopeDescriptor getScopeDescriptor() {
		return scopeDescriptor.get();
	}
	
	private static final InheritableThreadLocal<ScopeDescriptor> scopeDescriptor = new InheritableThreadLocal<ScopeDescriptor>() {
		
		@Override
		protected ScopeDescriptor initialValue() {
			return new ScopeDescriptor("","");
		}
		
	};
	
	
	
	public Properties getConfigParameters() throws IOException {
		Properties properties = new Properties();
		try {
			InputStream input = AccountingDataHarvesterPlugin.class.getClassLoader()
					.getResourceAsStream(PROPERTY_FILENAME);
			properties.load(input);
			return properties;
		} catch(Exception e) {
			logger.warn(
					"Unable to load {} file containing configuration properties. AccountingDataHarvesterPlugin will use defaults",
					PROPERTY_FILENAME);
		}
		return properties;
	}
	
	/** {@inheritDoc} */
	@Override
	public void launch(Map<String,Object> inputs) throws Exception {
		logger.debug("{} is starting", this.getClass().getSimpleName());
		
		if(inputs == null || inputs.isEmpty()) {
			throw new IllegalArgumentException("The can only be launched providing valid input parameters");
		}
		
		if(!inputs.containsKey(MEASURE_TYPE_INPUT_PARAMETER)) {
			throw new IllegalArgumentException("Please set required parameter '" + MEASURE_TYPE_INPUT_PARAMETER + "'");
		}
		
		AggregationType aggregationType = AggregationType.valueOf((String) inputs.get(MEASURE_TYPE_INPUT_PARAMETER));
		
		boolean reRun = true;
		if(inputs.containsKey(RERUN_INPUT_PARAMETER)) {
			try {
				reRun = (boolean) inputs.get(RERUN_INPUT_PARAMETER);
			} catch(Exception e) {
				throw new IllegalArgumentException("'" + RERUN_INPUT_PARAMETER + "' must be a boolean");
			}
		}
		
		boolean getVREUsers = true;
		if(inputs.containsKey(GET_VRE_USERS_INPUT_PARAMETER)) {
			try {
				reRun = (boolean) inputs.get(GET_VRE_USERS_INPUT_PARAMETER);
			} catch(Exception e) {
				throw new IllegalArgumentException("'" + GET_VRE_USERS_INPUT_PARAMETER + "' must be a boolean");
			}
		}
		
		boolean dryRun = true;
		if(inputs.containsKey(DRY_RUN_INPUT_PARAMETER)) {
			try {
				dryRun = (boolean) inputs.get(DRY_RUN_INPUT_PARAMETER);
			} catch(Exception e) {
				throw new IllegalArgumentException("'" + DRY_RUN_INPUT_PARAMETER + "' must be a boolean");
			}
		}
		
		if(inputs.containsKey(START_DATE_INPUT_PARAMETER)) {
			String startDateString = (String) inputs.get(START_DATE_INPUT_PARAMETER);
			start = DateUtils.UTC_DATE_FORMAT.parse(startDateString + " " + DateUtils.UTC);
		} else {
			start = DateUtils.getPreviousPeriod(aggregationType).getTime();
		}
		
		end = DateUtils.getEndDateFromStartDate(aggregationType, start, 1);
		
		logger.debug("Harvesting from {} to {} (ReRun:{} - GetVREUsers:{} - DryRun:{})", 
				DateUtils.format(start), DateUtils.format(end), reRun, getVREUsers, dryRun);
		
		Properties properties = getConfigParameters();
		getProperties().set(properties);
		
		ContextAuthorization contextAuthorization = new ContextAuthorization();
		
		// DatabaseManager dbaseManager = new DatabaseManager();
		AccountingDao dao = AccountingDao.get();
		
		Set<ScopeDescriptor> scopeDescriptorSet = dao.getContexts();
		Map<String,ScopeDescriptor> scopeDescriptorMap = new HashMap<>();
		for(ScopeDescriptor scopeDescriptor : scopeDescriptorSet) {
			scopeDescriptorMap.put(scopeDescriptor.getId(), scopeDescriptor);
		}
		scopeDescriptors.set(scopeDescriptorMap);
		
		
		Set<Dimension> dimensionSet = dao.getDimensions();
		Map<String,Dimension> dimensionMap = new HashMap<>();
		for(Dimension dimension : dimensionSet) {
			dimensionMap.put(dimension.getId(), dimension);
		}
		
		dimensions.set(dimensionMap);
		
		
		SortedSet<String> contexts = contextAuthorization.getContexts();
		
		// ArrayList<HarvestedData> data = new ArrayList<HarvestedData>();
		ArrayList<AccountingRecord> accountingRecords = new ArrayList<AccountingRecord>();
		
		String initialToken = SecurityTokenProvider.instance.get();
		
		VREAccessesHarvester vreAccessesHarvester = null;
		
		for(String context : contexts) {
			// Setting the token for the context
			Utils.setContext(contextAuthorization.getTokenForContext(context));
			
			ScopeBean scopeBean = new ScopeBean(context);
			
			ScopeDescriptor actualScopeDescriptor = scopeDescriptorMap.get(context);
			if(actualScopeDescriptor==null) {
				actualScopeDescriptor = new ScopeDescriptor(scopeBean.name(), context);
			}
			
			scopeDescriptor.set(actualScopeDescriptor);
			
			
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
			
			
			if((context.startsWith(SO_BIG_DATA_VO) || context.startsWith(SO_BIG_DATA_EU_VRE)
					|| context.startsWith(SO_BIG_DATA_IT_VRE))
					&& start.before(DateUtils.getStartCalendar(2018, Calendar.APRIL, 1).getTime())) {
				logger.info("Not Harvesting for {} from {} to {}", context, DateUtils.format(start),
						DateUtils.format(end));
			} else {
			
				try {
					// Collecting Google Analytics Data for VREs Accesses
					logger.info("Going to harvest VRE Accesses for {}", context);
					
					List<AccountingRecord> harvested = vreAccessesHarvester.getAccountingRecords();
					accountingRecords.addAll(harvested);
					
					/*
					List<HarvestedData> harvested = vreAccessesHarvester.getData();
					data.addAll(harvested);
					*/
				} catch(Exception e) {
					logger.error("Error harvesting VRE Accesses for {}", context, e);
				}
				
				try {
					// Collecting info on social (posts, replies and likes)
					logger.info("Going to harvest Social Interactions for {}", context);
					SocialInteractionsHarvester socialHarvester = new SocialInteractionsHarvester(start, end);
					
					List<AccountingRecord> harvested = socialHarvester.getAccountingRecords();
					accountingRecords.addAll(harvested);
					
					/*
					List<HarvestedData> harvested = socialHarvester.getData();
					data.addAll(harvested);
					*/
				} catch(Exception e) {
					logger.error("Error harvesting Social Interactions for {}", context, e);
				}
				
				try {
					// Collecting info on VRE users
					if(getVREUsers) {
						// Harvesting Users only for VREs (not for VO and ROOT which is the sum of the children contexts)
						// The VREUsers can be only Harvested for the last month
						if(scopeBean.is(Type.VRE) && start.equals(DateUtils.getPreviousPeriod(aggregationType).getTime())) {
							logger.info("Going to harvest Context Users for {}", context);
							VREUsersHarvester vreUsersHarvester = new VREUsersHarvester(start, end);
							
							List<AccountingRecord> harvested = vreUsersHarvester.getAccountingRecords();
							accountingRecords.addAll(harvested);
							
							/*
							List<HarvestedData> harvested = vreUsersHarvester.getData();
							data.addAll(harvested);
							*/
						}
					}
				} catch(Exception e) {
					logger.error("Error harvesting Context Users for {}", context, e);
				}
				
				if(context.startsWith(SO_BIG_DATA_CATALOGUE_CONTEXT)) {
					
					try {
						// Collecting info on Resource Catalogue (Dataset, Application, Deliverables, Methods)
						logger.info("Going to harvest Resource Catalogue Information for {}", context);
						ResourceCatalogueHarvester resourceCatalogueHarvester = new ResourceCatalogueHarvester(start, end,
								contexts);
						
						List<AccountingRecord> harvested = resourceCatalogueHarvester.getAccountingRecords();
						accountingRecords.addAll(harvested);
						
						/*
						List<HarvestedData> harvested = resourceCatalogueHarvester.getData();
						data.addAll(harvested);
						*/
						
					} catch(Exception e) {
						logger.error("Error harvesting Resource Catalogue Information for {}", context, e);
					}
					
					try {
						// Collecting info on Data/Method download
						logger.info("Going to harvest Data Method Download for {}", context);
						DataMethodDownloadHarvester dataMethodDownloadHarvester = new DataMethodDownloadHarvester(start,
								end, contexts);
						
						List<AccountingRecord> harvested = dataMethodDownloadHarvester.getAccountingRecords();
						accountingRecords.addAll(harvested);
						
						/*
						List<HarvestedData> harvested = dataMethodDownloadHarvester.getData();
						data.addAll(harvested);
						*/
						
					} catch(Exception e) {
						logger.error("Error harvesting Data Method Download for {}", context, e);
					}
					
				}
				
				if(context.startsWith(TAGME_CONTEXT)) {
					try {
						// Collecting info on method invocation
						logger.info("Going to harvest Method Invocations for {}", context);
						TagMeMethodInvocationHarvester tagMeMethodInvocationHarvester = new TagMeMethodInvocationHarvester(
								start, end);
						
						List<AccountingRecord> harvested = tagMeMethodInvocationHarvester.getAccountingRecords();
						accountingRecords.addAll(harvested);
						
						/*
						List<HarvestedData> harvested = tagMeMethodInvocationHarvester.getData();
						data.addAll(harvested);
						*/
						
					} catch(Exception e) {
						logger.error("Error harvesting Method Invocations for {}", context, e);
					}
				} else {
					try {
						// Collecting info on method invocation
						logger.info("Going to harvest Method Invocations for {}", context);
						MethodInvocationHarvester methodInvocationHarvester = new MethodInvocationHarvester(start, end);
						
						
						List<AccountingRecord> harvested = methodInvocationHarvester.getAccountingRecords();
						accountingRecords.addAll(harvested);
						
						/*
						List<HarvestedData> harvested = methodInvocationHarvester.getData();
						data.addAll(harvested);
						*/
					} catch(Exception e) {
						logger.error("Error harvesting Method Invocations for {}", context, e);
					}
				}
			}
		}
		
		Utils.setContext(initialToken);
		
		logger.debug("Harvest Measures from {} to {} are {}", DateUtils.format(start), DateUtils.format(end), accountingRecords);
		if(!dryRun) {
			dao.insertRecords(accountingRecords.toArray(new AccountingRecord[1]));
			//dbaseManager.insertMonthlyData(start, end, data, reRun);
		}else {
			logger.debug("Harvested measures are {}", accountingRecords);
		}
		
	}
	
	/** {@inheritDoc} */
	@Override
	protected void onStop() throws Exception {
		logger.debug("{} is stopping", this.getClass().getSimpleName());
	}
	
}
