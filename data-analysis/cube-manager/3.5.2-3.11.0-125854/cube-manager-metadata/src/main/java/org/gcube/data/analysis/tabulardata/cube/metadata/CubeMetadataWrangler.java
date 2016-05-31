package org.gcube.data.analysis.tabulardata.cube.metadata;

import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.metadata.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.model.table.TableType;

public interface CubeMetadataWrangler {
	
	public Table get(TableId id) throws NoSuchTableException;
	
	public Table getTableByName(String name) throws NoSuchTableException;
	
	public List<Table> getAll();
	
	public List<Table> getAll(TableType tableType);
	
//	public Table update(Table table);

	public Table save(Table table,  boolean overwrite);

	public void remove(TableId id) throws NoSuchTableException;

}
