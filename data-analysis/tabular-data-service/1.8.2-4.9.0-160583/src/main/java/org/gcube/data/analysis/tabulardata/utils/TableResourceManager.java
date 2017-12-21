package org.gcube.data.analysis.tabulardata.utils;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.commons.webservice.OperationManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TabularResourceManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.TaskManager;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.ExecuteRequest;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResource;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.TabularResourceType;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.operations.OperationExecution;
import org.gcube.data.analysis.tabulardata.commons.webservice.types.tasks.TaskInfo;
import org.gcube.data.analysis.tabulardata.operation.resource.TableImportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class TableResourceManager {

	private static Logger log = LoggerFactory.getLogger(TableResourceManager.class);
	
	@Inject
	TabularResourceManager trManager;
	
	@Inject
	OperationManager opManager;
	
	@Inject
	TaskManager taskManager;
	
	public void createTableResource(@Observes ResourceCreated resourceCreatedEvent){
		log.info("received creation event for resource "+resourceCreatedEvent.getName()+" of "+resourceCreatedEvent.getOwner());
		
		if (resourceCreatedEvent.getResource()==null){
			log.error("received creation event for a resource null");
			return;
		}
		
		//TODO: understand if deleting the following line mess-up evrething
		//AuthorizationProvider.instance.set(new AuthorizationToken(resourceCreatedEvent.getOwner()));
		try{
			TabularResource tr = trManager.createTabularResource(TabularResourceType.STANDARD);
			tr.setName(resourceCreatedEvent.getName());
			trManager.updateTabularResource(tr);
			
			Map<String, Object> instanceMap = new HashMap<String, Object>();
			instanceMap.put(TableImportFactory.targetTableImportParameter.getIdentifier(), (Object)resourceCreatedEvent.getResource().getTableId());
			instanceMap.put(TableImportFactory.useExistingTableParameter.getIdentifier(), true);
			OperationExecution opExecution =  new OperationExecution(102, instanceMap);
			TaskInfo info = opManager.execute(new ExecuteRequest(tr.getId(),opExecution));
			log.info("resource creation task started with task id "+info.getIdentifier());
			String[] requestIdentifier = new String[]{info.getIdentifier()};
			long start = System.currentTimeMillis();
			while (!info.getStatus().isFinal()){
				Thread.sleep(200);
				info = taskManager.get(requestIdentifier).get(0);
			}
			log.info("task finished "+info.getIdentifier()+" in "+(System.currentTimeMillis()-start)+" millis");
				
		}catch(Exception e){
			log.error("error trying to create TR from a resource",e);
			return;
		}
	}
	
	
}
