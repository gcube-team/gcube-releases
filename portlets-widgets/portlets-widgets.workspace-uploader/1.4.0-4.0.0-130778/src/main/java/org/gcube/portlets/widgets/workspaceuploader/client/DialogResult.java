/**
 * 
 */
package org.gcube.portlets.widgets.workspaceuploader.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;

/**
 * The Class DialogResult.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 4, 2014
 */
public class DialogResult extends DialogBox implements ClickHandler {

	/**
	 * Instantiates a new dialog result.
	 *
	 * @param img
	 *            the img
	 * @param title
	 *            the title
	 * @param msgTxt
	 *            the msg txt
	 */
	public DialogResult(Image img, String title, String msgTxt) {
		setText(title);
		Button closeButton = new Button("Ok", this);
		HTML msg = new HTML("<center>" + msgTxt + "</center>", true);
		msg.getElement().getStyle().setPadding(10, Unit.PX);

		DockPanel dock = new DockPanel();
		dock.setSpacing(4);
		dock.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		dock.getElement().getStyle().setBackgroundColor("#FFF");

		dock.add(closeButton, DockPanel.SOUTH);
		if (img != null)
			dock.add(img, DockPanel.WEST);

		dock.add(msg, DockPanel.CENTER);

		dock.setCellHorizontalAlignment(closeButton, DockPanel.ALIGN_CENTER);
		dock.setWidth("100%");
		dock.setHeight("100%");
		// setWidget(dock);
		add(dock);
		// setWidth("100%");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event
	 * .dom.client.ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
		hide();
	}
}