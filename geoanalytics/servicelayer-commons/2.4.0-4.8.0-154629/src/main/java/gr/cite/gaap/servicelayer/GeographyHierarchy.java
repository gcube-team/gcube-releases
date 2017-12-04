package gr.cite.gaap.servicelayer;

import java.util.ArrayList;
import java.util.List;

import gr.cite.geoanalytics.dataaccess.entities.geocode.GeocodeSystem;

public class GeographyHierarchy {
	private List<GeocodeSystem> mainHierarchy = null;
	private List<List<GeocodeSystem>> alternativeHierarchies = new ArrayList<List<GeocodeSystem>>();
	
	public List<GeocodeSystem> getMainHierarchy() {
		return mainHierarchy;
	}
	
	public void setMainHierarchy(List<GeocodeSystem> mainHierarchy) {
		this.mainHierarchy = mainHierarchy;
	}
	
	public List<List<GeocodeSystem>> getAlternativeHierarchies() {
		return alternativeHierarchies;
	}
	
	public void setAlternativeHierarchies(List<List<GeocodeSystem>> alternativeHierarchies) {
		this.alternativeHierarchies = alternativeHierarchies;
	}
	
	public void addAlternativeHierarchy(List<GeocodeSystem> hierarchy) {
		this.alternativeHierarchies.add(hierarchy);
	}
}
