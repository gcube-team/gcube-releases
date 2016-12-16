package it.eng.rdlab.um.beans;

import java.util.Map;

public interface DataModelWrapper 
{
	public String getStringParameter (String name);
	public Object getObjectParameter (String name);
	public Map<String,Object> getObjectMap ();
}
