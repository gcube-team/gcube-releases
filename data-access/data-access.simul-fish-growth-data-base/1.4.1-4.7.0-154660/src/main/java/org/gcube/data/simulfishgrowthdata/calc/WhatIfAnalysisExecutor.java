package org.gcube.data.simulfishgrowthdata.calc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

import org.gcube.data.simulfishgrowthdata.api.base.ModelerUtil;
import org.gcube.data.simulfishgrowthdata.model.GlobalModelWrapper;
import org.gcube.data.simulfishgrowthdata.util.UserFriendlyException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import gr.i2s.fishgrowth.model.Modeler;
import gr.i2s.fishgrowth.model.Scenario;

public class WhatIfAnalysisExecutor extends ScenarioExecutor {
	private static final Logger logger = LoggerFactory.getLogger(WhatIfAnalysisExecutor.class);

	String additionalSimilarityConstraint = null;

	public WhatIfAnalysisExecutor setAdditionalSimilarityConstraint(String additionalSimilarityConstraint) {
		this.additionalSimilarityConstraint = additionalSimilarityConstraint;
		return this;
	}

	public WhatIfAnalysisExecutor(Scenario scenario) {
		super(scenario);
	}

	public WhatIfAnalysisExecutor(Session session, Scenario scenario) {
		super(session, scenario);
	}

	@Override
	protected String processResults() {

		LinkedList<Daily> globalResults;
		try {
			long start = System.currentTimeMillis();
			Modeler modeler = new ModelerUtil().getModeler(mSession, mScenario.getModelerId());
			GlobalModelWrapper globalModel = new GlobalModelWrapper(mSession, modeler)
					.setAdditionalSimilarityConstraint(additionalSimilarityConstraint).create();
			GlobalModelScenarioExecutor globalExecutor = new GlobalModelScenarioExecutor(mSession, mScenario,
					globalModel);
			globalExecutor.run();
			globalResults = globalExecutor.getResults();
			long end = System.currentTimeMillis();
			if (logger.isTraceEnabled())
				logger.trace(String.format("Global model creation %d", end - start));
		} catch (Exception e) {
			if (logger.isDebugEnabled())
				logger.debug("Error processing analysis results", e);
			throw new RuntimeException("Error processing analysis results", new UserFriendlyException(
					"Could not process analysis data. There was an error estimating the Global Model.", e));
		}

		// Graphs
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
		String tableWeight = new String();
		String tableFCR = new String();
		String tableFood = new String();
		String recordWeight = "{c:[{v: \"%s\"}, {v: %.2f}, {v: %.2f}]},";
		String recordFCR = "{c:[{v: \"%s\"}, {v: %.2f}, {v: %.2f}]},";
		String recordFood = "{c:[{v: \"%s-%s\"}, {v: %.2f}, {v: %.2f}]},";
		int foodMonth = -1;
		int foodYear = -1;
		double foodCons = 0;
		double foodConsGlobal = 0;
		// a parallel iterator over the corresponding global model. Both
		// sets should have the same amount of data
		Iterator<Daily> globalIterator = globalResults.iterator();
		for (Daily daily : dailyResults) {
			Daily globalDaily = globalIterator.next();

			String day = dateFormat.format(daily.date.getTime());
			tableWeight = tableWeight + String.format(recordWeight, day, daily.mab, globalDaily.mab);
			tableFCR = tableFCR + String.format(recordFCR, day, daily.fcr > 0 ? daily.fcr : 0.0,
					globalDaily.fcr > 0 ? globalDaily.fcr : 0.0);
			// food consumption (monthly)
			if (foodMonth != daily.date.get(Calendar.MONTH) || foodYear != daily.date.get(Calendar.YEAR)) {
				// save and restart
				tableFood = tableFood + String.format(recordFood, foodMonth + 1, foodYear,
						foodCons > 0 ? foodCons / 1000.0 : 0.0, foodConsGlobal > 0 ? foodConsGlobal / 1000.0 : 0.0);
				foodCons = 0;
				foodConsGlobal = 0;
				foodMonth = daily.date.get(Calendar.MONTH);
				foodYear = daily.date.get(Calendar.YEAR);
			}
			foodCons += daily.food;
			foodConsGlobal += globalDaily.food;
		}
		// take care of possible orphan acc values
		if (foodCons > 0) {
			tableFood = tableFood + String.format(recordFood, foodMonth + 1, foodYear, foodCons / 1000.0,
					foodConsGlobal > 0 ? foodConsGlobal / 1000.0 : 0.0);
		}

		return tableWeight + "gri2sbbridge" + tableFCR + "gri2sbbridge" + tableFood;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).toString();
	}

}
