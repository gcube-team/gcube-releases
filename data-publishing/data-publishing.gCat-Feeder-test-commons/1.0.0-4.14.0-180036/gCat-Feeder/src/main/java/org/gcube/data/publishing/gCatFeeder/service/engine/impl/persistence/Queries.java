package org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence;

import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.CALLER_CONTEXT;
import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.CALLER_ID;
import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.CALLER_TOKEN;
import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.COLLECTORS;
import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.CONTROLLERS;
import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.END;
import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.ID;
import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.REPORT_URL;
import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.START;
import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.STATUS;
import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.TABLE;
import static org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DBField.ExecutionDescriptor.fields;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.gcube.data.publishing.gCatFeeder.service.engine.impl.persistence.DatabaseConnectionDescriptor.Flavor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionDescriptor;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionRequest;
import org.gcube.data.publishing.gCatFeeder.service.model.ExecutionStatus;
import org.gcube.data.publishing.gCatFeeder.service.model.fault.InvalidRequest;


public class Queries {
	
	public static final String getInitDB(DatabaseConnectionDescriptor.Flavor flavor) {
		return "CREATE TABLE IF NOT EXISTS "+TABLE+" ("
			+ID+" "+(flavor.equals(Flavor.POSTGRES)?"BIGSERIAL":"bigint auto_increment") +" NOT NULL,"
			+CALLER_TOKEN+" VARCHAR NOT NULL,"
			+CALLER_ID+" VARCHAR NOT NULL,"
			+CALLER_CONTEXT+" VARCHAR NOT NULL,"
			+STATUS+" VARCHAR(40) NOT NULL,"
			+REPORT_URL+" VARCHAR,"
			+COLLECTORS+" text,"
			+CONTROLLERS+" text,"
			+START+" timestamp with time zone,"
			+END+" timestamp with time zone,"
			+"primary key ("+ID+"))";			
	}
	

	public static final Query GET_BY_ID=new Query("Select * from "+TABLE+" where "+ID+" = ?",
			new DBField[] {fields.get(ID)});

	public static final Query UPDATE=new Query("UPDATE "+TABLE+" SET "
			+START+"=?, "+END+"=?, "+ STATUS+"=?, "+REPORT_URL+"=? WHERE "+ID+"=?",
			new DBField[] {fields.get(START),
					fields.get(END),
					fields.get(STATUS),
					fields.get(REPORT_URL),
					fields.get(ID)});

	public static final Query ACQUIRE= new Query("UPDATE "+TABLE+" SET "
			+STATUS+"='"+ExecutionStatus.RUNNING+"' WHERE "+ID+"=? AND "+STATUS+"='"+ExecutionStatus.PENDING+"'",
			new DBField[] {fields.get(ID)});

	public static final Query GET_ALL= new Query("SELECT * FROM "+TABLE+" ORDER BY "+END+" DESC",
			new DBField[] {});

	
	
	
	/*
	 * SAME CONTEXT,
	 * status is RUNNING OR PENDING
	 * SAME COLLECTORS
	 * SAME CATALOGUES 
	 */
	public static final Query GET_SIMILAR=new Query ("SELECT * FROM "+TABLE+" WHERE "
			+CALLER_CONTEXT+"=? AND "+COLLECTORS+"=? AND "+CONTROLLERS+"=? AND "
			/* STATUS */ +"("+STATUS+"='"+ExecutionStatus.RUNNING+"' OR "+STATUS+"='"+ExecutionStatus.PENDING+"')",
			new DBField[] {fields.get(CALLER_CONTEXT),
					fields.get(COLLECTORS),
					fields.get(CONTROLLERS)});


	public static final Query INSERT_NEW=new Query ("INSERT INTO "+TABLE+" ("
			+CALLER_TOKEN+","
			+CALLER_ID+","
			+CALLER_CONTEXT+","
			+STATUS+","			
			+COLLECTORS+","
			+CONTROLLERS+") VALUES (?,?,?,'"+ExecutionStatus.PENDING+"',?,?)",
			new DBField[] {fields.get(CALLER_TOKEN),fields.get(CALLER_ID),fields.get(CALLER_CONTEXT),
					fields.get(COLLECTORS),fields.get(CONTROLLERS)});

	public static final ExecutionDescriptor translateRow(ResultSet row) throws SQLException {
		ExecutionDescriptor toReturn=new ExecutionDescriptor();
		toReturn.setCallerContext(row.getString(CALLER_CONTEXT));
		toReturn.setCallerEncryptedToken(row.getString(CALLER_TOKEN));
		toReturn.setCallerIdentity(row.getString(CALLER_ID));
		toReturn.getCatalogues().addAll(fromField(row.getString(CONTROLLERS)));
		toReturn.getCollectors().addAll(fromField(row.getString(COLLECTORS)));
		Timestamp endTime=row.getTimestamp(END);
		if(endTime!=null)
			toReturn.setEndTime(endTime.toInstant());

		toReturn.setId(row.getLong(ID));
		toReturn.setReportUrl(row.getString(REPORT_URL));
		Timestamp startTime=row.getTimestamp(START);
		if(startTime!=null)
		toReturn.setStartTime(startTime.toInstant());
		toReturn.setStatus(ExecutionStatus.valueOf(row.getString(STATUS)));
		return toReturn;
	}

	public static final DBQueryDescriptor translateObject(ExecutionDescriptor descriptor) throws InvalidRequest{
		try{
			return new DBQueryDescriptor().
					add(fields.get(CALLER_CONTEXT),descriptor.getCallerContext()).
					add(fields.get(CALLER_TOKEN),descriptor.getCallerEncryptedToken()).
					add(fields.get(CALLER_ID),descriptor.getCallerIdentity()).
					add(fields.get(CONTROLLERS),toField(descriptor.getCatalogues())).
					add(fields.get(COLLECTORS),toField(descriptor.getCollectors())).
					add(fields.get(END),descriptor.getEndTime()!=null?java.sql.Timestamp.from(descriptor.getEndTime()):null).
					add(fields.get(ID),descriptor.getId()).
					add(fields.get(REPORT_URL),descriptor.getReportUrl()).
					add(fields.get(START),descriptor.getStartTime()!=null?java.sql.Timestamp.from(descriptor.getStartTime()):null).
					add(fields.get(STATUS),descriptor.getStatus().toString());
		}catch(Throwable t) {
			throw new InvalidRequest(t);
		}
	}

	public static final DBQueryDescriptor translateObject(ExecutionRequest request) throws InvalidRequest{
		try{
			return new DBQueryDescriptor().
					add(fields.get(CALLER_CONTEXT),request.getContext()).
					add(fields.get(CALLER_TOKEN),request.getEncryptedToken()).
					add(fields.get(CALLER_ID),request.getCallerID()).
					add(fields.get(CONTROLLERS),toField(request.getToInvokeControllers())).
					add(fields.get(COLLECTORS),toField(request.getToInvokeCollectors()));
			//				add(fields.get(END),descriptor.getEndTime()!=null?java.sql.Timestamp.from(descriptor.getEndTime()):null).
			//				add(fields.get(ID),descriptor.getId()).
			//				add(fields.get(REPORT_URL),descriptor.getReportUrl()).
			//				add(fields.get(START),descriptor.getStartTime()!=null?java.sql.Timestamp.from(descriptor.getStartTime()):null).
			//				add(fields.get(STATUS),descriptor.getStatus().toString());
		}catch(Throwable t) {
			throw new InvalidRequest(t);
		}
	}

	private static final Set<String> fromField(String fieldValue){
		HashSet<String> toReturn=new HashSet<>();
		for(String v:fieldValue.split(","))
			toReturn.add(v);
		return toReturn;
	}

	private static final String toField(Set<String> values){
		StringBuilder toReturn=new StringBuilder();
		if(values!=null&&!values.isEmpty()) {
			ArrayList<String> sorted=new ArrayList<String>(values);
			Collections.sort(sorted);
			for(String v:sorted)
				toReturn.append(v+",");
			return toReturn.substring(0, toReturn.length()-1);
		}
		else return "";
	}
}
