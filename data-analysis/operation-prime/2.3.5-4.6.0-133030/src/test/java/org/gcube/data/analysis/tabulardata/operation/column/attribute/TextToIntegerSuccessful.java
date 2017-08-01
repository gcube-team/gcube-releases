package org.gcube.data.analysis.tabulardata.operation.column.attribute;

import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.IntegerType;

public class TextToIntegerSuccessful extends BaseAttributeColumnTypeChangeTest{

	@Override
	protected DataType getTargetDataType() {
		return new IntegerType();
	}

	@Override
	protected int getColumnIndex() {
		return 2;
	}

}
