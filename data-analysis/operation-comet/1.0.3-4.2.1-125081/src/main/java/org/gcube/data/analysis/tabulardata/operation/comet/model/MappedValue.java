package org.gcube.data.analysis.tabulardata.operation.comet.model;

public class MappedValue {

	private String sourceValue;
	private String targetValue;
	private String fieldName;
	public MappedValue(String sourceValue, String targetValue, String fieldName) {
		super();
		this.sourceValue = sourceValue;
		this.targetValue = targetValue;
		this.fieldName = fieldName;
	}
	/**
	 * @return the sourceValue
	 */
	public String getSourceValue() {
		return sourceValue;
	}
	/**
	 * @param sourceValue the sourceValue to set
	 */
	public void setSourceValue(String sourceValue) {
		this.sourceValue = sourceValue;
	}
	/**
	 * @return the targetValue
	 */
	public String getTargetValue() {
		return targetValue;
	}
	/**
	 * @param targetValue the targetValue to set
	 */
	public void setTargetValue(String targetValue) {
		this.targetValue = targetValue;
	}
	/**
	 * @return the fieldLabel
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param fieldName the fieldLabel to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	
}
