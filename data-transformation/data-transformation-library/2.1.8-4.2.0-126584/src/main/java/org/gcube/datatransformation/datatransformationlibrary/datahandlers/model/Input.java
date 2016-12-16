package org.gcube.datatransformation.datatransformationlibrary.datahandlers.model;

import java.util.Arrays;

import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;

/**
 * The Input is a wrap of a DataSource.
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class Input {
	private String inputType;
	private String inputValue;
	private Parameter[] inputparameters;

	@Override
	public String toString() {
		return "Input [inputType=" + inputType + ", inputValue=" + inputValue + ", inputparameters=" + Arrays.toString(inputparameters) + "]";
	}
	
	public Input() {
	}

	public Input(String inputType, String inputValue, Parameter[] inputParameters) {
		this.inputType = inputType;
		this.inputValue = inputValue;
		this.inputparameters = inputParameters;
	}

	/**
	 * Gets the inputType value for this Input.
	 * 
	 * @return inputType
	 */
	public String getInputType() {
		return inputType;
	}

	/**
	 * Sets the inputType value for this Input.
	 * 
	 * @param inputType
	 */
	public void setInputType(String inputType) {
		this.inputType = inputType;
	}

	/**
	 * Gets the inputValue value for this Input.
	 * 
	 * @return inputValue
	 */
	public String getInputValue() {
		return inputValue;
	}

	/**
	 * Sets the inputValue value for this Input.
	 * 
	 * @param inputValue
	 */
	public void setInputValue(String inputValue) {
		this.inputValue = inputValue;
	}

	/**
	 * Gets the inputParameters value for this Input.
	 * 
	 * @return inputParameters
	 */
	public Parameter[] getInputParameters() {
		return inputparameters;
	}

	/**
	 * Sets the inputParameters value for this Input.
	 * 
	 * @param inputParameters
	 */
	public void setInputParameters(Parameter[] inputParameters) {
		this.inputparameters = inputParameters;
	}

	/**
	 * Gets the i'th {@link Parameter} from inputParameters
	 * 
	 * @param i
	 *            Index of the parameter
	 * @return The {@link Parameter}
	 */
	public Parameter getInputParameters(int i) {
		return this.inputparameters[i];
	}

	/**
	 * Sets the i'th {@link Parameter} to inputParameters
	 * 
	 * @param i
	 *            Index of the parameter
	 * @param _value
	 *            The {@link Parameter} to be set.
	 */
	public void setInputParameters(int i, Parameter _value) {
		this.inputparameters[i] = _value;
	}

	private Object __equalsCalc = null;

	public synchronized boolean equals(Object obj) {
		if (!(obj instanceof Input))
			return false;
		Input other = (Input) obj;
		if (this == obj)
			return true;
		if (__equalsCalc != null) {
			return (__equalsCalc == obj);
		}
		__equalsCalc = obj;
		boolean _equals;
		_equals = true
				&& ((this.inputType == null && other.getInputType() == null) || (this.inputType != null && this.inputType.equals(other.getInputType())))
				&& ((this.inputValue == null && other.getInputValue() == null) || (this.inputValue != null && this.inputValue.equals(other.getInputValue())))
				&& ((this.inputparameters == null && other.getInputParameters() == null) || (this.inputparameters != null && java.util.Arrays.equals(
						this.inputparameters, other.getInputParameters())));
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
		if (getInputType() != null) {
			_hashCode += getInputType().hashCode();
		}
		if (getInputValue() != null) {
			_hashCode += getInputValue().hashCode();
		}
		if (getInputParameters() != null) {
			for (int i = 0; i < java.lang.reflect.Array.getLength(getInputParameters()); i++) {
				Object obj = java.lang.reflect.Array.get(getInputParameters(), i);
				if (obj != null && !obj.getClass().isArray()) {
					_hashCode += obj.hashCode();
				}
			}
		}
		__hashCodeCalc = false;
		return _hashCode;
	}
}
