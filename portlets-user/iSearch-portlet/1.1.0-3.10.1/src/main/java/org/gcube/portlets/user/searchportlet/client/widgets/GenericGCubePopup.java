package org.gcube.portlets.user.searchportlet.client.widgets;

import org.gcube.portlets.user.gcubewidgets.client.popup.GCubeDialog;
import org.gcube.portlets.user.searchportlet.client.CollectionsPanel;
import org.gcube.portlets.user.searchportlet.client.SearchPortlet;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A generic gcube dialog pop up with a close button. 
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class GenericGCubePopup extends GCubeDialog {

	//private VerticalPanel mainPanel = new VerticalPanel();
	private FlowPanel mainPanel = new FlowPanel();
	
	private Widget child;
	private String label;

	public GenericGCubePopup(Widget child, String label, boolean autoShow) {
		new GenericGCubePopup(child, label, -1, -1, autoShow);
	}


	public GenericGCubePopup(final Widget child, String label, int width, int height, boolean autoShow) {
		this.child = child;
		this.label = label;
		if (width != -1 && height != -1)
			this.setPixelSize(width, height);
		this.setText(this.label);
		this.setHeight("400px");

		Button close = new Button("Close");
		close.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				AsyncCallback<Void> setSelectedCollectionsToSessionCallback = new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						
					}

					@Override
					public void onSuccess(Void result) {
						
					}
				};SearchPortlet.searchService.setSelectedCollectionsToSession(((CollectionsPanel)child).getSelectedCollections(), setSelectedCollectionsToSessionCallback);
				hide();		
			}    	  
		});
		mainPanel.setWidth("100%");
		mainPanel.setHeight("100%");
		mainPanel.add(this.child);
		mainPanel.add(new HTML("<hr align=\"left\" size=\"1\" width=\"100%\" color=\"gray\" noshade>"));
		HorizontalPanel hozPanel = new HorizontalPanel();
		hozPanel.setSpacing(5);
		hozPanel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
		hozPanel.setWidth("100%");
		hozPanel.add(close);
		mainPanel.add(hozPanel);
		this.add(mainPanel);
		if (autoShow) {
			this.show();
			this.center();
		}
	}

}

