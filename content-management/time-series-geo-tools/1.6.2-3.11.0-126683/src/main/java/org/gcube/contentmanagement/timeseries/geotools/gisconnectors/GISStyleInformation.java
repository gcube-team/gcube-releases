package org.gcube.contentmanagement.timeseries.geotools.gisconnectors;

import java.awt.Color;

public class GISStyleInformation {

	public String getStyleName() {
		return styleName;
	}
	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}
	
	public String getStyleAttribute() {
		return styleAttribute;
	}
	public void setStyleAttribute(String styleAttribute) {
		this.styleAttribute = styleAttribute;
	}
	public Integer getNumberOfClasses() {
		return numberOfClasses;
	}
	public void setNumberOfClasses(Integer numberOfClasses) {
		this.numberOfClasses = numberOfClasses;
	}
	public Color getGradientBase() {
		return gradientBase;
	}
	public void setGradientBase(Color gradientBase) {
		this.gradientBase = gradientBase;
	}
	public Color getGradientMax() {
		return gradientMax;
	}
	public void setGradientMax(Color gradientMax) {
		this.gradientMax = gradientMax;
	}
	public Class getValuesType() {
		return valuesType;
	}
	public void setValuesType(Class valuesType) {
		this.valuesType = valuesType;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public Scales getScaleType() {
		return scaleType;
	}
	public void setScaleType(Scales scaleType) {
		this.scaleType = scaleType;
	}
	public static enum Scales{ logarithm, linear};
	
	private String styleName;
	
	private String styleAttribute; //maxSpeciesCountInACell
	private Integer numberOfClasses;
	private Color gradientBase;
	private Color gradientMax;
	private Class valuesType;
	private Double max;
	private Double min;
	private Scales scaleType;
	
}
