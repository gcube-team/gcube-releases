package it.eng.rdlab.um.beans;

import java.util.HashMap;
import java.util.Map;

public class GenericModelWrapper implements DataModelWrapper
{
	private GenericModel model;
	
	public GenericModelWrapper (GenericModel model)
	{
		this.model = model;
	}
	

	@Override
	public String getStringParameter (String name)
	{
		return model.getStringObject(name);
	}
	
	@Override
	public Object getObjectParameter (String name)
	{
		return model.getObject(name);
	}


	@Override
	public Map<String, Object> getObjectMap() 
	{
		return new HashMap<String, Object> (this.model.getObjectMap());
	}
	
}
