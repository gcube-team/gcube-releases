package org.gcube.data.analysis.tabulardata.operation.validation.typecast;

import org.gcube.data.analysis.tabulardata.model.datatype.DataType;
import org.gcube.data.analysis.tabulardata.model.datatype.DateType;

public class TextToDateValidation extends BaseColumnTypeCastValidatorTest {

	@Override
	protected DataType getTargetType() {
		return new DateType();
	}

}
