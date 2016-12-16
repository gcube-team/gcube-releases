package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext.FOLDERS;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Generator;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.AquaMapsObject;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Job;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Species;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.types.SubmittedStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;


public class JobManager extends SubmittedManager{

	public static final String toDropTables="temptables";
	public static final String toDropTablesJobId=SubmittedFields.jobid+"";
	public static final String toDropTablesTableName="tablename";

	public static final String tempFolders="tempfolders";
	public static final String tempFoldersJobId=SubmittedFields.jobid+"";
	public static final String tempFoldersFolderName="foldername";

	public static final String selectedSpecies="selectedspecies";
	public static final String selectedSpeciesStatus=SubmittedFields.status+"";
	public static final String selectedSpeciesJobId=SubmittedFields.jobid+"";
	public static final String selectedSpeciesSpeciesID=SpeciesOccursumFields.speciesid+"";
	public static final String selectedSpeciesIsCustomized="iscustomized";
	//******************************************* working tables management ***************************************

	protected static final String workingTables="workingtables";
	protected static final String tableField="tablename";
	protected static final String tableTypeField="tabletype";



	protected static void setWorkingTable(int submittedId,String tableType,String tableName)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			logger.trace("inserting working table reference "+submittedId+", "+tableType+" : "+tableName);
			List<List<Field>> rows= new ArrayList<List<Field>>();
			List<Field> row=new ArrayList<Field>();
			row.add(new Field(SubmittedFields.searchid+"",submittedId+"",FieldType.INTEGER));
			row.add(new Field(tableTypeField,tableType,FieldType.STRING));
			row.add(new Field(tableField,tableName,FieldType.STRING));
			rows.add(row);	
			try{
				session.insertOperation(workingTables, rows);
			}catch(Exception e1){
				logger.trace("trying toupdate working table reference "+submittedId+", "+tableType+" : "+tableName);
				List<List<Field>> values= new ArrayList<List<Field>>();
				List<Field> value=new ArrayList<Field>();
				value.add(new Field(tableField,tableName,FieldType.STRING));
				values.add(row);
				List<List<Field>> keys= new ArrayList<List<Field>>();
				List<Field> key=new ArrayList<Field>();
				key.add(new Field(SubmittedFields.searchid+"",submittedId+"",FieldType.INTEGER));
				key.add(new Field(tableTypeField,tableType,FieldType.STRING));
				keys.add(key);
				session.updateOperation(workingTables, keys, values);
			}
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}

	protected static String getWorkingTable (int submittedId,String tableType)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter=new ArrayList<Field>();
			filter.add(new Field(SubmittedFields.searchid+"",submittedId+"",FieldType.INTEGER));
			filter.add(new Field(tableTypeField,tableType,FieldType.STRING));
			ResultSet rs= session.executeFilteredQuery(filter, workingTables, tableField, OrderDirection.ASC);
			if(rs.next())
				return rs.getString(tableField);
			else return null;
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}

	public static void setWorkingHCAF(int submittedId,String tableName)throws Exception{
		setWorkingTable(submittedId,ResourceType.HCAF.toString(),tableName);
	}
	public static void setWorkingHSPEN(int submittedId,String tableName)throws Exception{
		setWorkingTable(submittedId,ResourceType.HSPEN.toString(),tableName);
	}
	public static void setWorkingHSPEC(int submittedId,String tableName)throws Exception{
		setWorkingTable(submittedId,ResourceType.HSPEC.toString(),tableName);
	}
	public static String getWorkingHCAF(int submittedId)throws Exception{
		return getWorkingTable(submittedId,ResourceType.HCAF.toString());
	}
	public static String getWorkingHSPEN(int submittedId)throws Exception{
		return getWorkingTable(submittedId,ResourceType.HSPEN.toString());
	}
	public static String getWorkingHSPEC(int submittedId)throws Exception{
		return getWorkingTable(submittedId,ResourceType.HSPEC.toString());
	}

	public static void addToDropTableList(int jobId,String tableName)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();

			List<List<Field>> rows= new ArrayList<List<Field>>();
			List<Field> row=new ArrayList<Field>();
			row.add(new Field(toDropTablesJobId,jobId+"",FieldType.INTEGER));
			row.add(new Field(toDropTablesTableName,tableName,FieldType.STRING));
			rows.add(row);			
			session.insertOperation(toDropTables, rows);
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	public static void addToDeleteTempFolder(int jobId,String folderName)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();

			List<List<Field>> rows= new ArrayList<List<Field>>();
			List<Field> row=new ArrayList<Field>();
			row.add(new Field(tempFoldersJobId,jobId+"",FieldType.INTEGER));
			row.add(new Field(tempFoldersFolderName,folderName,FieldType.STRING));
			rows.add(row);			
			try{
				session.insertOperation(tempFolders, rows);
			}catch(Exception e ){
				logger.error("checking already inserted temp folders..");
				List<Field> filter=new ArrayList<Field>();
				filter.add(new Field(tempFoldersJobId,jobId+"",FieldType.INTEGER));
				boolean found= false;
				for(List<Field> f:Field.loadResultSet(session.executeFilteredQuery(filter, tempFolders, tempFoldersJobId, OrderDirection.ASC))){
					for(Field g:f)
						if(g.name().equals(tempFoldersFolderName)&&g.value().equals(folderName))
							found=true;
				}
				if(!found)logger.warn("Unable to register temp folder "+folderName);
			}
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}

	public static void updateSpeciesStatus(int jobId,String speciesId[],SpeciesStatus status)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<List<Field>> values= new ArrayList<List<Field>>();
			List<List<Field>> keys= new ArrayList<List<Field>>();
			for(String id:speciesId){
				List<Field> value=new ArrayList<Field>();
				value.add(new Field(selectedSpeciesStatus,status+"",FieldType.STRING));			
				values.add(value);

				List<Field> key=new ArrayList<Field>();
				key.add(new Field(selectedSpeciesJobId,jobId+"",FieldType.INTEGER));
				key.add(new Field(selectedSpeciesSpeciesID,id,FieldType.STRING));
				keys.add(key);
			}
			if(values.size()>0)
				session.updateOperation(selectedSpecies, keys, values);
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	public static String[] getSpeciesByStatus(int jobId,SpeciesStatus status)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();

			List<Field> filters= new ArrayList<Field>();
			filters.add(new Field(selectedSpeciesJobId,jobId+"",FieldType.INTEGER));
			if(status!=null) filters.add(new Field(selectedSpeciesStatus,status+"",FieldType.STRING));
			ResultSet rs = session.executeFilteredQuery(filters, selectedSpecies, selectedSpeciesStatus, OrderDirection.ASC);
			ArrayList<String> toReturn=new ArrayList<String>(); 
			while(rs.next()){
				toReturn.add(rs.getString(selectedSpeciesSpeciesID));
			}
			return toReturn.toArray(new String[toReturn.size()]);
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	public static boolean isJobComplete(int jobId) throws Exception{
		DBSession session=null;
		try{
			logger.debug("Checking if "+jobId+" is completed..");
			session=DBSession.getInternalDBSession();
			List<Field> filter=new ArrayList<Field>();
			filter.add(new Field(SubmittedFields.jobid+"",jobId+"",FieldType.INTEGER));
			long count=session.getCount(submittedTable, filter);
			logger.debug("Found "+count+" aquamaps object for jobId ");
			Field statusField=new Field(SubmittedFields.status+"",SubmittedStatus.Error+"",FieldType.STRING);
			filter.add(statusField);
			long errorCount=session.getCount(submittedTable, filter);
			logger.debug("Found "+errorCount+" ERROR aquamaps object for jobId ");
			statusField.value(SubmittedStatus.Completed+"");
			long completedCount=session.getCount(submittedTable, filter);
			logger.debug("Found "+completedCount+" COMPLETED aquamaps object for jobId ");
			return (completedCount+errorCount-count==0);

		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	public static void cleanTemp(int jobId)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			logger.debug("cleaning tables for : "+jobId);
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(toDropTablesJobId,jobId+"",FieldType.INTEGER));
			ResultSet rs=session.executeFilteredQuery(filter, toDropTables, toDropTablesTableName, OrderDirection.ASC);
			while(rs.next()){
				String table=rs.getString(toDropTablesTableName);
				session.dropTable(table);
			}
			session.deleteOperation(toDropTables, filter);

			logger.debug("cleaning folders for : "+jobId);
			rs=session.executeFilteredQuery(filter, tempFolders, tempFoldersFolderName, OrderDirection.ASC);

			while(rs.next()){
				String folder=rs.getString(tempFoldersFolderName);
				try{									
					ServiceUtils.deleteFile(folder);
				}catch(Exception e1){
					logger.debug("unable to delete temp Folder : "+folder,e1);
				}
			}
			session.deleteOperation(tempFolders, filter);

			logger.debug("Cleaning serialized requests / generation data for objects..");
			for(Submitted obj:getObjects(jobId)){
				try{
					if(obj.getSerializedRequest()!=null)ServiceUtils.deleteFile(obj.getSerializedRequest());
				}catch(Exception e){
					logger.warn("Unable to delete file "+obj.getSerializedRequest(), e);
				}
				try{
					Generator.cleanData(obj);
				}catch(Exception e){
					logger.warn("Unable to clean generation data for obj "+obj,e);
				}
			}
			Submitted job=getSubmittedById(jobId);
			try{
				ServiceUtils.deleteFile(job.getSerializedRequest());
			}catch(Exception e){
				logger.warn("Unable to delete serialized file "+job.getSerializedRequest());
			}
			
				logger.debug("cleaning speceisSelection for : "+jobId);
				session.deleteOperation(selectedSpecies, filter);
				logger.debug("cleaning references to working tables for : "+jobId);

				filter=new ArrayList<Field>();
				filter.add(new Field(SubmittedFields.searchid+"",jobId+"",FieldType.INTEGER));
				session.deleteOperation(workingTables, filter);
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}
	public static boolean isSpeciesListReady(int jobId,Set<String> toCheck)throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(selectedSpeciesJobId,jobId+"",FieldType.INTEGER));
			Field idField=new Field(selectedSpeciesSpeciesID,"",FieldType.STRING);
			filter.add(idField);
			for(String id : toCheck){
				idField.value(id);
				ResultSet rs= session.executeFilteredQuery(filter, selectedSpecies, selectedSpeciesSpeciesID, OrderDirection.ASC);
				if(rs.next()){
					if(!rs.getString(selectedSpeciesStatus).equalsIgnoreCase(SpeciesStatus.Ready+""))
						return false;
				}else throw new Exception("SpeciesID "+id+" not found in jobId "+jobId+" selection");
			}
			return true;
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}








	public static boolean isSpeciesSetCustomized(int submittedId,Set<String> ids)throws Exception{
		DBSession session=null;		
		try{
			logger.trace("Checking species customizations flag..");
			session=DBSession.getInternalDBSession();

			List<Field> filter= new ArrayList<Field>();
			filter.add(new Field(selectedSpeciesJobId,submittedId+"",FieldType.INTEGER));
			Field idField=new Field(selectedSpeciesSpeciesID,"",FieldType.STRING);
			filter.add(idField);
			for(String id : ids){
				idField.value(id);
				ResultSet rs= session.executeFilteredQuery(filter, selectedSpecies, selectedSpeciesSpeciesID, OrderDirection.ASC);
				if(rs.next()){
					if(rs.getInt(selectedSpeciesIsCustomized)==0)
						return false;
				}else throw new Exception("SpeciesID "+id+" not found in jobId "+submittedId+" selection");
			}

			return true;
		}catch(Exception e){
			logger.error("unable to check species customization flag",e);
			throw e;
		}finally{
			if(session!=null) session.close();
		}
	}





	/**Creates a new entry in Job table and AquaMap table (for every aquamap object in job)
	 * 
	 * @return new job id
	 */
	public static Job insertNewJob(Job toPerform) throws Exception{
		//		logger.trace("Creating new pending Job");
		DBSession session=null;

		////*************** Send to publisher
		try{

			session=DBSession.getInternalDBSession();
			session.disableAutoCommit();

			////*************** Insert references into local DB
			logger.trace("Inserting references into internal DB...");


			//Uncomment here to insert job references

			

			PreparedStatement ps=null;
			PreparedStatement psCompleted=null;
			List<Field> row=null;
			ResultSet rs=null;
			Submitted submittedJob=getSubmittedById(toPerform.getId());
			logger.debug("Submitted Job is "+submittedJob.toXML());
			
			for(AquaMapsObject obj : toPerform.getAquaMapsObjectList()){
				
				
				//************* CHECK IF CUSTOMIZED
				boolean customized=false;
				for(Species s: obj.getSelectedSpecies()){
					if(toPerform.getEnvelopeCustomization().containsKey(s.getId())||
							toPerform.getEnvelopeWeights().containsKey(s.getId())){
						customized=true;
						break;
					}
				}
				String serializedObjectPath=ServiceContext.getContext().getFolderPath(FOLDERS.SERIALIZED)+File.separator+ServiceUtils.generateId("OBJ", ".xml");
				AquaMapsXStream.serialize(serializedObjectPath, obj);
				String speciesCoverage=obj.getCompressedSpeciesCoverage();
				row=new ArrayList<Field>();
				row.add(submittedJob.getField(SubmittedFields.author));
				row.add(new Field(SubmittedFields.gisenabled+"",obj.getGis()+"",FieldType.BOOLEAN));
				row.add(new Field(SubmittedFields.isaquamap+"",true+"",FieldType.BOOLEAN));
				row.add(new Field(SubmittedFields.jobid+"",submittedJob.getSearchId()+"",FieldType.INTEGER));
				row.add(new Field(SubmittedFields.saved+"",false+"",FieldType.BOOLEAN));
				row.add(submittedJob.getField(SubmittedFields.sourcehcaf));
				row.add(submittedJob.getField(SubmittedFields.sourcehspec));
				row.add(submittedJob.getField(SubmittedFields.sourcehspen));
				row.add(submittedJob.getField(SubmittedFields.submissiontime));
				row.add(new Field(SubmittedFields.title+"",obj.getName(),FieldType.STRING));
				row.add(new Field(SubmittedFields.type+"",obj.getType()+"",FieldType.STRING));				
				row.add(new Field(SubmittedFields.iscustomized+"",customized+"",FieldType.BOOLEAN));
				row.add(new Field(SubmittedFields.speciescoverage+"",speciesCoverage,FieldType.STRING));
				row.add(new Field(SubmittedFields.serializedobject+"",serializedObjectPath,FieldType.STRING));
				row.add(new Field(SubmittedFields.todelete+"",false+"",FieldType.BOOLEAN));
				row.add(submittedJob.getField(SubmittedFields.forceregeneration));
				if(!customized&&!submittedJob.isForceRegeneration()){
					
					//***************CHECK IF EXISTING LAYERS / IMG
					
					List<Submitted> alreadyGenerated=AquaMapsManager.getObjectsByCoverage(toPerform.getSourceHSPEC().getSearchId(), speciesCoverage,obj.getGis(), false);
					logger.debug("Found "+alreadyGenerated.size()+" generated objects");

					if(alreadyGenerated.size()>0){
						// ****** FOUND EVERYTHING
						Submitted toUse=alreadyGenerated.get(0);
						row.add(toUse.getField(SubmittedFields.filesetid));
						if(obj.getGis())							
							row.add(toUse.getField(SubmittedFields.gispublishedid));
						
						row.add(new Field(SubmittedFields.starttime+"",System.currentTimeMillis()+"",FieldType.LONG));
						row.add(new Field(SubmittedFields.endtime+"",System.currentTimeMillis()+"",FieldType.LONG));
						row.add(new Field(SubmittedFields.status+"",SubmittedStatus.Completed+"",FieldType.STRING));
						obj.setStatus(SubmittedStatus.Completed);
					}else{
						//NEED TO GENERATE
						if(obj.getGis()){
							logger.debug("No GIS already generated, looking for img fileset..");
							alreadyGenerated=AquaMapsManager.getObjectsByCoverage(toPerform.getSourceHSPEC().getSearchId(), obj.getCompressedSpeciesCoverage(),false, false);
							logger.debug("Found "+alreadyGenerated.size()+" generated objects");
							if(alreadyGenerated.size()>0){
								Submitted toUse=alreadyGenerated.get(0);
								row.add(toUse.getField(SubmittedFields.filesetid));							
							}
						}
						row.add(new Field(SubmittedFields.status+"",SubmittedStatus.Pending+"",FieldType.STRING));
					}					
				}else //************** CUSTOMIZED, GENERATE EVERYTHING 
					row.add(new Field(SubmittedFields.status+"",SubmittedStatus.Pending+"",FieldType.STRING));
				
				if(obj.getStatus().equals(SubmittedStatus.Completed)){
					if(psCompleted==null)psCompleted=session.getPreparedStatementForInsert(row, submittedTable);
					session.fillParameters(row,0, psCompleted).executeUpdate();
					rs=psCompleted.getGeneratedKeys();
				}else{
					if(ps==null)ps=session.getPreparedStatementForInsert(row, submittedTable);
					session.fillParameters(row,0, ps).executeUpdate();
					rs=ps.getGeneratedKeys();
				}
				rs.next();
				obj.setId(rs.getInt(SubmittedFields.searchid+""));
			}
			session.commit();
			
			if(isJobComplete(toPerform.getId())){
				setStartTime(toPerform.getId());				
				toPerform.setStatus(SubmittedStatus.Completed);
				logger.debug("All objects completed");
				if(toPerform.getIsGis()){
					//NB Groups are not used anymore
					
//					ServiceContext.getContext().getPublisher().store(WMSContext.class,  new Generator<WMSContext>(new WMSGenerationRequest(toPerform.getId())){
//						@Override
//						public WMSContext generate() throws Exception {
//							return generateWMSContext(((WMSGenerationRequest)request).getJobId());
//						}
//					}, new StoreConfiguration(StoreMode.USE_EXISTING, 
//							new UpdateConfiguration(true, true, true)));
				}				
				updateStatus(toPerform.getId(), SubmittedStatus.Completed);
			}else{
				//Initialize working variables 
				if((toPerform.getSelectedSpecies().size()>0)){

					boolean hasPerturbation=false;
					if((toPerform.getEnvelopeCustomization().size()>0)) hasPerturbation=true;

					boolean hasWeight=false;
					if((toPerform.getEnvelopeWeights().size()>0)) hasWeight=true;


					List<Field> fields=new ArrayList<Field>();
					fields.add(new Field(selectedSpeciesJobId,"",FieldType.INTEGER));
					fields.add(new Field(selectedSpeciesSpeciesID,"",FieldType.STRING));
					fields.add(new Field(selectedSpeciesStatus,"",FieldType.STRING));
					fields.add(new Field(selectedSpeciesIsCustomized,"",FieldType.BOOLEAN));

					PreparedStatement psSpecies=session.getPreparedStatementForInsert(fields, selectedSpecies);
					fields.get(0).value(toPerform.getId()+"");
					for(Species s:toPerform.getSelectedSpecies()){
						String status=SpeciesStatus.Ready.toString();
						if((hasWeight)&&(toPerform.getEnvelopeWeights().containsKey(s.getId())))status=SpeciesStatus.toGenerate.toString();
						if((hasPerturbation)&&(toPerform.getEnvelopeCustomization().containsKey(s.getId())))status=SpeciesStatus.toCustomize.toString();
						fields.get(1).value(s.getId());
						fields.get(2).value(status);
						fields.get(3).value((hasWeight||hasPerturbation)+"");
						psSpecies=session.fillParameters(fields,0, psSpecies);
						psSpecies.executeUpdate();
					}
				}else throw new Exception("Invalid job, no species found");


				//Setting selected sources as working tables

				setWorkingHCAF(toPerform.getId(), SourceManager.getSourceName(toPerform.getSourceHCAF().getSearchId()));
				setWorkingHSPEC(toPerform.getId(), SourceManager.getSourceName(toPerform.getSourceHSPEC().getSearchId()));
				setWorkingHSPEN(toPerform.getId(), SourceManager.getSourceName(toPerform.getSourceHSPEN().getSearchId()));

				AquaMapsXStream.serialize(submittedJob.getSerializedRequest(), toPerform);
			}
			session.commit();
			return toPerform;
		}catch (Exception e){
			throw e;
		}finally {
			if(session!=null) session.close();
		}
	}



	public static List<Submitted> getObjects (int jobId)throws Exception{
		List<Field> filters=new ArrayList<Field>();
		Field jobIdField=new Field();
		jobIdField.value(jobId+"");
		jobIdField.type(FieldType.INTEGER);
		jobIdField.name(SubmittedFields.jobid+"");
		filters.add(jobIdField);
		return getList(filters);
	}
}
