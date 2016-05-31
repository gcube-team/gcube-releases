package org.gcube.contentmanager.storageserver.parse.utils;

import java.util.ArrayList;
import java.util.Set;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.gcube.common.scope.impl.ServiceMapScannerMediator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);
	
	public static boolean validationScope(String scope){
		logger.debug("scope Validation for scope "+scope);
		ScopeBean scopeBean=new ScopeBean(scope);
		if((scopeBean.is(Type.VRE)))
			scope=scopeBean.enclosingScope().toString();
		Set<String> scopeSet=new ServiceMapScannerMediator().getScopeKeySet();
		for(String scopeItem : scopeSet){
			logger.debug("scope scanned: "+scopeItem);
			if(scope.equals(scopeItem))
				return true;
		}
		return false;
	}
	
	public static ArrayList<String> getVOScopes(String scope){
		ArrayList<String> vos=new ArrayList<String>();
		ScopeBean scopeBean=new ScopeBean(scope);
		//retrieve INFRA scope
		while(!scopeBean.is(Type.INFRASTRUCTURE)){
			logger.debug("the scope "+scope+" is not an INFRA scope ");
			scopeBean=new ScopeBean(scopeBean.enclosingScope().toString());
		}
		scope=scopeBean.toString();
		if(scopeBean.is(Type.INFRASTRUCTURE)){
			Set<String> scopeSet=new ServiceMapScannerMediator().getScopeKeySet();
			for(String scopeItem : scopeSet){
				//retrieve all Vo scopes
				logger.debug("scope scanned: "+scopeItem);
				if(scopeItem.contains(scope) && (new ScopeBean(scopeItem).is(Type.VO))){
					logger.debug("found vo scope: "+scopeItem);
					vos.add(scopeItem);
				}
			}
		}
		return vos;
	}

}
