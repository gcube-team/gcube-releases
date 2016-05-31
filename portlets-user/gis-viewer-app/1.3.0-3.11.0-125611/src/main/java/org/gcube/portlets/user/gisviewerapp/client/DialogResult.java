/**
 *
 */
package org.gcube.portlets.user.gisviewerapp.client;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class DialogResult.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class DialogResult extends DialogBox implements ClickHandler {

	private DockPanel dock = new DockPanel();
	private Button closeButton;
	private CheckBox checkShowAgain;

	/**
	 * Instantiates a new dialog result.
	 *
	 * @param img the img
	 * @param text the text
	 */
	public DialogResult(Image img, String title, String text) {

	    setText(title);
	    closeButton = new Button("Close", this);
	    dock.setSpacing(4);
	    dock.setWidth("100%");

	    dock.add(closeButton, DockPanel.SOUTH);
	    if(img!=null)
	    	dock.add(img, DockPanel.WEST);

	    VerticalPanel vp = new VerticalPanel();
	    HTML txt = new HTML(text);
	    txt.getElement().getStyle().setPaddingLeft(5, Unit.PX);
	    vp.add(txt);

	    HorizontalPanel hp = new HorizontalPanel();
	    hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
	    checkShowAgain = new CheckBox();
//	    checkShowAgain.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
	    hp.add(checkShowAgain);
	    hp.add(new HTML("Don't show this again"));
	    hp.getElement().getStyle().setMarginTop(10, Unit.PX);
	    vp.add(hp);
	    dock.add(vp, DockPanel.CENTER);


	    dock.setCellHorizontalAlignment(closeButton, DockPanel.ALIGN_RIGHT);
	    setWidget(dock);
	 }


	/**
	 * @return the checkShowAgain
	 */
	public CheckBox getCheckShowAgain() {

		return checkShowAgain;
	}

	/**
	 * @return the closeButton
	 */
	public Button getCloseButton() {
		return closeButton;
	}


	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	@Override
	public void onClick(ClickEvent event) {
		hide();

	}

	/**
	 * Adds the to center panel.
	 *
	 * @param w the w
	 */
	public void addToCenterPanel(Widget w){
		  dock.add(w, DockPanel.CENTER);
	}
}