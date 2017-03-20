package org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.threads;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsservice.impl.ServiceContext;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.CustomQueryManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.db.managers.SubmittedManager;
import org.gcube.application.aquamaps.aquamapsservice.impl.publishing.Generator;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.PropertiesConstants;
import org.gcube.application.aquamaps.aquamapsservice.impl.util.ServiceUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Submitted;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.fields.SubmittedFields;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.FieldType;
import org.gcube.application.aquamaps.publisher.Publisher;
import org.gcube.application.aquamaps.publisher.impl.model.FileSet;
import org.gcube.application.aquamaps.publisher.impl.model.Layer;
import org.gcube.application.aquamaps.publisher.impl.model.WMSContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeletionMonitor extends Thread {

	final static Logger logger= LoggerFactory.getLogger(DeletionMonitor.class);

	private long interval;

	final static Publisher publisher=ServiceContext.getContext().getPublisher();

	private static Generator<FileSet> fileSetDestroyer=new Generator<FileSet>(null,FileSet.class);

	private static Generator<Layer> layerDestroyer=new Generator<Layer>(null,Layer.class);

	private static Generator<WMSContext> wmsContextDestroyer=new Generator<WMSContext>(null,WMSContext.class);




	public DeletionMonitor(long interval) {
		super("DELETION");
		this.interval=interval;
	}

	@Override
	public void run() {
		while(true){
			try{

				ArrayList<Field> toDeleteFilter=new ArrayList<Field>();
				toDeleteFilter.add(new Field(SubmittedFields.todelete+"",true+"",FieldType.BOOLEAN));
				List<Submitted> foundList=SubmittedManager.getList(toDeleteFilter);
				if(foundList.size()>0)logger.trace("Found "+foundList.size()+" to deleteObjects");
				for(Submitted toDeleteSubmitted:foundList){
					try{
						long start=System.currentTimeMillis();
						logger.debug("Deleting submitted "+toDeleteSubmitted);
						if(toDeleteSubmitted.getIsAquaMap()){
							boolean deleteLayer=toDeleteSubmitted.getGisEnabled();
							boolean deleteFileSet=true;
							if(!toDeleteSubmitted.getIsCustomized()){
								ArrayList<Field> fileSetFilter=new ArrayList<Field>();
								fileSetFilter.add(new Field(SubmittedFields.filesetid+"",toDeleteSubmitted.getFileSetId(),FieldType.STRING));
								for(Submitted found:SubmittedManager.getList(fileSetFilter)){
									if(!found.getSearchId().equals(toDeleteSubmitted.getSearchId())) {
										deleteFileSet=false;
										break;
									}
								}
								ArrayList<Field> gisFilter=new ArrayList<Field>();
								gisFilter.add(new Field(SubmittedFields.gispublishedid+"",toDeleteSubmitted.getGisPublishedId(),FieldType.STRING));							
								for(Submitted found:SubmittedManager.getList(gisFilter)){
									if(!found.getSearchId().equals(toDeleteSubmitted.getSearchId())) {
										deleteLayer=false;
										break;
									}
								}
							}
							if(deleteFileSet&&toDeleteSubmitted.getFileSetId()!=null&&!toDeleteSubmitted.getFileSetId().equalsIgnoreCase("null"))
								try{								
									publisher.deleteById(FileSet.class, fileSetDestroyer, toDeleteSubmitted.getFileSetId());
								}catch(Exception e){
									logger.warn("Unable to delete FileSet "+toDeleteSubmitted.getFileSetId(),e);
								}
								if(deleteLayer&&toDeleteSubmitted.getGisPublishedId()!=null&&!toDeleteSubmitted.getGisPublishedId().equalsIgnoreCase("null"))
									try{ 
										publisher.deleteById(Layer.class,layerDestroyer,toDeleteSubmitted.getGisPublishedId());
									}catch(Exception e){
										logger.warn("Unable to delete Layer "+toDeleteSubmitted.getGisPublishedId(),e);
									}
						}else {						
							try{
								publisher.deleteById(WMSContext.class,wmsContextDestroyer,toDeleteSubmitted.getGisPublishedId());
							}catch(Exception e){
								logger.warn("Unable to delete WMS "+toDeleteSubmitted.getGisPublishedId(),e);
							}
						}
						try{
							ServiceUtils.deleteFile(toDeleteSubmitted.getSerializedRequest());
						}catch(Exception e){
							logger.warn("Unable to delete File "+toDeleteSubmitted.getSerializedRequest());
						}
						try{
							ServiceUtils.deleteFile(toDeleteSubmitted.getSerializedObject());
						}catch(Exception e){
							logger.warn("Unable to delete File "+toDeleteSubmitted.getSerializedObject());
						}
						SubmittedManager.deleteFromTables(toDeleteSubmitted.getSearchId());					
						logger.debug("Deleted in "+(System.currentTimeMillis()-start));
					}catch(Exception e){
						logger.warn("Unable to delete submitted "+toDeleteSubmitted,e);
					}
				}
				if(ServiceContext.getContext().getPropertyAsBoolean(PropertiesConstants.CUSTOM_QUERY_DELETION)){
					try{
						int deletedCustomQueries=CustomQueryManager.clean();
						if(deletedCustomQueries>0)logger.trace("Deleted "+deletedCustomQueries+" custom queries");
					}catch(Exception e){
						logger.warn("Unable to clean custom queries");
						logger.debug("Exception was ",e);
					}}
			}catch(Exception e){
				logger.warn("UNEXPECTED EXCEPTION",e);
			}
			finally{			
				try{
					Thread.sleep(interval);			
				}catch(InterruptedException e){
					//WAKE UP
				}
			}
		}
	}


}
