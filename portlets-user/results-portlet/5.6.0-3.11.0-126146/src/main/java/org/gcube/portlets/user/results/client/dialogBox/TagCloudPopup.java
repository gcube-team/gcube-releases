package org.gcube.portlets.user.results.client.dialogBox;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.widget.collectionsindexedwords.client.IndexVisualisationPanel;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is a Dialog box to display an object's metadata
 * It accepts a string and displays it to a pop up
 * The string is already transformed to have the desired representation
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class TagCloudPopup extends GCubeDialog {
	private VerticalPanel main_panel = null;


	public TagCloudPopup(Controller control, int width, int height) {
		setText("");
		setModal(true);
		setAutoHideEnabled(true);
		main_panel = new VerticalPanel();
		setAnimationEnabled(true);
		setPixelSize(width, height);
	
		
		final IndexVisualisationPanel ivp = new IndexVisualisationPanel(Document.get().createUniqueId(), 400, 300);
		main_panel.add(ivp);
		AsyncCallback<Integer> getCurrentQueryIndexCallback = new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Integer arg0) {
				if (arg0 != null) {
					ivp.visualiseQueryStats(arg0, 50);
				}
				else {
					TagCloudPopup.this.hide();
					Window.alert("Cannot visualize the terms of the current query");
				}
			}
		};control.getNewresultset().getModel().getResultService().getCurrentQueryIndexNumber(getCurrentQueryIndexCallback);

//		Button close = new Button("Close");
//		close.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				hide();		
//			}    	  
//		});
//		main_panel.add(new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));
//		HorizontalPanel hozPanel = new HorizontalPanel();
//		hozPanel.setSpacing(5);
//		hozPanel.add(close);
//		hozPanel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
//		hozPanel.setWidth("100%");	      
//		main_panel.add(hozPanel);
		main_panel.setPixelSize(400, 305);
		setWidget(main_panel);

		this.show();
		this.center();
	}
}