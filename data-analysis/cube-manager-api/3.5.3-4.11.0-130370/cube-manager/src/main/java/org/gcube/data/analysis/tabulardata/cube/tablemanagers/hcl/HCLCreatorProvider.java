package org.gcube.data.analysis.tabulardata.cube.tablemanagers.hcl;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.gcube.data.analysis.tabulardata.cube.data.DatabaseWrangler;
import org.gcube.data.analysis.tabulardata.cube.events.TableCreationEvent;
import org.gcube.data.analysis.tabulardata.cube.metadata.CubeMetadataWrangler;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableCreator;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableManager;
import org.gcube.data.analysis.tabulardata.model.table.Table;

public class HCLCreatorProvider implements Provider<TableCreator>{
	
	private DatabaseWrangler dbWrangler;
	
	private CubeMetadataWrangler hclMetadataWrangler;
	
	private TableManager tableManager;
	
	private Event<TableCreationEvent> tableCreatedEvent;

	@Inject
	public HCLCreatorProvider(DatabaseWrangler dbWrangler,
			CubeMetadataWrangler hclMetadataWrangler,
			TableManager tableManager, Event<TableCreationEvent> tableCreatedEvent) {
		super();
		this.dbWrangler = dbWrangler;
		this.hclMetadataWrangler = hclMetadataWrangler;
		this.tableManager= tableManager;
		this.tableCreatedEvent = tableCreatedEvent;
	}

	@Produces
	@Named("HCL")
	@Override
	public TableCreator get() {
		return new HCLCreator(dbWrangler, hclMetadataWrangler, tableManager, tableCreatedEvent);
	}
	
}
