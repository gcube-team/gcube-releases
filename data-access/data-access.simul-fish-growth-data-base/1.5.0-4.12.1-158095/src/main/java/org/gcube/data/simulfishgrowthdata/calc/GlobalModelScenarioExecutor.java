package org.gcube.data.simulfishgrowthdata.calc;

import java.util.LinkedList;
import java.util.List;

import org.gcube.data.simulfishgrowthdata.model.GlobalModelWrapper;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gr.i2s.fishgrowth.model.Fcr;
import gr.i2s.fishgrowth.model.Mortality;
import gr.i2s.fishgrowth.model.Scenario;
import gr.i2s.fishgrowth.model.Sfr;

/**
 * this executor relies on {@link #globalModel}, rather the database, in order
 * to get temperature and KPIs
 *
 */
public class GlobalModelScenarioExecutor extends ScenarioExecutor {
	private static final Logger logger = LoggerFactory.getLogger(GlobalModelScenarioExecutor.class);

	GlobalModelWrapper globalModel;

	LinkedList<Daily> mResults;

	public GlobalModelScenarioExecutor(final Scenario scenario, final GlobalModelWrapper globalModel) {
		super(scenario);
		this.globalModel = globalModel;
	}

	public GlobalModelScenarioExecutor(final Session session, final Scenario scenario,
			final GlobalModelWrapper globalModel) {
		super(session, scenario);
		this.globalModel = globalModel;
	}

	@Override
	protected void loadModelTemperature() {
		try {
			System.arraycopy(globalModel.temperatureBiMonthly, 0, modelTemperature, 0,
					globalModel.temperatureBiMonthly.length);
		} catch (Exception e) {
			logger.error("problem transfering data", e);
		}
	}

	@Override
	protected void loadMortalities(List<Mortality> mortalities) {
		try {
			mortalities.addAll(globalModel.mortalities);

		} catch (Exception e) {
			logger.error("problem transfering data", e);
		}
	}

	@Override
	protected void loadSfrs(List<Sfr> sfrs) {
		try {
			sfrs.addAll(globalModel.sfrs);

		} catch (Exception e) {
			logger.error("problem transfering data", e);
		}
	}

	@Override
	protected void loadFcrs(List<Fcr> fcrs) {
		try {
			fcrs.addAll(globalModel.fcrs);

		} catch (Exception e) {
			logger.error("problem transfering data", e);
		}
	}

	public LinkedList<Daily> getResults() {
		return mResults;
	}

	@Override
	protected String processResults() {
		// No processing, just provide the result
		// shallow copy
		mResults = new LinkedList<>(dailyResults);
		return "";
	}

}
