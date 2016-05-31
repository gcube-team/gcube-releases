package org.gcube.vremanagement.whnmanager.utils;

import java.util.Iterator;

import org.gcube.common.resources.gcore.Resource;
import org.gcube.common.resources.gcore.ResourceMediator;
import org.gcube.common.resources.gcore.ScopeGroup;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationUtils {
	
	private static final Logger log = LoggerFactory.getLogger(ValidationUtils.class);
	
	public static void valid(String name, Object object){
		if (object==null)
	          throw new IllegalArgumentException(name+" is null");
	}
	
	public  static < R extends Resource > boolean isPresent(R resource, String currentScope){
		ScopeGroup<String> scopes=resource.scopes();
		boolean founded= false;
		for(Iterator<String> it=scopes.iterator(); it.hasNext();){
			String scope=it.next();
			if(scope.equals(currentScope))
				founded=true;
		}
		if(founded)
			return true;
		return false;
	}
	
	
	public  static < R extends Resource > boolean isNotPresent(R resource, String currentScope){
		ScopeGroup<String> scopes=resource.scopes();
		boolean found= false;
		for(Iterator<String> it=scopes.iterator(); it.hasNext();){
			String scope=it.next();
			if(scope.equals(currentScope))
				found=true;
		}
		if(!found)
			return true;
		return false;
	}
	
	
	/**
	 * If scope is a VRE scope and the VO and INFRA scopes are not present in the resource, this method add these scopes to the resource
	 * @param resource the resource
	 * @param scope a scope
	 */
	public static <T extends Resource> void addEnclosingScopesOnResource(T resource, String scope){
		log.trace("add enclosed scopes of "+scope+" to the resource with id: "+resource.id());
		if(new ScopeBean(scope).is(Type.VRE)){
			String voScope=new ScopeBean(scope).enclosingScope().toString();
			String infraScope=new ScopeBean(voScope).enclosingScope().toString();
// The scope collection is a set, I can add scope without checking		
			log.debug("adding "+voScope+" to the resource "+resource.id());
			ResourceMediator.setScope(resource, voScope);
			log.debug("adding "+infraScope+" to the resource "+resource.id());
			ResourceMediator.setScope(resource, infraScope);
		}else if(new ScopeBean(scope).is(Type.VO)){
			String infraScope=new ScopeBean(scope).enclosingScope().toString();
			log.debug("adding "+infraScope+" to the resource "+resource.id());
// The scope collection is a set, I can add scope without checking
			ResourceMediator.setScope(resource, infraScope);
		}
	}
	
	public static <T extends Resource> boolean isTheLastScopeOnResource(T resource, String scope){
		if(resource.scopes().size() == 0)
			return true;
		if(new ScopeBean(scope).is(Type.VRE)){
			if(anotherBrotherVREOrVOOnResource(resource, scope)){
				return false;
			}else return true;
		}else if(new ScopeBean(scope).is(Type.VO)){
			if(anotherSonVREOnResource(resource, scope)){
				throw new IllegalArgumentException("the resource "+resource.id()+" have another scope defined in the same VO. The VO is  "+scope);
			}else return true;
		}else{ // is a INFRA scope
			if(anotherInfraScopeOnResource(resource, scope)){
				throw new IllegalArgumentException("the resource "+resource.id()+" have another scope defined in the same INFRA. The INFRA is  "+scope);
			}else return true;
		}
	}
	
	public static <T extends Resource> boolean anotherBrotherVREOrVOOnResource(T resource, String scope){
		if(!new ScopeBean(scope).is(Type.VRE))
			throw new IllegalArgumentException("anotherBrotherVREOrVOOnResource method: the input scope must be a VRE scope");
		String enclosedScope=new ScopeBean(scope).enclosingScope().toString();
		for(String s : resource.scopes()){
			if(isChildScope(enclosedScope, s)) return true;
		}
		return false;
	}

	public static <T extends Resource> boolean anotherSonVREOnResource(T resource, String scope){
		if(!new ScopeBean(scope).is(Type.VO))
			throw new IllegalArgumentException("anotherSonVREOnResource method: the input scope must be a VO scope");
		for(String s : resource.scopes()){
			if(isChildScope(scope, s)) return true;
		}
		return false;
	}

	public static boolean isChildScope(String fatherScope, String sonScope) {
		ScopeBean currentEnclosedScope=new ScopeBean(sonScope).enclosingScope();
		if((currentEnclosedScope != null) && (currentEnclosedScope.toString().equals(fatherScope))){
			log.debug("check scope"+fatherScope+": found another son VRE scope "+sonScope);
			return true;
		}else return false;
	}

	public static <T extends Resource> boolean anotherInfraScopeOnResource(T resource, String scope){
		if(!new ScopeBean(scope).is(Type.INFRASTRUCTURE))
			throw new IllegalArgumentException("anotherInfraScopeOnResource method: the input scope must be a INFRASTRUCTURE scope");
		String infraScopeFound=null;
		for(String s : resource.scopes()){
			while(new ScopeBean(s).enclosingScope() != null){
				s=new ScopeBean(s).enclosingScope().toString();
				
			}
			infraScopeFound=s;
			if(infraScopeFound.equals(scope)){
				log.debug("check scope"+scope+": found another scope on infra "+s);
				return true;
			}
		}
		return false;
	}



}

