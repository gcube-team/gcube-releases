/**
 * 
 */
package org.gcube.dataaccess.spql.model.where;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.gcube.dataaccess.spql.util.CalendarUtil;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class ParserDate extends AbstractParsableValue<Calendar> {
	
	protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat();
	
	protected String text;

	/**
	 * @param text
	 */
	public ParserDate(String text) {
		this.text = text;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTextValue() {
		return DATE_FORMAT.format(getValue().getTime());
	}

	@Override
	public void parse() {
		Calendar calendar = CalendarUtil.parseCalendar(text);
		if (calendar == null) throw new RuntimeException("Invalid date format for text "+text);
		setValue(calendar);
	}

}
