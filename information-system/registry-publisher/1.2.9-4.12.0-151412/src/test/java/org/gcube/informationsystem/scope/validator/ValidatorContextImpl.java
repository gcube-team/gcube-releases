package org.gcube.informationsystem.scope.validator;

import java.util.Arrays;
import java.util.List;

import org.gcube.informationsystem.publisher.scope.IValidatorContext;
import org.gcube.informationsystem.publisher.scope.Validator;

public class ValidatorContextImpl implements IValidatorContext{

	final static List<Validator> validators = Arrays.asList(new MyGenericResourceValidator(), new MyServiceEndpointValidator());
	
	@Override
	public List<Validator> getValidators() {
		return validators;
	}

}
