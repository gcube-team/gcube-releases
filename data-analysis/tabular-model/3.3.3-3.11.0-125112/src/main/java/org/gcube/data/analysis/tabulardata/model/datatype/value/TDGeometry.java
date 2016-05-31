package org.gcube.data.analysis.tabulardata.model.datatype.value;

import org.gcube.data.analysis.tabulardata.expression.MalformedExpressionException;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.GeometryType;

public class TDGeometry extends TDTypeValue {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6336508039560210266L;

	private static final String GEOMETRY_REGEXPR = "(\\s*POINT\\s*\\(\\s*(-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*\\)\\s*$)" +
			"|(\\s*LINESTRING\\s*\\((\\s*(-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*,)+\\s*((-)?\\d+(\\.\\d+)?\\s+(-)?\\d+(\\.\\d+)?\\s*)\\)\\s*$)";
	
	private String value;
	
	@SuppressWarnings("unused")
	private TDGeometry() {}
	
	public TDGeometry(String geometryAsString) {
		super();
		this.value = geometryAsString;
		if (!validateGeometry(geometryAsString))
			throw new IllegalArgumentException("the string represention of the geometry is not valid");
	}
	
	@Override
	public int compareTo(TDTypeValue o) {
		return this.value.compareTo(((TDGeometry) o).getValue());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TDInteger other = (TDInteger) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	@Override
	public void validate() throws MalformedExpressionException {
		if (value==null || !validateGeometry(value)) 
			throw new MalformedExpressionException("the string represention of the geometry is not valid");
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static boolean validateGeometry(String geometryAsString){
		return geometryAsString.matches(GEOMETRY_REGEXPR);
	}
	
	@Override
	public DataType getReturnedDataType() {
		return new GeometryType();
	}
}
