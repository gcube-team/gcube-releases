package gr.cite.geoanalytics.functions.production.kpis;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.simulfishgrowthdata.util.GCubeUtils;

import gr.cite.bluebridge.analytics.model.Consumption;
import gr.cite.bluebridge.analytics.model.Consumption.Monthly;
import gr.cite.geoanalytics.functions.functions.Attribute;
import gr.cite.geoanalytics.functions.functions.Function;
import gr.cite.geoanalytics.functions.production.ProductionEvaluator;

public class FoodConsumptionSeaBassFunction implements Function, Serializable {

	private static final long serialVersionUID = -6915800403768631565L;

	private static final Long speciesId = 1L;
	
	private static final int maturity = 18;

	private String tenant;

	private ProductionEvaluator productionEvaluator;
	
	public FoodConsumptionSeaBassFunction(String tenant, Map<String, String> credentials) throws Exception {
		this.tenant = tenant;
		GCubeUtils.prefillDBCredentials(credentials);
		productionEvaluator = new ProductionEvaluator(true);
	}

	@Override
	public List<Attribute> execute(double x, double y) throws Exception {
		try {	
			Consumption consumption = productionEvaluator.getConsumptionFromSimulFishGrowthData(tenant, speciesId, x, y, maturity);
			
			Monthly last = consumption.getMonthly().get(consumption.getMonthly().size() - 1);
			
			Attribute attribute = new Attribute("feed", new Double(last.getFood()));
			
			List<Attribute> result = new ArrayList<Attribute>();
			result.add(attribute);
			return result;
		} catch (Exception e) {
			throw new Exception("Could not calculate food consumption for long lat [" + x + ", " + y + "]", e);
		}
	}
	
	public void destroy() {
		productionEvaluator.destroy();
	}

	@Override
	public List<Entry<String, Class>> getResultsSchema() {
		List<Map.Entry<String, Class>> schema = new ArrayList<Map.Entry<String, Class>>();
		schema.add(new AbstractMap.SimpleEntry<String, Class>("feed", Double.class));
		return schema;
	}
}
