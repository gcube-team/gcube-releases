package org.gcube.data.analysis.tabulardata.model.datatype;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.datatype.value.TDDate;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

@XmlRootElement
public class DateType extends DataType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 65390041737763710L;

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj.getClass().equals(DateType.class);
	}

	@Override
	public String toString() {
		return "Date";
	}
	
	@Override
	public String getName() {
		return "Date";
	}

	@Override
	public TDTypeValue getDefaultValue() {		
		return new TDDate(new Date(0l));
	}
	
	@Override
	public TDTypeValue fromString(String value) {
		throw new IllegalArgumentException("Unable to parse value");
	}
}
