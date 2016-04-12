package org.gcube.portlets.admin.wftemplates.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.gcube.portlets.admin.wfdocslibrary.shared.EdgeDirection;
import org.gcube.portlets.admin.wfdocslibrary.shared.EdgePoint;
import org.gcube.portlets.admin.wfdocslibrary.shared.ForwardAction;
import org.gcube.portlets.admin.wfdocslibrary.shared.Step;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRoleDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfTemplate;
import org.gcube.portlets.admin.wftemplates.client.WfTemplatesServiceAsync;
import org.gcube.portlets.admin.wftemplates.client.event.DeleteTemplateEvent;
import org.gcube.portlets.admin.wftemplates.client.view.WfStep;
import org.gcube.portlets.admin.wftemplates.client.view.WfTemplatesView;
import org.gcube.portlets.admin.wftemplates.client.view.dialog.AddRolesDialog;
import org.gcube.portlets.admin.wftemplates.client.view.dialog.AddStepDialog;
import org.gcube.portlets.admin.wftemplates.client.view.dialog.AddTemplateDialog;

import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.orange.links.client.DiagramController;
import com.orange.links.client.connection.Connection;
import com.orange.links.client.event.TieLinkEvent;
import com.orange.links.client.event.TieLinkHandler;
import com.orange.links.client.utils.Direction;
import com.orange.links.client.utils.Point;

/**
 * <code> WfTemplatesPresenter </code> class is the presenter component accorinding to the MVP model of this webapp
 *@see Presenter
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version May 2011 (0.1) 
 */
public class WfTemplatesPresenter implements Presenter {
	/*
	 * the view componente must implement the following methods of the Display interface
	 */
	public interface Display {
		HasClickHandlers getSaveButton();
		HasClickHandlers getCreateNewButton();
		HasClickHandlers getAddNewStepButton();
		HasClickHandlers getDeleteButton();
		HasClickHandlers getResetButton();

		SingleSelectionModel<WfTemplate> getTableSelectionModel();

		MyDiagramController getDiagramController();

		void enableSaveButton(boolean enabled);
		void enableDeleteButton(boolean enabled);
		void setData(List<WfTemplate> list);

		void displaySelectedWfTemplate(WfGraph graph,TieLinkHandler linkHandler, HandlerManager eventBus);
		void showCreateNewTemplate(TieLinkHandler linkHandler, HandlerManager eventBus);

		void enableGraphControlPanel(boolean enabled);
		void enableDropPanel(boolean enabled);
		void showLoading(boolean show);
		void addNewStep(String stepName, String description, int...leftTop);
		void addNewRoles(Connection selectedEdge, String toDisplay);
		Widget asWidget();		
		
		void resetView();
	}

	private final WfTemplatesServiceAsync rpcService;
	private final HandlerManager eventBus;
	private final Display display;
	private final TieLinkHandler linkHandler;
	private ArrayList<WfRoleDetails> rolesCache = null;
	private HashMap<Connection, ArrayList<WfRoleDetails>> edgesMap;

	/**
	 * 		
	 * @param rpcService .
	 * @param eventBus .
	 * @param display .
	 */
	public WfTemplatesPresenter(final WfTemplatesServiceAsync rpcService, final HandlerManager eventBus, Display display) {
		super();
		this.rpcService = rpcService;
		this.eventBus = eventBus;
		this.display = display;
		linkHandler = new TieLinkHandler() {			
			public void onTieLink(TieLinkEvent event) {
				showAddRoleDialog(event.getConnection());
			}
		};
		edgesMap = new HashMap<Connection, ArrayList<WfRoleDetails>>();
		display.enableDeleteButton(false);
	}
	/**
	 * method used to catch and consequently fire the events raised from the view controls
	 */
	public void bind() {		
		display.getSaveButton().addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				AddTemplateDialog dlg = new AddTemplateDialog(eventBus);
				dlg.show();
			}
		});

		display.getCreateNewButton().addClickHandler(new ClickHandler() {   
			public void onClick(ClickEvent event) {
				edgesMap = new HashMap<Connection, ArrayList<WfRoleDetails>>();
				display.showCreateNewTemplate(linkHandler, eventBus);
				display.enableGraphControlPanel(true);
				display.enableDeleteButton(false);
				display.getTableSelectionModel().setSelected(display.getTableSelectionModel().getSelectedObject(), false);
				display.enableDropPanel(true);
			}
		});

		display.getDeleteButton().addClickHandler(new ClickHandler() {   
			public void onClick(ClickEvent event) {
				WfTemplate toDelete = display.getTableSelectionModel().getSelectedObject();
				if (Window.confirm("Are you sure you want to delete the selected Workflow Template?"))
					eventBus.fireEvent(new DeleteTemplateEvent(toDelete));
			}
		});

		display.getResetButton().addClickHandler(new ClickHandler() {   
			public void onClick(ClickEvent event) {
				edgesMap = new HashMap<Connection, ArrayList<WfRoleDetails>>();
				display.showCreateNewTemplate(linkHandler, eventBus);
				display.enableGraphControlPanel(true);
				display.getTableSelectionModel().setSelected(display.getTableSelectionModel().getSelectedObject(), false);
				display.enableDeleteButton(false);
			}
		});

		display.getAddNewStepButton().addClickHandler(new ClickHandler() {   
			public void onClick(ClickEvent event) {
				AddStepDialog dlg = new AddStepDialog(eventBus);
				dlg.setWidth(400);
				dlg.show();
			}
		});

		// Add a selection model to handle user selection on the table	
		display.getTableSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			public void onSelectionChange(SelectionChangeEvent event) {
				WfTemplate selected = 	display.getTableSelectionModel().getSelectedObject();
				if (selected != null) {
					display.displaySelectedWfTemplate(selected.getGraph(), linkHandler, eventBus);
					display.enableGraphControlPanel(false);
					display.enableDropPanel(false);						
					display.enableDeleteButton(true);
				}
			}
		});
	}

	/**
	 * go method, executed at the beginning
	 */
	public void go(HasWidgets container) {
		bind();
		container.clear();
		container.add(display.asWidget());	
		fetchTemplatesList();
	}

	@Override
	public void addRolesOnConnection(Connection selectedEdge, ArrayList<WfRoleDetails> roles) {
		edgesMap.put(selectedEdge, roles);
		String toDisplay = "";
		for (int i = 0; i < roles.size(); i++) {
			toDisplay += roles.get(i).getDisplayName() + " ";
		}
		display.addNewRoles(selectedEdge, toDisplay);
	}
	/**
	 * 
	 */
	private void showAddRoleDialog(final Connection selectedEdge) {		
		if (rolesCache == null) {		
			rpcService.getRoleDetails(new AsyncCallback<ArrayList<WfRoleDetails>>() {
				@Override
				public void onSuccess(ArrayList<WfRoleDetails> roles) {
					AddRolesDialog dlg = new AddRolesDialog(eventBus, selectedEdge, roles);
					dlg.show();
					rolesCache = roles;
				}
				@Override
				public void onFailure(Throwable arg0) {
					Info.display("Error on Server", "possible cause: "+arg0.getMessage());
				}
			});
		}
		else {
			AddRolesDialog dlg = new AddRolesDialog(eventBus, selectedEdge, rolesCache);
			dlg.show();
		}
	}

	private void fetchTemplatesList() {
		display.showLoading(true);

		rpcService.getTemplates(new AsyncCallback<ArrayList<WfTemplate>>() {

			@Override
			public void onSuccess(ArrayList<WfTemplate> list) {
				display.showLoading(false);
				display.setData(list);				
			}

			@Override
			public void onFailure(Throwable arg0) {
				display.showLoading(false);
				Window.alert("Error while trying to fetch templates: " + arg0.getMessage());
			}
		});
	}

	public void addNewStep(String label, String description) {
		display.addNewStep(label, description);
	}



	/**
	 * called when saving the template, send the template to the service
	 */
	@Override
	public void doSaveTemplate(String templateName) {
		WfGraph toSave = getWorkflowFromView();
		GWT.log("Calling SaveTemplate()");
		rpcService.saveTemplate(templateName, toSave, new AsyncCallback<Boolean>() {
			public void onFailure(Throwable arg0) {
				Info.display("Saving Failure", "Server reports: " +  arg0.getCause());
				for (int i = 0; i < arg0.getStackTrace().length; i++) {
					GWT.log(arg0.getStackTrace()[i].toString());
				}
			}
			public void onSuccess(Boolean result) {
				if (result) {
					Info.display("Saving Successful", "Workflow correctly saved");
					edgesMap = new HashMap<Connection, ArrayList<WfRoleDetails>>();
					display.resetView();
					fetchTemplatesList();
					
				}
				else
					Info.display("Saving Problem", "Workflow not saved");	
			}
		});
	}
	/**
	 * return the WfGraph instance taken from the View in that moment
	 * @return
	 */
	private WfGraph getWorkflowFromView() {
		WfGraph wf = null;
		//get the steps
		Step[] steps = getSteps().toArray(new Step[0]);
		for (int i = 0; i < steps.length; i++) {
			GWT.log("Step " + steps[i].getLabel());
		}
		GWT.log("Instanzio WfGraph(steps)");
		//set the step
		wf = new WfGraph(steps);
		/*
		 * for each connection, add the edge to the graph
		 */
		for (Entry<Connection, ArrayList<WfRoleDetails>> elem : edgesMap.entrySet()) {
			Connection c = elem.getKey();
			Step start = getStepByCoordinates(c.getStartShape().getLeft(), c.getStartShape().getTop());
			Step end = getStepByCoordinates(c.getEndShape().getLeft(), c.getEndShape().getTop());
			GWT.log("Before getEdgePointsFromConnection ");
			ArrayList<EdgePoint> points = getEdgePointsFromConnection(c);
			GWT.log("After getEdgePointsFromConnection ");
			WfRole[] roles = new WfRole[0];
			if ( elem.getValue() != null ) {
				roles = new WfRole[elem.getValue().size()];
				for (int i = 0; i < roles.length; i++) {
					roles[i] = new WfRole(elem.getValue().get(i).getId(), elem.getValue().get(i).getDisplayName(), "");
				}
			}		
			GWT.log("Before addEdge");
			wf.addEdge(start, end, new ForwardAction(points, roles));
			GWT.log("Edge added");
		}
		return wf;
	}


	/**
	 * 
	 * @param c the connection
	 * @return the list of points where the connection bends
	 */
	private ArrayList<EdgePoint> getEdgePointsFromConnection(Connection c) {
		ArrayList<EdgePoint> toReturn = new ArrayList<EdgePoint>();
		for (Point p : c.getMovablePoints() ) {
			EdgePoint ep = new EdgePoint(p.getLeft(), p.getTop());
			if (p.getDirection() != null) 
				ep.setEdgeDirection(getEdgeDirection(p.getDirection()));
			toReturn.add(ep);
		}
		return toReturn;
	}
	/**
	 * simply converts the Direction to a Serializable version
	 * @param d .
	 * @return .
	 */
	private EdgeDirection getEdgeDirection(Direction d) {
		return new EdgeDirection(d.toString(), d.getAngle());
	}

	/**
	 * 
	 * @param left l
	 * @param top t
	 * @return the Step instance associated to the given left and top
	 */
	private Step getStepByCoordinates(int left, int top) {
		for (Step s : getSteps()) {
			if (s.getLeft() == left && s.getTop() == top)
				return s;
		}
		return null;
	}
	/**
	 * 
	 * @return the list of Steps in the current view
	 */
	private ArrayList<Step> getSteps() {
		ArrayList<Step> toReturn = new ArrayList<Step>();
		//get the container panel	
		AbsolutePanel container = display.getDiagramController().getView();
		int containerLeft = container.getAbsoluteLeft();
		int containerTop = container.getAbsoluteTop();
		int wtsNO = container.getWidgetCount();
		for (int i = 0; i < wtsNO; i++) {
			Widget w = container.getWidget(i);
			int left = w.getAbsoluteLeft() -  containerLeft;
			int top = +w.getAbsoluteTop() - containerTop;
			if (w instanceof WfStep) {
				WfStep toAdd = (WfStep) w;
				if (toAdd.getText().equalsIgnoreCase("Start"))
					toReturn.add(0, new Step(left, top, toAdd.getText(), toAdd.getDescription()));
				else
					toReturn.add(new Step(left, top, toAdd.getText(), toAdd.getDescription()));
			}				
		}
		return toReturn;
	}
	/**
	 * remove the connection from the model
	 */
	@Override
	public void doRemoveConnectionFromModel(Connection selected) {
		edgesMap.remove(selected);		
	}
	/**
	 * remove the connections attached to the step from the model
	 */
	@Override
	public void doRemoveStep(WfStep step) {
		AbsolutePanel container = display.getDiagramController().getView();
		int containerLeft = container.getAbsoluteLeft();
		int containerTop = container.getAbsoluteTop();
		int left = step.getAbsoluteLeft() -  containerLeft;
		int top = +step.getAbsoluteTop() - containerTop;
		ArrayList<Connection> toRemove = new ArrayList<Connection>();
		GWT.log("doRemoveStep() Step: " + left + " - " + top);
		for (Entry<Connection, ArrayList<WfRoleDetails>> elem : edgesMap.entrySet()) {
			Connection c = elem.getKey();
			//removes start connections
			if (c.getStartShape().getLeft() ==  left && c.getStartShape().getTop() == top) {
				toRemove.add(c);
				continue;
			}
			//removed end connections
			if (c.getEndShape().getLeft() ==  left && c.getEndShape().getTop() == top) {
				toRemove.add(c);
				continue;
			}
		}
		//effectively removes the connections
		for (Connection connection : toRemove) {
			display.getDiagramController().deleteConnection(connection);
		}
	}
	@Override
	public void doRemoveConnectionFromView(Connection selected) {
		DiagramController myDC = (DiagramController) display.getDiagramController();
		myDC.deleteConnection(selected);		
	}
	@Override
	public void doDeleteTemplate(WfTemplate toDelete) {
		rpcService.deleteTemplate(toDelete, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable arg0) {
				Info.display("Error", "Server reported error while trying to delete the template");

			}

			@Override
			public void onSuccess(Boolean arg0) {
				Info.display("Success", "Workflow Template correctly deleted");	
				edgesMap = new HashMap<Connection, ArrayList<WfRoleDetails>>();
				display.resetView();
				fetchTemplatesList();
			}
		});

	}
}
