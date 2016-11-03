package gr.cite.bluebridge.analytics.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelInput {

	private List<Fish> fishes;
	private double taxRate;	
	private double feedPrice;	
	private double fryPrice;	
	private double sellingPrice;	
	private boolean isOffShoreAquaFarm;
	private Consumption consumption;
	private Map<Integer, FryGeneration> generationsPerYear;	

	public ModelInput() {
		fishes = new ArrayList<Fish>();
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
}
