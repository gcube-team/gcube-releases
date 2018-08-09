package gr.cite.geoanalytics.util.mail.types;

import java.util.EnumMap;
import java.util.Map;

public class MailTextTemplate extends MailTemplate
{
	private static EnumMap<MailType, String> texts = new EnumMap<MailType, String>(MailType.class);

	private void populate()
	{
		
		texts.put(MailType.PASSWORD_REQUEST, "<h>Your password has been reset</h>" +
				   "<p>You can login with the following password: %"+MailParameter.PASSWORD.parameterString()+"</p>" +
				   "<p>This password is temporary, please change it as soon as possible</p>");
		texts.put(MailType.ALERT_ACCOUNT_LOCK, "<h>Geopolis Admin Alert</h>" +
				   							   "<p>Incident timestamp: %"+MailParameter.DATETIME.parameterString()+"</p>" +
											   "<p>User account for user </p><p><b>%"+MailParameter.USERNAME.parameterString()+"</b></p>" +
											   "<p> has been locked after exceeding maximum allowed rate of %"+MailParameter.NUMLOGINS.parameterString() + 
											   " in %" + MailParameter.LOGIN_CHECK_PERIOD.parameterString() + " %"+MailParameter.LOGIN_CHECK_PERIOD_UNIT.parameterString()+"</p>");
	}
	
	@Override
	protected Map<MailType, String> getTemplates()
	{
		return texts;
	}
	
	public synchronized String getText(MailType type)
	{
		if(texts.isEmpty()) populate();
		return texts.get(type).replaceAll("\\s%.*\\s", " ");
	}
	
	public synchronized String getText(MailType type, Map<MailParameter, String> params)
	{
		if(texts.isEmpty()) populate();
		return fillTemplate(type, params);
	}
	
	public static synchronized void setTextTemplate(MailType type, String template)
	{
		texts.put(type, template);
	}
}
