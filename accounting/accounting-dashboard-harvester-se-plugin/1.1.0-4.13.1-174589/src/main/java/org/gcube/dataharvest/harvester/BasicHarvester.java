package org.gcube.dataharvest.harvester;

import java.text.ParseException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gcube.accounting.accounting.summary.access.model.internal.Dimension;
import org.gcube.accounting.accounting.summary.access.model.update.AccountingRecord;
import org.gcube.common.authorization.client.Constants;
import org.gcube.common.authorization.library.AuthorizationEntry;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataharvest.AccountingDataHarvesterPlugin;
import org.gcube.dataharvest.datamodel.HarvestedDataKey;
import org.gcube.dataharvest.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicHarvester {
	
	private static Logger logger = LoggerFactory.getLogger(BasicHarvester.class);
	
	protected final Date start;
	protected final Date end;
	protected final Instant instant;
	
	public BasicHarvester(Date start, Date end) throws ParseException {
		this.start = start;
		this.end = end;
		
		Calendar toSetOnDB = DateUtils.dateToCalendar(start);
		toSetOnDB.add(Calendar.DAY_OF_MONTH, 15);
		instant = toSetOnDB.toInstant();
		
		logger.debug("Creating {} for the period {} {} ", this.getClass().getSimpleName(), DateUtils.format(start), DateUtils.format(end));
	}
	
	public static String getCurrentContext(String token) throws Exception {
		AuthorizationEntry authorizationEntry = Constants.authorizationService().get(token);
		String context = authorizationEntry.getContext();
		logger.info("Context of token {} is {}", token, context);
		return context;
	}
	
	public static void setContext(String token) throws Exception {
		SecurityTokenProvider.instance.set(token);
		ScopeProvider.instance.set(getCurrentContext(token));
	}
	
	public static String getCurrentContext() throws Exception {
		String token = SecurityTokenProvider.instance.get();
		return getCurrentContext(token);
	}
	
	public abstract List<AccountingRecord> getAccountingRecords() throws Exception;

	public Dimension getDimension(HarvestedDataKey harvestedDataKey) {
		return AccountingDataHarvesterPlugin.getDimension(harvestedDataKey.getKey());
	}
	
	
	
}
