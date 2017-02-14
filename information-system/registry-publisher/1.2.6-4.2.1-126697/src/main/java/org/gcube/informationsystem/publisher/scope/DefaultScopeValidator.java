package org.gcube.informationsystem.publisher.scope;

import java.util.List;

import org.gcube.common.resources.gcore.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultScopeValidator<R> implements Validator<Resource>{

	private static Logger log = LoggerFactory.getLogger(DefaultScopeValidator.class);
	
	@Override
	public <R extends Resource> void validate(R resource) {
//		log.info("validate method of "+this.getClass());	
//		String currentScope=ScopeProvider.instance.get();
//		ScopeGroup<String> scopes=resource.scopes();
//		boolean founded= false;
//		for(Iterator<String> it=scopes.iterator(); it.hasNext();){
//			String scope=it.next();
//			if(scope.equals(currentScope))
//				founded=true;
//		}
//		if(!founded)
//			throw new IllegalStateException(" scope "+currentScope+" not present in resource");
		
	}

	@Override
	public Class type() {
		return Resource.class;
	}

	@Override
	public <R extends Resource> void checkScopeCompatibility(R resource,
			List<String> scopesList) {
//		for(String scope: scopesList){
//			ScopeGroup<String> scopes=resource.scopes();
//			for(Iterator<String> it=scopes.iterator(); it.hasNext();){
//				String scopeItem=it.next();
//				if((!scopeItem.contains(scope)) && (!scope.contains(scopeItem)))
//					throw new IllegalStateException("The scope "+scope+" is not compatible with scope: "+scopeItem+" that is present in the resource"+ resource.id());
//			}
//		}
	}


}
