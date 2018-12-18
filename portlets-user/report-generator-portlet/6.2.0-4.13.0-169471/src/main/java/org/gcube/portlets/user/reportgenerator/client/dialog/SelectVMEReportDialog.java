package org.gcube.portlets.user.reportgenerator.client.dialog;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.reportgenerator.client.ReportService;
import org.gcube.portlets.user.reportgenerator.client.ReportServiceAsync;
import org.gcube.portlets.user.reportgenerator.client.events.SelectedReportEvent;
import org.gcube.portlets.user.reportgenerator.shared.VMEReportBean;
import org.gcube.portlets.user.reportgenerator.shared.VMETypeIdentifier;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class SelectVMEReportDialog extends Window {
	private static final int WIDTH = 700;
	private static final int HEIGHT = 450;

	private ReportServiceAsync reportService = (ReportServiceAsync) GWT.create(ReportService.class);
	private ContentPanel mainPanel = new ContentPanel();
	private HandlerManager eventBus;
	private HorizontalPanel hp = new HorizontalPanel();
	private StoreFilterField<VMEReportBean> filter;
	private VMETypeIdentifier type;

	public enum Action {SELECT, ASSOCIATE, DELETE }

	/**
	 * 
	 * @param eventBus the bus to fire events into
	 */
	public SelectVMEReportDialog(final HandlerManager eventBus, VMETypeIdentifier type, final Action action) {
		this.eventBus = eventBus;
		this.type = type;
		setModal(true);
		setResizable(false);
		mainPanel.setHeaderVisible(false);
		setWidth(WIDTH);  
		setHeight(HEIGHT);  

		mainPanel.setWidth(WIDTH-10);
		mainPanel.setHeight(HEIGHT-50);		

		filter = new StoreFilterField<VMEReportBean>() {

			@Override
			protected boolean doSelect(Store<VMEReportBean> store, VMEReportBean parent, VMEReportBean record,	String property, String filter) {
				String name = record.getName();
				name = name.toLowerCase();
				if (name.contains(filter.toLowerCase())) {
					return true;
				}
				return false;
			}
		};
		filter.setEmptyText("Filter by ...");
		filter.setWidth("250px");
		hp.add(filter);
		hp.setPixelSize(250, 20);
		hp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);


		add(hp);
		add(mainPanel);
		showLoading();

		setHeading(getHeading(type, action));
		if (type == VMETypeIdentifier.Vme) {
			reportService.listVMEReports(new AsyncCallback<ArrayList<VMEReportBean>>() {
				@Override
				public void onFailure(Throwable caught) {
					mainPanel.unmask();			
					showCommunicationError();
				}

				@Override
				public void onSuccess(ArrayList<VMEReportBean> refReports) {
					mainPanel.unmask();	
					showAvailableReports(refReports, action);
				}
			});
		} else {
			reportService.listVMEReportRefByType(type, new AsyncCallback<ArrayList<VMEReportBean>>() {
				@Override
				public void onFailure(Throwable caught) {
					mainPanel.unmask();	
					showCommunicationError();
				}

				@Override
				public void onSuccess(ArrayList<VMEReportBean> refReports) {
					mainPanel.unmask();	
					showAvailableReports(refReports, action);
				}
			});
		}
	}

	private void showCommunicationError() {
		mainPanel.add(new Html(""
				+ "<div style=\"text-align: center; font-size: 16px; margin: 100px 20px;\">We're sorry, it seems something is broken in the communication with the VMEs Repository. "
				+ "</div>"
				+ "<div style=\"text-align: center; font-size: 16px; margin: 50px 20px;\">"
				+ "In the meantime you may <a target=\"_blank\" href=\"https://support.d4science.research-infrastructures.eu\">report the issue.<a/></div>"
				+ ""));
		mainPanel.setLayout(new FitLayout()); 
		mainPanel.layout();
	}
	/**
	 * 
	 * @param reports
	 * @param isAssociation
	 */
	public void showAvailableReports(List<VMEReportBean> reports, final Action theAction) {  

		ColumnModel cm = null;
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  


		ColumnConfig column = new ColumnConfig();  

		column.setId("rfmo");  
		column.setHeader("Owner");  
		column.setWidth(15);  
		column.setHidden(true);
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("name");  
		column.setHeader(type == VMETypeIdentifier.Vme ? "VME Title" : "Identifier");  
		column.setWidth(150);  
		configs.add(column);  

		column = new ColumnConfig();  
		column.setId("id");  
		column.setHeader("ID");
		column.setWidth(6);  
		configs.add(column);  
		cm = new ColumnModel(configs); 

		/**
		 * load the grid data
		 */
		GroupingStore<VMEReportBean> store = new GroupingStore<VMEReportBean>();
		store.add(reports);  
		if (type != VMETypeIdentifier.Rfmo)
			store.groupBy("rfmo");


		filter.setEmptyText(type == VMETypeIdentifier.Vme ? "Filter by Title" : "Filter by Identifier");
		filter.bind(store);

		final Grid<VMEReportBean> grid = new Grid<VMEReportBean>(store, cm); 

		grid.getElement().getStyle().setBorderWidth(0, Unit.PX);
		grid.setAutoExpandColumn("name"); 
		grid.setBorders(false); 
		grid.setStripeRows(true);


		GroupingView view = new GroupingView();  
		view.setShowGroupedColumn(true); 
		view.setStartCollapsed(true);
		grid.setView(view);

		grid.addListener(Events.OnMouseOver, new Listener<GridEvent<VMEReportBean>>(){
			@Override
			public void handleEvent(GridEvent<VMEReportBean> ge) {
				if (ge != null && ge.getModel() != null) {
					grid.setTitle(ge.getModel().getName());
				}
			}
		});

		grid.addListener(Events.CellDoubleClick, new Listener<GridEvent<VMEReportBean>>(){
			@Override
			public void handleEvent(GridEvent<VMEReportBean> ge) {
				if (ge != null && ge.getModel() != null) {
					hide();
					switch (theAction) {
					case SELECT:
						openSelected(grid.getSelectionModel().getSelectedItem(), type, theAction);
						break;
					case ASSOCIATE:
						associateSelected(grid.getSelectionModel().getSelectedItem(), type, theAction);
						break;
					case DELETE:
						deleteSelected(grid.getSelectionModel().getSelectedItem(), type, theAction);
						break;
					}											
				}
			}
		});

		final ColumnModel finalCM = cm;
		view.setGroupRenderer(new GridGroupRenderer() {  
			public String render(GroupColumnData data) {  
				String f = finalCM.getColumnById(data.field).getHeader();
				String l = data.models.size() == 1 ? "Item" : "Items";  
				return f + ": " + data.group + " (" + data.models.size() + " " + l + ")";  
			}  
		});  

		view.setForceFit(true); 
		ContentPanel gridPanel = new ContentPanel(new FitLayout());
		gridPanel.setHeaderVisible(false);
		gridPanel.add(grid); 



		gridPanel.addButton(new Button("Cancel", new SelectionListener<ButtonEvent>() {  
			@Override  
			public void componentSelected(ButtonEvent ce) {  
				hide();
			}  
		})); 

		switch (theAction) {
		case SELECT:
			gridPanel.addButton(new Button("Open Selected", new SelectionListener<ButtonEvent>() {  
				@Override  
				public void componentSelected(ButtonEvent ce) {  
					openSelected(grid.getSelectionModel().getSelectedItem(), type, theAction);
					hide();					
				}  
			})); 
			break;
		case ASSOCIATE:
			gridPanel.addButton(new Button("Associate Selected", new SelectionListener<ButtonEvent>() {  
				@Override  
				public void componentSelected(ButtonEvent ce) {  
					hide();
					associateSelected(grid.getSelectionModel().getSelectedItem(), type, theAction);
				}  
			})); 
			break;
		case DELETE:
			gridPanel.addButton(new Button("Delete Selected from VME-DB", new SelectionListener<ButtonEvent>() {  
				@Override  
				public void componentSelected(ButtonEvent ce) {  
					hide();
					deleteSelected(grid.getSelectionModel().getSelectedItem(), type, theAction);
				}  
			})); 
			break;
		}						
		mainPanel.add(gridPanel);
		mainPanel.setLayout(new FitLayout()); 
		mainPanel.layout();
	}	
	private void associateSelected(VMEReportBean selectedItem, VMETypeIdentifier type, Action theAction) {
		eventBus.fireEvent(new SelectedReportEvent(selectedItem.getId(), selectedItem.getName(), type, theAction));		
	}
	private void openSelected(VMEReportBean selectedItem, VMETypeIdentifier type, Action theAction) {
		eventBus.fireEvent(new SelectedReportEvent(selectedItem.getId(), selectedItem.getName(), type, theAction));
	}
	private void deleteSelected(VMEReportBean selectedItem, VMETypeIdentifier type, Action theAction) {
		eventBus.fireEvent(new SelectedReportEvent(selectedItem.getId(), selectedItem.getName(), type, theAction));		
	}
	/**
	 * 
	 */
	private void showLoading() {
		mainPanel.mask("Asking for available Reports, please wait ... ", "loading-indicator");		
	}
	/**
	 * the heading of the window
	 * @param type
	 * @param isAssociation
	 * @return
	 */
	private String getHeading(VMETypeIdentifier type, Action theAction) {
		String toReturn = "";
		switch (type) {
		case Vme:
			toReturn = "VME";
			break;
		case FisheryAreasHistory:
			toReturn = "fishing foot print";
			break;
		case VMEsHistory:
			toReturn = "regional history";
			break;
		case InformationSource:
			toReturn = "Information Source";
			break;
		case GeneralMeasure:
			toReturn = "General Measure";
			break;
		case Rfmo:
			toReturn = "RFMO";
			break;
		default:
			toReturn = "Unknown Category!";
			break;
		}

		switch (theAction) {
		case SELECT:
			toReturn = "Edit " + toReturn;
			break;
		case ASSOCIATE:
			toReturn = "Associate " + toReturn;
			break;
		case DELETE:
			toReturn = "Delete " + toReturn;
			break;
		}
		return toReturn;
	}
}
