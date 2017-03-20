package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Analysis;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.AnalysisFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.AnalysisType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.gcube_system.namespaces.application.aquamaps.types.PagedRequestSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalysisTableManager {

	final static Logger logger= LoggerFactory.getLogger(AnalysisTableManager.class);
	
	private static final String analysisTable="analysis_table";
	
	public String getJSONRequests(List<Field> filters, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return DBUtils.toJSon(session.executeFilteredQuery(filters, analysisTable, settings.getOrderField(), settings.getOrderDirection()),settings.getOffset(),settings.getOffset()+settings.getLimit());
		}catch(Exception e){
			throw e;
		}finally{
			if(session!=null) session.close();
		}
	}
	
	public static String insertRequest(Analysis toInsert)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			toInsert.setId(ServiceUtils.generateId("An", ""));
			toInsert.setStatus(SubmittedStatus.Pending);
			toInsert.setSubmissiontime(System.currentTimeMillis());
			List<List<Field>> rows=new ArrayList<List<Field>>();
			List<Field> toInsertRow=new ArrayList<Field>();
			logger.debug("Inserting request, fields are :");
			for(Field f:toInsert.toRow())
				if(f.value()!=null&&!f.value().equalsIgnoreCase("null")){
					toInsertRow.add(f);
					logger.debug(f.toXML());
				}
			rows.add(toInsertRow);
			session.insertOperation(analysisTable, rows);
			return toInsert.getId();
		}catch(Exception e){
			throw e;
		}finally{
			if(session!=null) session.close();
		}
	}
	private static void updateField(String id, List<Field> values)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> key= new ArrayList<Field>();
			key.add(new Field(AnalysisFields.id+"",id,FieldType.STRING));
			keys.add(key);
			List<List<Field>> rows=new ArrayList<List<Field>>();
			rows.add(values);
			session.updateOperation(analysisTable, keys, rows);
		}catch(Exception e){
			throw e;
		}finally{
			if(session!=null) session.close();
		}
	}
	public static ArrayList<Analysis> getList(ArrayList<Field> filter)throws Exception{
		DBSession session=null;
		if(filter==null) filter=new ArrayList<Field>();
		try{
			session=DBSession.getInternalDBSession();
			return Analysis.loadResultSet(
					session.executeFilteredQuery(filter, analysisTable, AnalysisFields.submissiontime+"", OrderDirection.ASC));
		}catch(Exception e){
			throw e;
		}finally{
			if(session!=null) session.close();
		}
	}
	public static List<Analysis> getList(List<Field> filter, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Analysis> toReturn=new ArrayList<Analysis>();
			ResultSet rs=session.executeFilteredQuery(filter,analysisTable, settings.getOrderField(), settings.getOrderDirection());
			int rowIndex=0;
			while(rs.next()&&toReturn.size()<settings.getLimit()){
				if(rowIndex>=settings.getOffset()) toReturn.add(new Analysis(rs));
				rowIndex++;				
			}
			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
//	private static Field getField(String id, String field)throws Exception{
//		DBSession session=null;
//		try{
//			session=DBSession.getInternalDBSession();
//			List<Field> key= new ArrayList<Field>();
//			key.add(new Field(SourceGenerationRequestFields.id+"",id,FieldType.STRING));
//			ResultSet rs= session.executeFilteredQuery(key, analysisTable, field, OrderDirection.ASC);
//			if(rs.next())
//				for(Field f:Field.loadRow(rs)){
//					if(f.name().equals(field)) return f;
//				}
//			throw new Exception("Field not found "+field);
//		}catch(Exception e){
//			throw e;
//		}finally{
//			if(session!=null) session.close();
//		}
//	}
	public static void addReportId(int reportId, String id)throws Exception{
		Analysis req=getById(id);		
		req.addReportId(reportId);
		updateField(id, new ArrayList<Field>(Arrays.asList(new Field[]{
				req.getField(AnalysisFields.reportid)
		})));
	}
	
	public static void addCompletedAnalysis(String id,AnalysisType toAdd)throws Exception{
		Analysis req=getById(id);		
		req.getPerformedAnalysis().add(toAdd);
		updateField(id, new ArrayList<Field>(Arrays.asList(new Field[]{
				req.getField(AnalysisFields.performedanalysis)
		})));
	}
	
	public static void removeReportId(int reportId, String id)throws Exception{
		Analysis req=getById(id);		
		req.removeReportId(reportId);
		updateField(id, new ArrayList<Field>(Arrays.asList(new Field[]{
				req.getField(AnalysisFields.reportid)
		})));
	}
	public static void setPhasePercent(double percent,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(AnalysisFields.currentphasepercent+"",percent+"",FieldType.DOUBLE));
		updateField(id, fields);
	}
	public static void setStartTime(String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(AnalysisFields.starttime+"",System.currentTimeMillis()+"",FieldType.LONG));
		updateField(id,fields);
	}
	public static void setArchivePath(String id,String path)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(AnalysisFields.archivelocation+"",path,FieldType.STRING));
		updateField(id,fields);
	}
	public static void setStatus(SubmittedStatus status, String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(AnalysisFields.status+"",status+"",FieldType.STRING));
		if(status.equals(SubmittedStatus.Completed)||status.equals(SubmittedStatus.Error)){
			fields.add(new Field(AnalysisFields.endtime+"",System.currentTimeMillis()+"",FieldType.LONG));
			fields.add(new Field(AnalysisFields.currentphasepercent+"",100+"",FieldType.DOUBLE));
		}
		updateField(id, fields);
	}
	
	public static String getJSONList(List<Field> filters, PagedRequestSettings settings) throws Exception{
		if(filters==null) filters=new ArrayList<Field>();
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return DBUtils.toJSon(session.executeFilteredQuery(filters, analysisTable, settings.getOrderField(), settings.getOrderDirection()),
					settings.getOffset(),settings.getOffset()+settings.getOffset()+settings.getLimit());
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}

	public static int delete(ArrayList<Field> filter)throws Exception{
		DBSession session=null;
		if(filter==null) filter=new ArrayList<Field>();
		try{
			session=DBSession.getInternalDBSession();
			PreparedStatement ps= session.getPreparedStatementForDelete(filter, analysisTable);
			return session.fillParameters(filter,0, ps).executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(session!=null) session.close();
		}
	}
	
	public static Analysis getById(String id)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filters=new ArrayList<Field>();
			filters.add(new Field(AnalysisFields.id+"",id+"",FieldType.STRING));
			return loadRS(session.executeFilteredQuery(filters, analysisTable, AnalysisFields.id+"", OrderDirection.ASC)).iterator().next();
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	public static int delete (String id)throws Exception{
		ArrayList<Field> filter=new ArrayList<Field>();
		filter.add(new Field(AnalysisFields.id+"",id,FieldType.STRING));
		return delete(filter);
	}
	
	public static Long getCount(List<Field> filter)throws Exception{
		DBSession session=null;
		if(filter==null) filter=new ArrayList<Field>();
		try{
			session=DBSession.getInternalDBSession();
			return session.getCount(analysisTable, filter);
		}catch(Exception e){
			throw e;
		}finally{
			if(session!=null) session.close();
		}
	}
	
	private static Set<Analysis> loadRS(ResultSet rs) throws Exception{
		HashSet<Analysis> toReturn=new HashSet<Analysis>();
		while(rs.next()){
			toReturn.add(new Analysis(rs));
		}
		return toReturn;
	}
}
