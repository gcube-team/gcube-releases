package org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DBQueryDescriptor {

	private Map<DBField,Object> condition=new HashMap<DBField,Object>(); 

	public DBQueryDescriptor() {
		// TODO Auto-generated constructor stub
	}
	
	public Map<DBField, Object> getCondition() {
		return condition;
	}
	

	public DBQueryDescriptor(Map<DBField, Object> condition) {
		super();
		this.condition = condition;
	}

	public DBQueryDescriptor(DBField field, Object value) {		
		add(field,value);
	}
	
	
	public String toString() {
		StringBuilder builder=new StringBuilder();
		if(condition.isEmpty())return "EMPTY";
		for(Entry<DBField,Object> entry : condition.entrySet()) {
			builder.append(String.format("%1$s = %2$s AND ", entry.getKey().getFieldName(),entry.getValue()));
		}
		return builder.substring(0,builder.lastIndexOf(" AND ")).toString();
	}
	
	public DBQueryDescriptor add(DBField field,Object obj) {
		condition.put(field, obj);
		return this;
	}
}
