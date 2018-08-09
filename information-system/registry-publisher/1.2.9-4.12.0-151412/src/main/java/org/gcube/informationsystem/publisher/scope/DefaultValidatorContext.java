package org.gcube.informationsystem.publisher.scope;

import java.util.Arrays;
import java.util.List;

import org.gcube.common.resources.gcore.Resource;


public class DefaultValidatorContext implements IValidatorContext{

	final static List<Validator> validators = Arrays.asList((Validator)new DefaultScopeValidator());
	
	@Override
	public List<Validator> getValidators() {
		return  validators;
	}

}
