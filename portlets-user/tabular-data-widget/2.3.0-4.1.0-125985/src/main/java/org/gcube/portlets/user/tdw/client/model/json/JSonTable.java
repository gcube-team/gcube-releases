/**
 * 
 */
package org.gcube.portlets.user.tdw.client.model.json;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public final class JSonTable extends JavaScriptObject {
	
	protected JSonTable(){}
	
	public static final native JSonTable getJSonTable(String json) /*-{
	    var data = eval('(' + json + ')');
	    return data;
	}-*/;
	
	/**
	 * Return a list of {@link JSonValue} from the specified JSON string. 
	 * @param json the JSON string.
	 * @param rowsField the list root.
	 * @return the extracted list.
	 */
	public List<JSonValue> getList(String rowsField) {
		JsArray<JSonValue> jsArray = getRows(rowsField);
		JSonValue[] a = new JSonValue[jsArray.length()];
		for (int i = 0; i < jsArray.length(); i++) a[i] = jsArray.get(i);
		return Arrays.asList(a);
	};

	public final native JsArray<JSonValue> getRows(String rowsField) /*-{
        return this[rowsField];
    }-*/;
	
	public final native int getTotalLength(String fieldName)
	/*-{
	    return this[fieldName];
	}-*/;

	public final native int getOffset(String fieldName)
	/*-{
	    return this[fieldName];
	}-*/;
}
