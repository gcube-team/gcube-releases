package org.gcube.data.analysis.tabulardata.model.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.datatype.value.TDNumeric;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

/**
 * The type numeric can store numbers with up to 1000 digits of precision and
 * perform calculations exactly. A Numeric is defined with a precision and a
 * scale. The scale of a numeric is the count of decimal digits in the
 * fractional part, to the right of the decimal point. The precision of a
 * numeric is the total count of significant digits in the whole number, that
 * is, the number of digits to both sides of the decimal point.
 * 
 * @author "Luigi Fortunati"
 * 
 */
@XmlRootElement(name = "Numeric")
@XmlAccessorType(XmlAccessType.FIELD)
public class NumericType extends DataType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1778127136791503220L;

	/**
	 * The precision is the total count of significant digits in the whole
	 * number.
	 */
	Integer precision = null;

	/**
	 * The scale of a numeric is the count of decimal digits in the fractional
	 * part.
	 */
	Integer scale = null;

	/**
	 * Define a Numeric type with default precision and scale
	 */
	public NumericType() {
	}

	/**
	 * Creates a Numeric type with a given precision
	 * 
	 * @param precision
	 *            total count of significant digits in the whole number
	 */
	public NumericType(int precision) {
		if (precision <= 0)
			throw new IllegalArgumentException("Precision must be positive");
		this.precision = precision;
	}

	/**
	 * Creates a Numeric type with a given precision and scale.
	 * 
	 * @param precision
	 *            total count of significant digits in the whole number
	 * @param scale
	 *            count of decimal digits in the fractional part
	 * @throws IllegalArgumentException
	 */
	public NumericType(int precision, int scale) {
		if (precision <= 0)
			throw new IllegalArgumentException("Precision must be positive.");
		if (scale < 0 || scale > precision)
			throw new IllegalArgumentException("Scale must be 0 or less than precision.");
		this.precision = precision;
		this.scale = scale;
	}

	public Integer getPrecision() {
		return precision;
	}

	public Integer getScale() {
		return scale;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((precision == null) ? 0 : precision.hashCode());
		result = prime * result + ((scale == null) ? 0 : scale.hashCode());
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
		NumericType other = (NumericType) obj;
		if (precision == null) {
			if (other.precision != null)
				return false;
		} else if (!precision.equals(other.precision))
			return false;
		if (scale == null) {
			if (other.scale != null)
				return false;
		} else if (!scale.equals(other.scale))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("Numeric");
		if(scale!=null)		
		builder.append("("+precision+","+scale+")");
		else if(precision!=null)
			builder.append("("+precision+")");		
		return builder.toString();
	}

	@Override
	public String getName() {
		return "Numeric";
	}

	@Override
	public TDTypeValue getDefaultValue() {		
		return new TDNumeric(0d);
	}
	
	@Override
	public TDTypeValue fromString(String value) {
		return new TDNumeric(Double.parseDouble(value));
	}
}
