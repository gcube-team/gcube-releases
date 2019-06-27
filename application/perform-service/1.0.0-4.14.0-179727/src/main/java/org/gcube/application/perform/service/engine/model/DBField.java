package org.gcube.application.perform.service.engine.model;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;


public class DBField {

	
	public static class ImportedData{
	
	public static class AnagraphicGrow{		
		public static final String TABLE="output_anagraphic_grow";
		
		public static final String FARM_ID="int_farmid";
		public static final String ROUTINE_ID="int_routineid";
		
	}
	
	public static class AnagraphicPreGrow{		
		public static final String TABLE="output_anagraphic_grow";
		
		public static final String FARM_ID="int_farmid";
		public static final String ROUTINE_ID="int_routineid";
		
	}
	
	
	public static class AnagraphicHatchery{		
		public static final String TABLE="output_anagraphic_grow";
		
		public static final String FARM_ID="int_farmid";
		public static final String ROUTINE_ID="int_routineid";
		
	}
	
	public static class BatchesTable{
		public static final String TABLE="output_batches";
		
		public static final String FARM_ID="int_farmid";
		public static final String ROUTINE_ID="int_routineid";
		
	}
	
	public static class AnnualTable{
		public static final String TABLE="output_annual";
		
		public static final String FARM_ID="int_farmid";
		public static final String ROUTINE_ID="int_routineid";
		
	}
	
	public static class AntibioticsTable{
		public static final String TABLE="output_antibiotics";
		
		public static final String FARM_ID="int_farmid";
		public static final String ROUTINE_ID="int_routineid";
		
	}
	
	public static class AntiparasiticTable{
		public static final String TABLE="output_antiparasitic";
		
		public static final String FARM_ID="int_farmid";
		public static final String ROUTINE_ID="int_routineid";
		
	}
	
	public static class LethalIncidentsTable{
		public static final String TABLE="output_lethal_incidents";
		
		public static final String FARM_ID="int_farmid";
		public static final String ROUTINE_ID="int_routineid";
		
	}
	
	
	}
	
	
	public static class Batch{
		
		public static final String TABLE="batches";
		
		public static final Map<String,DBField> fields=new HashMap<>();
		
		public static final String BATCH_ID="id";
		public static final String UUID="uuid";
		public static final String FARM_ID="farmid";
		public static final String BATCH_TYPE="type";
		public static final String BATCH_NAME="name";
		
		static {
			fields.put(BATCH_ID, new DBField(Types.BIGINT,BATCH_ID));
			fields.put(UUID, new DBField(Integer.MIN_VALUE,UUID));
			fields.put(FARM_ID, new DBField(Types.BIGINT,FARM_ID));
			fields.put(BATCH_TYPE, new DBField(Types.VARCHAR,BATCH_TYPE));
			fields.put(BATCH_NAME, new DBField(Types.VARCHAR,BATCH_NAME));
		}
	}
	
	public static class Farm{
		
		public static final String TABLE="completefarms";
		
		public static final Map<String,DBField> fields=new HashMap<>();
		
		public static final String FARM_ID="id";
		public static final String COMPANY_ID="companyid";
		public static final String ASSOCIATION_ID="associationid";
		public static final String UUID="uuid";
		public static final String COMPANY_UUID="companyuuid";
		public static final String ASSOCIATION_UUID="associationuuid";
		
		public static final String FARM_LABEL="name";
		public static final String ASSOCIATION_LABEL="association_name";
		public static final String COMPANY_LABEL="company_name";
		
		
		static {
			fields.put(FARM_ID, new DBField(Types.BIGINT,FARM_ID));
			fields.put(COMPANY_ID, new DBField(Types.BIGINT,COMPANY_ID));
			fields.put(ASSOCIATION_ID, new DBField(Types.BIGINT,ASSOCIATION_ID));
			fields.put(UUID, new DBField(Integer.MIN_VALUE,UUID));
			fields.put(COMPANY_UUID, new DBField(Integer.MIN_VALUE,COMPANY_UUID));
			fields.put(ASSOCIATION_UUID, new DBField(Integer.MIN_VALUE,ASSOCIATION_UUID));
			fields.put(FARM_LABEL, new DBField(Types.VARCHAR,FARM_LABEL));
			fields.put(ASSOCIATION_LABEL, new DBField(Types.VARCHAR,ASSOCIATION_LABEL));
			fields.put(COMPANY_LABEL, new DBField(Types.VARCHAR,COMPANY_LABEL));
			
		}
	}
	
	public static class ImportRoutine{
		public static final String TABLE="imports";
		
		public static final Map<String,DBField> fields=new HashMap<>();
		
		public static final String ID="id";
		public static final String FARM_ID="farmid";
		public static final String BATCH_TYPE="batch_type";
		public static final String SOURCE_URL="sourceurl";
		public static final String SOURCE_VERSION="sourceversion";
		
		public static final String START="start_time";
		public static final String END="end_time";
		public static final String STATUS="status";
		public static final String CALLER="caller";
		public static final String COMPUTATION_ID="computation_id";
		public static final String COMPUTATION_URL="computation_url";
		public static final String COMPUTATION_OPID="computation_opid";
		public static final String COMPUTATION_OPNAME="computation_opname";
		public static final String COMPUTATION_REQ="computation_req";
		public static final String LOCK="lock";
		
		
		
		
		
		static {			
			fields.put(FARM_ID, new DBField(Types.BIGINT,FARM_ID));
			fields.put(ID, new DBField(Types.BIGINT,ID));
			fields.put(BATCH_TYPE, new DBField(Types.VARCHAR,BATCH_TYPE));
			fields.put(SOURCE_URL, new DBField(Types.VARCHAR,SOURCE_URL));
			fields.put(SOURCE_VERSION, new DBField(Types.VARCHAR,SOURCE_VERSION));
			
			fields.put(START, new DBField(Types.TIMESTAMP,START));
			fields.put(END, new DBField(Types.TIMESTAMP,END));
			fields.put(STATUS, new DBField(Types.VARCHAR,STATUS));
			fields.put(CALLER, new DBField(Types.VARCHAR,CALLER));
			fields.put(COMPUTATION_ID, new DBField(Types.VARCHAR,COMPUTATION_ID));
			fields.put(COMPUTATION_URL, new DBField(Types.VARCHAR,COMPUTATION_URL));
			fields.put(COMPUTATION_OPID, new DBField(Types.VARCHAR,COMPUTATION_OPID));
			fields.put(COMPUTATION_OPNAME, new DBField(Types.VARCHAR,COMPUTATION_OPNAME));
			fields.put(COMPUTATION_REQ, new DBField(Types.VARCHAR,COMPUTATION_REQ));
			fields.put(LOCK, new DBField(Types.VARCHAR,LOCK));
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
