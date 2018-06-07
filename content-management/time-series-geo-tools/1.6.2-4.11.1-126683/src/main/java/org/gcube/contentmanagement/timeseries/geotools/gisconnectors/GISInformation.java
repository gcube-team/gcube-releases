package org.gcube.contentmanagement.timeseries.geotools.gisconnectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GISInformation {
	
	private String geoNetworkUrl;
	private String geoNetworkUserName;
	private String geoNetworkPwd;
	
	private String gisUrl;
	private String gisUserName;
	private String gisPwd;
	private String gisDataStore;
	private String gisWorkspace;

	private GISGroupInformation group;
	private List<GISLayerInformation> layers;
	private Map<String, List<GISStyleInformation>> styles;

	public GISInformation() {
		layers = new ArrayList<GISLayerInformation>();
		styles = new HashMap<String, List<GISStyleInformation>>();
	}

	public void clean(){
		layers = new ArrayList<GISLayerInformation>();
		styles = new HashMap<String, List<GISStyleInformation>>();
	}
	
	public String getGisUrl() {
		return gisUrl;
	}

	public void setGisUrl(String gisUrl) {
		this.gisUrl = gisUrl;
	}

	public String getGisUserName() {
		return gisUserName;
	}

	public void setGisUserName(String gisUserName) {
		this.gisUserName = gisUserName;
	}

	public String getGisPwd() {
		return gisPwd;
	}

	public void setGisPwd(String gisPwd) {
		this.gisPwd = gisPwd;
	}

	public void setGroup(GISGroupInformation group) {
		this.group = group;
	}

	public GISGroupInformation getGroup() {
		return group;
	}

	public void setLayers(List<GISLayerInformation> layers) {
		this.layers = layers;
	}

	public List<GISLayerInformation> getLayers() {
		return layers;
	}

	public void addLayer(GISLayerInformation layer) {
		layers.add(layer);
	}

	public void addStyle(String layer, GISStyleInformation style) {
		List<GISStyleInformation> listOfStyles = styles.get(layer);
		if (listOfStyles == null)
			listOfStyles = new ArrayList<GISStyleInformation>();

		listOfStyles.add(style);

		styles.put(layer, listOfStyles);
	}

	public void setStyles(Map<String, List<GISStyleInformation>> styles) {
		this.styles = styles;
	}

	public Map<String, List<GISStyleInformation>> getStyles() {
		return styles;
	}

	public ArrayList<String> getStylesNames(String layerName) {
		ArrayList<String> stylesNames = new ArrayList<String>();
		List<GISStyleInformation> stylel = styles.get(layerName);

		if (stylel != null) {
			for (GISStyleInformation style : stylel)
				stylesNames.add(style.getStyleName());
		}
		
		return stylesNames;
	}

	public ArrayList<String> getAllStylesNames() {
		ArrayList<String> stylesNames = new ArrayList<String>();
		for (List<GISStyleInformation> stylel : styles.values()) {
			if (stylel != null) {
				for (GISStyleInformation style : stylel)
					stylesNames.add(style.getStyleName());
			}
		}
		return stylesNames;
	}

	public void setGisDataStore(String gisDataStore) {
		this.gisDataStore = gisDataStore;
	}

	public String getGisDataStore() {
		return gisDataStore;
	}

	public void setGisWorkspace(String gisWorkspace) {
		this.gisWorkspace = gisWorkspace;
	}

	public String getGisWorkspace() {
		return gisWorkspace;
	}

	public String getGeoNetworkUrl() {
		return geoNetworkUrl;
	}

	public void setGeoNetworkUrl(String geoNetworkUrl) {
		this.geoNetworkUrl = geoNetworkUrl;
	}

	public String getGeoNetworkUserName() {
		return geoNetworkUserName;
	}

	public void setGeoNetworkUserName(String geoNetworkUserName) {
		this.geoNetworkUserName = geoNetworkUserName;
	}

	public String getGeoNetworkPwd() {
		return geoNetworkPwd;
	}

	public void setGeoNetworkPwd(String geoNetworkPwd) {
		this.geoNetworkPwd = geoNetworkPwd;
	}

}
