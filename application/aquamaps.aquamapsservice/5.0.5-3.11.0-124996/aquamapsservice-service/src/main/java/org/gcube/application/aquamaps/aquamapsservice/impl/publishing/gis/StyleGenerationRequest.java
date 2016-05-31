package org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis;

import java.awt.Color;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.AquaMapsManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.isconfig.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StyleGenerationRequest {

	final static Logger logger= LoggerFactory.getLogger(StyleGenerationRequest.class);
	
	
	public static enum ClusterScaleType{
		linear,logarithmic
	}
	
	
	private String nameStyle; 
	private  String attributeName; 
	private  int nClasses; 
	private  Color c1; 
	private  Color c2; 
	private  Class typeValue;
	private  String max;
	private  String min;
	private  ClusterScaleType clusterScaleType;  
	
	
	public static String getDefaultDistributionStyle() throws Exception{		
		return ConfigurationManager.getVODescriptor().getGeoServers().get(0).getDefaultDistributionStyle();
	}
	
	
	public static StyleGenerationRequest getBiodiversityStyle(int min,int max, ClusterScaleType type, String mapName){
		StyleGenerationRequest toReturn= new StyleGenerationRequest();
		toReturn.setAttributeName(AquaMapsManager.maxSpeciesCountInACell);
		toReturn.setNameStyle(ServiceUtils.generateId(mapName, type+"Style"));
		toReturn.setC1(Color.YELLOW);
		toReturn.setC2(Color.RED);
		toReturn.setClusterScaleType(type);
		toReturn.setMax(String.valueOf(max));
		toReturn.setMin(String.valueOf(min));
		int Nclasses=((max-min)>4)?5:max-min;
		logger.debug("Found "+Nclasses+" classes for style (min : "+min+", max : "+max+")");
		toReturn.setNClasses(Nclasses);
		toReturn.setTypeValue(Integer.class);
		return toReturn;
	}
	
	
	/**
	 * @return the nameStyle
	 */
	public String getNameStyle() {
		return nameStyle;
	}
	/**
	 * @param nameStyle the nameStyle to set
	 */
	public void setNameStyle(String nameStyle) {
		this.nameStyle = nameStyle;
	}
	/**
	 * @return the attributeName
	 */
	public String getAttributeName() {
		return attributeName;
	}
	/**
	 * @param attributeName the attributeName to set
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}
	/**
	 * @return the nClasses
	 */
	public int getNClasses() {
		return nClasses;
	}
	/**
	 * @param classes the nClasses to set
	 */
	public void setNClasses(int classes) {
		nClasses = classes;
	}
	/**
	 * @return the c1
	 */
	public Color getC1() {
		return c1;
	}
	/**
	 * @param c1 the c1 to set
	 */
	public void setC1(Color c1) {
		this.c1 = c1;
	}
	/**
	 * @return the c2
	 */
	public Color getC2() {
		return c2;
	}
	/**
	 * @param c2 the c2 to set
	 */
	public void setC2(Color c2) {
		this.c2 = c2;
	}
	/**
	 * @return the typeValue
	 */
	public Class getTypeValue() {
		return typeValue;
	}
	/**
	 * @param typeValue the typeValue to set
	 */
	public void setTypeValue(Class typeValue) {
		this.typeValue = typeValue;
	}
	/**
	 * @return the max
	 */
	public String getMax() {
		return max;
	}
	/**
	 * @param max the max to set
	 */
	public void setMax(String max) {
		this.max = max;
	}
	/**
	 * @return the min
	 */
	public String getMin() {
		return min;
	}
	/**
	 * @param min the min to set
	 */
	public void setMin(String min) {
		this.min = min;
	}
	public void setClusterScaleType(ClusterScaleType clusterScaleType) {
		this.clusterScaleType = clusterScaleType;
	}
	public ClusterScaleType getClusterScaleType() {
		return clusterScaleType;
	}
	
}
