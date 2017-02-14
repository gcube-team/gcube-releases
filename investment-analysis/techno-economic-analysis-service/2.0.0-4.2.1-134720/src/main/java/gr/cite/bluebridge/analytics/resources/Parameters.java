package gr.cite.bluebridge.analytics.resources;

public class Parameters {
	private Long modelId;
	private Double taxRate;	
	private Double discountRate;
	private Double feedPrice;	
	private Double fryPrice;	
	private Double sellingPrice;
	private Double fishMix;
	private Integer maturity;

	private Boolean isOffShoreAquaFarm;
	private Object inflationRate;
	
	public Integer getMaturity() {
		return maturity;
	}

	public void setMaturity(Integer maturity) {
		this.maturity = maturity;
	}
	
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
	
	public Double getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(Double discountRate) {
		this.discountRate = discountRate;
	}
	
	public Object getInflationRate() {
		return inflationRate;
	}

	public void setInflationRate(Object inflationRate) {
		this.inflationRate = inflationRate;
	}
	
	public String validate(){
		String errorMessage = "";		
		errorMessage += (taxRate < 0) 			? "\nTax Rate cannot be negative" 					: "";
		errorMessage += (feedPrice <= 0) 		? "\nFeed Price must be greater than 0" 			: "";
		errorMessage += (fryPrice <= 0) 		? "\nFry Price must be greater than 0" 				: "";
		errorMessage += (sellingPrice <= 0) 	? "\nSelling Price must be greater than 0" 			: "";	
		errorMessage += (discountRate < 0) 		? "\nDiscount Rate  cannot be negative" 			: "";
		
/*		for(Map.Entry <Integer, Double> entry : inflationRate.entrySet()){
			errorMessage += (entry.getValue() <= 0) 	? "\nInflation Rate cannot be negative (year " + entry.getKey() : "";
		}*/
		
		return errorMessage;
	}	
}
