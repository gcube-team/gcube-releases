package org.gcube.data.analysis.tabulardata.operation.column.attribute;

import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.NumericType;

public class TextToNumericFallback extends BaseAttributeColumnTypeChangeTest{

	@Override
	protected DataType getTargetDataType() {
		return new NumericType();
	}

	@Override
	protected int getColumnIndex() {
		return 1;
	}

}
