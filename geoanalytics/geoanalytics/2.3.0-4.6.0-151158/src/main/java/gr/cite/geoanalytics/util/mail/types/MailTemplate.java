package gr.cite.geoanalytics.util.mail.types;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class MailTemplate
{

	public synchronized Set<MailParameter> getParams(MailType type)
	{
		Set<MailParameter> res = new HashSet<MailParameter>();
		Pattern p = Pattern.compile("(%((\\w)*(\\s)*))\\W");
		Matcher m = p.matcher(getTemplates().get(type));
		while(m.find())
			res.add(MailParameter.fromParameterString(m.group(2).trim()));
		return res;
	}
	
	protected abstract Map<MailType, String> getTemplates();
	
	protected String fillTemplate(MailType type, Map<MailParameter, String> params)
	{
		String ret = getTemplates().get(type);
		for(Map.Entry<MailParameter, String> param : params.entrySet())
			ret = ret.replaceAll("\\s%"+param.getKey().parameterString() + "(\\s)?", param.getValue()); //TODO check why not working and propagae to subject template
		Set<MailParameter> remaining = getParams(type);
		remaining.removeAll(params.keySet());
		for(MailParameter p : remaining)
			ret = ret.replaceAll("\\s%"+p.parameterString()+"\\s", " ");
		return ret;
	}
}
