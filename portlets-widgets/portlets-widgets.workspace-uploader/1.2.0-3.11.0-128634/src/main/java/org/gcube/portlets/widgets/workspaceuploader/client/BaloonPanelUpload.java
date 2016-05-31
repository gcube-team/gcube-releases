/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.client;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;


/**
 * The Class BaloonPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 25, 2015
 */
public class BaloonPanelUpload extends PopupPanel{

	private boolean shouldHide = false;
	/**
	 * @return the shouldHide
	 */
	public boolean isShouldHide() {
		return shouldHide;
	}

	/**
	 * Instantiates a new baloon panel go top.
	 *
	 * @param baloonText the baloon text
	 * @param shouldHide true if the popup should be automatically hidden when the user clicks outside of it or the history token changes.
	 */
	public BaloonPanelUpload(ScrollPanel panel, boolean shouldHide) {
		super(shouldHide);
		this.shouldHide = shouldHide;
		setAutoHideEnabled(shouldHide);
		setStyleName("baloonGoTop");
		// some sample widget will be content of the balloon
//		HTML text = new HTML(baloonText);
		setWidget(panel);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.PopupPanel#show()
	 */
	@Override
	public void show() {
		super.show();
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.PopupPanel#hide()
	 */
	@Override
	public void hide() {
		super.hide();
	}
}