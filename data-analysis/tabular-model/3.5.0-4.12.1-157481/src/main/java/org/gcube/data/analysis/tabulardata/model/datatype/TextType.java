package org.gcube.data.analysis.tabulardata.model.datatype;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.datatype.value.TDText;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

@XmlRootElement(name="Text")
@XmlAccessorType(XmlAccessType.FIELD)
public class TextType extends DataType{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3012818814370962508L;
	
	
	private int length;
	
	/**
	 * Creates a Text data type with a maximum lenght of 256 characters
	 */
	public TextType() {
		length = 256;
	}
	
	/**
	 * Creates a Text data type with a given maximum length
	 * @param length the number of allowed maximum characters
	 */
	@Deprecated
	public TextType(int length) {
		super();
		if(length<1) throw new IllegalArgumentException("Length must be greater than 0.");
		this.length = length;
	}

	@Deprecated
	public int getLenght() {
		return length;
	}	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + length;
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
		TextType other = (TextType) obj;
		if (length != other.length)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Text [lenght=");
		builder.append(length);
		builder.append("]");
		return builder.toString();
	}

	@Override
	public String getName() {
		return "Text";
	}

	@Override
	public TDTypeValue getDefaultValue() {		
		return new TDText(" ");
	}
	
	@Override
	public TDTypeValue fromString(String value) {
		return new TDText(value.length()<=length?value:value.substring(0,length-1));
	}
}
