package org.gcube.portlets.widgets.githubconnector.client.util;

import org.gcube.portlets.widgets.githubconnector.client.resource.GCResources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class WaitDialog extends DialogBox {
	private HandlerRegistration resizeHandlerRegistration;
	private HTML msg;
	private String msgWidth = "200px";
	private String msgHeight = "25px";
	private Timer t;

	public WaitDialog(int zIndex){
		init("Please Wait", "Working...", zIndex);
	}
	
	public WaitDialog(String title, String text, int zIndex) {
		init(title, text, zIndex);
	}

	private void init(String title, String text, int zIndex) {
		GWT.log("WaitDialog:[title=" + title + ", text=" + text + "]");
		GCResources.INSTANCE.wizardCSS().ensureInjected();
		setModal(true);
		setGlassEnabled(true);
		initHandler();
		setText(title);
		
		msg = new HTML("<div class='"
				+ GCResources.INSTANCE.wizardCSS().getProgressBarContainer()
				+ "'>" + "<div  class='"
				+ GCResources.INSTANCE.wizardCSS().getProgressBar()
				+ "' style='width:50%'></div>" + "<div class='"
				+ GCResources.INSTANCE.wizardCSS().getProgressBarText() + "'>"
				+ text + "</div>" + "</div><br>");

		msg.setWidth(msgWidth);
		msg.setHeight(msgHeight);

		setWidget(msg);
		
		if(zIndex>0){
			getGlassElement().getStyle().setZIndex(zIndex+2);
			getElement().getStyle().setZIndex(zIndex+3);
		}
		center();
		startTimer();
	}

	private void initHandler() {
		resizeHandlerRegistration = Window
				.addResizeHandler(new ResizeHandler() {

					@Override
					public void onResize(ResizeEvent event) {
						center();

					}
				});

	}

	private void startTimer() {
		t = new Timer() {
			private int width = 0;

			@Override
			public void run() {
				if (width > 100) {
					width = 0;
				}
				msg.getElement().getFirstChildElement().getFirstChildElement()
						.getStyle().setWidth(width, Unit.PCT);
				width += 10;
			}
		};

		// Schedule the timer to run once in 200 millseconds.
		t.scheduleRepeating(400);

	}

	private void stopTimer() {
		t.cancel();
	}

	@Override
	public void hide() {
		stopTimer();
		if (resizeHandlerRegistration != null) {
			resizeHandlerRegistration.removeHandler();
			resizeHandlerRegistration = null;
		}
		super.hide();
	}

}