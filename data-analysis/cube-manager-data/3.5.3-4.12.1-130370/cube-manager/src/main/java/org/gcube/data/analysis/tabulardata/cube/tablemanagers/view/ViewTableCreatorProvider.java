package org.gcube.data.analysis.tabulardata.cube.tablemanagers.view;

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

public class ViewTableCreatorProvider implements Provider<TableCreator> {

	private DatabaseWrangler dbWrangler;
	private CubeMetadataWrangler mdWrangler;
	private TableManager tableManager;
	private Event<TableCreationEvent> tableCreatedEvent;
	@Inject
	public ViewTableCreatorProvider(DatabaseWrangler dbWrangler, CubeMetadataWrangler mdWrangler,
			TableManager tableManager, Event<TableCreationEvent> tableCreatedEvent) {
		this.dbWrangler = dbWrangler;
		this.mdWrangler = mdWrangler;
		this.tableManager = tableManager;
		this.tableCreatedEvent = tableCreatedEvent;
	}

	@Produces
	@Named("ViewTable")
	@Override
	public TableCreator get() {
		return new ViewTableCreator(dbWrangler, mdWrangler, tableManager, tableCreatedEvent);
	}

}
