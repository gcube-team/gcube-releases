package gr.cite.bluebridge.analytics.resources;

import gr.cite.bluebridge.analytics.model.Consumption;

public class Parameters {
	
	private Long modelId;
	private Double taxRate;	
	private Double feedPrice;	
	private Double fryPrice;	
	private Double sellingPrice;
	private Double fishMix;
	private Boolean isOffShoreAquaFarm;

	public Double getTaxRate() {
		return taxRate;
	}

	public Long getModelId() {
		return modelId;
	}

	public void setModelId(Long modelId) {
		this.modelId = modelId;
	}

	public void setTaxRate(Double taxRate) {
		this.taxRate = taxRate;
	}

	public Double getFeedPrice() {
		return feedPrice;
	}

	public void setFeedPrice(Double feedPrice) {
		this.feedPrice = feedPrice;
	}

	public Double getFryPrice() {
		return fryPrice;
	}

	public void setFryPrice(Double fryPrice) {
		this.fryPrice = fryPrice;
	}

	public Double getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(Double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public Boolean getIsOffShoreAquaFarm() {
		return isOffShoreAquaFarm;
	}

	public void setIsOffShoreAquaFarm(Boolean isOffShoreAquaFarm) {
		this.isOffShoreAquaFarm = isOffShoreAquaFarm;
	}

	public Double getFishMix() {
		return fishMix;
	}

	public void setFishMix(Double fishMix) {
		this.fishMix = fishMix;
	}
}
