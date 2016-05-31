package org.gcube.portlets.user.results.client.dialogBox;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * A generic XML viewer pop up. 
 * Accepts an XML string and displays it in a tree hierarchy format
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class GenericXMLViewerPopup extends GCubeDialog {

	private VerticalPanel mainPanel = new VerticalPanel();

	private String payload;
	private String divID;

	public GenericXMLViewerPopup(String payload, String divID, boolean autoShow) {
		new GenericXMLViewerPopup(payload, divID, -1, -1, autoShow);
	}


	public GenericXMLViewerPopup(String payload, String divID, int width, int height, boolean autoShow) {
		this.payload = payload;
		this.divID = divID;
		HTML treePanel = new HTML("<div id=\"" + divID + "\" style=\"height: 450px; width: 600px\"></div>");	
		if (width != -1 && height != -1)
			this.setPixelSize(width, height);
		this.setText("Metadata");
		//this.setAutoHideEnabled(true);

		Button close = new Button("Close");
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				hide();		
			}    	  
		});
		mainPanel.add(treePanel);
		mainPanel.add(new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));
		HorizontalPanel hozPanel = new HorizontalPanel();
		hozPanel.setSpacing(5);
		hozPanel.add(close);
		hozPanel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		hozPanel.setWidth("100%");	      
		mainPanel.add(hozPanel);
		this.add(mainPanel);
		if (autoShow) {
			this.show();
			showTree(this.divID, payload);
			this.center();
		}
	}

	//	@Override
	//	public void show() {
	//		this.show();
	//		showTree(divID, payload);
	//	}

	private static native void showTree(String divid, String xml) 
	/*-{
		$wnd.showTree(divid, xml);
	}-*/;

}

