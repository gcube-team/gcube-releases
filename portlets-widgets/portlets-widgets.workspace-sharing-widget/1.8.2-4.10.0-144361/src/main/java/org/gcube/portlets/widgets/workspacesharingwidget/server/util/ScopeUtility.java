package org.gcube.portlets.widgets.workspacesharingwidget.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ScopeUtility {
	
	private static final String SCOPE_SEPARATOR = "/";
	private String root;
	private String vo;
	private String vre;
	
	private String rootName;
	private String voName;
	private String vreName;
	
	public static Logger logger = LoggerFactory.getLogger(ScopeUtility.class);
	
	
	public ScopeUtility(String scopeName) throws Exception {

		if(scopeName!=null){
			String[] scopes = scopeName.split(SCOPE_SEPARATOR);
			
			if(scopes==null)
				return;
			
			for (int i=0; i<scopes.length; i++) {
				logger.info("splitted scope is "+scopes[i]);
			}

			if(scopes!=null && scopes.length>1){
				
				if(scopes[0]==null || scopes[0].isEmpty())
					setScopeLevels(scopes, 1);
				else
					setScopeLevels(scopes, 0);
			}
			else{
				logger.warn("root scope not found!");
				root = SCOPE_SEPARATOR;
				throw new Exception("Root scope not found");
			}
			
		}
	}
	
	private void setScopeLevels(String[] scopes, int startIndex){
		try{
			logger.info("splitted scopes legth is "+scopes.length);
			root = SCOPE_SEPARATOR+scopes[startIndex];
			rootName = scopes[startIndex];
			
			logger.info("root is "+root);
			
			int voIndex = startIndex+1;
//			logger.info("vo index is "+voIndex);
			if(scopes.length > voIndex){
				vo = root+SCOPE_SEPARATOR+scopes[voIndex];
				voName = scopes[voIndex];
			}
			
			int vreIndex = startIndex+2;
//			logger.info("vre index is "+vreIndex);
			if(scopes.length > vreIndex){
				vre = vo + SCOPE_SEPARATOR + scopes[vreIndex];
				vreName = scopes[vreIndex];
			}
			
			logger.info("vo is "+vo);
			logger.info("vre is "+vre);
		}catch(Exception e){
			logger.error("Error occurred when calculating scope levels: ",e);
		}
		
	}
	
	public String getScopeRoot() {
		return root;
	}

	public String getRoot() {
		return root;
	}

	public String getVo() {
		return vo;
	}

	public String getVre() {
		return vre;
	}
	
	public String getRootName() {
		return rootName;
	}

	public String getVoName() {
		return voName;
	}

	public String getVreName() {
		return vreName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ScopeUtility [root=");
		builder.append(root);
		builder.append(", vo=");
		builder.append(vo);
		builder.append(", vre=");
		builder.append(vre);
		builder.append(", rootName=");
		builder.append(rootName);
		builder.append(", voName=");
		builder.append(voName);
		builder.append(", vreName=");
		builder.append(vreName);
		builder.append("]");
		return builder.toString();
	}
	

	
	public static void main(String[] args) {
		String scope = "/gcube/devsec";
		
		try {
			ScopeUtility filter = new ScopeUtility(scope);
			System.out.println(filter);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
