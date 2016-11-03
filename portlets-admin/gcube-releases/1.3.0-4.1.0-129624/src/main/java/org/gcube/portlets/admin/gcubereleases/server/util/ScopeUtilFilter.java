package org.gcube.portlets.admin.gcubereleases.server.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


public class ScopeUtilFilter {
	
	private static final String SCOPE_SEPARATOR = "/";
	private String scopeRoot = null;
	public static Logger logger = Logger.getLogger(ScopeUtilFilter.class);
	public Map<String, String> hashScopesFiltered = new HashMap<String, String>();
	
	public static final String ALLSCOPE = "All spaces";
	public static final String IDALLSCOPE = "ID All spaces";

	/**
	 * 
	 * @param scopeName
	 * @param addIdAllSpaces if true add "ID All spaces" - "All spaces" pair into map
	 */
	public ScopeUtilFilter(String scopeName, boolean addIdAllSpaces) {

		if(scopeName!=null){
			String[] scopes = scopeName.split(SCOPE_SEPARATOR);
			
			if(scopes!=null && scopes.length>1){
				scopeRoot = SCOPE_SEPARATOR+scopes[1];
				logger.trace("found root "+scopeRoot);
			}
			else{
				logger.warn("root scope not found!");
				scopeRoot = SCOPE_SEPARATOR;
			}
			
			if(addIdAllSpaces)
				hashScopesFiltered.put(ALLSCOPE, IDALLSCOPE); //PUT DEFAULT ID ALL SCOPE
			
		}
	}
	
	public List<String> convertListScopeToPortlet(List<String> listScopes){
		
		logger.trace("Scope converting...");
		
		List<String> scopesConverted = new ArrayList<String>();
		scopesConverted.add(ALLSCOPE);
		
		if(scopeRoot.compareTo(SCOPE_SEPARATOR)==0){
			
			logger.warn("root scope is '"+SCOPE_SEPARATOR+"' return list scopes passed in input");
			return listScopes;
		}
		
		for (String scope : listScopes) {
			
			if(scope.compareTo(scopeRoot)==0){ //CASE SCOPE IS ROOT
				
				logger.trace("found scope root "+scope+" added to list without converting");
				
				hashScopesFiltered.put(scopeRoot, scopeRoot);
				scopesConverted.add(scopeRoot);
				
			} else{
			
				int index = scope.indexOf(scopeRoot,0);
				int start = index+scopeRoot.length();
				int end = scope.length();
				//DEBUG
//				System.out.println("\n\n ");
//				System.out.println("index "+index);
//				System.out.println("start "+start);
//				System.out.println("end "+end);
				
				if(index!=-1){ //CASE SCOPE ROOT IS FOUND
					
					String filerString = scope.substring(index+start, scope.length());
	
					hashScopesFiltered.put(filerString, scope);
					
					scopesConverted.add(filerString);
					
					logger.trace("scope "+scope+ " is converted in: "+filerString);
					
				}else{ //CASE SCOPE ROOT NOT IS FOUND
					logger.warn("scope "+scope + " was reject from filter");
				}
			}
		}
		
		logger.trace("Scope converting was completed");
		
		return scopesConverted;
	}
	
	
	public String getPortalScopeFromFilteredScope(String scope){
		
		String portalScope = hashScopesFiltered.get(scope);
		
		if(portalScope==null){
			logger.warn("scope not found in scope fiter, return root scope "+scopeRoot);
			return scopeRoot;
		}
		
		return portalScope;
	}
	
	public Map<String, String> getHashScopesFiltered() {
		return hashScopesFiltered;
	}
	
	
	//TEST
	private void printScopeName(String scopeName){
		
		String[] scopes = scopeName.split(SCOPE_SEPARATOR);
		
		for (String scope : scopes) {
			System.out.println("scope split: "+scope);
		}
	}
	
	
	private void printScopes(){
		
		for (String key : hashScopesFiltered.keySet()) {
		
			System.out.println("Scope found: "+ hashScopesFiltered.get(key) +" with key: "+ key);
		}
	}
	
	public String getScopeRoot() {
		return scopeRoot;
	}
	
	public static void main(String[] args) {
		String scope = "/gcube";
		
		List<String> listTest  = Arrays.asList(new String[]{"/gcube/devsec/devre", "/gcube/devsec","/gcube/devsec/devNEXT", "/", "/gcub", "/gcube"});
		
		ScopeUtilFilter filter = new ScopeUtilFilter(scope, false);
		
		System.out.println("scope root is: "+filter.getScopeRoot());
		
		filter.convertListScopeToPortlet(listTest);
		
		
		System.out.println("get portal scope for /devsec: "+filter.getPortalScopeFromFilteredScope("/devsec"));
		
		filter.printScopes();
		
	}
	

}
