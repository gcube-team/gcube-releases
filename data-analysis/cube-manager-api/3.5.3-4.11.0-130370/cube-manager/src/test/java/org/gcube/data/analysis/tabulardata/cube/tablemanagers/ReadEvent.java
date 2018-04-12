package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import org.gcube.data.analysis.tabulardata.cube.events.TableCreationEvent;

@Singleton
public class ReadEvent {
			
	public void created(@Observes TableCreationEvent tableCreatedEvent){
		System.out.println("received event for table "+tableCreatedEvent.getTable().getId());
		
	}
}
