package org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class DBField {

	public static final class ExecutionDescriptor{
		public static final String TABLE="executions";
		
		public static final Map<String,DBField> fields=new HashMap<>();
		
		public static final String ID="id";	
		public static final String CALLER_TOKEN="caller_token";
		public static final String CALLER_ID="caller_id";
		public static final String CALLER_CONTEXT="caller_context";
		public static final String STATUS = "status";
		public static final String REPORT_URL="report_url";
		
		public static final String COLLECTORS="collectors";
		public static final String CONTROLLERS="controllers";
		
		public static final String START="start_time";
		public static final String END="end_time";
		
		static {
			fields.put(ID, new DBField(Types.BIGINT,ID));
			fields.put(CALLER_TOKEN, new DBField(Types.VARCHAR,CALLER_TOKEN));
			fields.put(CALLER_ID, new DBField(Types.VARCHAR,CALLER_ID));
			fields.put(CALLER_CONTEXT, new DBField(Types.VARCHAR,CALLER_CONTEXT));
			fields.put(STATUS, new DBField(Types.VARCHAR,STATUS));
			fields.put(REPORT_URL, new DBField(Types.VARCHAR,REPORT_URL));
			fields.put(START, new DBField(Types.TIMESTAMP,START));
			fields.put(END, new DBField(Types.TIMESTAMP,END));
			fields.put(COLLECTORS, new DBField(Types.VARCHAR,COLLECTORS));
			fields.put(CONTROLLERS, new DBField(Types.VARCHAR,CONTROLLERS));
		}
		
	}
	
	
	
	public DBField(int type, String fieldName) {
		super();
		this.type = type;
		this.fieldName = fieldName;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public int getType() {
		return type;
	}
	
	private int type;	
	private String fieldName;
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBField other = (DBField) obj;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DBField ["+fieldName+"]";
	}
}
