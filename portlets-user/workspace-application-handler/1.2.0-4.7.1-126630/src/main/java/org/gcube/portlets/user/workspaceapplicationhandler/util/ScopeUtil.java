/**
 * 
 */
package org.gcube.portlets.user.workspaceapplicationhandler.util;

import org.apache.log4j.Logger;
import org.gcube.common.scope.impl.ScopeBean;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Oct 4, 2013
 *
 */
public class ScopeUtil {

	protected static Logger logger =  Logger.getLogger(ScopeUtil.class);
	
	
	public static String getInfrastructuresNameFromScopeBean(ScopeBean scopeBean) throws Exception{
		String infra = "";
		
		if(scopeBean==null || scopeBean.toString()==null || scopeBean.toString().isEmpty())
			throw new Exception("Scope is null or empty");
		
		String scope = scopeBean.toString(); //return fully scope
		
		if(scope.contains("/")){
			
			String[] scopeSplit = scope.split("/");
			
			if(scopeSplit.length<2){
				throw new Exception("Infrastructure name not found in "+scope);
			}
			
			infra = "/"+scopeSplit[1];
			
			logger.trace("scope is "+ scope +" infra: "+infra);
			
		}else
			throw new Exception("Invalid scope: "+scope);
		
		
		return infra;
		
	}
	
	public static void main(String[] args) {
		
		try {
			System.out.println(ScopeUtil.getInfrastructuresNameFromScopeBean(new ScopeBean("/gcube/devsec")));
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
