package org.gcube.data.simulfishgrowthdata.calc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import gr.i2s.fishgrowth.model.Scenario;

/**
 * setup executorbased on a database scenario
 * 
 */
public class ConsumptionScenarioExecutor extends ScenarioExecutor {
	private static final Logger logger = LoggerFactory.getLogger(ConsumptionScenarioExecutor.class);

	public ConsumptionScenarioExecutor(final Scenario scenario) {
		super(scenario);
		// volatile; bypass database
		setDoSave(false);
		if (logger.isTraceEnabled())
			logger.trace(String.format("ctor"));

	}

	public ConsumptionScenarioExecutor(final Session session, final Scenario scenario) {
		super(session, scenario);
		// volatile; bypass database
		setDoSave(false);
		if (logger.isTraceEnabled())
			logger.trace(String.format("ctor"));

	}

	@Override
	protected String processResults() {
		if (logger.isTraceEnabled())
			logger.trace(String.format("processing results"));

		String toRet = new String();

		final String tableSep = " , ";
		final String noSep = "";

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YY");
		String tableDaily = new String();
		final String recordDaily = "{\"day\": \"%s\", \"bm\":%s, \"fcre\":%.2f, \"fcrb\":%.2f, \"food\":%s, \"bmdead\":%s, \"mortality\":%.5f }";
		int counterDay = 0;

		SimpleDateFormat monthFormat = new SimpleDateFormat("MM/YY");
		String tableMonthly = new String();
		final String recordMonth = "{\"month\": \"%s\", \"food\":%s }";
		int counterMonth = 0;
		int curMonth = -1; // invalid
		int maxMonthDay = -1;
		double foodConsMonth = 0;

		for (Iterator<?> iterator = dailyResults.iterator(); iterator.hasNext();) {
			Daily daily = (Daily) iterator.next();
			{ // process daily
				String day = dateFormat.format(daily.date.getTime());
				String dayRec = String.format(recordDaily, day, new Double(daily.fishcount * daily.mab).intValue(),
						daily.fcrEcon, daily.fcrBiol, Math.ceil(daily.food / 1000.0), daily.deadBM.intValue(),
						daily.mortality);
				if (logger.isTraceEnabled())
					logger.trace(String.format("adding day [%s]", dayRec));

				tableDaily = tableDaily + (counterDay > 0 ? tableSep : noSep) + dayRec;
				counterDay++;
			}

			{ // process month
				if (daily.date.get(Calendar.MONTH) != curMonth) {
					// reset values
					foodConsMonth = 0;
					curMonth = daily.date.get(Calendar.MONTH);
					maxMonthDay = daily.date.getActualMaximum(Calendar.DAY_OF_MONTH);
					if (logger.isTraceEnabled()) {
						logger.trace(String.format("curMonth [%s] maxMonthDay [%s] foodConsMonth [%.2f]", curMonth,
								maxMonthDay, foodConsMonth));
					}
				}

				foodConsMonth += daily.food;

				if (daily.date.get(Calendar.DAY_OF_MONTH) == maxMonthDay || !iterator.hasNext()) {
					if (logger.isTraceEnabled())
						logger.trace(String.format("set month food [%.0f]", foodConsMonth));
					String month = monthFormat.format(daily.date.getTime());
					String monthRec = String.format(recordMonth, month, Math.ceil(foodConsMonth / 1000.0));
					if (logger.isTraceEnabled())
						logger.trace(String.format("adding month [%s]", monthRec));

					tableMonthly = tableMonthly + (counterMonth > 0 ? tableSep : noSep) + monthRec;
					counterMonth++;
				}

			}

		}

		toRet = String.format("{\"daily\":[%s], \"monthly\":[%s]}", tableDaily, tableMonthly);

		if (logger.isTraceEnabled())
			logger.trace(String.format("after processing results returning [%s]", toRet));

		return toRet;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).toString();
	}

}
