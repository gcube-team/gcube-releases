/**
 * 
 */
package org.gcube.portlets.user.dataminermanager.client.widgets;

import java.util.Map;
import java.util.Map.Entry;

import org.gcube.data.analysis.dataminermanagercl.shared.data.output.ObjectResource;
import org.gcube.data.analysis.dataminermanagercl.shared.data.output.Resource;

import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ResourceViewer {

	private Map<String, ? extends Resource> map;

	/**
	 * 
	 * @param map
	 *            map
	 */
	public ResourceViewer(Map<String, ? extends Resource> map) {
		this.map = map;

	}

	/**
	 * 
	 * @return html layout container
	 */
	public HtmlLayoutContainer getHtml() {
		String html = "";
		html += "<table class='jobViewer-table'>" + "    <colgroup>" + "    	<col>" + "    	<col>"
				+ "    	<col class='jobViewer-table-oce-first'>" + "    </colgroup>" + "    <tbody>";

		for (Entry<String, ? extends Resource> entry : map.entrySet())
			if (entry.getKey() != null) {
				if (entry.getValue() instanceof ObjectResource) {
					ObjectResource or = (ObjectResource) entry.getValue();
					html += "    <tr>" + "		<td>" + or.getName() + "</td>" + "		<td>" + or.getValue() + "</td>"
							+ "	</tr>";
				} else {

				}
			}

		html += "    </tbody>" + "</table>";
		return new HtmlLayoutContainer(html);
	}

}
