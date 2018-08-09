/**
 * 
 */
package org.gcube.portlets.user.td.csvimportwidget.client.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * 
 * @author "Giancarlo Panichi" 
 *  
 *
 */
public final class CSVData extends JavaScriptObject {
	
	protected CSVData(){}
	
	public static final native CSVData getCSVData(String json) /*-{
	    var data = eval('(' + json + ')');
	    return data;
	}-*/;
	
	public final List<CSVRow> getRows()
	{
		JsArray<CSVRow> jsRows = getJSRows();
		
		List<CSVRow> rows = new ArrayList<CSVRow>(jsRows.length());
		for (int i = 0; i < jsRows.length(); i++) {
			rows.add(jsRows.get(i));
		}
		
		return rows;
	}
	
	protected final native JsArray<CSVRow> getJSRows() /*-{
	    return this['records'];
	}-*/;

}
