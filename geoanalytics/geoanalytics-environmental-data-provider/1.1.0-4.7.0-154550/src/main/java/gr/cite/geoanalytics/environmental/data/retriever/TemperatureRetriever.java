package gr.cite.geoanalytics.environmental.data.retriever;

import gr.cite.geoanalytics.environmental.data.retriever.model.Data;
import gr.cite.geoanalytics.environmental.data.retriever.model.Temperature;

public class TemperatureRetriever extends DataRetriever<Temperature> {

	private static final String FOLDER = "temperature";
	private static final String SUFFIX = "-temperature.tif";

	public TemperatureRetriever() {
		super(FOLDER, SUFFIX);
	}

	@Override
	public Temperature castData(Data data) {
		return new Temperature(data);
	}
}
