package org.gcube.portlets.user.newsfeed.client.ui;

import org.gcube.portlets.user.newsfeed.client.event.ShowNewUpdatesEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class NewFeedsAvailable extends Composite {

	private static NewFeedsAvailableUiBinder uiBinder = GWT
			.create(NewFeedsAvailableUiBinder.class);

	interface NewFeedsAvailableUiBinder extends
	
	UiBinder<Widget, NewFeedsAvailable> {
	}
	
	private HandlerManager eventBus;
	
	@UiField HTML caption;
	@UiField HTMLPanel panel;


	public NewFeedsAvailable(int newUpdatesNo, HandlerManager eventBus) {
		initWidget(uiBinder.createAndBindUi(this));
		this.eventBus = eventBus;
		if (newUpdatesNo > 0) {
			updateNewUpdatesNo(newUpdatesNo);
			//create the fade transition effect
			Timer t = new Timer() {
				@Override
				public void run() {
					caption.addStyleName("new-feeds-show");
				}
			};
			t.schedule(100);
		}
		else throw new IllegalArgumentException("newUpdatesNo must be greater than 0");
	}
	
	public void updateNewUpdatesNo(int newUpdatesNo) {
		String messageToShow = newUpdatesNo > 1 ? "See " + newUpdatesNo + " new Updates" : "See 1 new Update";
		caption.setHTML(messageToShow);
		setBrowserWindowTitle(newUpdatesNo);
	}
	
	public static void setBrowserWindowTitle (int newUpdatesNo) {
	    if (Document.get() != null) {
	    	String currTitle = Document.get().getTitle();
	    	if (currTitle.startsWith("(")) {
	    		String newTitle = "(" + newUpdatesNo + currTitle.substring(2);
	    		Document.get().setTitle(newTitle);
	    	}
	    	else	
	    		Document.get().setTitle ("("+newUpdatesNo+") " + currTitle);
	    }
	}
	
	@UiHandler("caption")
	void onClick(ClickEvent e) {
		eventBus.fireEvent(new ShowNewUpdatesEvent());
	}
}
