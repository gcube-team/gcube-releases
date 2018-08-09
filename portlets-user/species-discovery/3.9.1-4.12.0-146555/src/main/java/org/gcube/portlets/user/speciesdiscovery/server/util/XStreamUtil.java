package org.gcube.portlets.user.speciesdiscovery.server.util;

import com.thoughtworks.xstream.XStream;

public class XStreamUtil<T> extends XStream{

	private String aliasName = "";
	
	public enum AliasItem {OCCURRECENCE,RESULTROW,TAXONOMYROW};

	public XStreamUtil(AliasItem alias, Class<T> type){
		
		switch (alias) {
		
			case OCCURRECENCE:
				
				this.aliasName = "occurrenceItem";
				
				break;
				
			case RESULTROW:
				
				this.aliasName = "resultItem";
				
				break;
				
			case TAXONOMYROW:
			
				this.aliasName = "taxonomyItem";
				
				break;
	
			default:
				break;
		}
		
		this.alias(aliasName, type);
	}
	
	public XStreamUtil(String aliasName, Class<T> type){
		
		this.aliasName = aliasName;
		this.alias(aliasName, type);
	}

	public XStreamUtil(){
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}
}
