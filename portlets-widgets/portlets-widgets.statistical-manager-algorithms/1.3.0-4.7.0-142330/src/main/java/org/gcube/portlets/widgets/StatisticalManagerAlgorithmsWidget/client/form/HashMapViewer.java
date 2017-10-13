/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.form;

import java.util.Map;
import java.util.Map.Entry;

import com.extjs.gxt.ui.client.widget.Html;

	

/**
 * @author ceras
 *
 */
public class HashMapViewer extends Html {

	/**
	 * @param map
	 */
	public HashMapViewer(Map<String, String> map) {
		super(getHtml(map));
	}

	/**
	 * @param map
	 * @return
	 */
	private static String getHtml(Map<String, String> map) {
		String html ="";
		html += "<table class='jobViewer-table'>" +
				"    <colgroup>" +
				"    	<col>" +
				"    	<col class='jobViewer-table-oce-first'>" +
				"    </colgroup>"+
				"    <tbody>";
		
		for (Entry<String, String> entry: map.entrySet())
			if (entry.getKey()!=null) {
				html += "    <tr>" +
						"		<td>"+entry.getKey()+"</td>" +
						"		<td>"+entry.getValue()+"</td>" +
						"	</tr>";
			}
		
		html += "    </tbody>" +
				"</table>";
		return html;
	}

}
