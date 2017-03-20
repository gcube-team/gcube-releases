package org.gcube.search.datafusion;

import java.util.ArrayList;
import java.util.List;

import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;
import gr.uoa.di.madgik.rr.element.search.Field;

/**
 * 
 * Forked from IndexCommon
 *
 */
public class RRadaptor {
	
	public RRadaptor() {
	}
	
	public static void initializeAdapter() throws Exception{
		ResourceRegistry.startBridging();
	}
	
	public String getFieldNameById(String fieldId) throws Exception{
		String fieldName = QueryHelper.GetFieldNameById(fieldId);
		if(fieldName == null) {
			throw new Exception("Could not find fieldName for fieldId: " + fieldId);
		}
		return fieldName;
	}
	
	public String getFieldIDFromName(String fieldName) throws Exception{
		List<Field> fields = Field.getFieldsWithName(false, fieldName);
		if(fields == null || fields.size() == 0) {
			throw new Exception("Could not find fieldId for fieldName: " + fieldName);
		}
		return fields.get(0).getID();
	}
	
	public List<String> getFieldIDsFromName(String fieldName) throws Exception{
		List<Field> fields = Field.getFieldsWithName(false, fieldName);
		if(fields == null || fields.size() == 0) {
			throw new Exception("Could not find fieldId for fieldName: " + fieldName);
		}
		
		List<String> ids = new ArrayList<String>();
		for (Field field : fields)
			ids.add(field.getID());
		
		return ids;
	}

}
