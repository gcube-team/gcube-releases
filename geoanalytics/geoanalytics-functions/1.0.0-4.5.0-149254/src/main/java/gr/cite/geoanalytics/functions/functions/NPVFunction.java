package gr.cite.geoanalytics.functions.functions;

import java.io.IOException;
import java.util.*;

import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.DirectPosition2D;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.geoanalytics.functions.techno.economic.logic.Evaluator;
import gr.cite.geoanalytics.functions.techno.economic.model.Consumption;
import gr.cite.geoanalytics.functions.techno.economic.model.Economics;
import gr.cite.geoanalytics.functions.techno.economic.model.Fish;
import gr.cite.geoanalytics.functions.techno.economic.model.FryGeneration;
import gr.cite.geoanalytics.functions.techno.economic.model.ModelInput;
import gr.cite.geoanalytics.geospatial.retrieval.RasterRetrievalHelper;

public class NPVFunction implements Function {

	public NPVFunction(String seaSurfaceTemperatureLayerUrl) {
		setSeaSurfaceTemperatureLayerUrl(seaSurfaceTemperatureLayerUrl);
	}
	
	private String seaSurfaceTemperatureLayerUrl;
	
	private GridCoverage2D seaSurfaceTemperatureCoverage = null;
	
	public double execute(double x, double y) throws Exception {
		Scanner scanner = new Scanner(getClass().getClassLoader().getResourceAsStream("SimulFish-Dataset.json"), "UTF-8");
		String consumptionString = scanner.useDelimiter("\\A").next();
		scanner.close();

		Consumption consumption = null;
		try {
			consumption = new ObjectMapper().readValue(consumptionString, Consumption.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Fish giltheadSeaBream = new Fish();
		giltheadSeaBream.setFish("Sea Bream");
		giltheadSeaBream.setInitialPrice(5.20d);
		giltheadSeaBream.setMixPercent(100d);
        
		ModelInput input = new ModelInput();
		
		input.getFishes().add(giltheadSeaBream);
		input.setTaxRate(29d);
		input.setDiscountRate(3.75d);
		input.setInflationRate("{ \"2018\" : \"0.65\" } ");
		input.setMaturity(18);
		input.setFeedPrice(1.15d);
		input.setFryPrice(0.2d);
		input.setOffShoreAquaFarm(true);
		input.setConsumption(consumption);

		Map<Integer, FryGeneration> generationsPerYear = new HashMap<>();
		generationsPerYear.put(1, new FryGeneration(750000, 2.17));
		generationsPerYear.put(4, new FryGeneration(750000, 2.17));
		generationsPerYear.put(7, new FryGeneration(750000, 2.17));
		generationsPerYear.put(10, new FryGeneration(750000, 2.17));
		input.setGenerationsPerYear(generationsPerYear);

		Economics economics = new Evaluator().calculate(input);
		
		return increaseNPVBasedOnTemperature(economics.getDepreciatedValues().getTargetIndicators().getNPV(), x, y);
	}
	
	private double increaseNPVBasedOnTemperature(double npv, double x, double y) throws Exception {
		float temperature = ((float[]) getSeaSurfaceTemperatureCoverage().evaluate(new DirectPosition2D(getSeaSurfaceTemperatureCoverage().getCoordinateReferenceSystem2D(), x, y)))[0];
		if(temperature <= 0) return npv;
		double factor = temperature - 288.15;//288.15 = 15 celsius
		return npv + npv * factor / 100;//foreach celcius unit > 0 increase npv 1%, < 0 decrease
	}
	
	public String getSeaSurfaceTemperatureLayerUrl() {
		return seaSurfaceTemperatureLayerUrl;
	}

	public void setSeaSurfaceTemperatureLayerUrl(String seaSurfaceTemperatureLayerUrl) {
		this.seaSurfaceTemperatureLayerUrl = seaSurfaceTemperatureLayerUrl;
	}
	
	private GridCoverage2D getSeaSurfaceTemperatureCoverage() throws Exception {
		if(seaSurfaceTemperatureCoverage == null){
			seaSurfaceTemperatureCoverage = RasterRetrievalHelper.getCoverage(getSeaSurfaceTemperatureLayerUrl());	
		}
		return seaSurfaceTemperatureCoverage;
	}
}
