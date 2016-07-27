package org.gcube.contentmanagement.timeseries.geotools.representations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

public class GISLayer {
	private String layerName;
	private String layerTitle;
	private String preferredStyleName;
	private List<String> csquarecodes;
	private List<String> geometries;
	private Map<String,String> squaresVsValues;
	private Map<String,String> squaresVsOtherInfo;
	private List<Double> values;
	private List<String> infos;
	private boolean isNotEmpty;
	
	public void forceNotEmpty(){
		isNotEmpty = true;
	}
	
	public boolean isEmpty(){
		return ((!isNotEmpty)&&((csquarecodes==null) || (csquarecodes.size()==0)));
	}
	
 	public List<String> getInfos() {
		return infos;
	}

	public void setInfos(List<String> infos) {
		this.infos = infos;
	}

	private String valuesColumnName;
	private double min;
	private double max;
	
	public List<Double> getValues() {
		return values;
	}

	public void setValues(List<Double> values) {
		this.values = values;
	}

	public List<String> getGeometries() {
		return geometries;
	}

	
	public GISLayer(String name){
		this.csquarecodes = new ArrayList<String>();
		this.geometries = new ArrayList<String>();
		this.values = new ArrayList<Double>();
		this.infos = new ArrayList<String>();
		this.layerName = name;
		squaresVsValues = new HashMap<String, String>();
		squaresVsOtherInfo = new HashMap<String, String>();
		min = Double.MAX_VALUE;
		max = -Double.MAX_VALUE;
		isNotEmpty = false;
	}
	
	public void appendListofSquares(List<String> csquareCodes, Double valueForAll,String information){
		for (String square:csquareCodes){
			csquarecodes.add(square);
			values.add(valueForAll);
			infos.add(information);
		}
	}
	
	
	//merge a List of GISLayers with the current layer
	public void mergeGISLayers(List<GISLayer> layers){
		
		for (GISLayer layer:layers){
			List<String> csquares = layer.getCsquareCodes();
			List<Double> values = layer.getValues();
			List<String> infoss = layer.getInfos();
			if ((layerTitle == null)||(layerTitle.length()==0))
				layerTitle = layer.getLayerTitle();
			
			appendListofSquares(csquares, values,infoss);
		}
		AnalysisLogger.getLogger().warn("continuing to merge");
		//generating parallel lists
		for (String key:squaresVsValues.keySet()){
			String svalue = this.squaresVsValues.get(key);
			if (svalue==null) svalue = "0";
			double value = Double.parseDouble(svalue);
			csquarecodes.add(key);
			values.add(value);
			String info = this.squaresVsOtherInfo.get(key);
			if (info==null) info = "";
			infos.add(info);
		}
	}
	

	public void appendListofSquares(List<String> csquareCodes, List<Double> values,List<String> infos){
		int i = 0;
		
		for (String square:csquareCodes){
		
			String svalue = this.squaresVsValues.get(square);
			
			double value = values.get(i);
			if (svalue != null){
				double prevval = Double.parseDouble(svalue);
				value += prevval;
				
			}
			
			String information = this.squaresVsOtherInfo.get(square);
			if (information== null)
				information = "";
			String inf = infos.get(i);
			if ((inf!=null)&&(inf.length()>0))
				information += inf+"; ";
			
			
			squaresVsValues.put(square, ""+value);
			squaresVsOtherInfo.put(square,  information);
			
			if (value>max)
				max = value;
			if (value<min)
				min = value;
			i++;
		}
		
		if (max == min) max = min +1;
		
		
	}
	
	
	public void addTriple(String csquarecode,String geom,Double prob){
		csquarecodes.add(csquarecode);
		geometries.add(geom);
		values.add(prob);
	}


	public void setLayerName(String layerName) {
		this.layerName = layerName;
	}


	public String getLayerName() {
		return layerName;
	}
	
	public List<String> getCsquareCodes(){
		return csquarecodes;
	}
	
	public void setGeometries(List<String> geometries){
		this.geometries = geometries;
	}
	
	public void setValuesColumnName(String valuesColumnName) {
		this.valuesColumnName = valuesColumnName;
	}

	public String getValuesColumnName() {
		return valuesColumnName;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMin() {
		return min;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMax() {
		return max;
	}

	public String getPreferredStyleName() {
		return preferredStyleName;
	}

	public void setPreferredStyleName(String preferredStyleName) {
		this.preferredStyleName = preferredStyleName;
	}

	public String getLayerTitle() {
		return layerTitle;
	}

	public void setLayerTitle(String layerTitle) {
		this.layerTitle = layerTitle;
	}
	
}
