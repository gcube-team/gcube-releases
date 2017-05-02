package org.gcube.portlets.user.tdwx.client;

import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class PagingToolBarX extends PagingToolBar {

	public PagingToolBarX(int pageSize) {
		super(pageSize);
	}

	public void fixPageTextWidth() {
		pageText.getElement()
				.setAttribute(
						"style",
						"width: 30px !important;"
								+ "margin: 0px;left: 108px;top: 2px;");

	}

}
