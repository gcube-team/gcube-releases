/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: ScrollablePanel.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.panels;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

/**
 * @author Daniele Strollo (ISTI-CNR)
 *
 */
public class ScrollablePanel extends ContentPanel {

	public ScrollablePanel() {
		super();
		this.setLayout(new FitLayout());
		this.setScrollMode(Scroll.AUTO);
		this.setHeaderVisible(false);
		this.setWidth("100%");
		this.setHeight("100%");
		this.setLayoutOnChange(true);

		this.getHeader().setStyleName("x-hide-panel-header");
	}

	public ScrollablePanel(final String id) {
		this();
		this.setId(id);
	}

	public ScrollablePanel(final Component widget) {
		this();
		this.setScrollableWidget(widget);
	}

	public ScrollablePanel(final String id, final Component widget) {
		this(id);
		this.setScrollableWidget(widget);
	}

	public final void setScrollableWidget(final Component widget) {
		this.removeAll();
		widget.setWidth("100%");
		widget.setHeight("100%");
		this.add(widget);
		this.layout(true);
	}

	@Override
	protected final void onRender(final Element parent, final int pos) {
		super.onRender(parent, pos);
	}
}
