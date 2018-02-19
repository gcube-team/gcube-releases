/**
 * 
 */
package org.gcube.portlets.user.trainingcourse.client.dialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

// TODO: Auto-generated Javadoc
/**
 * The Class DialogResult.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 19, 2018
 */
public class DialogResult extends DialogBox implements ClickHandler {
	 
	/** The dock. */
	private DockPanel dock = new DockPanel();
	
	/** The close button. */
	private Button closeButton;
	
	/**
	 * Instantiates a new dialog result.
	 *
	 * @param img the img
	 * @param title the title
	 * @param msg the msg
	 */
	public DialogResult(Image img, String title, String msg) {
		getElement().setClassName("gwt-DialogBoxNew");
	    setText(title);
	    closeButton = new Button("Close", this);
	    dock.setSpacing(4);
	    dock.setWidth("100%");
	    dock.add(new HTML(msg), DockPanel.CENTER);
	    
	    dock.add(closeButton, DockPanel.SOUTH);
	    if(img!=null)
	    	dock.add(img, DockPanel.WEST);
	    
	    dock.setCellHorizontalAlignment(closeButton, DockPanel.ALIGN_RIGHT);
	    setWidget(dock);
	 }


	/**
	 * Gets the close button.
	 *
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