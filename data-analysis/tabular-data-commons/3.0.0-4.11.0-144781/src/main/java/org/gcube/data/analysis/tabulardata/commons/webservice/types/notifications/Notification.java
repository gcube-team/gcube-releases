package org.gcube.data.analysis.tabulardata.commons.webservice.types.notifications;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Notification {

	private static SimpleDateFormat dateFormat =new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z"); 
	
	private Calendar date;
	private AffectedObject affectedObject;
	private UpdateEvent event;
	private String humanReadableDescription;
		
	@SuppressWarnings("unused")
	private Notification(){}
	
	public Notification(AffectedObject affectedObject, UpdateEvent event,
			String humanReadableDescription, Calendar date) {
		super();
		this.affectedObject = affectedObject;
		this.event = event;
		this.humanReadableDescription = humanReadableDescription;
		this.date = date;
	}
	
	public AffectedObject getAffectedObject() {
		return affectedObject;
	}
	public UpdateEvent getEvent() {
		return event;
	}
	public String getHumanReadableDescription() {
		return humanReadableDescription;
	}

	public Calendar getDate() {
		return date;
	}

	@Override
	public String toString() {
		return "Notification [date="+dateFormat.format(date.getTime())+" affectedObject=" + affectedObject + ", event="
				+ event + ", humanReadableDescription="
				+ humanReadableDescription + "]";
	}
}
