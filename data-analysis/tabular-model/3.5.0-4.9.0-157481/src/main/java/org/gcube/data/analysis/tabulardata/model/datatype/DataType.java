package org.gcube.data.analysis.tabulardata.model.datatype;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlSeeAlso;

import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

@XmlSeeAlso({
	BooleanType.class, 
	DateType.class,
	GeometryType.class, 
	IntegerType.class,
	NumericType.class,
	TextType.class})
public abstract class DataType implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5373334437509816055L;

	public abstract String getName();
	
	public abstract TDTypeValue getDefaultValue();
	
	public abstract TDTypeValue fromString(String value);
}
