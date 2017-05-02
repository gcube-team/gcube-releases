package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers;

import java.io.File;
import java.io.FileWriter;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.client.Constants;
import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.DBSession;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.BulkStatus;
import org.gcube.application.aquamaps.aquamapsservice.stubs.GetBulkUpdatesStatusResponseType;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BulkItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.ItemResources;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SpeciesOccursumFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.publisher.Publisher;
import org.gcube.application.aquamaps.publisher.impl.model.FileSet;
import org.gcube.application.aquamaps.publisher.impl.model.Layer;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.blobstorage.service.IClient;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;
import org.gcube.contentmanager.storageclient.wrapper.MemoryType;
import org.gcube.contentmanager.storageclient.wrapper.StorageClient;
import org.gcube_system.namespaces.application.aquamaps.types.OrderDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BulkReportsManager {

	final static Logger logger= LoggerFactory.getLogger(BulkReportsManager.class);

	private static final String tablename="bulkrequests";

	private static final String remoteFileField="remoteid";
	private static final String scopeField="scope";
	private static final String includeGisField="includegis";
	private static final String includeCustomField="includecustom";
	private static final String lowerintervalField="lowerinterval";
	private static final String searchidField="searchid";
	private static final String statusField="status";
	private static final String submissionTimeField="submissionTime";


	static{
		new BulkMonitorThread().start();
	}


	public static String insertRequest(Long lowerInterval,boolean includeGis, boolean includeCustom) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> toAdd=new ArrayList<Field>();
			String id=ServiceUtils.generateId("B", "");
			toAdd.add(new Field(searchidField,id,FieldType.STRING));
			toAdd.add(new Field(scopeField,ServiceContext.getContext().getScope().toString()));
			toAdd.add(new Field(includeGisField,includeGis+"",FieldType.BOOLEAN));
			toAdd.add(new Field(includeCustomField,includeCustom+"",FieldType.BOOLEAN));
			toAdd.add(new Field(lowerintervalField,lowerInterval+"",FieldType.LONG));
			toAdd.add(new Field(statusField,BulkStatus.PENDING+"",FieldType.STRING));
			toAdd.add(new Field(submissionTimeField,System.currentTimeMillis()+"",FieldType.LONG));
			ArrayList<List<Field>> rows=new ArrayList<List<Field>>();
			rows.add(toAdd);
			session.insertOperation(tablename, rows);
			return id;
		}finally{
			if(session!=null) session.close();
		}
	}


	public static GetBulkUpdatesStatusResponseType getStatus(String id) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter=new ArrayList<Field>();
			filter.add(new Field(searchidField,id,FieldType.STRING));
			ResultSet rs=session.executeFilteredQuery(filter, tablename, searchidField, OrderDirection.ASC);
			if(rs.next()){
				return new GetBulkUpdatesStatusResponseType(rs.getString(remoteFileField), org.gcube.application.aquamaps.aquamapsservice.stubs.BulkStatus.fromString(rs.getString(statusField)));
			}else throw new Exception ("Bulk request with id "+id+" not found");
		}finally{
			if(session!=null) session.close();
		}
	}



	public static List<Field> getFirstPending() throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> filter=new ArrayList<Field>();
			filter.add(new Field(statusField,BulkStatus.PENDING+"",FieldType.STRING));			
			ResultSet rs=session.executeFilteredQuery(filter, tablename, submissionTimeField, OrderDirection.ASC);
			if(rs.next()){
				return Field.loadRow(rs);
			}else return null;
		}finally{
			if(session!=null) session.close();
		}
	}


	public static void update(String id, BulkStatus status, String remoteId) throws Exception{
		DBSession session=null;
		try{
			session=DBSession.getInternalDBSession();
			List<Field> key=new ArrayList<Field>();
			key.add(new Field(searchidField,id,FieldType.STRING));
			ArrayList<List<Field>> keys=new ArrayList<List<Field>>();
			keys.add(key);


			List<Field> toSetValue=new ArrayList<Field>();
			toSetValue.add(new Field(statusField,status+"",FieldType.STRING));
			toSetValue.add(new Field(remoteFileField,remoteId,FieldType.STRING));
			ArrayList<List<Field>> rows=new ArrayList<List<Field>>();
			rows.add(toSetValue);

			session.updateOperation(tablename, keys, rows);			

		}finally{
			if(session!=null) session.close();
		}
	}


	/**
	 * Returns the remote path 
	 * 
	 * @param user
	 * @param updateInterval
	 * @return
	 * @throws Exception 
	 */
	public static String prepareBulk(Long updateInterval,String scope, boolean includeGIS, boolean includeCustom) throws Exception{
		logger.debug("Preparing bulk updates from time "+ServiceUtils.formatTimeStamp(updateInterval));
		DBSession session=null;
		Publisher pub=ServiceContext.getContext().getPublisher();
		String publisherHost=ServiceContext.getContext().getPublisher().getWebServerUrl();		

		File local=File.createTempFile("bulk", ".xml");
		AquaMapsXStream xstream=AquaMapsXStream.getXMLInstance();			
		ObjectOutputStream oos=	xstream.createObjectOutputStream(new FileWriter(local));
		try{
			long count=0;
			long speciesCount=0;
			// Get Species List 
			session=DBSession.getInternalDBSession();
			ResultSet rsSpecies=session.executeQuery(
					"Select "+SpeciesOccursumFields.speciesid+","+SpeciesOccursumFields.genus+","+SpeciesOccursumFields.species+" from speciesoccursum");
			logger.debug("Got Species list, gonna check maps..");
			while(rsSpecies.next()){
				String speciesId=rsSpecies.getString(1);
				String scientificName=rsSpecies.getString(2)+"_"+rsSpecies.getString(3);
				BulkItem item=new BulkItem();
				item.setSpeciesId(speciesId);
				try{
					//Check - add fileset
					List<FileSet> fileSets=pub.getFileSetsBySpeciesIds(scientificName);
					//					logger.debug(String.format("Got %s filesets for %s (%s), checking generation time..",fileSets.size(),scientificName,speciesId));
					for(FileSet fs:fileSets){
						try{
							if(!includeCustom||(includeCustom&&fs.isCustomized())){
								if(fs.getMetaInfo().getDataGenerationTime() >= updateInterval){
									Integer hspecId=Integer.parseInt(fs.getTableId());
									if(!item.getResources().containsKey(hspecId)) item.getResources().put(hspecId, new ItemResources());
									String uri=null;
									for(org.gcube.application.aquamaps.publisher.impl.model.File f: fs.getFiles()){
										if(uri==null||f.getName().equals("Earth")){											
											uri=publisherHost+f.getStoredUri();

										}
									}
									item.getResources().get(hspecId).addResource(false, fs.isCustomized(), uri);
								}
							}
						}catch(Throwable t){
							logger.debug("Unable to check fs "+fs.getId(),t);
						}
					}

					// Check - add gis
					if(includeGIS){
						List<Layer> foundLayers=pub.getLayersBySpeciesIds(scientificName);
						//						logger.debug(String.format("Got %s layers for %s (%s), checking generation time..",foundLayers.size(),scientificName,speciesId));
						for(Layer l:foundLayers){
							try{
								if(!includeCustom||(includeCustom&&l.isCustomized())){
									if(l.getMetaInfo().getDataGenerationTime() >= updateInterval){
										Integer hspecId=Integer.parseInt(l.getTableId());
										if(!item.getResources().containsKey(hspecId)) item.getResources().put(hspecId, new ItemResources());
										item.getResources().get(hspecId).addResource(true, l.isCustomized(), l.getId());
									}
								}
							}catch(Throwable t){
								logger.debug("Unable to check layer "+l.getId(),t);
							}
						}
					}
					if(item.hasResources()){
						oos.writeObject(item);
						count++;
					}			
					speciesCount++;
				}catch(Exception e){
					logger.error("Unable to gather information for species "+speciesId,e);
				}
			}
			oos.flush();
			oos.close();
			oos=null;
			logger.debug("Serialized "+count+" bulk itmes out of "+speciesCount+" species into local file "+local.getAbsolutePath());
			ScopeProvider.instance.set(scope);
			IClient client=new StorageClient(Constants.SERVICE_CLASS, Constants.SERVICE_NAME, Constants.SERVICE_NAME, AccessType.SHARED, MemoryType.VOLATILE).getClient();
			String remoteId=client.put(true).LFile(local.getAbsolutePath()).RFile("/img/"+local.getName());
			logger.debug("Created remote file "+remoteId);
			return remoteId;
		}finally{
			if(session!=null)session.close();
			if(oos!=null)oos.close();
		}


	}

	//************************************** Monitor Thread


	public static class BulkMonitorThread extends Thread{

		public BulkMonitorThread() {
			super();
			this.setName("BulkMonitorThread");			
		}


		@Override
		public void run() {
			while(true){
				//**************Check for old/invalid
				try{
					String scope=null;
					Long lowerInterval=null;
					String id=null;
					boolean includeGIS=false;
					boolean includeCustom=false;
					List<Field> pending=getFirstPending();
					if(pending!=null){
						logger.debug("found pending bulk request.. "+pending);
						for(Field f:pending){
							if(f.name().equalsIgnoreCase(includeCustomField))includeCustom=f.getValueAsBoolean();
							else if(f.name().equalsIgnoreCase(includeGisField))includeGIS=f.getValueAsBoolean();
							else if(f.name().equalsIgnoreCase(scopeField))scope=f.value();
							else if(f.name().equalsIgnoreCase(lowerintervalField))lowerInterval=f.getValueAsLong();
							else if(f.name().equalsIgnoreCase(searchidField))id=f.value();
						}					
						update(id, BulkStatus.ONGOING,"");
						try{
							String remoteId=prepareBulk(lowerInterval, scope, includeGIS, includeCustom);
							update(id, BulkStatus.COMPLETED,remoteId);
						}catch(Exception e){
							logger.error("Unable to prepare bulk ",e);
							update(id, BulkStatus.ERROR,"");
						}
					}
				}catch(Exception e){
					logger.error("Unexpected Exception",e);
				}finally{
					try{
						Thread.sleep(60*1000);
					}catch(InterruptedException e){}
				}
			}
		}

	}



}
