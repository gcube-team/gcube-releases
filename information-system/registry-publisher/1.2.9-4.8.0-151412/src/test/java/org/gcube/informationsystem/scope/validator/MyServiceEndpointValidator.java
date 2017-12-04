package org.gcube.informationsystem.scope.validator;

import java.util.List;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.informationsystem.publisher.scope.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class  MyServiceEndpointValidator<R>  implements Validator<ServiceEndpoint>{

	private static Logger log = LoggerFactory.getLogger(MyServiceEndpointValidator.class);
	
	@Override
	public <R extends Resource> void validate(R resource) {
		log.info("validate method of "+this.getClass());
		
	}

	@Override
	public Class type() {
		return ServiceEndpoint.class;
	}

	@Override
	public <R extends Resource> void checkScopeCompatibility(R resource,
			List<String> scopes) {
		// TODO Auto-generated method stub
		
	}

	

}
