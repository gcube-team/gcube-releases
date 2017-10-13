package org.gcube.data.analysis.tabulardata.operation.column.attribute;

import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

public class TextToBooleanFallback extends BaseAttributeColumnTypeChangeTest {

	@Override
	protected DataType getTargetDataType() {
		return new BooleanType();
	}

	@Override
	protected int getColumnIndex() {
		return 1;
	}

}
