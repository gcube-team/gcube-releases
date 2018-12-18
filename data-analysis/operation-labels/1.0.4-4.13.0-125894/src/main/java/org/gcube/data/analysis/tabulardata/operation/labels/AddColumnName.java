package org.gcube.data.analysis.tabulardata.operation.labels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.column.ColumnReference;
import org.gcube.data.analysis.tabulardata.model.exceptions.NoSuchColumnException;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.EmptyType;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddColumnName extends MetadataWorker {

	private static final Logger log = LoggerFactory.getLogger(AddColumnName.class);

	private CubeManager cubeManager;

	private Map<ColumnReference, LocalizedText> namesToSet = null;

	public AddColumnName(CubeManager cubeManager,OperationInvocation Invocation) {
		super(Invocation);
		this.cubeManager = cubeManager;
	}

	@Override
	protected EmptyType execute() throws WorkerException {
		retrieveNameToSet();
		updateProgress(0.3f,"Adding label metadata");
		Table table = cubeManager.getTable(getSourceInvocation().getTargetTableId()); 
		createTableWithNewColumnLabels(table.getId());
		updateProgress(0.6f,"Changing labels on view");
		if (table.contains(DatasetViewTableMetadata.class)){
			TableId viewId = table.getMetadata(DatasetViewTableMetadata.class).getTargetDatasetViewTableId();
			createTableWithNewColumnLabels(viewId);
		}
		return EmptyType.instance();
	}

	@SuppressWarnings("unchecked")
	private void retrieveNameToSet() {
		namesToSet = (Map<ColumnReference, LocalizedText>) getSourceInvocation().getParameterInstances().get(
				AddColumnNameFactory.NAME_LABEL_PARAMETER.getIdentifier());
	}

	private Table createTableWithNewColumnLabels(TableId tableId){
		TableMetaCreator tmc=cubeManager.modifyTableMeta(tableId);

		for(Entry<ColumnReference, LocalizedText> entry: namesToSet.entrySet()){

			NamesMetadata oldNamesMetadata=getOldNamesMetadata(entry.getKey().getColumnId());
			List<LocalizedText> oldTexts = oldNamesMetadata.getTexts();
			List<LocalizedText> newTexts = new ArrayList<LocalizedText>();

			//Skip labels with the same locale as newer one
			for(LocalizedText existent:oldTexts)
				if(!existent.getLocale().equalsIgnoreCase(entry.getValue().getLocale()))
					newTexts.add(existent);

			newTexts.add(entry.getValue());
			log.debug("Setting names for col with id "+entry.getKey().getColumnId()+": " + newTexts);
			NamesMetadata newNamesMetadata = new NamesMetadata(newTexts);
			tmc.setColumnMetadata(entry.getKey().getColumnId(), newNamesMetadata);
		}
		return tmc.create();
	}

	
	
	private NamesMetadata getOldNamesMetadata(ColumnLocalId columnId){
		try {
			return cubeManager.getTable(getSourceInvocation().getTargetTableId()).
					getColumnById(columnId).getMetadata(NamesMetadata.class);
		} catch (NoSuchColumnException e) {
			throw new RuntimeException("Provided target column id does not exist");
		} catch (NoSuchTableException e) {
			throw new RuntimeException("Provided target table id does not exist");
		} catch (NoSuchMetadataException e) {
			return new NamesMetadata(new ArrayList<LocalizedText>());
		}
	}
}
