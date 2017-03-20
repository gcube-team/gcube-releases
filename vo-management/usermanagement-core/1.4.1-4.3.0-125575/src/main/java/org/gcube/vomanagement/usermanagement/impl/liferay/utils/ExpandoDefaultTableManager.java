package org.gcube.vomanagement.usermanagement.impl.liferay.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.liferay.portlet.expando.model.ExpandoTable;
import com.liferay.portlet.expando.service.ExpandoTableLocalServiceUtil;


/**
 * 
 * Utility class to load the table of custom fields
 * 
 * @author Ciro Formisano
 *
 */
public class ExpandoDefaultTableManager 
{

	private Map<Long, ExpandoTable> tableMap;
	private final String DEFAULT_TABLE_NAME = "DEFAULT_TABLE";
	
	private static ExpandoDefaultTableManager instance;
	
	public static ExpandoDefaultTableManager getInstance ()
	{
		if (instance == null)
		{
			instance = new ExpandoDefaultTableManager();
		}
		
		return instance;
	}
	
	public ExpandoDefaultTableManager() 
	{
		this.tableMap = new HashMap<Long, ExpandoTable> ();
	}
	
	public ExpandoTable getExpandoDefaultTable (long classId)
	{
		ExpandoTable response = this.tableMap.get(classId);
		
		if (response == null)
		{
			try 
			{
				response = getExpandoDefaultTableFromDatabase(classId);
				
				if (response != null) this.tableMap.put(classId, response);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return response;
	}
	
	private ExpandoTable getExpandoDefaultTableFromDatabase (long classId) throws Exception
	{
		int numberOfTables = ExpandoTableLocalServiceUtil.getExpandoTablesCount();
		List<ExpandoTable> tables = ExpandoTableLocalServiceUtil .getExpandoTables(0, numberOfTables);
		ExpandoTable response = null;
		
		if (tables != null)
		{
			Iterator<ExpandoTable> tableIterator = tables.iterator();
			
			
			while (tableIterator.hasNext() && response == null)
			{
				ExpandoTable currentTable = tableIterator.next();
				
				if (currentTable.getName().equals(DEFAULT_TABLE_NAME) && currentTable.getClassNameId() == classId) response = currentTable;
			}
		}
		
		return response;

	}

}
