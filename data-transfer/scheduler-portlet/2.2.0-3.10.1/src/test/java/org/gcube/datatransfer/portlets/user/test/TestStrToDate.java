package org.gcube.datatransfer.portlets.user.test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestStrToDate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String stringDate="04.03.1-17.56";
		String format="dd.MM.yy-hh.mm";
		Date date = null;
		try {
			date=new SimpleDateFormat(format).parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println("date="+new SimpleDateFormat(format).format(date));
		
		
		String tmp = "id--name--num";
		String[] parts=tmp.split("--");
		System.out.println(parts.length);
	}

}
