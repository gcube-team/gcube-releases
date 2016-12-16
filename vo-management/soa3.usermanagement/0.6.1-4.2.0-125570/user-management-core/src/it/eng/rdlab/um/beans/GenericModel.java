package it.eng.rdlab.um.beans;

import java.util.HashMap;
import java.util.Map;

public abstract class GenericModel
{
	private String id;
	private Map<String, Object> objectMap;
	
	protected GenericModel ()
	{
		this.objectMap = new HashMap<String, Object>();
	}
	
	protected GenericModel (GenericModel genericModel)
	{
		this.objectMap = genericModel.getObjectMap();
	}
	
	protected GenericModel (String id)
	{
		this ();
		this.id = id;
	}
	
	protected String getId ()
	{
		return id;
	}
	
	protected void setId (String id)
	{
		this.id = id;
	}
	
	
	protected void addObject (String name, Object value)
	{
		this.objectMap.put(name, value);
	}

	protected Object getObject (String name)
	{
		return this.objectMap.get(name);
		
	}
	
	protected String getStringObject (String name)
	{
		String string = (String) this.objectMap.get(name);
		return string !=null ? string : "";
	
	}
	
	protected Map<String, Object> getObjectMap ()
	{
		return this.objectMap;
	}

	
}
