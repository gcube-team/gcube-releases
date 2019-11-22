/**
 *
 */

package org.gcube.portlets.user.performfishanalytics.server.util.database.data;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant;
import org.gcube.portlets.user.performfishanalytics.client.PerformFishAnalyticsConstant.BATCH_LEVEL;
import org.gcube.portlets.user.performfishanalytics.shared.PopulationType;
import org.gcube.portlets.user.performfishanalytics.shared.Quarter;

/**
 * The Class QuarterData.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jan 15, 2019
 */
public class QuarterData {

	/**
	 * Gets the list quarter.
	 *
	 * @param batchType
	 *            the batch type
	 * @return the list quarter
	 */
	public static ArrayList<Quarter> getListQuarter(PopulationType batchType) {

		List<Quarter> listQuarter = new ArrayList<Quarter>();
		if (batchType.getName().startsWith(PerformFishAnalyticsConstant.BATCH_LEVEL.GROW_OUT_AGGREGATED.name() )|| batchType.getName().startsWith(BATCH_LEVEL.GROW_OUT_AGGREGATED_CLOSED_BATCHES.name())) {
			listQuarter.add(new Quarter(
				java.util.UUID.randomUUID().toString(), "1st quarter", "", batchType));
			listQuarter.add(new Quarter(
				java.util.UUID.randomUUID().toString(), "2nd quarter", "", batchType));
			listQuarter.add(new Quarter(
				java.util.UUID.randomUUID().toString(), "3rd quarter", "", batchType));
			listQuarter.add(new Quarter(
				java.util.UUID.randomUUID().toString(), "4th quarter", "", batchType));
		}
		else {
			listQuarter.add(new Quarter(
				java.util.UUID.randomUUID().toString(), "1", "", batchType));
			listQuarter.add(new Quarter(
				java.util.UUID.randomUUID().toString(), "2", "", batchType));
			listQuarter.add(new Quarter(
				java.util.UUID.randomUUID().toString(), "3", "", batchType));
			listQuarter.add(new Quarter(
				java.util.UUID.randomUUID().toString(), "4", "", batchType));
		}
		return (ArrayList<Quarter>) listQuarter;
	}
}
