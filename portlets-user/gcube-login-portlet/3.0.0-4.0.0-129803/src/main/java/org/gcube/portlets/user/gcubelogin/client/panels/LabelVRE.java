package org.gcube.portlets.user.gcubelogin.client.panels;

import org.gcube.portlets.user.gcubelogin.client.commons.ActionButton;
import org.gcube.portlets.user.gcubelogin.client.commons.ActionButton.ButtonType;
import org.gcube.portlets.user.gcubelogin.client.commons.UIConstants;
import org.gcube.portlets.user.gcubelogin.shared.VRE;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class LabelVRE extends Composite {
	private Grid grid_panel = null;
	private ActionButton button;

	public LabelVRE(final VRE vre) {

		grid_panel = new Grid(1, 5);
		initWidget(grid_panel);


		HTML name = new HTML(vre.getName());
		name.setStyleName("font_12 font_family font_bold");
		String description = vre.getDescription();
		//description = description.substring(0, 100) + " ... ";
		HTML descriptionHTML = new HTML(description);

		Label desc = new Label(descriptionHTML.getText());
		desc.setStyleName("font_10 font_family overflow_hidden");


		HTML dots = new HTML("...  ");	
		dots.setStyleName("font_10 font_family");


		// Add an Info button
		Image info_Button = new Image(UIConstants.INFO_IMAGE);
		info_Button.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				InfoDialog p = new InfoDialog(vre.getGroupName(), vre.getDescription());
				p.setAnimationEnabled(false);
				p.setPopupPosition(event.getClientX(), event.getClientY());
				p.show();				
			}
		});

		info_Button.addStyleName("pointer");
		name.setPixelSize(110, 15);

		try {
			switch (vre.getUserBelonging()) {
			case BELONGING:
				button = new ActionButton(vre, ButtonType.ENTER); 

				name.addStyleName("font_color_enter");
				break;
			case PENDING:
				button = new ActionButton(vre, ButtonType.PENDING); 
				name.addStyleName("font_color_pending");
				break;
			case NOT_BELONGING:
				button = new ActionButton(vre, ButtonType.ASK_4_REG); 
				name.addStyleName("font_color_ask");
				break;
			}
		} catch (NullPointerException e) {
			button = new ActionButton(vre, ButtonType.ASK_4_REG); 
			name.addStyleName("font_color_ask");
		}

		dots.setPixelSize(20, 15);
		desc.setPixelSize(550, 15);
		grid_panel.setWidget(0, 0, name);
		grid_panel.setWidget(0, 1, desc);
		grid_panel.setWidget(0, 2, dots);	
		grid_panel.setWidget(0, 3, info_Button);
		grid_panel.setWidget(0, 4, button);



	}

	public static native String getURL()/*-{
	return $wnd.location;
	}-*/;


}
