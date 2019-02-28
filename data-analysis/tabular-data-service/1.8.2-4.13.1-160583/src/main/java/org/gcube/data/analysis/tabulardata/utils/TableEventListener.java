package org.gcube.data.analysis.tabulardata.utils;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.analysis.tabulardata.cube.events.TableCreationEvent;
import org.gcube.data.analysis.tabulardata.cube.events.TableRemovedEvent;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@Singleton
@Slf4j
public class TableEventListener {
	
	public void onCreated(@Observes TableCreationEvent event){
		TableId tableId = event.getTable().getId();
		String threadGroup = Thread.currentThread().getThreadGroup().getName();
		if(threadGroup.equals("main")) return;
		log.debug("received event creation for table "+tableId.getValue()+" for thread group "+threadGroup);
		TableContainer.add(tableId);		
	}
	
	public void onRemoved(@Observes TableRemovedEvent event){
		TableId tableId = event.getTableId();
		String threadGroup = Thread.currentThread().getThreadGroup().getName();
		if(threadGroup.equals("main")) return;
		log.debug("received event deletion for table "+tableId.getValue()+" for thread group "+threadGroup);
		if (TableContainer.get(threadGroup)!=null && TableContainer.get(threadGroup).remove(tableId))
			log.debug("table "+tableId.getValue()+" removed from container in group "+threadGroup);
	}

}
