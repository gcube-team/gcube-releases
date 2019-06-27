package org.gcube.application.perform.service.engine.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

import org.gcube.application.perform.service.engine.model.DBField;
import org.gcube.application.perform.service.engine.model.DBField.ImportRoutine;
import org.gcube.application.perform.service.engine.model.anagraphic.Batch;
import org.gcube.application.perform.service.engine.model.anagraphic.Farm;
import org.gcube.application.perform.service.engine.model.importer.ImportRoutineDescriptor;
import org.gcube.application.perform.service.engine.model.importer.ImportStatus;
import org.gcube.application.perform.service.engine.utils.CommonUtils;
import org.gcube.application.perform.service.engine.utils.ScopeUtils;

public class Queries {

	public static final Query GET_BATCH_BY_DESCRIPTIVE_KEY= new Query("Select * from batches where farmid=? AND type=? AND name= ?", 
			new DBField[] {DBField.Batch.fields.get(DBField.Batch.FARM_ID),
					DBField.Batch.fields.get(DBField.Batch.BATCH_TYPE),
					DBField.Batch.fields.get(DBField.Batch.BATCH_NAME)}); 
	
	public static final Query INSERT_BATCH=new Query("INSERT INTO batches (uuid,farmid,type,name) VALUES (?,?,?,?) ON CONFLICT DO NOTHING",
			new DBField[] {DBField.Batch.fields.get(DBField.Batch.UUID),
					DBField.Batch.fields.get(DBField.Batch.FARM_ID),
					DBField.Batch.fields.get(DBField.Batch.BATCH_TYPE),
					DBField.Batch.fields.get(DBField.Batch.BATCH_NAME)});
	
	public static final Query GET_FARM_BY_ID=new Query("SELECT * from completefarms WHERE id = ?",			
			new DBField[] {DBField.Farm.fields.get(DBField.Farm.FARM_ID)});
	
	public static final Query GET_BATCH_BY_ID=new Query("Select * from batches where id = ?",
			new DBField[] {DBField.Batch.fields.get(DBField.Batch.BATCH_ID)});
	
	public static final Query GET_BATCH_BY_FARM_ID=new Query("Select * from batches where farmid = ?",
			new DBField[] {DBField.Batch.fields.get(DBField.Batch.FARM_ID)});
	
	
	
	public static final Query GET_OLDER_EQUIVALENT_IMPORT_ROUTINE=new Query("Select * from "+ImportRoutine.TABLE+" WHERE "
			+ImportRoutine.FARM_ID+"=? AND "
			+ImportRoutine.BATCH_TYPE+"=? AND "
			+ImportRoutine.SOURCE_URL+"=? AND "
			+ImportRoutine.ID+"<>? AND "
			+"("+ImportRoutine.END+"<? OR "+ImportRoutine.END+" IS NULL) ",
			new DBField[]{
					DBField.ImportRoutine.fields.get(ImportRoutine.FARM_ID),
					DBField.ImportRoutine.fields.get(ImportRoutine.BATCH_TYPE),
					DBField.ImportRoutine.fields.get(ImportRoutine.SOURCE_URL),
					DBField.ImportRoutine.fields.get(ImportRoutine.ID),
					DBField.ImportRoutine.fields.get(ImportRoutine.END)});
	

	// Imports with lock = hostname or lock == null
	public static final Query ORPHAN_IMPORTS=new Query("SELECT * from "+ImportRoutine.TABLE+" where "+ImportRoutine.LOCK+" = ? OR "+ImportRoutine.LOCK+" IS NULL ",
			new DBField[]{DBField.ImportRoutine.fields.get(ImportRoutine.LOCK)});
	// "acquire"
				// set lock = hostname where ID =? and LOCK is null
				// Acquired = updated rows == 1
	public static final Query ACQUIRE_IMPORT_ROUTINE=new Query("UPDATE "+ImportRoutine.TABLE+" SET "+ImportRoutine.LOCK+"=? WHERE "+ImportRoutine.ID+" = ? AND "+ImportRoutine.LOCK+" IS NULL",
			new DBField[]{DBField.ImportRoutine.fields.get(ImportRoutine.LOCK),
					DBField.ImportRoutine.fields.get(ImportRoutine.ID)});
		
	public static final Query GET_IMPORT_ROUTINE_BY_ID=new Query("SELECT *  from "+ImportRoutine.TABLE+" WHERE "+ImportRoutine.ID+" = ?",
			new DBField[]{DBField.ImportRoutine.fields.get(ImportRoutine.ID)});
	public static final Query INSERT_ROUTINE=new Query("INSERT INTO "+ImportRoutine.TABLE+"("
			+ImportRoutine.BATCH_TYPE+","
			+ImportRoutine.CALLER+","
			+ImportRoutine.COMPUTATION_ID+","
			+ImportRoutine.COMPUTATION_OPID+","
			+ImportRoutine.COMPUTATION_OPNAME+","
			+ImportRoutine.COMPUTATION_REQ+","
			+ImportRoutine.COMPUTATION_URL+","
//			+ImportRoutine.END+","
			+ImportRoutine.FARM_ID+","
			+ImportRoutine.LOCK+","
			+ImportRoutine.SOURCE_URL+","
			+ImportRoutine.SOURCE_VERSION+","
			+ImportRoutine.START+","
			+ImportRoutine.STATUS+") values (?,?,?,?,?,?,?,?,?,?,?,?,?)",
			new DBField[]{DBField.ImportRoutine.fields.get(ImportRoutine.BATCH_TYPE),
					DBField.ImportRoutine.fields.get(ImportRoutine.CALLER),
					DBField.ImportRoutine.fields.get(ImportRoutine.COMPUTATION_ID),
					DBField.ImportRoutine.fields.get(ImportRoutine.COMPUTATION_OPID),
					DBField.ImportRoutine.fields.get(ImportRoutine.COMPUTATION_OPNAME),
					DBField.ImportRoutine.fields.get(ImportRoutine.COMPUTATION_REQ),
					DBField.ImportRoutine.fields.get(ImportRoutine.COMPUTATION_URL),
//					DBField.ImportRoutine.fields.get(ImportRoutine.END),
					DBField.ImportRoutine.fields.get(ImportRoutine.FARM_ID),
					DBField.ImportRoutine.fields.get(ImportRoutine.LOCK),
					DBField.ImportRoutine.fields.get(ImportRoutine.SOURCE_URL),
					DBField.ImportRoutine.fields.get(ImportRoutine.SOURCE_VERSION),
					DBField.ImportRoutine.fields.get(ImportRoutine.START),
					DBField.ImportRoutine.fields.get(ImportRoutine.STATUS)});
	
	public static final Query FILTER_IMPORTS=new Query("SELECT *  from "+ImportRoutine.TABLE+" WHERE "+ImportRoutine.FARM_ID+" = ?",
			new DBField[]{DBField.ImportRoutine.fields.get(ImportRoutine.FARM_ID)});
	
	
	public static final Query LAST_GROUPED_IMPORTS=new Query("Select * from "+ImportRoutine.TABLE+" where "+ImportRoutine.FARM_ID+"=? AND ("+ImportRoutine.BATCH_TYPE+","+ImportRoutine.END+") IN "
			+ "(Select "+ImportRoutine.BATCH_TYPE+",max("+ImportRoutine.END+") as "+ImportRoutine.END+" from "+ImportRoutine.TABLE+" WHERE "+ImportRoutine.FARM_ID+"=? AND "+ImportRoutine.STATUS+" = ? group by "+ImportRoutine.BATCH_TYPE+")",
			new DBField[]{DBField.ImportRoutine.fields.get(ImportRoutine.FARM_ID),
					DBField.ImportRoutine.fields.get(ImportRoutine.FARM_ID),
					DBField.ImportRoutine.fields.get(ImportRoutine.STATUS)});
	
	
	
	public static final Query UPDATE_IMPORT_STATUS=new Query("UPDATE "+ImportRoutine.TABLE+" SET "+ImportRoutine.STATUS+"= ?, "+ImportRoutine.END+"=? WHERE "+ImportRoutine.ID+"=?",
			new DBField[]{DBField.ImportRoutine.fields.get(ImportRoutine.STATUS),
					DBField.ImportRoutine.fields.get(ImportRoutine.END),
					DBField.ImportRoutine.fields.get(ImportRoutine.ID)});
	
	
	// LOADERS 
	
	public static Batch rowToBatch(ResultSet rs) throws SQLException {
		Batch toReturn=new Batch();
		toReturn.setFarmId(rs.getLong(DBField.Batch.FARM_ID));
		toReturn.setId(rs.getLong(DBField.Batch.BATCH_ID));
		toReturn.setName(rs.getString(DBField.Batch.BATCH_NAME));
		toReturn.setType(rs.getString(DBField.Batch.BATCH_TYPE));
		toReturn.setUuid((UUID)rs.getObject(DBField.Batch.UUID));
		return toReturn;
	}
	
	
	public static Farm rowToFarm(ResultSet rs) throws SQLException {
		Farm toReturn=new Farm();
		toReturn.setAssociationId(rs.getLong(DBField.Farm.ASSOCIATION_ID));
		toReturn.setAssociationUUID((UUID)rs.getObject(DBField.Farm.ASSOCIATION_UUID));
		toReturn.setCompanyId(rs.getLong(DBField.Farm.COMPANY_ID));
		toReturn.setCompanyUUID((UUID)rs.getObject(DBField.Farm.COMPANY_UUID));
		toReturn.setId(rs.getLong(DBField.Farm.FARM_ID));
		toReturn.setUUID((UUID)rs.getObject(DBField.Farm.UUID));
		return toReturn;
	}
	
	
	public static ImportRoutineDescriptor rowToDescriptor(ResultSet rs) throws SQLException {
		ImportRoutineDescriptor toReturn=new ImportRoutineDescriptor();
		toReturn.setBatch_type(rs.getString(ImportRoutine.BATCH_TYPE));
		toReturn.setCaller(rs.getString(ImportRoutine.CALLER));
		toReturn.setComputationId(rs.getString(ImportRoutine.COMPUTATION_ID));
		toReturn.setComputationOperator(rs.getString(ImportRoutine.COMPUTATION_OPID));
		toReturn.setComputationOperatorName(rs.getString(ImportRoutine.COMPUTATION_OPNAME));
		toReturn.setComputationRequest(rs.getString(ImportRoutine.COMPUTATION_REQ));
		toReturn.setComputationUrl(rs.getString(ImportRoutine.COMPUTATION_URL));
		
		Timestamp endTime=rs.getTimestamp(ImportRoutine.END);
		if(endTime!=null)
			toReturn.setEndTime(endTime.toInstant());
		toReturn.setFarmId(rs.getLong(ImportRoutine.FARM_ID));
		toReturn.setId(rs.getLong(ImportRoutine.ID));
		
		toReturn.setLock(rs.getString(ImportRoutine.LOCK));
		toReturn.setSourceUrl(rs.getString(ImportRoutine.SOURCE_URL));
		
		toReturn.setSourceVersion(rs.getString(ImportRoutine.SOURCE_VERSION));
		toReturn.setStartTime(rs.getTimestamp(ImportRoutine.START).toInstant());
		toReturn.setStatus(ImportStatus.valueOf(rs.getString(ImportRoutine.STATUS)));
		try {
			toReturn.setSubmitterIdentity(ScopeUtils.getClientId(CommonUtils.decryptString(rs.getString(ImportRoutine.CALLER))));
		}catch(Throwable t) {
			
		}
		return toReturn;
	}
	
}
