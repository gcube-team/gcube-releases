package org.gcube.data.analysis.tabulardata.operation.labels;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.tabulardata.cube.CubeManager;
import org.gcube.data.analysis.tabulardata.cube.exceptions.NoSuchTableException;
import org.gcube.data.analysis.tabulardata.cube.tablemanagers.TableMetaCreator;
import org.gcube.data.analysis.tabulardata.metadata.NoSuchMetadataException;
import org.gcube.data.analysis.tabulardata.model.metadata.common.LocalizedText;
import org.gcube.data.analysis.tabulardata.model.metadata.common.NamesMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.WorkerException;
import org.gcube.data.analysis.tabulardata.operation.worker.results.EmptyType;
import org.gcube.data.analysis.tabulardata.operation.worker.types.MetadataWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddTableName extends MetadataWorker {

	private static final Logger log = LoggerFactory.getLogger(AddTableName.class);

	private CubeManager cubeManager;

	private LocalizedText nameToSet = null;

	public AddTableName(CubeManager cubeManager, OperationInvocation invocation) {
		super(invocation);
		this.cubeManager = cubeManager;
	}

	@Override
	protected EmptyType execute() throws WorkerException {
		retrieveNameToSet();
		updateProgress(0.3f,"Adding label metadata");
		createTableWithNewLabel();
		return EmptyType.instance();
	}

	private void retrieveNameToSet() {
		nameToSet = (LocalizedText) getSourceInvocation().getParameterInstances().get(
				AddTableNameFactory.NAME_LABEL_PARAMETER.getIdentifier());
	}

	private Table createTableWithNewLabel() {
		TableMetaCreator tmc = cubeManager.modifyTableMeta(getSourceInvocation().getTargetTableId());
		NamesMetadata oldNamesMetadata = getOldNamesMetadata();
		List<LocalizedText> oldTexts = oldNamesMetadata.getTexts();
		List<LocalizedText> newTexts = new ArrayList<LocalizedText>(oldTexts);
		newTexts.add(nameToSet);
		log.debug("Setting names: " + newTexts);
		NamesMetadata newNamesMetadata = new NamesMetadata(newTexts);
		tmc.setTableMetadata(newNamesMetadata);
		return tmc.create();
	}

	private NamesMetadata getOldNamesMetadata() {
		try {
			return cubeManager.getTable(getSourceInvocation().getTargetTableId()).getMetadata(NamesMetadata.class);
		} catch (NoSuchTableException e) {
			throw new RuntimeException("Provided target table id does not exist");
		} catch (NoSuchMetadataException e) {
			return new NamesMetadata(new ArrayList<LocalizedText>());
		}
	}

}
