package org.gcube.data.analysis.tabulardata.model.datatype;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.data.analysis.tabulardata.model.datatype.value.TDBoolean;
import org.gcube.data.analysis.tabulardata.model.datatype.value.TDTypeValue;

@XmlRootElement
public class BooleanType extends DataType {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5624083247561469083L;

	@Override
	public String toString() {
		return "Boolean";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		else
			return (BooleanType.class == obj.getClass());
	}

	@Override
	public String getName() {
		return "Boolean";
	}

	
	@Override
	public TDTypeValue getDefaultValue() {
		return new TDBoolean(false);
	}
	
	@Override
	public TDTypeValue fromString(String value) {
		return new TDBoolean(Boolean.parseBoolean(value));
	}
}
