package org.gcube.portlets.admin.wftemplates.client.presenter;

import org.gcube.portlets.admin.wftemplates.client.view.WfStep;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;
import com.orange.links.client.DiagramController;

public class MyDropController extends SimpleDropController {

	private static final String CSS_DEMO_BIN_DRAGGABLE_ENGAGE = "onDropTarget";

	AbsolutePanel targetPanel;
	MyDiagramController dc;

	public MyDropController(AbsolutePanel dropPanel, MyDiagramController dc2) {
		super(dropPanel);
		this.targetPanel = dropPanel;
		dc = dc2;
	}


	public void onDrop(DragContext context) {
		GWT.log("DROPPED");
		WfStep removed = (WfStep) context.draggable;	
		dc.raiseWidgetRemovedEvent(removed);
		super.onDrop(context);
	}


	public void onEnter(DragContext context) {
		super.onEnter(context);
		targetPanel.getElement().getStyle().setBackgroundColor("#333");
		targetPanel.getElement().getStyle().setBackgroundColor("rgba(51,51,51,0.50");
		targetPanel.getElement().getStyle().setOpacity(1);
	}


	public void onLeave(DragContext context) {
		GWT.log("LEAVE LEAVE ");
		targetPanel.getElement().getStyle().setBackgroundColor("#333");
		targetPanel.getElement().getStyle().setBackgroundColor("rgba(51,51,51,0.30");
		targetPanel.getElement().getStyle().setOpacity(1);
		super.onLeave(context);
	}


	public void onPreviewDrop(DragContext context) throws VetoDragException {
		super.onPreviewDrop(context);

	}
}
