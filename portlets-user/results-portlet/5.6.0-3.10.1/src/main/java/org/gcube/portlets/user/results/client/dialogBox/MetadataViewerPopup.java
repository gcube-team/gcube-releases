package org.gcube.portlets.user.results.client.dialogBox;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is a Dialog box to display an object's metadata
 * It accepts a string and displays it to a pop up
 * The string is already transformed to have the desired representation
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class MetadataViewerPopup extends GCubeDialog {
	private VerticalPanel main_panel = null;


	public MetadataViewerPopup(String transformedMetadata, int width, int height) {
		setText("Metadata");
		setModal(true);
		main_panel = new VerticalPanel();
		setAnimationEnabled(true);
		setPixelSize(width, height);
		ScrollPanel scroller = new ScrollPanel();
		scroller.setPixelSize(width, height);

		HTML metadataHTML = new HTML(transformedMetadata, true);
		scroller.add(metadataHTML);
		scroller.setStyleName("padding");

		Button close = new Button("Close");
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();		
			}    	  
		});
		main_panel.add(scroller);
		main_panel.add(new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));
		HorizontalPanel hozPanel = new HorizontalPanel();
		hozPanel.setSpacing(5);
		hozPanel.add(close);
		hozPanel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		hozPanel.setWidth("100%");	      
		main_panel.add(hozPanel);
		scroller.setPixelSize(600, 400);
		main_panel.setPixelSize(600, 450);
		setWidget(main_panel);

		this.show();
		this.center();
	}
}