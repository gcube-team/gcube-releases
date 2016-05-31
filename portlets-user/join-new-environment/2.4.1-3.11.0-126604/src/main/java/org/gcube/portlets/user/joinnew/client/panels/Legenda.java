package org.gcube.portlets.user.joinnew.client.panels;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

public class Legenda extends Composite {
	private SimplePanel main_panel = null;
	
	public Legenda() {
		Init();
		initWidget(main_panel);
	}
	
	private void Init() {
		this.main_panel = new SimplePanel();
//		ODLFlexTable table = new ODLFlexTable();
//		//table.setStyleName("bgBlank border_bottom border_left border_right border_top");
//		table.setCellPadding(0);
//		table.setCellSpacing(3);
//		table.setText(0, 0, UIConstants.enter_legenda);
//		table.getFlexCellFormatter().setStyleName(0, 0, "font_10 font_bold font_family");
//		table.setWidget(0, 2, new Image(UIConstants.TRASPARENT_IMAGE));
//		table.getFlexCellFormatter().setStyleName(0, 2, "backcolor_green border_bottom border_left border_right border_top");
//		table.getFlexCellFormatter().setWidth(0, 2, "12px");
//		table.setText(0, 5, UIConstants.ask_legenda);
//		table.getFlexCellFormatter().setStyleName(0, 5, "font_10 font_bold font_family");
//		table.setWidget(0, 7, new Image(UIConstants.TRASPARENT_IMAGE));
//		table.getFlexCellFormatter().setStyleName(0, 7, "backcolor_blu border_bottom border_left border_right border_top");
//		table.getFlexCellFormatter().setWidth(0, 7, "12px");
//		table.setText(0, 10, UIConstants.pending_legenda);
//		table.getFlexCellFormatter().setStyleName(0, 10, "font_10 font_bold font_family");
//		table.setWidget(0, 12, new Image(UIConstants.TRASPARENT_IMAGE));
//		table.getFlexCellFormatter().setStyleName(0, 12, "backcolor_yellow border_bottom border_left border_right border_top");
//		table.getFlexCellFormatter().setWidth(0, 12, "12px");
//		
		//this.main_panel.add(table);
		
		
	}
}
