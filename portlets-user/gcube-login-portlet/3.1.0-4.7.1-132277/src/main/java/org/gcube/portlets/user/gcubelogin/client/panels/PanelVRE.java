package org.gcube.portlets.user.gcubelogin.client.panels;

import org.gcube.portlets.user.gcubelogin.client.commons.ActionButton;
import org.gcube.portlets.user.gcubelogin.client.commons.ActionButton.ButtonType;
import org.gcube.portlets.user.gcubelogin.client.commons.ODLFlexTable;
import org.gcube.portlets.user.gcubelogin.client.commons.PanelBorder;
import org.gcube.portlets.user.gcubelogin.client.commons.UIConstants;
import org.gcube.portlets.user.gcubelogin.shared.UserBelonging;
import org.gcube.portlets.user.gcubelogin.shared.VRE;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PanelVRE extends Composite {

	private PanelBorder main_panel = null;
	private VerticalPanel containerPanel = new VerticalPanel();
	private VRE vre;
	private Image image = null;
	private int width = UIConstants.imageVRE_width;
	private int height = UIConstants.imageVRE_height;
	private int tmp_width = UIConstants.imageVRE_width;
	private ODLFlexTable table = null;
	private ODLFlexTable hPanel = null;
	private Image info_Button = null;
	private ActionButton enter_Button = null;

	public PanelVRE(VRE vre) {
		this.vre = vre;
		Init();
		initWidget(main_panel);

	}

	/**
	 * 
	 * @param vreName .
	 * @return .
	 */
	private Grid getHeaderPanel(VRE vre) {
		Grid toReturn = new Grid(2, 1);
		toReturn.setWidth("100%");
		SimplePanel topStripe = new SimplePanel();
		SimplePanel stripeBottom = new SimplePanel();
		HTML vrelabel = new HTML(vre.getName()+".");
		vrelabel.setStyleName("vreLabel");
		topStripe.add(vrelabel);
		stripeBottom.setStyleName("bottomStripe");


		vrelabel.addStyleName("font_color_ask");
		stripeBottom.addStyleName("backcolor_blu");
		main_panel.setStyleName("border_color");

		HTML stripe = new HTML("&nbsp;");
		stripe.setWidth("100%");

		stripeBottom.add(stripe);
		stripeBottom.setWidth("100%");
		toReturn.setWidget(0, 0, topStripe);
		toReturn.setWidget(1, 0, stripeBottom);
		return toReturn;
	}

	private void Init() {

		// Create a static tree and a container to hold it
		// Wrap the static tree in a DecoratorPanel

		table = new ODLFlexTable();
		this.image = new Image(this.vre.getImageURL());

		this.image.setHeight(this.height + "px");
		this.image.setWidth(this.width + "px");
		table.setWidth(this.width + "px");
		table.setWidget(0, 0, this.image);

		hPanel = new ODLFlexTable();
		hPanel.setWidth(this.width + "px");

		// Add an Info button
		info_Button = new Image(UIConstants.INFO_IMAGE);
		info_Button.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				InfoDialog p = new InfoDialog(vre.getName(), vre.getDescription());
				p.setAnimationEnabled(false);
				p.setPopupPosition(event.getRelativeElement().getAbsoluteLeft(), event.getRelativeElement().getAbsoluteTop());
				p.show();
			}
		});
		info_Button.addStyleName("pointer");

		hPanel.setWidget(0, 0, info_Button);

		if (this.vre.getUserBelonging() == UserBelonging.BELONGING) {
			enter_Button = new ActionButton(vre,ButtonType.ENTER);
			} 
		else if (this.vre.getUserBelonging() == UserBelonging.NOT_BELONGING) {
			enter_Button = new ActionButton(vre,ButtonType.ASK_4_REG);
		} else {
			enter_Button = new ActionButton(vre,ButtonType.PENDING);
		}

		//enter_Button.ensureDebugId("cwBasicButton-normal");
		//enter_Button.setStylePrimaryName("Panel-Button");
		
		hPanel.setWidget(0, 1, enter_Button);

		hPanel.getFlexCellFormatter().setAlignment(0, 0, HasAlignment.ALIGN_LEFT, HasAlignment.ALIGN_MIDDLE);
		hPanel.getFlexCellFormatter().setAlignment(0, 1, HasAlignment.ALIGN_RIGHT, HasAlignment.ALIGN_MIDDLE);
		table.setWidget(1, 0, hPanel);

		final String[] styles = {"BlackBorder_top", "BlackBorder_middle", "BlackBorder_bottom"};
		this.main_panel = new PanelBorder(styles, 1);

		containerPanel.add(getHeaderPanel(this.vre));
		containerPanel.add(table);

		this.main_panel.setWidget(containerPanel);
	}


	public static native String getURL()/*-{
	return $wnd.location;
	}-*/;

	public void changeSizeWidth(int width) {

		if (width == 100) {
			this.image.setHeight(UIConstants.imageVRE_height + "px");
			this.image.setWidth(UIConstants.imageVRE_width + "px");
			this.hPanel.setWidth(UIConstants.imageVRE_width + "px");
			this.table.setWidth(UIConstants.imageVRE_width + "px");
			tmp_width = UIConstants.imageVRE_width;
			enter_Button.setVisible(true);
			hPanel.setWidget(0, 1, enter_Button);
		} else if (width == 50) {
			this.image.setHeight((UIConstants.imageVRE_height / 2) + 20 + "px");
			this.image.setWidth((UIConstants.imageVRE_width / 2) + 20 + "px");
			this.hPanel.setWidth((UIConstants.imageVRE_width / 2) + 20 + "px");
			this.table.setWidth((UIConstants.imageVRE_width / 2) + 20 + "px");
			tmp_width = (UIConstants.imageVRE_width / 2) + 20;
			enter_Button.setVisible(true);
			hPanel.setWidget(0, 1, enter_Button);
		}
	}

	public int getSizeWidth() {
		return this.tmp_width;
	}

	public String getName() {
		return this.vre.getName();
	}


}
