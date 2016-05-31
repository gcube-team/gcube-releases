package org.gcube.portlets.user.results.client;


import org.gcube.portlets.user.results.client.control.Controller;
import org.gcube.portlets.user.results.client.control.FlexTableRowDragController;
import org.gcube.portlets.user.results.client.model.Model;
import org.gcube.portlets.user.results.client.panels.HeaderBar;
import org.gcube.portlets.user.results.client.panels.LeftPanel;
import org.gcube.portlets.user.results.client.panels.RecordsPanel;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;


/**
 * <code> NewResultset </code> is the entry point class
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version January 2009 (2.0) 
 */
public class ResultsDisplayer implements EntryPoint {
	
	/**
	 * 
	 */	
	public static ResultsDisplayer singleton = null;
	/**
	 * 
	 * @return .
	 */
	public static ResultsDisplayer get() {
		return singleton;
	}
	
	private Controller controller;
	private Model model;
	private RecordsPanel recordsPanel;
	private LeftPanel leftPanel;
	private HeaderBar header;
	private HeaderBar footer;
	private DockPanel mainLayout = new DockPanel();
	
	private ScrollPanel bottomScrollerPanel = new ScrollPanel();
	
	public void onModuleLoad() {		
		singleton = this;		
		controller = new Controller(this);
		
		// create a DragController to manage drag-n-drop actions
	    // note: This creates an implicit DropController for the boundary panel
	    FlexTableRowDragController dragController = new FlexTableRowDragController(RootPanel.get("resultsetDIV"));
	    //dragController.setBehaviorBoundaryPanelDrop(false);
	    //dragController.setBehaviorCancelDocumentSelections(false);
	    dragController.setBehaviorDragProxy(true);

	 	mainLayout.setWidth("100%");
		mainLayout.setHeight("100%");
		mainLayout.setSpacing(4);
		
		model = new Model(controller);
		recordsPanel = new RecordsPanel(controller, dragController);
			
		header = new HeaderBar(controller, false);
		footer = new HeaderBar(controller, true);
		leftPanel = new LeftPanel(controller,dragController );		
		
		mainLayout.add(header, DockPanel.NORTH);	
		mainLayout.add(leftPanel, DockPanel.WEST);
		//TODO added 6/5/2014
		mainLayout.add(footer, DockPanel.SOUTH);
		
		bottomScrollerPanel.add(recordsPanel);		
		mainLayout.add(bottomScrollerPanel, DockPanel.EAST);
		
		mainLayout.setCellWidth(leftPanel, "" + LeftPanel.LEFTPANEL_WIDTH);
 
		//for fixing a DnD bug
		RootPanel.get("resultsetDIV").getElement().getStyle().setProperty("position", "relative");
		
		RootPanel.get("resultsetDIV").add(mainLayout);		
		
		int scrollerHeight = Window.getClientHeight() - bottomScrollerPanel.getAbsoluteTop();
		
		bottomScrollerPanel.setPixelSize(Window.getClientWidth()- LeftPanel.LEFTPANEL_WIDTH - 50, scrollerHeight);
		
		Window.addResizeHandler(new ResizeHandler() {
			
			public void onResize(ResizeEvent event) {
				int scrollerHeight = event.getHeight() - bottomScrollerPanel.getAbsoluteTop();
				bottomScrollerPanel.setPixelSize(Window.getClientWidth()-  LeftPanel.LEFTPANEL_WIDTH - 50, scrollerHeight);		
				leftPanel.getScroller().setPixelSize(LeftPanel.LEFTPANEL_WIDTH, scrollerHeight);		
			}
		});
	}	
	
	public ScrollPanel getBottomScrollerPanel() {
		return bottomScrollerPanel;
	}

	public HeaderBar getHeader() {
		return header;
	}
	
	public HeaderBar getFooter() {
		return footer;
	}

	public LeftPanel getLeftPanel() {
		return leftPanel;
	}

	public RecordsPanel getRecordsPanel() {
		return recordsPanel;
	}
	
	public Model getModel() {
		return model;
	}
	
	public Controller getController() {
		return controller;
	}
	
	public native static void mask() /*-{
    $doc.getElementById('maskdiv').style.display = 'block';
}-*/;

	public native static void unmask() /*-{
    $doc.getElementById('maskdiv').style.display = 'none';
}-*/;

}
