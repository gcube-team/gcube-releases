/**
 *
 */
package org.gcube.portlets.widgets.wsthreddssync.client.dialog;


import org.gcube.portlets.widgets.wsthreddssync.client.resource.Icons;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;



/**
 * The Class PanelConfirmBuilder.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 13, 2018
 */
public abstract class PanelConfirmBuilder {

	private DockPanel dock = new DockPanel();
	private Button yesButton;
	private VerticalPanel vpContainer;
	private ImageResource loading = Icons.ICONS.loading();
	private HorizontalPanel hpButtons = new HorizontalPanel();
	private Button noButton;

	/**
	 * On click no button.
	 */
	public abstract void onClickNoButton();

	/**
	 * On click yes button.
	 */
	public abstract void onClickYesButton();


	/**
	 * Instantiates a new panel confirm builder.
	 *
	 * @param img the img
	 * @param caption the caption
	 * @param text the text
	 */
	public PanelConfirmBuilder(Image img, String caption, String text, AlertType type) {
		dock.setSpacing(4);
		dock.setWidth("100%");
//		setHeading(caption);

		yesButton = new Button("Yes");

		yesButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				onClickYesButton();
			}
		});

		noButton = new Button("No");

		noButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onClickNoButton();
			}
		});

		vpContainer = new VerticalPanel();
		vpContainer.getElement().getStyle().setMargin(20.0, Unit.PX);
		Alert txt = new Alert(text);
		txt.setType(type);
		txt.setClose(false);
		vpContainer.add(txt);
		hpButtons = new HorizontalPanel();
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

		dock.add(vpContainer, DockPanel.CENTER);
//		add(dock);
	}


	/**
	 * Loader.
	 *
	 * @param message the message
	 */
	public void loader(String message){
		try{
			dock.remove(hpButtons);
		}catch(Exception e){}
		vpContainer.clear();
		HorizontalPanel hpMask = new HorizontalPanel();
		hpMask.add(new Image(loading));
		HTML html = new HTML(message);
		html.getElement().getStyle().setMarginLeft(5, Unit.PX);
		hpMask.add(html);
		vpContainer.add(hpMask);
	}

	/**
	 * Adds the to center panel.
	 *
	 * @param w the w
	 */
	public void addToCenterPanel(Widget w) {
		vpContainer.add(w);
	}

	/**
	 * Gets the dock.
	 *
	 * @return the dock
	 */
	public DockPanel getPanel() {
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

	/**
	 * Gets the no button.
	 *
	 * @return the no button
	 */
	public Button getNoButton() {
		return noButton;
	}
}