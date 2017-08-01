package org.gcube.data.analysis.tabulardata.operation.validation.typecast;

import org.gcube.data.analysis.tabulardata.model.datatype.BooleanType;
import org.gcube.data.analysis.tabulardata.model.datatype.DataType;

public class TextToBooleanValidationUnsuccessful extends BaseColumnTypeCastValidatorTest{

	@Override
	protected DataType getTargetType() {
		return new BooleanType();
	}

}
