package org.gcube.informationsystem.scope.validator;

import java.util.List;

import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.resources.gcore.Resource;
import org.gcube.informationsystem.publisher.scope.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyGenericResourceValidator<R> implements Validator<GenericResource> {

	private static Logger log = LoggerFactory.getLogger(MyGenericResourceValidator.class);
	
	@Override
	public <R extends Resource> void validate(R resource) {
		log.info("validate method of "+this.getClass());		
	}

	@Override
	public Class type() {
		return GenericResource.class;
	}

	@Override
	public <R extends Resource> void checkScopeCompatibility(R resource,
			List<String> scopes) {
		// TODO Auto-generated method stub
		
	}


}
