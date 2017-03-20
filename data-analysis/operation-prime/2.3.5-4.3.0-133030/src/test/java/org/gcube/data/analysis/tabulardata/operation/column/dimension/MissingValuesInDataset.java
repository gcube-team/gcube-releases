package org.gcube.data.analysis.tabulardata.operation.column.dimension;

public class MissingValuesInDataset extends ChangeToDimensionColumnTest{

	@Override
	protected int getTargetCodelistReferenceColumnIndex() {
		return 4;
	}

	@Override
	protected int getTargetDatasetColumnIndex() {
		return 4;
	}

}
