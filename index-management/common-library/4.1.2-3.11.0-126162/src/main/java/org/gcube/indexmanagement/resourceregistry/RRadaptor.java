package org.gcube.indexmanagement.resourceregistry;

import java.io.Serializable;
import java.util.List;

import gr.uoa.di.madgik.rr.ResourceRegistry;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;
import gr.uoa.di.madgik.rr.element.search.Field;

public class RRadaptor implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
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

}
