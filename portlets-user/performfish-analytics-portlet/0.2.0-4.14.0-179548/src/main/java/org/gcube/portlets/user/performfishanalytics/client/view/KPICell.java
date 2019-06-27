/**
 *
 */

package org.gcube.portlets.user.performfishanalytics.client.view;

import org.gcube.portlets.user.performfishanalytics.shared.KPI;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Jan 18, 2019
 */
public class KPICell extends AbstractCell<KPI> {

	/**
	 * The html of the image used for contacts.
	 */
	private String imageHtml;

	public KPICell(ImageResource image) {

		if (image == null)
			return;

		this.imageHtml = AbstractImagePrototype.create(image).getHTML();
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.google.gwt.cell.client.AbstractCell#render(com.google.gwt.cell.client
	 * .Cell.Context, java.lang.Object,
	 * com.google.gwt.safehtml.shared.SafeHtmlBuilder)
	 */
	@Override
	public void render(
		com.google.gwt.cell.client.Cell.Context context, KPI value,
		SafeHtmlBuilder sb) {

		// Value can be null, so do a null check..
		if (value == null) {
			return;
		}
		sb.appendHtmlConstant("<table>");
		// Add the contact image.
		sb.appendHtmlConstant("<tr>");
		// sb.appendHtmlConstant("<td rowspan='3'>");
		// sb.appendHtmlConstant(imageHtml);
		// sb.appendHtmlConstant("</td>");
		// Add the name and address.
		sb.appendHtmlConstant("<td style='font-size:95%;'>");
		sb.appendEscaped(value.getName());
		sb.appendHtmlConstant("</td></tr>");
		// sb.appendEscaped(value.getAddress());
		sb.appendHtmlConstant("</table>");
	}
}
