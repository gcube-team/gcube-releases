package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.gcube_system.namespaces.application.aquamaps.types.PagedRequestSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubmittedManager {

	final static Logger logger= LoggerFactory.getLogger(SubmittedManager.class);

	public static final String submittedTable="submitted";

		
	
	protected static Object getField(int id, SubmittedFields field)throws Exception{
		DBSession session=null;
		try{			
			session=DBSession.getInternalDBSession();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(SubmittedFields.searchid+"",id+"",FieldType.INTEGER));
			ResultSet rs= session.executeFilteredQuery(filter, submittedTable, SubmittedFields.searchid+"", OrderDirection.ASC);
			if(rs.next())
				return rs.getObject(field.toString());
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}

	protected static int updateField(int id,SubmittedFields field,FieldType type,Object value)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();			
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(SubmittedFields.searchid+"",id+"",FieldType.INTEGER));
			keys.add(filter);
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> valueList=new ArrayList<Field>();
			valueList.add(new Field(field+"",value+"",type));
			values.add(valueList);
			return session.updateOperation(submittedTable, keys, values);
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}


	public static int deleteFromTables(int submittedId)throws Exception{


		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(SubmittedFields.searchid+"",submittedId+"",FieldType.INTEGER));
			return session.deleteOperation(submittedTable, filter);			
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}


	public static int delete(int submittedId)throws Exception{
		Boolean isAquaMaps=isAquaMap(submittedId);
		DBSession session=null;		
		try{
			int count=0;
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> key=new ArrayList<Field>();
			key.add(new Field(SubmittedFields.searchid+"",submittedId+"",FieldType.INTEGER));
			keys.add(key);
			List<List<Field>> rows=new ArrayList<List<Field>>();
			List<Field> toSet=new ArrayList<Field>();
			toSet.add(new Field(SubmittedFields.todelete+"",true+"",FieldType.BOOLEAN));
			rows.add(toSet);
			session=DBSession.getInternalDBSession();
			session.disableAutoCommit();
			count+=session.updateOperation(submittedTable, keys, rows);
			if(!isAquaMaps){
				keys.get(0).set(0, new Field(SubmittedFields.jobid+"",submittedId+"",FieldType.INTEGER));
				count+=session.updateOperation(submittedTable, keys, rows);
			}		
			session.commit();
			return count;
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}		
	}


	// **************************************** getters *********************************

	public static int getHCAFTableId(int jobId)throws Exception{
		return (Integer) getField(jobId,SubmittedFields.sourcehcaf);		
	}
	public static int getHSPENTableId(int jobId)throws Exception{
		return (Integer) getField(jobId,SubmittedFields.sourcehspen);		
	}
	public static int getHSPECTableId(int jobId)throws Exception{
		return (Integer) getField(jobId,SubmittedFields.sourcehspec);		
	}
	public static Boolean isGIS(int submittedId) throws Exception{
		return ((Integer) getField(submittedId,SubmittedFields.gisenabled)==1);
	}
	
	public static List<String> getGisId(int submittedId)throws Exception{
		return CSVUtils.CSVToStringList((String) getField(submittedId,SubmittedFields.gispublishedid));
	}

	public static SubmittedStatus getStatus(int submittedId)throws Exception{
		return SubmittedStatus.valueOf((String) getField(submittedId,SubmittedFields.status));
	}
	public static String getAuthor(int submittedId)throws Exception{
		return (String) getField(submittedId,SubmittedFields.author);
	}

	public static Boolean isAquaMap(int submittedId)throws Exception{
		return ((Integer) getField(submittedId,SubmittedFields.isaquamap)==1);
	}

		
	
	

	public static int updateGISData(int submittedId,Boolean gisEnabled)throws Exception{
		return updateField(submittedId,SubmittedFields.gisenabled,FieldType.BOOLEAN,gisEnabled+"");
	}

	public static int markSaved(int submittedId)throws Exception{
		return updateField(submittedId,SubmittedFields.saved,FieldType.BOOLEAN,true);
	}

	public static int setGisPublishedId(int submittedId,String gisId)throws Exception{
		return updateField(submittedId,SubmittedFields.gispublishedid,FieldType.STRING,gisId);
	}
	
	
	public static int setSerializedPath(int submittedId,String path)throws Exception{
		return updateField(submittedId,SubmittedFields.serializedrequest,FieldType.STRING,path);
	}

	//******** Logic

	/**
	 * Updates internal Status, in case of Error status updates published element as side effect
	 */

	public static void updateStatus(int toUpdateId,SubmittedStatus statusValue)throws SQLException, IOException, Exception{
		updateField(toUpdateId,SubmittedFields.status,FieldType.STRING,statusValue.toString());		
		if(statusValue.equals(SubmittedStatus.Error)||statusValue.equals(SubmittedStatus.Completed)){
			updateField(toUpdateId,SubmittedFields.endtime,FieldType.LONG,System.currentTimeMillis()+"");
		}
		logger.trace("done submitted[ID : "+toUpdateId+"] status updateing status : "+statusValue.toString());
	}


	public static Submitted insertInTable(Submitted toInsert)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> rows=new ArrayList<List<Field>>();
			List<Field> row=new ArrayList<Field>();
			for(Field f: toInsert.toRow())
				if(!f.name().equals(SubmittedFields.searchid+"")) row.add(f);
			rows.add(row);
			List<List<Field>> inserted=session.insertOperation(submittedTable,rows);
			return new Submitted(inserted.get(0));
		}catch(Exception e){
			logger.error("Unable to insert submitted "+toInsert);
			throw e;}
		finally{if(session!=null) session.close();}
	}





	public static List<Submitted> getList(List<Field> filters)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return Submitted.loadResultSet(session.executeFilteredQuery(filters, submittedTable,null,null));
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}

	public static String getJsonList(List<Field> filters,PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return DBUtils.toJSon(session.executeFilteredQuery(filters, submittedTable,settings.getOrderField(),settings.getOrderDirection()),settings.getOffset(), settings.getLimit()+settings.getOffset());
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}

	public static Submitted getSubmittedById(int objId) throws Exception{
		List<Field> filter=new ArrayList<Field>();
		filter.add(new Field(SubmittedFields.searchid+"",objId+"",FieldType.INTEGER));
		List<Submitted> found= getList(filter,new PagedRequestSettings(1, 0, OrderDirection.ASC, SubmittedFields.searchid+""));
		return found.get(0);
	}


	public static void update(Submitted toUpdate)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Field> id=new ArrayList<Field>();
			id.add(toUpdate.getField(SubmittedFields.searchid));
			ArrayList<Field> values=new ArrayList<Field>();
			for(Field f: toUpdate.toRow())
				if(!f.name().equals(SubmittedFields.searchid+"")) values.add(f);
			PreparedStatement psUpdate=session.getPreparedStatementForUpdate(values, id, submittedTable);
			psUpdate=session.fillParameters(values, 0, psUpdate);
			psUpdate=session.fillParameters(id, values.size(), psUpdate);
			psUpdate.executeUpdate();
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}


	public static List<Submitted> getList(List<Field> filter, PagedRequestSettings settings)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Submitted> toReturn=new ArrayList<Submitted>();
			ResultSet rs=session.executeFilteredQuery(filter, submittedTable, settings.getOrderField(), settings.getOrderDirection());
			int rowIndex=0;
			while(rs.next()&&toReturn.size()<settings.getLimit()){
				if(rowIndex>=settings.getOffset()) toReturn.add(new Submitted(rs));
				rowIndex++;				
			}
			return toReturn;
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}

	public static Long getCount(List<Field> filter)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			return session.getCount(submittedTable, filter);
		}catch(Exception e){throw e;}
		finally{if(session!=null) session.close();}
	}

	public static void setStartTime(int submittedId)throws Exception{
		updateField(submittedId,SubmittedFields.starttime,FieldType.LONG,System.currentTimeMillis());
	}
}
