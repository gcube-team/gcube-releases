package gr.cite.bluebridge.analytics.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ModelInput {
	
	private double taxRate;	
	private double feedPrice;	
	private double fryPrice;	
	private double sellingPrice;
	private double discountRate;
	
	private int maturity;
	private boolean isOffShoreAquaFarm;
	private Consumption consumption;
	private List<Fish> fishes;
	private TreeMap<Integer, Double> inflationRate;
	private Map<Integer, FryGeneration> generationsPerYear;	

	public ModelInput() {
		fishes = new ArrayList<Fish>();
	}

	public int getMaturity() {
		return maturity;
	}

	public void setMaturity(int maturity) {
		this.maturity = maturity;
	}

	public double getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(double discountRate) {
		this.discountRate = discountRate;
	}

	public TreeMap<Integer, Double> getInflationRate() {
		return inflationRate;
	}

	public void setInflationRate(Object inflationRate) {
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<TreeMap<Integer, Double>> typeRef = new TypeReference<TreeMap<Integer,Double>>() {};
		try {
			this.inflationRate = mapper.readValue((String) inflationRate, typeRef);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Fish> getFishes() {
		return fishes;
	}

	public void setFishes(List<Fish> fishes) {
		this.fishes = fishes;
	}
	
	public double getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(double taxRate) {
		this.taxRate = taxRate;
	}

	public double getFeedPrice() {
		return feedPrice;
	}

	public void setFeedPrice(double feedPrice) {
		this.feedPrice = feedPrice;
	}

	public double getFryPrice() {
		return fryPrice;
	}

	public void setFryPrice(double fryPrice) {
		this.fryPrice = fryPrice;
	}

	public double getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public boolean isOffShoreAquaFarm() {
		return isOffShoreAquaFarm;
	}

	public void setOffShoreAquaFarm(boolean isOffShoreAquaFarm) {
		this.isOffShoreAquaFarm = isOffShoreAquaFarm;
	}
	
	public Consumption getConsumption() {
		return consumption;
	}

	public void setConsumption(Consumption consumption) {
		this.consumption = consumption;
	}

	public Map<Integer, Integer> getFeedNeedPerMonth() {
		return consumption.getFeedNeedPerMonth();
	}

	public Map<Integer, FryGeneration> getGenerationsPerYear() {
		return generationsPerYear;
	}
	
	public void setGenerationsPerYear(Map<Integer, FryGeneration> generationsPerYear) {
		this.generationsPerYear = generationsPerYear;
	}
	
	public Double getInflationRateOnYear(int year){
		return  inflationRate.containsKey(year) ? inflationRate.get(year) : inflationRate.floorEntry(year).getValue();
	}
	
	public void setCustomInflationRateOnYear(TreeMap<Integer, Double> map){
		this.inflationRate = map;
	}
}
