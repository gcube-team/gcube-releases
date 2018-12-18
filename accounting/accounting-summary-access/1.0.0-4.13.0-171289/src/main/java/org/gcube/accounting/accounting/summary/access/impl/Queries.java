package org.gcube.accounting.accounting.summary.access.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

import org.gcube.accounting.accounting.summary.access.impl.DBStructure.CONTEXTS;
import org.gcube.accounting.accounting.summary.access.impl.DBStructure.DIMENSIONS;
import org.gcube.accounting.accounting.summary.access.impl.DBStructure.Measure;
import org.gcube.accounting.accounting.summary.access.model.MeasureResolution;
import org.gcube.accounting.accounting.summary.access.model.ScopeDescriptor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Queries {

	@NonNull
	private Connection conn;
	
	public long getMeasureCount(String table) throws SQLException {
		ResultSet rs=conn.createStatement().executeQuery("Select count(*) from "+table);
		rs.next();
		return rs.getLong(1);
	}
	
	public ResultSet listDimensions() throws SQLException {
		return conn.createStatement().executeQuery("Select * from "+DIMENSIONS.TABLENAME+" order by "+DIMENSIONS.LABEL);
	}
	
	public ResultSet listContexts() throws SQLException {
		return conn.createStatement().executeQuery("Select * from "+CONTEXTS.TABLENAME+" order by "+CONTEXTS.LABEL);
	}
	
	/** 
	 *  Returned parameters order is 
	 *  Context, Dimension, Time, Measure, Measure
	 * 	
	 * 	NB : Measure is replicated in order to manage update on conflict 
	 *  
	 * @return PS with parameters
	 * @throws SQLException
	 */
	public PreparedStatement getMeasureInsertionPreparedStatement() throws SQLException {
		String query=String.format("INSERT INTO %1$s (%2$s,%3$s,%4$s,%5$s) values (?,?,?,?)"
				+" ON CONFLICT ON CONSTRAINT "+Measure.TABLENAME+"_pKey DO UPDATE SET "+Measure.MEASURE+"=?", 
				Measure.TABLENAME,Measure.CONTEXT,Measure.DIMENSION,Measure.TIME,Measure.MEASURE);
		return conn.prepareStatement(query); 
	}
	
	/**
	 * Returned parameters order is
	 * ID,LABEL,GROUP,AGGREGATED_MEASURE
	 * 
	 * 
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement getDimensionInsertionPreparedStatement() throws SQLException {
		String query=String.format("INSERT INTO %1$s (%2$s,%3$s,%4$s,%5$s) values (?,?,?,?)", 
				DIMENSIONS.TABLENAME,DIMENSIONS.ID,DIMENSIONS.LABEL,DIMENSIONS.GROUP,DIMENSIONS.AGGREGATED_MEASURE);
		return conn.prepareStatement(query);
	}
	
	
	/**
	 * Returned parameters order is
	 * ID,LABEL
	 * 
	 * 
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement getContextInsertionPreparedStatement() throws SQLException {
		String query=String.format("INSERT INTO %1$s (%2$s,%3$s) values (?,?)", 
				CONTEXTS.TABLENAME,CONTEXTS.ID,CONTEXTS.LABEL);
		return conn.prepareStatement(query);
	}
	
	
	/**
	 * Returns Dimensions.* fields
	 * 
	 * @param from
	 * @param to
	 * @param scope
	 * @param resolution
	 * @return
	 * @throws SQLException
	 */
	public ResultSet getAvailableDimensions(Instant from, Instant to, ScopeDescriptor scope,MeasureResolution resolution) throws SQLException{
		
		String query= String.format("Select * from %1$s where %2$s in (Select distinct(%3$s) from %4$s where %5$s AND %6$s)",
				DIMENSIONS.TABLENAME, DIMENSIONS.ID,
				Measure.DIMENSION,Measure.TABLENAME,
//				String.format("EXTRACT(YEAR FROM %1$s) >= EXTRACT(YEAR FROM CAST (? AS TIMESTAMP)) AND EXTRACT (MONTH FROM %1$s)>= EXTRACT(MONTH FROM CAST (? AS TIMESTAMP)) "
//						+ "AND EXTRACT(YEAR FROM %1$s) <= EXTRACT(YEAR FROM CAST (? AS TIMESTAMP)) AND EXTRACT (MONTH FROM %1$s)<= EXTRACT(MONTH FROM CAST (? AS TIMESTAMP)) ", Measure.TIME),
				"true",
				Measure.CONTEXT+" IN "+asIDSet(scope));
		PreparedStatement toReturn= conn.prepareStatement(query);
//		toReturn.setTimestamp(1, new Timestamp(from.toEpochMilli()));
//		toReturn.setTimestamp(2, new Timestamp(from.toEpochMilli()));
//		toReturn.setTimestamp(3, new Timestamp(to.toEpochMilli()));
//		toReturn.setTimestamp(4, new Timestamp(to.toEpochMilli()));
		
		
		log.debug("Performing query {} ",query);		
		
		return toReturn.executeQuery();
	}
	
	/**
	 * Prepares a statement for Getting Dim=? in time interval for the scope set
	 * 
	 * PS params :
	 * 		1- long current slice of time 
	 * 		2- long current slice of time
	 * 		3- String dimension
	 * 
	 * 
	 * @param from
	 * @param to
	 * @param scope
	 * @param resolution
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement prepareMeasuresByDimension(ScopeDescriptor scope, MeasureResolution resolution) throws SQLException{
		//single scope
//		"Select measure from measures where context=? and time ok and measure.id=? order by time ASC"; 
		// multi scope 
		return conn.prepareStatement(String.format("Select sum(%1$s) as %1$s from %2$s where %3$s AND %4$s AND %5$s=? group by %6$s order by %7$s",
				Measure.MEASURE,Measure.TABLENAME,
				Measure.CONTEXT+" IN "+asIDSet(scope), 			//context ok
				String.format("EXTRACT(YEAR FROM %1$s) = EXTRACT(YEAR FROM CAST (? AS TIMESTAMP)) AND EXTRACT (MONTH FROM %1$s)= EXTRACT(MONTH FROM CAST (? AS TIMESTAMP)) ",Measure.TIME),		// time ok, PS Parameter
				Measure.DIMENSION,								// dimension ok, PS parameter
				Measure.TIME,									// group by (time)
				Measure.TIME));									// order by time
				
				
	}
	
	
	
	
	public static final String asIDSet(ScopeDescriptor desc){
		return "("+scopeList(desc)+")";
	}
	
	
	private static final String scopeList(ScopeDescriptor desc) {
		StringBuilder setBuilder=new StringBuilder();
		setBuilder.append("'"+desc.getId()+"'"+",");
		if(desc.hasChildren()) {
			for(ScopeDescriptor child : desc.getChildren())				
				setBuilder.append(scopeList(child)+",");
						
		}
			String toReturn=setBuilder.toString();
			return toReturn.substring(0, toReturn.lastIndexOf(","));
			
	}
}
