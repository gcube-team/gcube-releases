package org.gcube.spatial.data.geonetwork.utils;

import java.util.HashSet;
import java.util.Set;

import org.gcube.common.scope.api.ScopeProvider;

public class ScopeUtils {

	public static String getCurrentScope(){
		return ScopeProvider.instance.get();
	}
	
	public static String getCurrentScopeName(){
		String current=getCurrentScope();
		return current.substring(current.lastIndexOf('/')+1);
	}
	
	public static Set<String> getParentScopes(){
		String currentScope=getCurrentScope();
		String[] splitted=currentScope.substring(1).split("/");
		HashSet<String> toReturn=new HashSet<String>();
		for(int i=0;i<splitted.length-1;i++){
			toReturn.add(splitted[i]);
		}
		return toReturn;
	}
}
