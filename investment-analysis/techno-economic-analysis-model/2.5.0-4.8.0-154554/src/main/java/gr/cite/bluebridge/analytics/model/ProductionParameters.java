package gr.cite.bluebridge.analytics.model;

import java.util.Map;

public class ProductionParameters {
	private int largeCage;
	private int smallCage;
	private int anchorsSystem;
	private boolean isOffShoreAquaFarm;
	private int supportEquipment;
	private int fryNeedPerYear;
	private double packagingPerFish;
	private Consumption consumption;

	private Map<Integer, Integer> feedNeedPerMonth;
	private Map<Integer, FryGeneration> generationsPerYear; // generation month paired with count of fries for each month

	public Map<Integer, FryGeneration> getGenerationsPerYear() {
		return generationsPerYear;
	}

	public void setGenerationsPerYear(Map<Integer, FryGeneration> generationsPerYear) {
		this.generationsPerYear = generationsPerYear;
	}

	public Map<Integer, Integer> getFeedNeedPerMonth() {
		return feedNeedPerMonth;
	}

	public void setFeedNeedPerMonth(Map<Integer, Integer> feedNeedPerMonth) {
		this.feedNeedPerMonth = feedNeedPerMonth;
	}

	public void setOffShoreAquaFarm(boolean isOffShoreAquaFarm) {
		this.isOffShoreAquaFarm = isOffShoreAquaFarm;
	}

	public int getLargeCage() {
		return largeCage;
	}

	public void setLargeCage(int largeCage) {
		this.largeCage = largeCage;
	}

	public int getSmallCage() {
		return smallCage;
	}

	public void setSmallCage(int smallCage) {
		this.smallCage = smallCage;
	}

	public int getAnchorsSystem() {
		return anchorsSystem;
	}

	public void setAnchorsSystem(int anchorsSystem) {
		this.anchorsSystem = anchorsSystem;
	}

	public boolean getIsOffShoreAquaFarm() {
		return isOffShoreAquaFarm;
	}

	public void setIsOffShoreAquaFarm(boolean isOffShoreAquaFarm) {
		this.isOffShoreAquaFarm = isOffShoreAquaFarm;
	}

	public int getSupportEquipment() {
		return supportEquipment;
	}

	public void setSupportEquipment(int supportEquipment) {
		this.supportEquipment = supportEquipment;
	}

	public double getPackagingPerFish() {
		return packagingPerFish;
	}

	public void setPackagingPerFish(double packagingPerFish) {
		this.packagingPerFish = packagingPerFish;
	}

	public int getFryNeedPerYear() {
		return fryNeedPerYear;
	}

	public void setFryNeedPerYear(int fryNeedPerYear) {
		this.fryNeedPerYear = fryNeedPerYear;
	}

	public Consumption getConsumption() {
		return consumption;
	}

	public void setConsumption(Consumption consumption) {
		this.consumption = consumption;
	}
}
