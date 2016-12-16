package org.gcube.data.transfer.model.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;



@XmlAccessorType(XmlAccessType.NONE)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Slf4j
public class DateWrapper {

	static DatatypeFactory factory=null;
	
	static {
		try{
			factory=DatatypeFactory.newInstance();
		}catch(Exception e){
			log.error("Unexpected exception ",e);
		}
		
		
	}
	
	public static DateWrapper getInstance(){
		return new DateWrapper(Calendar.getInstance());
	}
	
	
//	static SimpleDateFormat dateFormat = new SimpleDateFormat();
	
	
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	@XmlElement
	public Calendar value;
	
	
	@Override
	public String toString() {
 // not all the date are mandatory, if a date is optional the value is null		
		if(value != null){
			GregorianCalendar c = new GregorianCalendar();
			c.setTime(value.getTime());
			XMLGregorianCalendar date2 = factory.newXMLGregorianCalendar(c);
			return date2.toXMLFormat();			
		}
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
