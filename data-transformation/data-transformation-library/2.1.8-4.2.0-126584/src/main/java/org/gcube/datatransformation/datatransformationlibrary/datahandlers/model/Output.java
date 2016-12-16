package org.gcube.datatransformation.datatransformationlibrary.datahandlers.model;

import java.util.Arrays;

import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;

/**
 * The Output is a wrap of a DataSink.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class Output {
	private String outputType;
	private String outputValue;
	private Parameter[] outputparameters;

	@Override
	public String toString() {
		return "Output [outputType=" + outputType + ", outputValue=" + outputValue + ", outputparameters=" + Arrays.toString(outputparameters) + "]";
	}

	public Output() {
	}

	public Output(String outputType, String outputValue, Parameter[] outputparameters) {
		this.outputType = outputType;
		this.outputValue = outputValue;
		this.outputparameters = outputparameters;
	}

	/**
	 * Gets the outputType value for this Output.
	 * 
	 * @return outputType
	 */
	public String getOutputType() {
		return outputType;
	}

	/**
	 * Sets the outputType value for this Output.
	 * 
	 * @param outputType
	 */
	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	/**
	 * Gets the outputValue value for this Output.
	 * 
	 * @return outputValue
	 */
	public String getOutputValue() {
		return outputValue;
	}

	/**
	 * Sets the outputValue value for this Output.
	 * 
	 * @param outputValue
	 */
	public void setOutputValue(String outputValue) {
		this.outputValue = outputValue;
	}

	/**
	 * Gets the outputparameters value for this Output.
	 * 
	 * @return outputparameters
	 */
	public Parameter[] getOutputparameters() {
		return outputparameters;
	}

	/**
	 * Sets the outputparameters value for this Output.
	 * 
	 * @param outputparameters
	 */
	public void setOutputparameters(Parameter[] outputparameters) {
		this.outputparameters = outputparameters;
	}

	public Parameter getOutputparameters(int i) {
		return this.outputparameters[i];
	}

	public void setOutputparameters(int i, Parameter _value) {
		this.outputparameters[i] = _value;
	}

	private java.lang.Object __equalsCalc = null;

	public synchronized boolean equals(java.lang.Object obj) {
		if (!(obj instanceof Output))
			return false;
		Output other = (Output) obj;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((this.outputType == null && other.getOutputType() == null) || (this.outputType != null && this.outputType.equals(other.getOutputType())))
				&& ((this.outputValue == null && other.getOutputValue() == null) || (this.outputValue != null && this.outputValue
						.equals(other.getOutputValue())))
				&& ((this.outputparameters == null && other.getOutputparameters() == null) || (this.outputparameters != null && java.util.Arrays.equals(
						this.outputparameters, other.getOutputparameters())));
		__equalsCalc = null;
		return _equals;
	}

	private transient boolean __hashCodeCalc = false;

	public synchronized int hashCode() {
		if (__hashCodeCalc) {
			return 0;
		}
		__hashCodeCalc = true;
		int _hashCode = 1;
		if (getOutputType() != null) {
			_hashCode += getOutputType().hashCode();
		}
		if (getOutputValue() != null) {
			_hashCode += getOutputValue().hashCode();
		}
		if (getOutputparameters() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getOutputparameters()); i++) {
				java.lang.Object obj = java.lang.reflect.Array.get(getOutputparameters(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		__hashCodeCalc = false;
		return _hashCode;
	}
}
