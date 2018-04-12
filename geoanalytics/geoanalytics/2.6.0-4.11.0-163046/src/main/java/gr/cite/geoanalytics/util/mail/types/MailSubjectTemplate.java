package gr.cite.geoanalytics.util.mail.types;

import java.util.EnumMap;
import java.util.Map;

public class MailSubjectTemplate extends MailTemplate
{
	private static EnumMap<MailType, String> subjects = new EnumMap<MailType, String>(MailType.class);
	
	private void populate()
	{
		subjects.put(MailType.PASSWORD_REQUEST, "Geopolis - Your password has been reset");
		subjects.put(MailType.ALERT_ACCOUNT_LOCK, "Geopolis Alert - User account has been locked");
	}
	

	@Override
	protected Map<MailType, String> getTemplates()
	{
		return subjects;
	}
	
	public synchronized String getSubject(MailType type)
	{
		if(subjects.isEmpty()) populate();
		return subjects.get(type).replaceAll("\\s%.*\\s", " ");
	}
	
	public synchronized String getSubject(MailType type, Map<MailParameter, String> params)
	{
		if(subjects.isEmpty()) populate();
		return fillTemplate(type, params);
	}
	
	public synchronized void setSubjectTemplate(MailType type, String template)
	{
		subjects.put(type, template);
	}
}
