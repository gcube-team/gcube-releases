package gr.cite.gaap.datatransferobjects;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeocodeDeleteSelection {
	private static Logger logger = LoggerFactory.getLogger(GeocodeDeleteSelection.class);

	public List<LayerInfo> terms = Collections.emptyList();

	
	
	public GeocodeDeleteSelection() {
		super();
		logger.trace("Initialized default contructor for GeocodeDeleteSelection");
	}

	public List<LayerInfo> getTerms() {
		return terms;
	}

	public void setTerms(List<LayerInfo> terms) {
		this.terms = terms;
	}
}
