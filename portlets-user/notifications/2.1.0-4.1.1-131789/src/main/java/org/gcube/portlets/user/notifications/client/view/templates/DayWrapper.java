package org.gcube.portlets.user.notifications.client.view.templates;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.CalendarUtil;

/**
 * @author Massimiliano Assante ISTI-CNR
 *
 */
public class DayWrapper extends Composite {

	private static DayWrapperUiBinder uiBinder = GWT
			.create(DayWrapperUiBinder.class);

	interface DayWrapperUiBinder extends UiBinder<Widget, DayWrapper> {
	}
	
	@UiField HTML dayLabel;
	
	public DayWrapper(Date day) {
		initWidget(uiBinder.createAndBindUi(this));
	
		Date now = new Date();
		@SuppressWarnings("deprecation")
		int currYear = now.getYear(); 
		@SuppressWarnings("deprecation")
		int notificationDateYear = day.getYear(); 
			
		
				
		int dayInBetween = CalendarUtil.getDaysBetween(now, day);
		String theDay = "";
		switch (dayInBetween) {
		case 0: 
			theDay = "Today";
			break;
		case -1: 
			theDay = "Yesterday";
			break;
		default:
			if (currYear == notificationDateYear)
				theDay = (DateTimeFormat.getFormat("EE, dd MMMM").format(day));
			else {
				theDay = (DateTimeFormat.getFormat("dd MMMM yyyy").format(day));
			}
			break;
		}
				
		this.dayLabel.setHTML(theDay);
		
	}

}
