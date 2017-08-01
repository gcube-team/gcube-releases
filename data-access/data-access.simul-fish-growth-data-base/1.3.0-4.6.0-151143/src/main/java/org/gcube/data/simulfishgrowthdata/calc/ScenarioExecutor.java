package org.gcube.data.simulfishgrowthdata.calc;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.gcube.data.simulfishgrowthdata.api.base.FcrUtil;
import org.gcube.data.simulfishgrowthdata.api.base.ModelerFullUtil;
import org.gcube.data.simulfishgrowthdata.api.base.MortalityUtil;
import org.gcube.data.simulfishgrowthdata.api.base.ScenarioUtil;
import org.gcube.data.simulfishgrowthdata.api.base.SfrUtil;
import org.gcube.data.simulfishgrowthdata.api.base.SiteUtil;
import org.hibernate.Session;
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
 */
public abstract class ScenarioExecutor extends Executor {
	private static final Logger logger = LoggerFactory.getLogger(ScenarioExecutor.class);

	private boolean doSave = true;

	protected Session mSession = null;

	public ScenarioExecutor(final Scenario scenario) {
		super(scenario);
		if (logger.isTraceEnabled())
			logger.trace(String.format("ctor"));

	}

	public ScenarioExecutor(final Session session, final Scenario scenario) {
		super(scenario);
		if (logger.isTraceEnabled())
			logger.trace(String.format("ctor"));
		mSession = session;
	}

	public ScenarioExecutor setDoSave(boolean doSave) {
		this.doSave = doSave;
		return this;
	}

	@Override
	protected void beforeRun() {

		long start = System.currentTimeMillis();

		try {
			// load scenario

			loadModelTemperature();
			makeTemperatureTable();
			if (logger.isTraceEnabled())
				logger.trace(String.format("temperature table [%s]", temperatureTable));

			List<Fcr> fcrs = new ArrayList<Fcr>();
			loadFcrs(fcrs);
			adjustFcrs(fcrs);
			if (logger.isTraceEnabled())
				logger.trace(String.format("fcr table [%s]", fcrTable));

			List<Sfr> sfrs = new ArrayList<Sfr>();
			loadSfrs(sfrs);
			adjustSfrs(sfrs);
			if (logger.isTraceEnabled())
				logger.trace(String.format("sfr table [%s]", sfrTable));

			List<Mortality> mortalities = new ArrayList<Mortality>();
			loadMortalities(mortalities);
			adjustMortalities(mortalities);
			if (logger.isTraceEnabled())
				logger.trace(String.format("mortality table [%s]", mortalityTable));

		} catch (Exception e) {
			throw new RuntimeException("Problem on beforeRun", e);
		}

		long duration = System.currentTimeMillis() - start;
		if (logger.isDebugEnabled())
			logger.debug(String.format("preparation took [%s] ms", duration));

	}

	protected void adjustMortalities(List<Mortality> mortalities) {
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
		// last temperature didn't get a chance to see a temperature
		// value
		// change
		if (tempColumn != null) {
			if (lastLimit > 0) {
				tempColumn.put(Range.closedOpen(0.0, lastLimit), 0.0);
			}
			mortalityTable.put(curTemp, tempColumn);
		}
	}

	protected void adjustSfrs(List<Sfr> sfrs) {
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
		// last temperature didn't get a chance to see a temperature
		// value
		// change
		if (tempColumn != null) {
			if (lastLimit > 0) {
				tempColumn.put(Range.closedOpen(0.0, lastLimit), 0.0);
			}
			sfrTable.put(curTemp, tempColumn);
		}
	}

	protected void adjustFcrs(List<Fcr> fcrs) {
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
		// last temperature didn't get a chance to see a temperature
		// value
		// change
		if (tempColumn != null) {
			if (lastLimit > 0) {
				tempColumn.put(Range.closedOpen(0.0, lastLimit), 0.0);
			}
			fcrTable.put(curTemp, tempColumn);
		}
	}

	protected void makeTemperatureTable() {
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
	}

	protected void loadModelTemperature() throws Exception {
		ModelerFull modeler = mSession == null ? new ModelerFullUtil().getModelerFull(mScenario.getModelerId())
				: new ModelerFullUtil().getModelerFull(mSession, mScenario.getModelerId());
		Site entity = mSession == null ? new SiteUtil().getSite(modeler.getSiteId())
				: new SiteUtil().getSite(mSession, modeler.getSiteId());
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
	}

	protected void loadMortalities(List<Mortality> values) throws Exception {
		values.addAll(mSession == null ? new MortalityUtil().getMortalities(mScenario.getModelerId())
				: new MortalityUtil().getMortalities(mSession, mScenario.getModelerId()));
		if (logger.isTraceEnabled())
			logger.trace(String.format("mortalities list from db [%s]", values));
	}

	protected void loadSfrs(List<Sfr> values) throws Exception {
		values.addAll(mSession == null ? new SfrUtil().getSfrs(mScenario.getModelerId())
				: new SfrUtil().getSfrs(mSession, mScenario.getModelerId()));
		if (logger.isTraceEnabled())
			logger.trace(String.format("sfr list from db [%s]", values));
	}

	protected void loadFcrs(List<Fcr> values) throws Exception {
		values.addAll(mSession == null ? new FcrUtil().getFcrs(mScenario.getModelerId())
				: new FcrUtil().getFcrs(mSession, mScenario.getModelerId()));
		if (logger.isTraceEnabled())
			logger.trace(String.format("fcr list from db [%s]", values));
	}

	@Override
	protected void afterRun() {
		if (doSave) {
			if (logger.isDebugEnabled())
				logger.debug(String.format("updating [%s]", mScenario));

			try {
				if (mSession == null)
					new ScenarioUtil().update(mScenario);
				else
					new ScenarioUtil().update(mSession, mScenario);
			} catch (Exception e) {
				throw new RuntimeException("Problem on afterRun", e);
			}

		}
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).toString();
	}

}
