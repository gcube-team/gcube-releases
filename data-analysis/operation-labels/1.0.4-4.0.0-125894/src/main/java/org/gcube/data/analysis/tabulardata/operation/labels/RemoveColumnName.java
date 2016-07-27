package org.gcube.data.analysis.tabulardata.operation.labels;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.exceptions.NoSuchColumnException;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.EmptyType;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveColumnName extends MetadataWorker {

	private static final Logger log = LoggerFactory.getLogger(RemoveColumnName.class);
	
	private CubeManager cubeManager;

	private LocalizedText nameToRemove = null;
	
	
	

	public RemoveColumnName(OperationInvocation sourceInvocation,
			CubeManager cubeManager) {
		super(sourceInvocation);
		this.cubeManager = cubeManager;
	}




	@Override
	protected EmptyType execute() throws WorkerException {
		retrieveNameToRemove();
		updateProgress(0.3f,"Removing label metadata");
		createTableWithRemovedColumnLabel();
		return EmptyType.instance();
	}

	
	private void retrieveNameToRemove() {
		nameToRemove = (LocalizedText) getSourceInvocation().getParameterInstances().get(
				RemoveColumnNameFactory.NAME_LABEL_PARAMETER.getIdentifier());
	}
	
	
	private Table createTableWithRemovedColumnLabel(){
		TableMetaCreator tmc=cubeManager.modifyTableMeta(getSourceInvocation().getTargetTableId());
		NamesMetadata oldNamesMetadata=getOldNamesMetadata();
		List<LocalizedText> oldTexts = oldNamesMetadata.getTexts();
		List<LocalizedText> newTexts = new ArrayList<LocalizedText>(oldTexts);
		newTexts.remove(nameToRemove);
		log.debug("Setting names: " + newTexts);
		NamesMetadata newNamesMetadata = new NamesMetadata(newTexts);
		tmc.setColumnMetadata(getSourceInvocation().getTargetColumnId(), newNamesMetadata);
		return tmc.create();
	}
	
	private NamesMetadata getOldNamesMetadata(){
		try {
			return cubeManager.getTable(getSourceInvocation().getTargetTableId()).
					getColumnById(getSourceInvocation().getTargetColumnId()).getMetadata(NamesMetadata.class);
		} catch (NoSuchColumnException e) {
			throw new RuntimeException("Provided target column id does not exist");
		} catch (NoSuchTableException e) {
			throw new RuntimeException("Provided target table id does not exist");
		} catch (NoSuchMetadataException e) {
			return new NamesMetadata(new ArrayList<LocalizedText>());
		}
	}
}
