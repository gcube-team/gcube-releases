/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server.util.database.data;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant.BATCH_LEVEL;
import org.gcube.portlets.user.performfishanalytics.shared.Period;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;



/**
 * The Class PeriodData.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Jan 15, 2019
 */
public class PeriodData {


	/**
	 * Gets the list periods.
	 *
	 * @param batchType the batch type
	 * @return the list periods
	 */
	public static ArrayList<Period> getListPeriods(PopulationType batchType){

		List<Period> listPeriod = new ArrayList<Period>();
		if(batchType.getName().startsWith(PerformFishAnalyticsConstant.BATCH_LEVEL.PRE_ONGROWING.name()) || batchType.getName().startsWith(BATCH_LEVEL.PRE_ONGROWING_CLOSED_BATCHES.name())){
			listPeriod.add(new Period(java.util.UUID.randomUUID().toString(), "early", "", batchType));
			listPeriod.add(new Period(java.util.UUID.randomUUID().toString(), "natural", "", batchType));
			listPeriod.add(new Period(java.util.UUID.randomUUID().toString(), "late", "", batchType));
		}else if(batchType.getName().startsWith(PerformFishAnalyticsConstant.BATCH_LEVEL.HATCHERY_INDIVIDUAL.name()) || batchType.getName().startsWith(BATCH_LEVEL.HATCHERY_INDIVIDUAL_CLOSED_BATCHES.name())){
			listPeriod.add(new Period(java.util.UUID.randomUUID().toString(), "natural", "", batchType));
			listPeriod.add(new Period(java.util.UUID.randomUUID().toString(), "late", "", batchType));
			listPeriod.add(new Period(java.util.UUID.randomUUID().toString(), "early", "", batchType));
		}else{
			listPeriod.add(new Period(java.util.UUID.randomUUID().toString(), "Early", "", batchType));
			listPeriod.add(new Period(java.util.UUID.randomUUID().toString(), "Natural", "", batchType));
			listPeriod.add(new Period(java.util.UUID.randomUUID().toString(), "Late", "", batchType));
		}

		return (ArrayList<Period>) listPeriod;
	}
}
