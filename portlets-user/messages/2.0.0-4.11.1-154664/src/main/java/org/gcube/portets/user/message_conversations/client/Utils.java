package org.gcube.portets.user.message_conversations.client;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.datepicker.client.CalendarUtil;

import gwt.material.design.client.constants.Color;

public class Utils {
	public static DateTimeFormat fmCurrentYear = DateTimeFormat.getFormat("MMM d H:mm");
	public static DateTimeFormat fmPastYears = DateTimeFormat.getFormat("MMM d H:mm ''yy");
	public static String ID_MODALBOOTSTRAP = "wpTreepopup";

	public static String getServiceEntryPoint() {
		return "/delegate/"+MessageConversations.ARTIFACT_ID;
	}

	public static Color getRandomColor() {
		Color toReturn = Color.values()[Random.nextInt(Color.values().length)];
		while (toReturn.name().startsWith("WHITE") || 
				toReturn.name().startsWith("GREY") || 
				toReturn.name().startsWith("TRANSPARENT") ||
				toReturn.name().startsWith("BROWN") || 
				toReturn.name().contains("LIGHTEN")  || 
				toReturn.name().contains("ACCENT")) {
			toReturn = Color.values()[Random.nextInt(Color.values().length)];
		}
		return toReturn;
	}


	public static boolean isMobile() {
		int screenWidth = RootPanel.get(MessageConversations.DIV_CONTAINER_ID).getOffsetWidth();
		return (screenWidth <= 768);
	}

	@SuppressWarnings("deprecation")
	public static String getFormatteDate(Date date) {
		Date now = new Date();		
		int dayInBetween = CalendarUtil.getDaysBetween(now, date);
		switch (dayInBetween) {
		case 0: 
			return"Today at " +  (DateTimeFormat.getFormat("H:mm").format(date));
		case -1: 
			return "Yesterday, "  +  (DateTimeFormat.getFormat("H:mm").format(date));
		case -2: 
			return "2 days ago, "  +  (DateTimeFormat.getFormat("H:mm").format(date));
		default:
			return(now.getYear() == date.getYear()) ? fmCurrentYear.format(date) : fmPastYears.format(date);
		}
	}

}
