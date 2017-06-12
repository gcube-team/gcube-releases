package org.gcube.data.analysis.tabulardata.operation.column.measure;

import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;

public class TextToInteger extends BaseMeasureColumnTypeChange {

	@Override
	protected DataType getTargetDataType() {
		return new IntegerType();
	}

	@Override
	protected int getColumnIndex() {
		return 2;
	}

}
