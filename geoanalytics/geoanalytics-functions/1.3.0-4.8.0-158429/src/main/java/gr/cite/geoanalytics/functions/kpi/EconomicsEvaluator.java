package gr.cite.geoanalytics.functions.kpi;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import gr.cite.bluebridge.analytics.logic.Evaluator;
import gr.cite.bluebridge.analytics.model.Consumption;
import gr.cite.bluebridge.analytics.model.Economics;
import gr.cite.bluebridge.analytics.model.Fish;
import gr.cite.bluebridge.analytics.model.FryGeneration;
import gr.cite.bluebridge.analytics.model.ModelInput;
import gr.cite.geoanalytics.functions.production.ProductionEvaluator;

public class EconomicsEvaluator {

	public EconomicsEvaluator() throws Exception {
		productionEvaluator = new ProductionEvaluator(true);
	}

	private ProductionEvaluator productionEvaluator;

	public Economics getEconomics(String tenant, long speciesId, double x, double y) throws Exception {
		Fish giltheadSeaBream = new Fish();
		giltheadSeaBream.setFish("Sea Bream");
		giltheadSeaBream.setInitialPrice(5.20d);
		giltheadSeaBream.setMixPercent(100d);

		ModelInput input = new ModelInput();

		TreeMap<Integer, Double> inflationRate = new TreeMap<>();
		inflationRate.put(2018, 0.65);

		input.getFishes().add(giltheadSeaBream);
		input.setTaxRate(29d);
		input.setDiscountRate(3.75d);
		input.setInflationRate(inflationRate);
		input.setMaturity(18);
		input.setFeedPrice(1.15d);
		input.setFryPrice(0.2d);
		input.setOffShoreAquaFarm(true);

		Map<Integer, FryGeneration> generationsPerYear = new HashMap<>();
		generationsPerYear.put(1, new FryGeneration(750000, 2.17));
		generationsPerYear.put(4, new FryGeneration(750000, 2.17));
		generationsPerYear.put(7, new FryGeneration(750000, 2.17));
		generationsPerYear.put(10, new FryGeneration(750000, 2.17));
		input.setGenerationsPerYear(generationsPerYear);

		// String scope = "/gcube/preprod/preECO";

		Consumption consumption = productionEvaluator.getConsumptionFromSimulFishGrowthData(tenant, speciesId, x, y, input.getMaturity());
		input.setConsumption(consumption);

		Economics economics = new Evaluator().calculate(input);

		return economics;
	}
	
	public void destroy() {
		productionEvaluator.destroy();
	}
}
