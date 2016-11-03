/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.dialog;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class DialogConfirm.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class DialogConfirm extends DialogBox implements ClickHandler {

	private DockPanel dock = new DockPanel();
	private Button yesButton;
	private HorizontalPanel hpContainer;
	
	/**
	 * Instantiates a new dialog confirm.
	 *
	 * @param img the img
	 * @param title the title
	 */
	public DialogConfirm(Image img, String title) {

		dock.setSpacing(4);
		dock.setWidth("100%");

		setText(title);

		yesButton = new Button("Yes");
		Button noButton = new Button("No", this);

		hpContainer = new HorizontalPanel();
//		hpContainer.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		hpContainer.getElement().getStyle().setMargin(20.0, Unit.PX);
		
		
		HorizontalPanel hpButtons = new HorizontalPanel();
		hpButtons.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
//		hpButtons.getElement().getStyle().setMarginTop(20.0, Unit.PX);
		hpButtons.setSpacing(3);
		yesButton.getElement().getStyle().setMarginRight(20.0, Unit.PX);
		hpButtons.add(yesButton);
		hpButtons.add(noButton);
		
		dock.add(hpButtons, DockPanel.SOUTH);
		dock.setCellHorizontalAlignment(hpButtons, DockPanel.ALIGN_CENTER);
		
		if (img != null)
			dock.add(img, DockPanel.WEST);


		dock.add(hpContainer, DockPanel.CENTER);
		
		setWidget(dock);
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

	/**
	 * Adds the to center panel.
	 *
	 * @param w the w
	 */
	public void addToCenterPanel(Widget w) {
		hpContainer.add(w);
	}

	/**
	 * Gets the dock.
	 *
	 * @return the dock
	 */
	public DockPanel getDock() {
		return dock;
	}

	/**
	 * Gets the yes button.
	 *
	 * @return the yes button
	 */
	public Button getYesButton() {
		return yesButton;
	}
}