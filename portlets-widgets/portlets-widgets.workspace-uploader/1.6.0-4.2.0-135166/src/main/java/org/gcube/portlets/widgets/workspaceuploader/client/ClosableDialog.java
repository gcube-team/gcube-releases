/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.client;


import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.widgets.workspaceuploader.client.resource.WorkspaceUploaderResources;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;


/**
 * The Class MyDialogBox.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 9, 2015
 */
@SuppressWarnings("deprecation")
public class ClosableDialog extends GCubeDialog {

	private Image close = WorkspaceUploaderResources.getImageCloseWin();
	private HorizontalPanel captionPanel = new HorizontalPanel();
	private HTML title = new HTML("");
	/**
	 * Instantiates a new my dialog box.
	 *
	 * @param panel the panel
	 * @param autoHide the auto hide
	 * @param modal the modal
	 */
	public ClosableDialog(ScrollPanel panel, boolean autoHide, boolean modal, String captionTitle) {
		super(autoHide, modal);
		init(captionTitle);
//		super.setGlassEnabled(true);
//		super.setAnimationEnabled(true);
		setWidget(panel);
	}
	
	/**
	 * Instantiates a new my dialog box.
	 *
	 * @param panel the panel
	 * @param autoHide the auto hide
	 * @param modal the modal
	 */
	public ClosableDialog(boolean autoHide, boolean modal, String captionTitle) {
		super(autoHide, modal);
		init(captionTitle);
	}
	
	private void init(String captionTitle){
		Element td = getCellElement(0, 1);
		DOM.removeChild(td, (Element) td.getFirstChildElement());
		DOM.appendChild(td, captionPanel.getElement());
		captionPanel.setStyleName("ClosableDialogCaption");
		title.setHTML(captionTitle);
		captionPanel.add(title);
		close.setStyleName("CloseButton");
		captionPanel.add(close);
		addStyleName("ClosableDialog");
	}

	/**
	 * Instantiates a new my dialog box.
	 *
	 * @param panel the panel
	 * @param autoHide the auto hide
	 */
	public ClosableDialog(ScrollPanel panel, boolean autoHide, String captionTitle) {
		this(panel, autoHide, false, captionTitle);
	}

	/**
	 * Instantiates a new my dialog box.
	 *
	 * @param panel the panel
	 */
	public ClosableDialog(ScrollPanel panel) {
		this(panel, false, "");
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.DialogBox#getHTML()
	 */
	@Override
	public String getHTML() {
		return this.title.getHTML();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.DialogBox#getText()
	 */
	@Override
	public String getText() {
		return this.title.getText();
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.DialogBox#setHTML(java.lang.String)
	 */
	@Override
	public void setHTML(String html) {
		this.title.setHTML(html);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.DialogBox#setText(java.lang.String)
	 */
	@Override
	public void setText(String text) {
		this.title.setText(text);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.DialogBox#onPreviewNativeEvent(com.google.gwt.user.client.Event.NativePreviewEvent)
	 */
	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent event) {
		NativeEvent nativeEvent = event.getNativeEvent();

		if (!event.isCanceled() && (event.getTypeInt() == Event.ONCLICK)
				&& isCloseEvent(nativeEvent)) {
			this.hide();
		}
		super.onPreviewNativeEvent(event);
	}

	/**
	 * Checks if is close event.
	 *
	 * @param event the event
	 * @return true, if is close event
	 */
	private boolean isCloseEvent(NativeEvent event) {
		return event.getEventTarget().equals(close.getElement());// compares
																	// equality
																	// of the
																	// underlying
																	// DOM
																	// elements
	}
}
