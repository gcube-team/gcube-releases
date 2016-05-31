package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.File;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.util.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ExportCSVSettings;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ExportOperation;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ExportStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.ExportTableStatusType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.utils.Storage;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.HomeManagerFactory;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExportManager extends Thread{

	final static Logger logger= LoggerFactory.getLogger(ExportManager.class);

	private static final String EXPORT_REFERENCE_TABLE="exports";
	private static final String EXPORT_ID="id";
	private static final String EXPORT_TABLE="toexporttable";
	private static final String EXPORT_SETTINGS="settings";
	private static final String EXPORT_STATUS="status";
	private static final String EXPORT_ERROR_MSG="errors";
	private static final String EXPORT_LOCAL_PATH="localpath";
	private static final String EXPORT_TIME="time";
	private static final String EXPORT_LOCATOR="locator";
	private static final String EXPORT_SCOPE="scope";
	//******** NEW
	private static final String EXPORT_OPERATION="operation";
	private static final String EXPORT_USER="username";
	private static final String EXPORT_BASKET="basket";
	private static final String EXPORT_NAME="name";


	public static String submitExportOperation(String tableName,String user,String basket,String name,ExportOperation operation, ExportCSVSettings settings) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();			
			String referenceId=ServiceUtils.generateId("EXPORT", "").toLowerCase();
			ArrayList<Field> row= new ArrayList<Field>();

			row.add(new Field(EXPORT_ID,referenceId,FieldType.STRING));
			row.add(new Field(EXPORT_TABLE,tableName,FieldType.STRING));
			row.add(new Field(EXPORT_SETTINGS,AquaMapsXStream.getXMLInstance().toXML(settings),FieldType.STRING));
			row.add(new Field(EXPORT_STATUS,ExportStatus._PENDING,FieldType.STRING));
			row.add(new Field(EXPORT_SCOPE,ServiceContext.getContext().getScope()+"",FieldType.STRING));
			row.add(new Field(EXPORT_OPERATION,operation.toString(),FieldType.STRING));
			row.add(new Field(EXPORT_USER,user,FieldType.STRING));
			row.add(new Field(EXPORT_BASKET,basket,FieldType.STRING));
			row.add(new Field(EXPORT_NAME,name,FieldType.STRING));

			ArrayList<List<Field>> rows=new ArrayList<List<Field>>();
			rows.add(row);
			session.insertOperation(EXPORT_REFERENCE_TABLE, rows);
			ExportManager thread=new ExportManager(referenceId);
			thread.start();
			return referenceId;
		}finally{if(session!=null) session.close();}
	}

	public static ExportTableStatusType getStatus(String requestId) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			ArrayList<Field> field=new ArrayList<Field>();
			field.add(new Field(EXPORT_ID,requestId,FieldType.STRING));
			ResultSet rs=session.executeFilteredQuery(field, EXPORT_REFERENCE_TABLE, EXPORT_ID, OrderDirection.ASC);
			if(rs.next()){
				ExportTableStatusType status=new ExportTableStatusType();
				status.setCsvSettings((ExportCSVSettings) AquaMapsXStream.getXMLInstance().fromXML(rs.getString(EXPORT_SETTINGS)));				
				status.setStatus(ExportStatus.fromValue(rs.getString(EXPORT_STATUS)));
				status.setRsLocator(rs.getString(EXPORT_LOCATOR));
				status.setTableName(rs.getString(EXPORT_TABLE));
				status.setErrors(rs.getString(EXPORT_ERROR_MSG));
				logger.debug("Found export status [refID:"+requestId+"] : "+status.getStatus()+","+status.getTableName()+","+status.getRsLocator());
				return status;
			}else throw new Exception("Reference "+requestId+" not found");
		}finally{if(session!=null) session.close();}		
	}

	private static int updateField(String id, String field, FieldType objectType,Object value)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> keys=new ArrayList<List<Field>>();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(EXPORT_ID,id,FieldType.STRING));
			keys.add(filter);
			List<List<Field>> values=new ArrayList<List<Field>>();
			List<Field> valueList=new ArrayList<Field>();
			valueList.add(new Field(field+"",value+"",objectType));
			values.add(valueList);
			return session.updateOperation(EXPORT_REFERENCE_TABLE, keys, values);
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}

	//******************** INSTANCE

	private String referenceId;

	private ExportManager(String referenceId){		
		this.referenceId=referenceId;
		this.setName("EXPORTER_"+getId());
	}

	@Override
	public void run() {
		DBSession session=null;
		boolean found=false;		
		String tmpFileName=null;
		try{
			updateField(referenceId, EXPORT_STATUS, FieldType.STRING, ExportStatus._ONGOING);
			session=DBSession.getInternalDBSession();
			ArrayList<Field> field=new ArrayList<Field>();
			field.add(new Field(EXPORT_ID,referenceId,FieldType.STRING));
			ResultSet rs=session.executeFilteredQuery(field, EXPORT_REFERENCE_TABLE, EXPORT_ID, OrderDirection.ASC);			
			if(rs.next()){
				found=true;				
				ExportCSVSettings settings=(ExportCSVSettings) AquaMapsXStream.getXMLInstance().fromXML(rs.getString(EXPORT_SETTINGS));
				tmpFileName=session.exportTableToCSV(rs.getString(EXPORT_TABLE), settings.isHasHeader(),settings.getDelimiter().charAt(0));				
				updateField(referenceId, EXPORT_LOCAL_PATH, FieldType.STRING, tmpFileName);

				String scope=rs.getString(EXPORT_SCOPE);
				ExportOperation operation=ExportOperation.fromString(rs.getString(EXPORT_OPERATION));
				if(operation.equals(ExportOperation.SAVE)){
					//SAVE TO WORKSPACE
					String owner=rs.getString(EXPORT_USER);
					String destinationBasketId=rs.getString(EXPORT_BASKET);
					String toSaveName=rs.getString(EXPORT_NAME);
					logger.debug("Getting workspace for user "+owner+" under scope "+scope);
					ScopeProvider.instance.set(scope);
					HomeManagerFactory factory = HomeLibrary.getHomeManagerFactory();
					Workspace workspace = factory.getHomeManager().getHome(owner).getWorkspace();
					workspace.createExternalFile(toSaveName, "Exported table", "text/csv", new FileInputStream(tmpFileName), destinationBasketId);
					logger.debug("File saved into user's workspace");
				}else {
					//EXPORT TO CLIENT

					String id=Storage.storeFile((new File(tmpFileName)).getAbsolutePath(), false);
					logger.trace("Storage id "+id);

					updateField(referenceId, EXPORT_LOCATOR, FieldType.STRING, id);

				}

				updateField(referenceId, EXPORT_TIME, FieldType.LONG, System.currentTimeMillis());
				updateField(referenceId, EXPORT_STATUS, FieldType.STRING, ExportStatus.COMPLETED);
			}else throw new Exception("Reference "+referenceId+" not found");
		}catch(Exception e){
			if(found){
				try{
					updateField(referenceId, EXPORT_ERROR_MSG, FieldType.STRING, e.getMessage());
					updateField(referenceId, EXPORT_STATUS, FieldType.STRING, ExportStatus._ERROR);
				}catch(Exception e1){
					logger.error("Unable to update export reference id  "+referenceId,e);
				}
			}
		}finally{if(session!=null)
			try {
				session.close();
			} catch (Exception e) {

			}
		if(tmpFileName!=null)FileUtils.delete(new File(tmpFileName));
		}		
	}
}
