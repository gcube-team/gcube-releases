/**
 *
 */

package org.gcube.portlets.widgets.wsexplorer.client.view.grid;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.Column;

public abstract class MyToolTipColumn<T, C> extends Column<T, C> {

	interface Templates extends SafeHtmlTemplates {

		@Template("<div title=\"{0}\">")
		SafeHtml startToolTip(String toolTipText);

		@Template("</div>")
		SafeHtml endToolTip();
	}
	private static final Templates TEMPLATES = GWT.create(Templates.class);
	private final String toolTipText;

	public MyToolTipColumn(final Cell<C> cell, final String toolTipText) {

		super(cell);
		this.toolTipText = toolTipText;
	}

	@Override
	public void render(final Context context, final T object, final SafeHtmlBuilder sb) {

		sb.append(TEMPLATES.startToolTip(toolTipText));
		super.render(context, object, sb);
		sb.append(TEMPLATES.endToolTip());
	}
}
