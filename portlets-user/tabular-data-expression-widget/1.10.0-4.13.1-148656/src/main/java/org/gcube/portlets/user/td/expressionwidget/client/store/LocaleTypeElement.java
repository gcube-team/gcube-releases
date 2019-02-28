package org.gcube.portlets.user.td.expressionwidget.client.store;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class LocaleTypeElement implements Serializable {

	private static final long serialVersionUID = 1L;
	protected String localeName;
	
	public LocaleTypeElement() {
	}

	public LocaleTypeElement(String localeName){
		this.localeName=localeName;
	}
	

	public String getLocaleName() {
		return localeName;
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}
	
	public String id(){
		return localeName;
	}
	
	public String label(){
		return localeName;
	}
	
	
	
	@Override
	public String toString() {
		return "LocaleTypeElement [localeName=" + localeName + "]";
	}

	
	
}
