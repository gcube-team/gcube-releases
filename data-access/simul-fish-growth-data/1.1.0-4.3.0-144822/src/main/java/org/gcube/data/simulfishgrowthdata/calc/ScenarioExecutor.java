package org.gcube.data.simulfishgrowthdata.calc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.gcube.data.simulfishgrowthdata.api.FcrUtil;
import org.gcube.data.simulfishgrowthdata.api.ModelerFullUtil;
import org.gcube.data.simulfishgrowthdata.api.MortalityUtil;
import org.gcube.data.simulfishgrowthdata.api.ScenarioUtil;
import org.gcube.data.simulfishgrowthdata.api.SfrUtil;
import org.gcube.data.simulfishgrowthdata.api.SiteUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

import gr.i2s.fishgrowth.Simulator.Executor;
import gr.i2s.fishgrowth.model.Fcr;
import gr.i2s.fishgrowth.model.ModelerFull;
import gr.i2s.fishgrowth.model.Mortality;
import gr.i2s.fishgrowth.model.Scenario;
import gr.i2s.fishgrowth.model.Sfr;
import gr.i2s.fishgrowth.model.Site;

/**
 * setup executor based on a database scenario
 * 
 * @author bluebridge
 *
 */
public class ScenarioExecutor extends Executor {
	private static final Logger logger = LoggerFactory.getLogger(ScenarioExecutor.class);

	private boolean doSave = true;

	public ScenarioExecutor(final Scenario scenario) {
		super(scenario);
		if (logger.isTraceEnabled())
			logger.trace(String.format("ctor"));

	}

	public ScenarioExecutor setDoSave(boolean doSave) {
		this.doSave = doSave;
		return this;
	}

	@Override
	protected void beforeRun() {
		super.beforeRun();

		long start = System.currentTimeMillis();

		// these should be cached in the session
		try {
			ModelerFull modeler = new ModelerFullUtil().getModelerFull(mScenario.getModelerId());
			Site entity = new SiteUtil().getSite(modeler.getSiteId());
			modelTemperature[0] = (int) entity.getPeriodJanA();
			modelTemperature[1] = (int) entity.getPeriodJanB();
			modelTemperature[2] = (int) entity.getPeriodFebA();
			modelTemperature[3] = (int) entity.getPeriodFebB();
			modelTemperature[4] = (int) entity.getPeriodMarA();
			modelTemperature[5] = (int) entity.getPeriodMarB();
			modelTemperature[6] = (int) entity.getPeriodAprA();
			modelTemperature[7] = (int) entity.getPeriodAprB();
			modelTemperature[8] = (int) entity.getPeriodMayA();
			modelTemperature[9] = (int) entity.getPeriodMayB();
			modelTemperature[10] = (int) entity.getPeriodJunA();
			modelTemperature[11] = (int) entity.getPeriodJunB();
			modelTemperature[12] = (int) entity.getPeriodJulA();
			modelTemperature[13] = (int) entity.getPeriodJulB();
			modelTemperature[14] = (int) entity.getPeriodAugA();
			modelTemperature[15] = (int) entity.getPeriodAugB();
			modelTemperature[16] = (int) entity.getPeriodSepA();
			modelTemperature[17] = (int) entity.getPeriodSepB();
			modelTemperature[18] = (int) entity.getPeriodOctA();
			modelTemperature[19] = (int) entity.getPeriodOctB();
			modelTemperature[20] = (int) entity.getPeriodNovA();
			modelTemperature[21] = (int) entity.getPeriodNovB();
			modelTemperature[22] = (int) entity.getPeriodDecA();
			modelTemperature[23] = (int) entity.getPeriodDecB();
		} catch (Exception e) {
			logger.error("problem", e);
		}

		// temperature
		Calendar startDate = Calendar.getInstance();
		startDate.setTime(mScenario.getStartDate());
		startDate.set(Calendar.HOUR, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(mScenario.getTargetDate());
		endDate.set(Calendar.HOUR, 0);
		endDate.set(Calendar.MINUTE, 0);
		endDate.set(Calendar.SECOND, 0);
		endDate.set(Calendar.MILLISECOND, 0);
		for (Calendar curDate = startDate; !curDate.after(endDate); curDate.add(Calendar.DAY_OF_MONTH, 1))

		{
			int month = curDate.get(Calendar.MONTH) + 1;
			int dayMonth = curDate.get(Calendar.DAY_OF_MONTH);
			int middle = (month == 2 ? 14 : 15);
			int idx = ((2 * month) - (dayMonth > middle ? 0 : 1)) - 1;
			temperatureTable.put(curDate.getTimeInMillis(), modelTemperature[idx]);
		}
		if (logger.isTraceEnabled())
			logger.trace(String.format("temperature table from db [%s]", temperatureTable));

		// load scenario from db

		// FCR
		List<Fcr> fcrs = new ArrayList<>();

		{
			try {
				fcrs.addAll(new FcrUtil().getFcrs(mScenario.getModelerId()));

			} catch (Exception e) {
				logger.error("problem", e);
			}
			if (logger.isTraceEnabled())
				logger.trace(String.format("fcr list from db [%s]", fcrs));
			fcrs = fcrByTempWeight.sortedCopy(fcrs);
			if (logger.isTraceEnabled())
				logger.trace(String.format("fcr list from db sorted [%s]", fcrs));
			double lastLimit = 0;
			double prevMab = Double.MAX_VALUE;
			int curTemp = -1;
			RangeMap<Double, Double> tempColumn = null;
			// should be sorted to temperature asc, then weight desc
			for (Fcr fcr : fcrs) {
				if (fcr.getTemperature() != curTemp) {
					// temperature value changed; save previous
					if (tempColumn != null) {
						// take care of the lower limit
						if (lastLimit > 0) {
							tempColumn.put(Range.closedOpen(0.0, lastLimit), 0.0);
						}
						fcrTable.put(curTemp, tempColumn);
					}

					curTemp = fcr.getTemperature();
					// reset
					tempColumn = TreeRangeMap.create();
					prevMab = Double.MAX_VALUE;
				}
				double mab = fcr.getFromWeight();
				tempColumn.put(Range.closedOpen(mab, prevMab), fcr.getValue());
				lastLimit = mab;
				prevMab = mab;
			}
			// last temperature didn't get a chance to see a temperature value
			// change
			if (tempColumn != null) {
				if (lastLimit > 0) {
					tempColumn.put(Range.closedOpen(0.0, lastLimit), 0.0);
				}
				fcrTable.put(curTemp, tempColumn);
			}
		}
		if (logger.isTraceEnabled())
			logger.trace(String.format("fcr table [%s]", fcrTable));

		// SFR
		List<Sfr> sfrs = new ArrayList<>();

		{
			try {
				sfrs.addAll(new SfrUtil().getSfrs(mScenario.getModelerId()));

			} catch (Exception e) {
				logger.error("problem", e);
			}
			if (logger.isTraceEnabled())
				logger.trace(String.format("sfr list from db [%s]", sfrs));
			sfrs = sfrByTempWeight.sortedCopy(sfrs);
			if (logger.isTraceEnabled())
				logger.trace(String.format("sfr list from db sorted [%s]", sfrs));
			double lastLimit = 0;
			double prevMab = Double.MAX_VALUE;
			int curTemp = -1;
			RangeMap<Double, Double> tempColumn = null;
			// should be sorted to temperature asc, then weight desc
			for (Sfr sfr : sfrs) {
				if (sfr.getTemperature() != curTemp) {
					// temperature value changed; save previous
					if (tempColumn != null) {
						// take care of the lower limit
						if (lastLimit > 0) {
							tempColumn.put(Range.closedOpen(0.0, lastLimit), 0.0);
						}
						sfrTable.put(curTemp, tempColumn);
					}

					curTemp = sfr.getTemperature();
					// reset
					tempColumn = TreeRangeMap.create();
					prevMab = Double.MAX_VALUE;
				}
				double mab = sfr.getFromWeight();
				tempColumn.put(Range.closedOpen(mab, prevMab), sfr.getValue());
				lastLimit = mab;
				prevMab = mab;
			}
			// last temperature didn't get a chance to see a temperature value
			// change
			if (tempColumn != null) {
				if (lastLimit > 0) {
					tempColumn.put(Range.closedOpen(0.0, lastLimit), 0.0);
				}
				sfrTable.put(curTemp, tempColumn);
			}
		}
		if (logger.isTraceEnabled())
			logger.trace(String.format("sfr table [%s]", sfrTable));

		// Mortality
		List<Mortality> mortalities = new ArrayList<>();

		{
			try {
				mortalities.addAll(new MortalityUtil().getMortalities(mScenario.getModelerId()));

			} catch (Exception e) {
				logger.error("problem", e);
			}
			if (logger.isTraceEnabled())
				logger.trace(String.format("mortality list from db [%s]", mortalities));
			mortalities = mortalityByTempWeight.sortedCopy(mortalities);
			if (logger.isTraceEnabled())
				logger.trace(String.format("mortality list from db sorted [%s]", mortalities));
			double lastLimit = 0;
			double prevMab = Double.MAX_VALUE;
			int curTemp = -1;
			RangeMap<Double, Double> tempColumn = null;
			// should be sorted to temperature asc, then weight desc
			for (Mortality mortality : mortalities) {
				if (mortality.getTemperature() != curTemp) {
					// temperature value changed; save previous
					if (tempColumn != null) {
						// take care of the lower limit
						if (lastLimit > 0) {
							tempColumn.put(Range.closedOpen(0.0, lastLimit), 0.0);
						}
						mortalityTable.put(curTemp, tempColumn);
					}

					curTemp = mortality.getTemperature();
					// reset
					tempColumn = TreeRangeMap.create();
					prevMab = Double.MAX_VALUE;
				}
				double mab = mortality.getFromWeight();
				tempColumn.put(Range.closedOpen(mab, prevMab), mortality.getValue());
				lastLimit = mab;
				prevMab = mab;
			}
			// last temperature didn't get a chance to see a temperature value
			// change
			if (tempColumn != null) {
				if (lastLimit > 0) {
					tempColumn.put(Range.closedOpen(0.0, lastLimit), 0.0);
				}
				mortalityTable.put(curTemp, tempColumn);
			}
		}
		if (logger.isTraceEnabled())
			logger.trace(String.format("mortality table [%s]", mortalityTable));

		long duration = System.currentTimeMillis() - start;
		if (logger.isDebugEnabled())
			logger.debug(String.format("preparation took [%s] ms", duration));

	}

	@Override
	protected void afterRun() {
		try {
			super.afterRun();
			if (doSave) {
				if (logger.isDebugEnabled())
					logger.debug(String.format("updating [%s]", mScenario));
				new ScenarioUtil().update(mScenario);
			}
		} catch (Exception e) {
			logger.error("problem", e);
		}
	}

	@Override
	protected String processResults() {

		// Graphs
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		String tableWeight = new String();
		String tableFCR = new String();
		String tableFood = new String();
		String recordWeight = "{c:[{v: \"%s\"}, {v: %.2f}]},";
		String recordFCR = "{c:[{v: \"%s\"}, {v: %.2f}, {v: %.2f}]},";
		String recordFood = "{c:[{v: \"%s-%s\"}, {v: %.2f}]},";
		int foodMonth = -1;
		int foodYear = -1;
		double foodCons = 0;
		for (Daily daily : dailyResults) {
			String day = dateFormat.format(daily.date.getTime());
			tableWeight = tableWeight + String.format(recordWeight, day, daily.mab);
			// TODO a/ which fcr I need? and b/ the 2nd fcr should be the global
			// fcr
			if (daily.fcrBiol > 0)
				tableFCR = tableFCR + String.format(recordFCR, day, daily.fcrBiol, daily.fcrEcon);
			// food consumption (monthly)
			if (foodMonth != daily.date.get(Calendar.MONTH) || foodYear != daily.date.get(Calendar.YEAR)) {
				// save and restart
				if (foodCons > 0) {
					tableFood = tableFood + String.format(recordFood, foodMonth + 1, foodYear, foodCons / 1000.0);
				}
				foodCons = 0;
				foodMonth = daily.date.get(Calendar.MONTH);
				foodYear = daily.date.get(Calendar.YEAR);
			}
			foodCons += daily.food;
		}
		// take care of possible orphan acc values
		if (foodCons > 0) {
			tableFood = tableFood + String.format(recordFood, foodMonth + 1, foodYear, foodCons / 1000.0);
		}

		return tableWeight + "gri2sbbridge" + tableFCR + "gri2sbbridge" + tableFood;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).toString();
	}

}
