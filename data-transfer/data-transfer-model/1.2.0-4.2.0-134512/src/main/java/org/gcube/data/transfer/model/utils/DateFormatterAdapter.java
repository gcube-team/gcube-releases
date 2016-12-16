package org.gcube.data.transfer.model.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class DateFormatterAdapter extends XmlAdapter<String, Calendar> {

	@Override
	public String marshal(Calendar cal) throws Exception {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(cal.getTime());
		XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		return date2.toXMLFormat();
	}

	@Override
	public Calendar unmarshal(String date) throws Exception {
		XMLGregorianCalendar gregorianDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(gregorianDate.toGregorianCalendar().getTime());
		return cal;
		
	}

}