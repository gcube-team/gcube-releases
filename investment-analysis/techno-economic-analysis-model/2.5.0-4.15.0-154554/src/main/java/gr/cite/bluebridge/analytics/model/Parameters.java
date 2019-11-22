package gr.cite.bluebridge.analytics.model;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Parameters {
	
	private static Logger logger = LoggerFactory.getLogger(Parameters.class);
	
	private Long modelId;
	private String modelName;
	private String fishSpecies;
	private Double taxRate;
	private Double discountRate;
	private Double feedPrice;
	private Double fryPrice;
	private Double sellingPrice;
	private Double fishMix;
	private Integer maturity;

	private Boolean isOffShoreAquaFarm;
	private TreeMap<Integer, Double> inflationRate;

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	public String getFishSpecies() {
		return fishSpecies;
	}

	public void setFishSpecies(String fishSpecies) {
		this.fishSpecies = fishSpecies;
	}
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

	public TreeMap<Integer, Double> getInflationRate() {
		return inflationRate;
	}

	public void setInflationRate(TreeMap<Integer, Double> inflationRate) {
		this.inflationRate = inflationRate;
	}

	public String validate() {
		String errorMessage = "";
		try {	
			errorMessage += (modelId < 0) 		? "ModelId is not valid" : "";
			errorMessage += (taxRate < 0) 		? "Tax Rate cannot be negative" : "";
			errorMessage += (feedPrice <= 0) 	? "Feed Price must be greater than 0" : "";
			errorMessage += (fryPrice <= 0) 	? "Fry Price must be greater than 0" : "";
			errorMessage += (sellingPrice <= 0) ? "Selling Price must be greater than 0" : "";
			errorMessage += (discountRate < 0) 	? "Discount Rate  cannot be negative" : "";

			for (Map.Entry<Integer, Double> entry : inflationRate.entrySet()) {
				errorMessage += (entry.getValue() < 0) ? "Price Inflation Rate cannot be negative ( year " + entry.getKey() + " )": "";						
			}			
		} catch (Exception e) {
			logger.error(null, e);
			errorMessage = "One of the parameters was empty";
		}

		return errorMessage;
	}
	
	public void print(){
		logger.debug("modelId  = " + modelId);
		logger.debug("modelName  = " + modelName);
		logger.debug("taxRate = " + taxRate);
		logger.debug("discountRate = " + discountRate);
		logger.debug("feedPrice = " + feedPrice);
		logger.debug("fryPrice = " + fryPrice);
		logger.debug("sellingPrice = " + sellingPrice);
		logger.debug("fishMix = " + fishMix);
		logger.debug("maturity = " + maturity);
		logger.debug("inflationRate = " + inflationRate);
	}
}
