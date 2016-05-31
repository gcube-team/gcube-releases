package org.gcube.data.analysis.tabulardata.cube.tablemanagers.codelist;

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


public class CodelistCreatorProvider implements Provider<TableCreator> {

	private DatabaseWrangler dw;
	private CubeMetadataWrangler cmw;
	private TableManager tm;
	private Event<TableCreationEvent> tableCreatedEvent;
	
	
	@Inject
	public CodelistCreatorProvider(DatabaseWrangler dw,
			CubeMetadataWrangler cmw, TableManager tm, Event<TableCreationEvent> tableCreatedEvent) {
		super();
		this.dw = dw;
		this.cmw = cmw;
		this.tm=tm;
		this.tableCreatedEvent = tableCreatedEvent;
	}

	@Produces
	@Named("Codelist")
	@Override
	public TableCreator get() {
		return new CodelistCreator(dw, cmw, tm, this.tableCreatedEvent);
	}

}
