package org.gcube.application.aquamaps.publisher.impl.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.publisher.MetaInformations;
import org.gcube.application.aquamaps.publisher.impl.PublisherImpl;
import org.gcube.application.aquamaps.publisher.impl.model.searchsupport.FileSetSpeciesIdPair;
import org.gcube.application.aquamaps.publisher.impl.model.searchsupport.LayerSpeciesIdPair;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.dbinterface.Specification;
import org.gcube.common.dbinterface.persistence.ObjectPersistency;
import org.gcube.common.dbinterface.persistence.PersistencyCallback;
import org.gcube.common.dbinterface.persistence.annotations.FieldDefinition;
import org.gcube.common.dbinterface.persistence.annotations.TableRootDefinition;

@TableRootDefinition
public class FileSet extends CoverageDescriptor implements Storable{

	private static GCUBELog logger= new GCUBELog(FileSet.class);
	
	static{
		try{
			ObjectPersistency.get(FileSet.class).addCallback(new PersistencyCallback<FileSet>() {

				public void onObjectDeleted(FileSet obj) {
					for (File file: obj.getFiles())
						try{
							file.unpublish();
						}catch (Exception e) {
							logger.warn("error publisshing file",e);
						}
					java.io.File path = new java.io.File(obj.getStoringPath());
					while (path.getParent()!=null)
						path = path.getParentFile();
					try{
						FileUtils.deleteDirectory(path);
					}catch(Exception e){logger.warn("error deleting directory",e);}
					
					try{
						ObjectPersistency.get(LayerSpeciesIdPair.class).deleteByValue("id", obj.getId());
					}catch(Exception e){logger.warn("error deleting layer speciesId relation",e);}
					try{
						ObjectPersistency.get(FileSetSpeciesIdPair.class).deleteByValue("id", obj.getId());
					}catch(Exception e){logger.warn("error removing fileset speciesId relation",e);}	
				}
				
				public void onBeforeStore(FileSet obj) {
					java.io.File dir = new java.io.File(PublisherImpl.serverPathDir.getAbsolutePath()+java.io.File.separator+obj.getStoringPath());
					dir.mkdirs();
					logger.trace("publishing files in dir "+dir.getAbsolutePath());
					for (File file: obj.getFiles())
						try{
							logger.trace("publishing file "+file.getName());
							file.publish(obj.getStoringPath(),false);
							logger.trace(file.getName()+" published");
						}catch (Exception e) {
							logger.warn("error publishing file "+file.getName(),e);
						}
				}
				
				public void onObjectUpdated(FileSet obj) {
					java.io.File dir = new java.io.File(PublisherImpl.serverPathDir.getAbsolutePath()+java.io.File.separator+obj.getStoringPath());
					if (!dir.exists())dir.mkdirs();
					for (File file: obj.getFiles())
						try{
							if (file.getOriginalUri()!=null)
								file.publish(obj.getStoringPath(),true);
						}catch (Exception e) {
							logger.warn("error publishing file "+file.getName(),e);
						}
					
					try{
						ObjectPersistency.get(FileSetSpeciesIdPair.class).deleteByValue("id", obj.getId());
						for (String speciesId : obj.getSpeciesIds())
							ObjectPersistency.get(FileSetSpeciesIdPair.class).insert(new FileSetSpeciesIdPair(speciesId, obj.getId()));
					}catch(Exception e){logger.warn("error updating fileset speciesId relation",e);}
				}

				/* (non-Javadoc)
				 * @see org.gcube.common.dbinterface.persistence.PersistencyCallback#onObjectLoaded(java.lang.Object)
				 */
				@Override
				public void onObjectLoaded(FileSet obj) {
					ArrayList<String> toSet = new ArrayList<String>();
					try{
						Iterator<FileSetSpeciesIdPair> it = ObjectPersistency.get(FileSetSpeciesIdPair.class).getObjectByField("id", obj.getId()).iterator();
						while (it.hasNext())
							toSet.add(it.next().getSpeciesId());
					}catch(Exception e){ toSet = new ArrayList<String>();}
					obj.setSpeciesIds(toSet.toArray(new String[toSet.size()]));
				}

				/* (non-Javadoc)
				 * @see org.gcube.common.dbinterface.persistence.PersistencyCallback#onObjectStored(java.lang.Object)
				 */
				@Override
				public void onObjectStored(FileSet obj) {
					try{
						for (String speciesId : obj.getSpeciesIds())
							ObjectPersistency.get(FileSetSpeciesIdPair.class).insert(new FileSetSpeciesIdPair(speciesId, obj.getId()));
					}catch(Exception e){logger.warn("error storing fileset speciesId relation",e);}
				}
				
				
			});
		}catch (Exception e) {
			logger.error("error adding callback",e);
		}
	}
			
	
	
	@FieldDefinition(precision={40}, specifications={Specification.NOT_NULL, Specification.PRIMARY_KEY})
	private String id;
		
	@FieldDefinition()
	private List<File> files;
	
	@FieldDefinition()
	private MetaInformations metaInfo;
	
	@FieldDefinition()
	private String storingPath;
	
	@SuppressWarnings("unused")
	private FileSet(){}
	
	private String[] speciesIds;
	
	public FileSet(List<File> files, CoverageDescriptor coverage, String storingPath, MetaInformations metainfo) {
		super(coverage.getTableId(), coverage.getParameters());
		this.storingPath = storingPath;
		this.id= UUIDGenFactory.getUUIDGen().nextUUID();
		this.files = files;
		this.metaInfo = metainfo;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the files
	 */
	public List<File> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<File> files) {
		this.files = files;
	}

	/**
	 * @return the metaInfo
	 */
	public MetaInformations getMetaInfo() {
		return metaInfo;
	}


	/**
	 * @return the storingPath
	 */
	public String getStoringPath() {
		return storingPath;
	}
	
	/**
	 * @return the speciesIds
	 */
	public String[] getSpeciesIds() {
		return speciesIds;
	}

	/**
	 * @param speciesIds the speciesIds to set
	 */
	public void setSpeciesIds(String[] speciesIds) {
		this.speciesIds = speciesIds;
	}

	
}
