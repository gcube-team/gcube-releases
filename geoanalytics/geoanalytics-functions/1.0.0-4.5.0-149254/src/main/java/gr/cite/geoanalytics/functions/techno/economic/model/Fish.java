package gr.cite.geoanalytics.functions.techno.economic.model;

public class Fish {
	private String fish;
	private double mixPercent;
	private double initialPrice;
	private double weight = 400d;

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Fish() {
		
	}
	
	public String getFish() {
		return fish;
	}

	public void setFish(String fish) {
		this.fish = fish;
	}

	public double getMixPercent() {
		return mixPercent;
	}

	public void setMixPercent(double mixPercent) {
		this.mixPercent = mixPercent;
	}

	public double getInitialPrice() {
		return initialPrice;
	}

	public void setInitialPrice(double initialPrice) {
		this.initialPrice = initialPrice;
	}
}
