package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.environments.SourceGenerationRequest;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SourceGenerationPhase;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SourceGenerationRequestFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.gcube_system.namespaces.application.aquamaps.types.PagedRequestSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceGenerationRequestsManager {

	final static Logger logger= LoggerFactory.getLogger(SourceGenerationRequestsManager.class);

	public final static String requestsTable="source_generation_requests";


	public static String insertRequest(SourceGenerationRequest toInsert)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			toInsert.setId(ServiceUtils.generateId("HGGR", ""));
			toInsert.setPhase(SourceGenerationPhase.pending);
			toInsert.setSubmissiontime(System.currentTimeMillis());	
			toInsert.setGeneratedSources(new ArrayList<Integer>());
			toInsert.setJobIds(new ArrayList<Integer>());
			toInsert.setCurrentphasepercent(0d);
			toInsert.setEndtime(0l);
			toInsert.setEvaluatedComputationCount(0);			
			toInsert.setReportID(new ArrayList<Integer>());
			toInsert.setStarttime(0l);
			toInsert.setToGenerateTableCount(0);
			List<List<Field>> rows=new ArrayList<List<Field>>();
			List<Field> toInsertRow=new ArrayList<Field>();
			logger.debug("Inserting request, fields are :");
			for(Field f:toInsert.toRow())
				if(f.value()!=null&&!f.value().equalsIgnoreCase("null")){
					toInsertRow.add(f);
					logger.debug(f.toXML());
				}
			rows.add(toInsertRow);
			session.insertOperation(requestsTable, rows);
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
			key.add(new Field(SourceGenerationRequestFields.id+"",id,FieldType.STRING));
			keys.add(key);
			List<List<Field>> rows=new ArrayList<List<Field>>();
			rows.add(values);
			session.updateOperation(requestsTable, keys, rows);
		}catch(Exception e){
			throw e;
		}finally{
			if(session!=null) session.close();
		}
	}




	public static ArrayList<SourceGenerationRequest> getList(ArrayList<Field> filter)throws Exception{
		DBSession session=null;
		if(filter==null) filter=new ArrayList<Field>();
		try{
			session=DBSession.getInternalDBSession();
			return SourceGenerationRequest.loadResultSet(
					session.executeFilteredQuery(filter, requestsTable, SourceGenerationRequestFields.submissiontime+"", OrderDirection.ASC));
		}catch(Exception e){
			throw e;
		}finally{
			if(session!=null) session.close();
		}
	}

	
	public static List<SourceGenerationRequest> getList(List<Field> filter, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<SourceGenerationRequest> toReturn=new ArrayList<SourceGenerationRequest>();
			ResultSet rs=session.executeFilteredQuery(filter,requestsTable, settings.getOrderField(), settings.getOrderDirection());
			int rowIndex=0;
			while(rs.next()&&toReturn.size()<settings.getLimit()){
				if(rowIndex>=settings.getOffset()) toReturn.add(new SourceGenerationRequest(rs));
				rowIndex++;				
			}
			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}
	
	
	
	

	private static Field getField(String id, String field)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> key= new ArrayList<Field>();
			key.add(new Field(SourceGenerationRequestFields.id+"",id,FieldType.STRING));
			ResultSet rs= session.executeFilteredQuery(key, requestsTable, field, OrderDirection.ASC);
			if(rs.next())
				for(Field f:Field.loadRow(rs)){
					if(f.name().equals(field)) return f;
				}
			throw new Exception("Field not found "+field);
		}catch(Exception e){
			throw e;
		}finally{
			if(session!=null) session.close();
		}
	}

	public static void setPhase(SourceGenerationPhase phase, String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(SourceGenerationRequestFields.phase+"",phase+"",FieldType.STRING));
		if(phase.equals(SourceGenerationPhase.completed)||phase.equals(SourceGenerationPhase.error)){
			fields.add(new Field(SourceGenerationRequestFields.endtime+"",System.currentTimeMillis()+"",FieldType.LONG));
			fields.add(new Field(SourceGenerationRequestFields.currentphasepercent+"",100+"",FieldType.DOUBLE));
		}
		
		updateField(id, fields);
	}
	public static void addReportId(int reportId, String id)throws Exception{
		SourceGenerationRequest req=getById(id);		
		req.addReportId(reportId);
		updateField(id, new ArrayList<Field>(Arrays.asList(new Field[]{
				req.getField(SourceGenerationRequestFields.reportid)
		})));
	}
	public static void removeReportId(int reportId, String id)throws Exception{
		SourceGenerationRequest req=getById(id);		
		req.removeReportId(reportId);
		updateField(id, new ArrayList<Field>(Arrays.asList(new Field[]{
				req.getField(SourceGenerationRequestFields.reportid)
		})));
	}
	public static void setPhasePercent(double percent,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(SourceGenerationRequestFields.currentphasepercent+"",percent+"",FieldType.DOUBLE));
		updateField(id, fields);
	}
	public static void addGeneratedResource(int hspecId,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		ArrayList<String> current=CSVUtils.CSVToStringList(getField(id, SourceGenerationRequestFields.generatedsourcesid+"").value());
		current.add(hspecId+"");
		fields.add(new Field(SourceGenerationRequestFields.generatedsourcesid+"",CSVUtils.listToCSV(current),FieldType.STRING));
		updateField(id, fields);
	}
	public static void addJobIds(int jobId,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		ArrayList<String> current=CSVUtils.CSVToStringList(getField(id, SourceGenerationRequestFields.jobids+"").value());
		current.add(jobId+"");
		fields.add(new Field(SourceGenerationRequestFields.jobids+"",CSVUtils.listToCSV(current),FieldType.STRING));
		updateField(id, fields);
	}

	public static void setToGenerateTableCount(int count,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(SourceGenerationRequestFields.togeneratetablescount+"",count+"",FieldType.INTEGER));
		updateField(id, fields);
	}
	public static void setEvaluatedComputationCount(int count,String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(SourceGenerationRequestFields.evaluatedcomputationcount+"",count+"",FieldType.INTEGER));
		updateField(id, fields);
	}
	
	public static void setStartTime(String id)throws Exception{
		ArrayList<Field> fields=new ArrayList<Field>();
		fields.add(new Field(SourceGenerationRequestFields.starttime+"",System.currentTimeMillis()+"",FieldType.LONG));
		updateField(id,fields);
	}

	public static String getJSONList(List<Field> filters, PagedRequestSettings settings) throws Exception{
		if(filters==null) filters=new ArrayList<Field>();
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return DBUtils.toJSon(session.executeFilteredQuery(filters, requestsTable, settings.getOrderField(), settings.getOrderDirection()),
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
			PreparedStatement ps= session.getPreparedStatementForDelete(filter, requestsTable);
			return session.fillParameters(filter,0, ps).executeUpdate();
		}catch(Exception e){
			throw e;
		}finally{
			if(session!=null) session.close();
		}
	}

	
	public static int delete (String id)throws Exception{
		ArrayList<Field> filter=new ArrayList<Field>();
		filter.add(new Field(SourceGenerationRequestFields.id+"",id,FieldType.STRING));
		return delete(filter);
	}
	
	public static Long getCount(List<Field> filter)throws Exception{
		DBSession session=null;
		if(filter==null) filter=new ArrayList<Field>();
		try{
			session=DBSession.getInternalDBSession();
			return session.getCount(requestsTable, filter);
		}catch(Exception e){
			throw e;
		}finally{
			if(session!=null) session.close();
		}
	}
	public static SourceGenerationRequest getById(String id)throws Exception{
		SourceGenerationRequest request=new SourceGenerationRequest();
		request.setId(id);
		ArrayList<SourceGenerationRequest> found=getList(new ArrayList<Field>(Arrays.asList(new Field[]{
				request.getField(SourceGenerationRequestFields.id)
		})));
		if(found.size()>0) return found.get(0);
		else return null;
	}
}
