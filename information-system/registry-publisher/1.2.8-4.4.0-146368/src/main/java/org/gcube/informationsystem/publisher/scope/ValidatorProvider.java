package org.gcube.informationsystem.publisher.scope;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.gcube.common.resources.gcore.Resource;

public class ValidatorProvider {
	
	private static Map<Class, Validator> validatorsMap= new LinkedHashMap<Class, Validator>();
	
	
	public static Validator getValidator(Resource resource){
		Validator validator=null;
		if(validatorsMap.isEmpty()){
			IValidatorContext context= ScopeValidatorScanner.provider();
			List<Validator> validators=context.getValidators();
			for(Validator v :validators){
				validatorsMap.put(v.type(), v);
			}
		}
		validator=validatorsMap.get(resource.getClass());
		if (validator==null)
			validator=new DefaultScopeValidator();
		return validator;
	}


}
