package org.gcube.common.resources.gcore.common;

import javax.xml.bind.annotation.XmlAttribute;

public class BooleanWrapper {
	
	@XmlAttribute(name="value")
	public boolean value;

	public BooleanWrapper() {}
	
	public BooleanWrapper(boolean value) {
		this.value=value;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (value ? 1231 : 1237);
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
		BooleanWrapper other = (BooleanWrapper) obj;
		if (value != other.value)
			return false;
		return true;
	}
	
	

}
