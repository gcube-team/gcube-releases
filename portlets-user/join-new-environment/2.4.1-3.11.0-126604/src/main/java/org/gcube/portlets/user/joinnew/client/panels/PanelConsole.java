package org.gcube.portlets.user.joinnew.client.panels;

import java.util.Date;

import org.gcube.portlets.user.joinnew.client.commons.UIConstants;

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
				
		//panel_border.add(togglePanel);
		
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
