package gr.cite.bluebridge.analytics.test;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.bluebridge.analytics.logic.Evaluator;
import gr.cite.bluebridge.analytics.model.Consumption;
import gr.cite.bluebridge.analytics.model.Economics;
import gr.cite.bluebridge.analytics.model.Fish;
import gr.cite.bluebridge.analytics.model.FryGeneration;
import gr.cite.bluebridge.analytics.model.ModelInput;

public class ServiceTest {

	public static void main(String[] args) {
		ModelInput input = new ModelInput();
		Fish giltheadSeaBream = new Fish();
		giltheadSeaBream.setFish("giltheadSeaBream");
		giltheadSeaBream.setMixPercent(100d);
		giltheadSeaBream.setInitialPrice(5.20d);

		input.setOffShoreAquaFarm(true);
		input.getFishes().add(giltheadSeaBream);
		input.setFeedPrice(1.15);
		input.setFryPrice(0.20);
		input.setTaxRate(29d);
		input.setDiscountRate(3.75d);
		input.setMaturity(18);

		TreeMap<Integer, Double> inflationRate = new TreeMap<>();
		inflationRate.put(2018, 0.65d);
		input.setCustomInflationRateOnYear(inflationRate);

		Consumption consumption = getFixedData();
		input.setConsumption(consumption);

		Map<Integer, FryGeneration> generationsPerYear = new HashMap<>();
		generationsPerYear.put(1, new FryGeneration(750000, 2.17));
		generationsPerYear.put(4, new FryGeneration(750000, 2.17));
		generationsPerYear.put(7, new FryGeneration(750000, 2.17));
		generationsPerYear.put(10, new FryGeneration(750000, 2.17));

		input.setGenerationsPerYear(generationsPerYear);

		Economics economics = new Evaluator().calculate(input);
		Evaluator.printValues(economics.getDepreciatedValues());
		Evaluator.printValues(economics.getUndepreciatedValues());
	}

	public static Consumption getFixedData() {
		Scanner scanner = new Scanner(ServiceTest.class.getClassLoader().getResourceAsStream("SimulFish-Dataset.json"), "UTF-8");
		String consumptionString = scanner.useDelimiter("\\A").next();
		scanner.close();

		Consumption consumption = null;
		try {
			consumption = new ObjectMapper().readValue(consumptionString, Consumption.class);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return consumption;
	}
}
