package org.gcube.data.analysis.tabulardata.model.datatype;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.datatype.value.TDInteger;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

@XmlRootElement(name="Integer")
public class IntegerType extends DataType {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2672905856604007377L;

	@Override
	public String toString() {
		return "Integer";
	}	
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this.getClass().equals(obj.getClass())) return true;
		return false;
	}

	@Override
	public String getName() {
		return "Integer";
	}
	
	@Override
	public TDTypeValue getDefaultValue() {
		return new TDInteger(0);
	}
	@Override
	public TDTypeValue fromString(String value) {
		return new TDInteger(Integer.parseInt(value));
	}
}
