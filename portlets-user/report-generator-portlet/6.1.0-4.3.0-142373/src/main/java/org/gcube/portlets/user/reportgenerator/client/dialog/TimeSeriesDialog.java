package org.gcube.portlets.user.reportgenerator.client.dialog;

import org.gcube.portlets.d4sreporting.common.shared.TimeSeriesinfo;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * @author massi
 *
 */
public class TimeSeriesDialog extends DialogBox {

	/**
	 * 
	 * @param droppedTS .
	 */
	public TimeSeriesDialog(TimeSeriesinfo droppedTS) {
		setText(droppedTS.getTitle() + " Details");
		String name = droppedTS.getTitle();
		String description = droppedTS.getTimeSeriesDescription();
		String creator = droppedTS.getCreator();
		String date = droppedTS.getTimeSeriesCreationDate();
		long rowsNo = droppedTS.getDimension();
		String publisher = droppedTS.getPublisher();
		String rights = droppedTS.getRights();

		Grid metadata = new Grid(7, 2);
		metadata.setWidget(0, 0, new HTML("Name:", true));
		metadata.setWidget(0, 1, new HTML(name));
		metadata.setWidget(1, 0, new HTML("Creation Date: ", true));
		metadata.setWidget(1, 1, new HTML(date));
		metadata.setWidget(2, 0, new HTML("Total rows number: ", true));
		metadata.setWidget(2, 1, new HTML(""+rowsNo));
		metadata.setWidget(3, 0, new HTML("Description:", true));
		metadata.setWidget(3, 1, new HTML(description));
		metadata.setWidget(4, 0, new HTML("Creator: ", true));
		metadata.setWidget(4, 1, new HTML(creator));
		metadata.setWidget(5, 0, new HTML("publisher: ", true));
		metadata.setWidget(5, 1, new HTML(""+publisher));
		metadata.setWidget(5, 0, new HTML("rights: ", true));
		metadata.setWidget(5, 1, new HTML(""+rights));

		ScrollPanel scroller = new ScrollPanel();
		VerticalPanel main_panel = null;
		main_panel = new VerticalPanel();
		main_panel.addStyleName("bgBlank p8 font_family font_12");

		scroller.add(metadata);

		// PopupPanel is a SimplePanel, so you have to set it's widget property to
		// whatever you want its contents to be.
		Button close = new Button("Close");
		close.addClickListener(new ClickListener() {
			public void onClick(Widget arg0) {
				hide();			
			}    	  
		});
		main_panel.add(scroller);
		main_panel.add(new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));
		main_panel.add(close);
		scroller.setPixelSize(350, 200);
		main_panel.setPixelSize(350, 200);
		setWidget(main_panel);
	}
	
	/**
	 * 
	 */
	public void show() {
    	super.show();
        center();
      }
}
