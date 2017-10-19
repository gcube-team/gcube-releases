package gr.cite.geoanalytics.functions.kpi;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.simulfishgrowthdata.util.GCubeUtils;

import gr.cite.bluebridge.analytics.model.Economics;
import gr.cite.geoanalytics.functions.functions.Attribute;
import gr.cite.geoanalytics.functions.functions.Function;

public class IrrSeaBassFunction implements Function, Serializable {

	private static final long serialVersionUID = -6915800403768631565L;

	private static final Long speciesId = 1L;

	private String tenant;

	private EconomicsEvaluator economicsEvaluator;

	public IrrSeaBassFunction(String tenant, Map<String, String> credentials) throws Exception {
		this.tenant = tenant;
		GCubeUtils.prefillDBCredentials(credentials);
		economicsEvaluator = new EconomicsEvaluator();
	}

	@Override
	public List<Attribute> execute(double x, double y) throws Exception {
		try {
			Economics economics = economicsEvaluator.getEconomics(tenant, speciesId, x, y);
			double irr = economics.getDepreciatedValues().getTargetIndicators().getIRR();

			Attribute attribute = new Attribute("irr", new Double(irr));

			List<Attribute> result = new ArrayList<Attribute>();
			result.add(attribute);
			return result;
		} catch (Exception e) {
			throw new Exception("Could not calculate Sea Bass IRR for long lat [" + x + ", " + y + "]", e);
		}
	}
	
	public void destroy() {
		economicsEvaluator.destroy();
	}

	@Override
	public List<Entry<String, Class>> getResultsSchema() {
		List<Map.Entry<String, Class>> schema = new ArrayList<Map.Entry<String, Class>>();
		schema.add(new AbstractMap.SimpleEntry<String, Class>("irr", Double.class));
		return schema;
	}
}
