package gr.cite.geoanalytics.util.mail;

import java.util.HashMap;
import java.util.Map;

import gr.cite.geoanalytics.util.mail.types.MailParameter;
import gr.cite.geoanalytics.util.mail.types.MailSubjectTemplate;
import gr.cite.geoanalytics.util.mail.types.MailTextTemplate;
import gr.cite.geoanalytics.util.mail.types.MailType;

public class MailFormatter
{
	private MailType type;
	private Map<MailParameter, String> parameters = new HashMap<MailParameter, String>();
	private String subject = null;
	private String text = null;
	
	private MailFormatter(MailType type)
	{
		this.type = type;
	}
	
	public MailFormatter() { }
	
	public static MailFormatter forType(MailType type)
	{
		return new MailFormatter(type);
	}
	
	public MailFormatter withParameter(MailParameter parameter, String value)
	{
		if(this.type == null) throw new IllegalArgumentException("Mail formatter not properly initialized");
		this.parameters.put(parameter, value);
		return this;
	}
	
	public MailFormatter format()
	{
		MailSubjectTemplate st = new MailSubjectTemplate();
		subject = st.getSubject(type, parameters);
		
		MailTextTemplate tt = new MailTextTemplate();
		text = tt.getText(type, parameters);
		
		return this;
	}
	
	public String getSubject()
	{
		return subject;
	}
	
	public String getText()
	{
		return text;
	}
}
