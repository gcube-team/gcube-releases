package org.gcube.data.analysis.tabulardata.operation.column.attribute;

import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;

public class TextToDateSuccessful extends BaseAttributeColumnTypeChangeTest {

	@Override
	protected DataType getTargetDataType() {
		return new DateType();
	}

	@Override
	protected int getColumnIndex() {
		return 8;
	}

}
