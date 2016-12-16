/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client.data;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public final class CSVRow extends JavaScriptObject {

	
	protected CSVRow(){}
	
	/**
	 * @return the id
	 */
	public String getId() {
		//FIXME
		
		return getField("id");
	}
	

	
	public native String getField(String column)
	/*-{
	        return this[column].toString();
	}-*/;
	

}
