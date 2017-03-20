package it.eng.rdlab.um.ldap;

import it.eng.rdlab.um.beans.GenericModel;
import it.eng.rdlab.um.beans.GenericModelWrapper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class LdapAbstractModelWrapper implements LdapDataModelWrapper 
{
	protected GenericModelWrapper modelWrapper;
	protected List<String> objectClasses;
	protected Map<String, String> attributeMap;
	protected Map<String, List<String>> listAttributeMap;
	protected String dn; 
	private Log log;

	
	public LdapAbstractModelWrapper (GenericModel model)
	{
		this.log = LogFactory.getLog(this.getClass());
		this.log.debug("init");
		this.attributeMap = new HashMap<String, String> ();
		this.listAttributeMap = new HashMap<String, List<String>>();
		initModel(model);
	}
	
	@SuppressWarnings ("unchecked")
	private void initModel (GenericModel model)
	{
		this.log.debug("init model");
		this.log.debug("loading main parameters");
		this.modelWrapper = new GenericModelWrapper(model);
		Map<String, Object> objectMap = this.modelWrapper.getObjectMap();
		this.objectClasses = (List<String>) modelWrapper.getObjectParameter(LdapModelConstants.OBJECT_CLASSES);
		this.log.debug("loading attributes");
		Iterator<String> keys = objectMap.keySet().iterator();
		
		while (keys.hasNext())
		{
			String key = keys.next();
			Object value = objectMap.get(key);
			this.log.debug("loading attribute "+key+" = "+value);
			if (!key.equals(LdapModelConstants.OBJECT_CLASSES) && value instanceof String)
			{

					this.attributeMap.put(key, (String) value);
					this.log.debug("Attribute loaded");
			} 		
			else if (!key.equals(LdapModelConstants.OBJECT_CLASSES) && value instanceof List)
			{

				this.listAttributeMap.put(key, (List<String>) value);
				this.log.debug("Attribute loaded");
			}
			else
			{
				this.log.debug("Attribute "+ key+","+value +" refused");
			}

		}
		
	}
	
	
	@Override
	public String getStringParameter(String name) 
	{
		return this.modelWrapper.getStringParameter(name);
	}

	@Override
	public Object getObjectParameter(String name) 
	{
		return this.modelWrapper.getObjectParameter(name);
	}
	
	@Override
	public Map<String, Object> getObjectMap() 
	{
		return this.modelWrapper.getObjectMap();
	}

	@Override
	public List<String> getObjectClasses() 
	{
		return this.objectClasses;
	}


	@Override
	public Map<String, String> getAttributeMap() 
	{
		return this.attributeMap;
	}

	@Override
	public String getDistinguishedName() 
	{
		return this.dn;
	}
	
	@Override
	public Map<String, List<String>> getListAttributeMap() 
	{
		return this.listAttributeMap;
	}

}
