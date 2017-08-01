package gr.cite.geoanalytics.environmental.data.retriever;

import gr.cite.geoanalytics.environmental.data.retriever.model.Data;
import gr.cite.geoanalytics.environmental.data.retriever.model.Oxygen;

public class OxygenRetriever extends DataRetriever<Oxygen> {

	private static final String FOLDER = "oxygen";
	private static final String SUFFIX = "-oxygen.tif";

	public OxygenRetriever() throws Exception {
		super(FOLDER, SUFFIX);
	}

	public Oxygen castData(Data data) {
		return new Oxygen(data);
	}
}
