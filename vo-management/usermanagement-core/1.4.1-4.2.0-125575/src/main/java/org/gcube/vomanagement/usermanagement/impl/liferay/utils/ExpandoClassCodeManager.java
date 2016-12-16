package org.gcube.vomanagement.usermanagement.impl.liferay.utils;

import java.util.HashMap;
import java.util.Map;

import com.liferay.portal.service.ClassNameLocalServiceUtil;

/**
 * 
 * Utility class to load the classes of custom field
 * 
 * @author Ciro Formisano
 *
 */
public class ExpandoClassCodeManager 
{
	private Map<String, Long> map;
	
	private static ExpandoClassCodeManager instance;
	
	public static ExpandoClassCodeManager getInstance ()
	{
		if (instance == null) instance = new ExpandoClassCodeManager();
		
		return instance;
	}
	
	public ExpandoClassCodeManager() 
	{
		this.map = new HashMap<String, Long> ();
	}
	
	public long getClassCode (Class<?> type)
	{
		String className = type.getName();
		Long code = this.map.get(className);
		
		if (code == null)
		{
			code = ClassNameLocalServiceUtil.getClassNameId(type);
			this.map.put(className, code);
		}
		
		return code;
	}

}
