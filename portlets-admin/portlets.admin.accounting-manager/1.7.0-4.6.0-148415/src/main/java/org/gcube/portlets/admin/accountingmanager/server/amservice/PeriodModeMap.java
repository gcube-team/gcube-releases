package org.gcube.portlets.admin.accountingmanager.server.amservice;

import org.gcube.accounting.analytics.TemporalConstraint.AggregationMode;
import org.gcube.portlets.admin.accountingmanager.shared.data.AccountingPeriodMode;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class PeriodModeMap {
	
	public static AggregationMode getMode(AccountingPeriodMode mode){
		switch(mode){
		case DAILY:
			return AggregationMode.DAILY;
		case HOURLY:
			return AggregationMode.HOURLY;
		case MINUTELY:
			return AggregationMode.MINUTELY;
		case MONTHLY:
			return AggregationMode.MONTHLY;
		case YEARLY:
			return AggregationMode.YEARLY;
		default:
			return AggregationMode.YEARLY;
		
		}
	}
	
	
	public static AccountingPeriodMode getMode(AggregationMode mode){
		switch(mode){
		case DAILY:
			return AccountingPeriodMode.DAILY;
		case HOURLY:
			return AccountingPeriodMode.HOURLY;
		case MINUTELY:
			return AccountingPeriodMode.MINUTELY;
		case MONTHLY:
			return AccountingPeriodMode.MONTHLY;
		case YEARLY:
			return AccountingPeriodMode.YEARLY;
		default:
			return AccountingPeriodMode.YEARLY;
		
		}
	}
	
}
