package org.gcube.data.analysis.tabulardata.operation.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.Column;
import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.metadata.table.DatasetViewTableMetadata;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.export.csv.Constants;
import org.gcube.data.analysis.tabulardata.operation.export.json.JSONExportFactory;
import org.gcube.data.analysis.tabulardata.operation.factories.types.ExportWorkerFactory;
import org.gcube.data.analysis.tabulardata.operation.invocation.InvocationCreator;
import org.gcube.data.analysis.tabulardata.operation.invocation.OperationInvocation;
import org.gcube.data.analysis.tabulardata.operation.table.CreateView;
import org.gcube.data.analysis.tabulardata.operation.table.CreateViewFactory;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.GenericHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.exceptions.InvalidInvocationException;
import org.junit.Before;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class JSONExportTester extends OperationTester<ExportWorkerFactory> {

	Table codelistTable;

	Table datasetTable;

	@Inject
	GenericHelper genericHelper;

	@Inject
	CodelistHelper codelistHelper;

	@Inject
	JSONExportFactory factory;

	@Inject
	CreateViewFactory createView;
	
	@Before
	public void setupTestTables() throws InvalidInvocationException {
		codelistTable = codelistHelper.createSpeciesCodelist();
		datasetTable = genericHelper.createDatasetWithSpeciesAndRelationship(codelistTable);
		OperationInvocation invocation = InvocationCreator.getCreator(createView.getOperationDescriptor())
				.setTargetTable(datasetTable.getId())
				.setParameters(new HashMap<String, Object>())
				.create();
		CreateView worker = createView.createWorker(invocation);
		worker.run();
		System.out.println(" view is "+ 
				(datasetTable.contains(DatasetViewTableMetadata.class)? 
				datasetTable.getMetadata(DatasetViewTableMetadata.class).getTargetDatasetViewTableId() 
				: "unknow" ));
	}

	@Override
	protected JSONExportFactory getFactory() {
		return factory;
	}

	@Override
	protected Map<String, Object> getParameterInstances() {
		Map<String, Object> instances = new HashMap<String, Object>();
		instances.put(Constants.VIEW, true);
		
		List<String> columns = new ArrayList<>();
		for (Column col: datasetTable.getColumns())
			columns.add(col.getLocalId().getValue());
		
		
		instances.put(Constants.COLUMNS, columns);
		return instances;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		return datasetTable.getId();
	}

}

