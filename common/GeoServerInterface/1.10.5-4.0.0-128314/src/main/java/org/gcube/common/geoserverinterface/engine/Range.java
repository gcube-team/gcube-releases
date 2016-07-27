package org.gcube.common.geoserverinterface.engine;

import java.awt.Color;

public class Range{
	 
	 public Range(String toFilterProperty, Color toAssignColor, Object min,
			Object max, Condition condition) {
		super();
		this.toFilterProperty = toFilterProperty;
		this.toAssignColor = toAssignColor;
		this.min = min;
		this.max = max;
		this.condition = condition;
	}

	public static enum Condition{
		 GREATER_THEN_MIN,UP_TO_MAX,BETWEEN
	 }
	 
	 private String toFilterProperty;
	 private Color toAssignColor;
	 private Object min;
	 private Object max;
	 private Condition condition;
	 
	 	 
	 
	 
	 
	/**
	 * @return the min
	 */
	public Object getMin() {
		return min;
	}
	/**
	 * @param min the min to set
	 */
	public void setMin(Object min) {
		this.min = min;
	}
	/**
	 * @return the max
	 */
	public Object getMax() {
		return max;
	}
	/**
	 * @param max the max to set
	 */
	public void setMax(Object max) {
		this.max = max;
	}
	/**
	 * @return the toFilterProperty
	 */
	public String getToFilterProperty() {
		return toFilterProperty;
	}
	/**
	 * @param toFilterProperty the toFilterProperty to set
	 */
	public void setToFilterProperty(String toFilterProperty) {
		this.toFilterProperty = toFilterProperty;
	}
	/**
	 * @return the toAssignColor
	 */
	public Color getToAssignColor() {
		return toAssignColor;
	}
	/**
	 * @param toAssignColor the toAssignColor to set
	 */
	public void setToAssignColor(Color toAssignColor) {
		this.toAssignColor = toAssignColor;
	}
	 
	public Condition getCondition() {
		return condition;
	}
	
	@Override
	public String toString() {
		return min+"-"+max+":"+toAssignColor;
	}
	
 }