package org.gcube.data.analysis.tabulardata.operation.column.dimension;

public class ValidWithName extends ChangeToDimensionColumnTest{

	@Override
	protected int getTargetCodelistReferenceColumnIndex() {
		return 2;
	}

	@Override
	protected int getTargetDatasetColumnIndex() {
		return 3;
	}

}
