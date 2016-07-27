package org.gcube.data.transfer.model.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class DateWrapper {

	static SimpleDateFormat dateFormat = new SimpleDateFormat();
	
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	public Calendar value;
	
	@Override
	public String toString() {
 // not all the date are mandatory, if a date is optional the value is null		
		if(value != null)
			return dateFormat.format(value);
		else
			return "";
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
		DateWrapper other = (DateWrapper) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
}
