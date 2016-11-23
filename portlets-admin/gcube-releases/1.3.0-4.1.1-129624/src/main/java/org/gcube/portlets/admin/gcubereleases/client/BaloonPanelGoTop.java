/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.PopupPanel;


/**
 * The Class BaloonPanel.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 25, 2015
 */
public class BaloonPanelGoTop extends PopupPanel{

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
	 * @param shouldHide the should hide
	 */
	public BaloonPanelGoTop(String baloonText, boolean shouldHide) {
		super(shouldHide);
		this.shouldHide = shouldHide;
		setAutoHideEnabled(shouldHide);
		setStyleName("baloonGoTop");
		// some sample widget will be content of the balloon

		Anchor anchor = new Anchor(baloonText);
		anchor.setHref("#HeaderPage");
		anchor.setStyleName("color-white");
//		HTML text = new HTML(baloonText);
		setWidget(anchor);
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