package org.gcube.portlets.user.reportgenerator.client.dialog;

import org.gcube.portlets.d4sreporting.common.shared.Table;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author massi
 *
 */
public class TimeSeriesSampleDialog extends DialogBox {
	/**
	 * 
	 * @param table .
	 */
	public TimeSeriesSampleDialog(Table table) {
		super(true);
		setText("TS Sample");
		FlexTable flexTable = new FlexTable();
		
		VerticalPanel main_panel = null;
		main_panel = new VerticalPanel();
		main_panel.addStyleName("bgBlank p8 font_family font_12");
		
		for (int i = 0; i < table.getRowCount(); i++) {
			for (int j= 0; j < table.getColsNo(); j++){
				if (i == 0) {
					flexTable.getCellFormatter().setStyleName(i, j, "timeSeries_header");
					flexTable.setWidget(i, j, new HTML(table.getValue(i, j).getContent()));
				}
				else {
					flexTable.getCellFormatter().setStyleName(i, j, "timeSeries_td");
					flexTable.setWidget(i, j, new HTML(table.getValue(i, j).getContent()));
				}
			}
		}
		
		main_panel.add(flexTable);

		// PopupPanel is a SimplePanel, so you have to set it's widget property to
		// whatever you want its contents to be.
		Button close = new Button("Close");
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();					
			}    	  
		});
	
		main_panel.add(new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));
		main_panel.add(close);
		ScrollPanel scroller = new ScrollPanel();
		main_panel.setPixelSize(550, 340);
		scroller.setPixelSize(550, 360);
		scroller.add(main_panel);
		setWidget(scroller);
	}
	
	/**
	 * 
	 */
	public void show() {
    	super.show();
        center();
      }
}
