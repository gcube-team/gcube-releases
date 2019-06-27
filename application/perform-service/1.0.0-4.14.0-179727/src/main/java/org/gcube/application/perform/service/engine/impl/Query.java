package org.gcube.application.perform.service.engine.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

import org.gcube.application.perform.service.engine.model.DBField;
import org.gcube.application.perform.service.engine.model.DBQueryDescriptor;
import org.gcube.application.perform.service.engine.model.InvalidRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Query {

	protected static final Logger log= LoggerFactory.getLogger(Query.class);

	protected final String query;
	protected final ArrayList<DBField> psFields;

	public Query(String query,DBField[] fields) {
		this.query=query;
		this.psFields=fields!=null?new ArrayList<>(Arrays.asList(fields)):null;
	}


	public PreparedStatement prepare(Connection conn, int statementOption) throws SQLException {
		return conn.prepareStatement(getQuery(),statementOption);
	}
	public PreparedStatement prepare(Connection conn) throws SQLException {
		return conn.prepareStatement(getQuery());
	}


	public PreparedStatement get(Connection conn, DBQueryDescriptor desc) throws SQLException, InvalidRequestException {
		PreparedStatement ps=prepare(conn);
		return fill(ps,desc);
	}

	public PreparedStatement fill(PreparedStatement ps,DBQueryDescriptor desc) throws SQLException, InvalidRequestException{
		log.debug("Setting VALUES {} for Query {} ",desc,getQuery());
		ArrayList<DBField> fields=getPSFields();
		for(int i=0;i<fields.size();i++) {
			DBField field=fields.get(i);
			if(!desc.getCondition().containsKey(field))
				throw new InvalidRequestException("Missing field "+field);
			else {
				Object toSet=desc.getCondition().get(field);
				if(toSet==null) ps.setNull(i+1, field.getType());
				else if(field.getType()==Integer.MIN_VALUE) // UUID EXCEPTION
					ps.setObject(i+1, desc.getCondition().get(field));
				else ps.setObject(i+1, desc.getCondition().get(field), field.getType());
			}
		}				
		return ps;
	}


	public String getQuery() {
		return query; 
	}
	public ArrayList<DBField> getPSFields(){
		return psFields;
	}


}
