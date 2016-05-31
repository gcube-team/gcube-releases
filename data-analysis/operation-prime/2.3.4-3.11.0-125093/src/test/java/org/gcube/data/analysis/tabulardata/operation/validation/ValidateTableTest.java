package org.gcube.data.analysis.tabulardata.operation.validation;

import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import org.gcube.data.analysis.tabulardata.model.column.ColumnLocalId;
import org.gcube.data.analysis.tabulardata.model.table.Table;
import org.gcube.data.analysis.tabulardata.model.table.TableId;
import org.gcube.data.analysis.tabulardata.operation.test.OperationTester;
import org.gcube.data.analysis.tabulardata.operation.test.util.CodelistHelper;
import org.gcube.data.analysis.tabulardata.operation.test.util.DatasetHelper;
import org.gcube.data.analysis.tabulardata.operation.worker.WorkerFactory;
import org.junit.runner.RunWith;

import com.googlecode.jeeunit.JeeunitRunner;

@RunWith(JeeunitRunner.class)
public class ValidateTableTest extends OperationTester<ValidateTableFactory> {

	@Inject
	DatasetHelper helper;
	
	@Inject 
	CodelistHelper codelistHelper;
	
	@Inject 
	ValidateTableFactory factory;
	
	@Override
	protected WorkerFactory getFactory() {
		return factory; 
	}

	@Override
	protected Map getParameterInstances() {
		return Collections.EMPTY_MAP;
	}

	@Override
	protected ColumnLocalId getTargetColumnId() {
		return null;
	}

	@Override
	protected TableId getTargetTableId() {
		Table codelist=codelistHelper.createSpeciesCodelist();
		return helper.createSampleDataset(codelist).getId();
	}

}
