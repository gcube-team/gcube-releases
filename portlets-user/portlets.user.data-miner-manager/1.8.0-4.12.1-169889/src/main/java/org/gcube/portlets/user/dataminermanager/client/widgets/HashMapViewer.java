/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.widgets;

import java.util.Map;
import java.util.Map.Entry;

import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class HashMapViewer extends SimpleContainer {

	private Map<String, String> map;

	/**
	 * 
	 * @param map
	 *            map
	 */
	public HashMapViewer(Map<String, String> map) {
		super();
		this.map = map;
		init();

	}

	/**
	 * 
	 */
	private void init() {
		String html = "";
		html += "<table class='jobViewer-table'>" + "    <colgroup>" + "    	<col>"
				+ "    	<col class='jobViewer-table-oce-first'>" + "    </colgroup>" + "    <tbody>";

		for (Entry<String, String> entry : map.entrySet())
			if (entry.getKey() != null) {
				html += "    <tr>" + "		<td>" + entry.getKey() + "</td>" + "		<td>" + entry.getValue()
						+ "</td>" + "	</tr>";
			}

		html += "    </tbody>" + "</table>";
		HtmlLayoutContainer htmlContainer = new HtmlLayoutContainer(html);
		add(htmlContainer);
		forceLayout();
	}

}
