package org.gcube.data.analysis.tabulardata.operation.column.attribute;

import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.TextType;

public class DateToText extends BaseAttributeColumnTypeChangeTest{

	@Override
	protected DataType getTargetDataType() {
		return new TextType();
	}

	@Override
	protected int getColumnIndex() {
		return 9;
	}

}
