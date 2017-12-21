package org.gcube.data.analysis.tabulardata.cube.tablemanagers;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.cube.metadata.CubeMetadataWrangler;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;

@Singleton
public class TableMetaCreatorProvider {

	private CubeMetadataWrangler cmw;

	@Inject
	public TableMetaCreatorProvider(CubeMetadataWrangler cmw, TableManager tm) {
		this.cmw = cmw;
	}

	public TableMetaCreator get(TableId tableId) throws NoSuchTableException{
		Table table;
		try {
			table = cmw.get(tableId);
		} catch (org.gcube.data.analysis.tabulardata.cube.metadata.exceptions.NoSuchTableException e) {
			throw new NoSuchTableException(tableId);
		}
		
		return new DefaultTableMetaCreator(cmw, table);
	}
	
}
