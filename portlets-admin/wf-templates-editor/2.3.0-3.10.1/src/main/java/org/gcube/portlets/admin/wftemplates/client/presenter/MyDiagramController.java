package org.gcube.portlets.admin.wftemplates.client.presenter;

import org.gcube.portlets.admin.wftemplates.client.event.ConnectionRemovedEvent;
import org.gcube.portlets.admin.wftemplates.client.event.StepRemovedEvent;
import org.gcube.portlets.admin.wftemplates.client.view.WfStep;

import com.google.gwt.event.shared.HandlerManager;
import com.orange.links.client.DiagramController;
import com.orange.links.client.canvas.DiagramCanvas;
import com.orange.links.client.connection.Connection;
/**
 * 
 * @author massi
 *
 */
public class MyDiagramController extends DiagramController {
	
	 HandlerManager eventBus;
	
	public MyDiagramController(DiagramCanvas canvas, HandlerManager eventBus) {
		super(canvas);
		this.eventBus = eventBus;
	}
	@Override
	public void deleteConnection(Connection c) {
		super.deleteConnection(c);
		eventBus.fireEvent(new ConnectionRemovedEvent(c));
	}

	public void raiseWidgetRemovedEvent(WfStep removed) {
		eventBus.fireEvent(new StepRemovedEvent(removed));
	}
}

