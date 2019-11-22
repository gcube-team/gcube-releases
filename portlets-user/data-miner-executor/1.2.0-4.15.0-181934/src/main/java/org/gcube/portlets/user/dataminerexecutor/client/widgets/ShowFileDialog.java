package org.gcube.portlets.user.dataminerexecutor.client.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.ui.Frame;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;

/**
 * 
 * Simple file show dialog
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ShowFileDialog extends Dialog {

	private String url;
	private Frame frame;

	public ShowFileDialog(String url) {
		super();
		this.url = url;
		init();
		create();
	}

	private void init() {
		setWidth("640px");
		setHeight("480px");
		setResizable(true);
		setHeadingText("View");
		setModal(true);
		setMaximizable(true);
		setPredefinedButtons(PredefinedButton.CLOSE);
		setButtonAlign(BoxLayoutPack.CENTER);
		
	}

	private void create() {
		VerticalLayoutContainer vc = new VerticalLayoutContainer();
		vc.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				manageResize(event);
			}
		});
		frame = new Frame(url+"?content-disposition=inline");
	
		frame.getElement().setAttribute("style", "margin:auto;");
		frame.getElement().getStyle().setBorderWidth(0, Unit.PX);
		frame.getElement().getStyle().setBackgroundColor("white");
		vc.add(frame, new VerticalLayoutData(-1,-1));
		add(vc);
	}
	
	private void manageResize(ResizeEvent event){
		frame.setHeight(String.valueOf(event.getHeight())+"px");
		frame.setWidth(String.valueOf(event.getWidth())+"px");
		forceLayout();
	}
	
}