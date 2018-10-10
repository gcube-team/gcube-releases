package org.gcube.application.datamanagementfacilityportlet.servlet;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;

import java.io.File;

import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.Resource;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.utils.CSVUtils;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationProgress;
import org.gcube.portlets.user.csvimportwizard.client.progress.OperationState;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVParserConfiguration;
import org.gcube.portlets.user.csvimportwizard.server.csv.CSVTarget;
import org.gcube.portlets.user.csvimportwizard.shared.HeaderPresence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CSVImporter implements CSVTarget {
	
	
	
	private static final Logger logger = LoggerFactory.getLogger(CSVImporter.class);

	
	@Override
	public void importCSV(HttpSession httpsession, File csvFile, String fileName, CSVParserConfiguration parserConfiguration, boolean[] columnToImportMask, final OperationProgress operationProgress) {							
				operationProgress.setState(OperationState.INPROGRESS);
				
				try{
					
					final ASLSession session=Utils.getSession(httpsession);
					ScopeProvider.instance.set(session.getScope().toString());
					logger.trace("Sending csvFile to service for user "+session.getUsername());
					final Long totalCount=CSVUtils.countCSVRows(csvFile.getAbsolutePath(), parserConfiguration.getDelimiter(), parserConfiguration.isHasHeader());
					operationProgress.setTotalLenght(totalCount);
//					final Integer importingId=Utils.get(path).getDMService(request.getSession()).importResource(csvFile, request.getSession().getUsername(), request.getType());
					Boolean[] mask=new Boolean[columnToImportMask.length];
					for(int i=0;i<columnToImportMask.length;i++)mask[i]=columnToImportMask[i];
					ResourceType toImportType=(ResourceType) session.getAttribute(Tags.TO_IMPORT_TYPE);
					final DataManagement dm=dataManagement().build();
					final Integer importingId=dm.asyncImportResource(
							csvFile, session.getUsername(), toImportType, parserConfiguration.getCharset().toString(), mask, parserConfiguration.isHasHeader(), parserConfiguration.getDelimiter());
					
					
					Thread updater=new Thread(){
						@Override
						public void run() {
							try{
								ScopeProvider.instance.set(session.getScope().toString());
								logger.trace("CSVImport monitor thread started, id is "+importingId);
								boolean completed=false;
								while(!completed){									
									Resource importing =dm.loadResource(importingId);
									logger.trace("Importing resource status : "+importing);
									switch(importing.getStatus()){
										case Completed: completed=true;
														operationProgress.setElaboratedLenght(totalCount);
														operationProgress.setState(OperationState.COMPLETED);
														break;
										case Error: 	completed=true;
														operationProgress.setState(OperationState.FAILED);
														operationProgress.setFailed("Import failed on server.", "Please, check selected resource type to import.");
														break;
										default:	completed=importing.getRowCount()==totalCount.intValue();
													operationProgress.setElaboratedLenght(importing.getRowCount());
													logger.debug("Elaborated "+importing.getRowCount()+" / "+totalCount+" operationProgress is "+operationProgress);
													break;
									}									
									try{ Thread.sleep(2*1000);	}catch(InterruptedException e){}
								}
								operationProgress.setState(OperationState.COMPLETED);
							}catch(Exception e){
								logger.error("Unexpected Error while updateing status",e);
								operationProgress.setState(OperationState.FAILED);
								operationProgress.setFailed("Unable to update progress", e.getMessage());								
							}
						}
					};
					updater.start();					
				}catch(Exception e){
					logger.trace("Unable to complete",e);
					operationProgress.setState(OperationState.FAILED);
					e.printStackTrace();
					operationProgress.setFailed("Unable to import", e.getMessage());
				}finally{
					try{
						FileUtils.forceDelete(csvFile);
					}catch(Throwable t){logger.trace("Unable to delete "+csvFile,t);}
				}
			}


	@Override
	public String getId() {
		return Tags.CSV_TARGET;
	}
	}

