package gr.cite.bluebridge.analytics.model;

public class FryGeneration {
	int count;			// number of fish cultivated 
	double weightPerFish;	
	
	public FryGeneration(int count, double weightPerFish) {
		this.count = count;
		this.weightPerFish = weightPerFish;
	}	

	public double getWeightPerFish() {
		return weightPerFish;
	}

	public void setWeightPerFish(double weightPerFish) {
		this.weightPerFish = weightPerFish;
	}

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
}
