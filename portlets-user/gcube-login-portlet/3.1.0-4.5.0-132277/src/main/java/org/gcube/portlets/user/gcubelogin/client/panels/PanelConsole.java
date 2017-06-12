package org.gcube.portlets.user.gcubelogin.client.panels;

import java.util.Date;

import org.gcube.portlets.user.gcubelogin.client.commons.UIConstants;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class PanelConsole extends Composite {
	
	private static PanelConsole singleton = null;
	private HorizontalPanel main_panel = null;
	private PanelFilter filter = null;
	public ToggleButton toggleButton0;
	public ToggleButton toggleButton50;
	public ToggleButton toggleButton100;
	
	public static PanelConsole get()
	{ 
		return singleton;
	}
	
	public PanelConsole() {
		Init();
		initWidget(main_panel);
		if (singleton == null) singleton = this;
	}
	
	private void Init() {
		main_panel = new HorizontalPanel();
		main_panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		//main_panel.setStyleName("console");
		this.filter = new PanelFilter();
		
		SimplePanel panel_border = new SimplePanel();
		HorizontalPanel togglePanel = new HorizontalPanel();
		togglePanel.setStyleName("p4");
		
		Image im_list = new Image(UIConstants.application_view_list);
		im_list.setTitle("view as a list");
		toggleButton0 = new ToggleButton(im_list);


		
		Image im_icons = new Image(UIConstants.application_view_icons);
		im_icons.setTitle("view as icons");
		toggleButton50 = new ToggleButton(im_icons);

		
		Image im_title = new Image(UIConstants.application_view_tile);
		im_title.setTitle("view as images");
		toggleButton100 = new ToggleButton(im_title);
		
		toggleButton0.setStyleName("selectable");
		toggleButton50.setStyleName("selectable");
		toggleButton100.setStyleName("selectable");

		//normalToggleButton.ensureDebugId("cwCustomButton-toggle-normal");
		togglePanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));
		togglePanel.add(toggleButton0);
		togglePanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));
		togglePanel.add(toggleButton50);
		togglePanel.add(new HTML("&nbsp;&nbsp;&nbsp;"));
		togglePanel.add(toggleButton100);

		toggleButton0.addClickListener(new ClickListener() {
			
			public void onClick(Widget arg0) {
				
				if (!toggleButton0.isDown()) {
					toggleButton0.setDown(true);
				} else {
					PanelBody.get().setVisible(false);
					PanelBody.get().changeSizeWidth(0);
					PanelBody.get().setVisible(true);
					toggleButton50.setDown(false);
					toggleButton100.setDown(false);
					Cookies.setCookie(UIConstants.COOKIE_NAME, "0", getExpiryDate());
				}
			}
		});
		toggleButton50.addClickListener(new ClickListener() {
			
			public void onClick(Widget arg0) {
				if (!toggleButton50.isDown()) {
					toggleButton50.setDown(true);				
				} else {
					PanelBody.get().setVisible(false);
					PanelBody.get().changeSizeWidth(50);
					PanelBody.get().setVisible(true);
					toggleButton100.setDown(false);
					toggleButton0.setDown(false);
					Cookies.setCookie(UIConstants.COOKIE_NAME, "1", getExpiryDate());
				}
			}
		});
		toggleButton100.addClickListener(new ClickListener() {
			
			public void onClick(Widget arg0) {
				
				if (!toggleButton100.isDown()) {
					toggleButton100.setDown(true);					
				} else {
					PanelBody.get().setVisible(false);
					PanelBody.get().changeSizeWidth(100);
					PanelBody.get().setVisible(true);
					toggleButton0.setDown(false);
					toggleButton50.setDown(false);
					Cookies.setCookie(UIConstants.COOKIE_NAME, "2", getExpiryDate());
				}
			}
		});

		
		panel_border.add(togglePanel);
		main_panel.setCellHorizontalAlignment(togglePanel, HasHorizontalAlignment.ALIGN_CENTER);
		main_panel.add(panel_border);
		main_panel.add(this.filter);

		main_panel.setCellHorizontalAlignment(this.filter, HasHorizontalAlignment.ALIGN_RIGHT);
		main_panel.setWidth("100%");
		//main_panel.setStyleName("backcolor_green");
	}
	
	
	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Date getExpiryDate() {
		Date expiryDate = new Date();		
		int month = expiryDate.getMonth();
		month += UIConstants.COOKIE_MONTHS_EXPIRY_TIME ;
		expiryDate.setMonth(month);		
		return expiryDate;
	}
	
}
