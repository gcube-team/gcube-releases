package org.gcube.accounting.aggregator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.gcube.accounting.datamodel.validations.validators.RegexReplace;
import org.gcube.accounting.persistence.AccountingPersistenceConfiguration;
import org.gcube.documentstore.records.DSMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RegexRulesAggregator implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(RegexRulesAggregator.class);
	
	private static final ScheduledExecutorService REGEX_REDISCOVERY_POOL;
	
	static {
		
		REGEX_REDISCOVERY_POOL = Executors.newScheduledThreadPool(50, new ThreadFactory() {
			
			private int counter = 0;
			private static final String prefix = "RegexRediscoveryThread";	 
	
			public Thread newThread(Runnable r) {
			   return new Thread(r, prefix + "-" + counter++);
			}
		});
		
	}
	
	
	
	protected TimeUnit timeUnit = TimeUnit.MINUTES;
	protected long delay =  TimeUnit.MINUTES.toMinutes(15);
	
	protected static final String DELAY = "delay";
	protected static final String TIME_UNIT = "timeUnit";
	protected static final String JSON_ARRAY_CALLED_METHOD_RULES = "jsonArrayCalledMethodRules";
	
	protected ScheduledFuture<?> rulesReloader;
	
	protected List<RegexReplace> regexReplaceList;
	
	protected AccountingPersistenceConfiguration accountingPersistenceConfiguration;
	
	protected static RegexRulesAggregator instance;
	
	public synchronized static RegexRulesAggregator getInstance() {
		if(instance==null) {
			instance = new RegexRulesAggregator();
		}
		return instance;
	}
	
	protected RegexRulesAggregator() {
		regexReplaceList = new ArrayList<>();
		readConfiguration();
	}
		
	public List<RegexReplace> getRegexReplaceList() {
		synchronized(regexReplaceList) {
			return regexReplaceList;
		}
	}

	public RegexReplace addRegexReplace(String serviceClass, String serviceName, String regex, String replace) {
		RegexReplace regexReplace = new RegexReplace(serviceClass, serviceName, regex, replace);
		return addRegexReplace(regexReplace);
	}
	
	public RegexReplace addRegexReplace(RegexReplace regexReplace) {
		synchronized(regexReplaceList) {
			regexReplaceList.add(regexReplace);
		}
		return regexReplace;
	}
	
	
	protected ScheduledFuture<?> reloadAggregatorRules;
	
	public void readConfiguration() {
		try {
			accountingPersistenceConfiguration = new AccountingPersistenceConfiguration(this.getClass());
			
			try {
				String delayString = accountingPersistenceConfiguration.getProperty(DELAY);
				delay = Long.parseLong(delayString);
				
				String timeUnitString = accountingPersistenceConfiguration.getProperty(TIME_UNIT);
				timeUnit = TimeUnit.valueOf(timeUnitString.toUpperCase());
			}catch (Exception e) {
				logger.warn("Unable to retrieve regex reload delay. Goign to use last known delay {} {}", delay, timeUnit.name().toLowerCase());
			}
			
			String rulesString = accountingPersistenceConfiguration.getProperty(JSON_ARRAY_CALLED_METHOD_RULES);
			ObjectMapper mapper = DSMapper.getObjectMapper();
			JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, RegexReplace.class);
			List<RegexReplace> rules = mapper.readValue(rulesString, type);
			synchronized(regexReplaceList) {
				regexReplaceList = rules;
			}
			
		} catch(Exception e) {
			logger.error("Unable to properly load RegexRules", e);
		}
	}
	
	@Override
	public void run() {
		readConfiguration();
	}
	
	public void start() {
		if(reloadAggregatorRules == null) {
			reloadAggregatorRules = REGEX_REDISCOVERY_POOL.scheduleAtFixedRate(this, delay, delay, timeUnit);
		}
	}
	
	/**
	 * Stop rule reloader. Use only if you really know what you do.
	 */
	public void stop() {
		if(reloadAggregatorRules != null) {
			try {
				reloadAggregatorRules.cancel(true);
				reloadAggregatorRules = null;
			}catch (Throwable t) {
				logger.error("Unable to properly stop {} reloader", this.getClass().getSimpleName(), t);
			}
		}
	}
	
}
