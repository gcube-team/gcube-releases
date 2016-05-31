package org.gcube.portlets.user.searchportlet.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This class has all the needed information that are passed from the servlet to the client for a query that
 * is saved to the user's workspace
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class SavedBasketQueriesInfo implements IsSerializable
{
	
	private String queryDescription;
	private String queryType;
	
	public SavedBasketQueriesInfo() {
		
	}
	
	public SavedBasketQueriesInfo(String description, String type) {
		this.queryDescription = description;
		this.queryType = type;
	}
	
	public String getQueryDescription() {
		return this.queryDescription;
	}
	
	public String getQueryType() {
		return this.queryType;
	}

}
