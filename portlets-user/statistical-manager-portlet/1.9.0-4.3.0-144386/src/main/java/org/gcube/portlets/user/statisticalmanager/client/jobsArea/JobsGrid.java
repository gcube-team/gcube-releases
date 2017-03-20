/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.jobsArea;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.statisticalmanager.client.Constants;
import org.gcube.portlets.user.statisticalmanager.client.StatisticalManager;
import org.gcube.portlets.user.statisticalmanager.client.bean.ComputationStatus;
import org.gcube.portlets.user.statisticalmanager.client.bean.ComputationStatus.Status;
import org.gcube.portlets.user.statisticalmanager.client.bean.JobItem;
import org.gcube.portlets.user.statisticalmanager.client.bean.Operator;
import org.gcube.portlets.user.statisticalmanager.client.bean.OperatorsClassification;
import org.gcube.portlets.user.statisticalmanager.client.events.JobsGridGotDirtyEvent;
import org.gcube.portlets.user.statisticalmanager.client.events.JobsGridHandler;
import org.gcube.portlets.user.statisticalmanager.client.events.ResubmitJobEvent;
import org.gcube.portlets.user.statisticalmanager.client.events.TablesGridGotDirtyEvent;
import org.gcube.portlets.user.statisticalmanager.client.resources.Images;
import org.gcube.portlets.user.statisticalmanager.client.util.EventBusProvider;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

/**
 * @author ceras
 *
 */
public class JobsGrid extends ContentPanel {

	private Grid<JobItem> grid;
	protected static final String ERROR_GET_OPERATORS = "Operators not loaded.";
	private static final String LOADING_OPERATORS_MESSAGE = "Loading Operators...";
	private static final ImageResource ICON_CANCEL = StatisticalManager.resources.cancel();
	private static final ImageResource ICON_RESUBMIT = StatisticalManager.resources.resubmit();
	boolean dirty=true;
	private JobsGridHandler handler;
	private Timer gridUpdaterTimer;

 
	/**
	 * @param service
	 */
	public JobsGrid(JobsGridHandler handler) {
		super();
		bind();

		this.handler = handler;
		this.setHeading(".: Computations");
		this.setLayout(new FitLayout());
		setToolBar();
		
		gridUpdaterTimer = new Timer() {
			@Override
			public void run() {
				softUpdateGrid();
			}
		};

	}
	
	private void bind() {
		EventBusProvider.getInstance().addHandler(JobsGridGotDirtyEvent.TYPE, new JobsGridGotDirtyEvent.JobsGridGotDirtyHandler() {
			@Override
			public void onJobsGridGotDirty(JobsGridGotDirtyEvent event) {
				JobsGrid.this.dirty = true;
			}
		});
	}

	protected void softUpdateGrid() {
		if (grid!=null) {
			ListStore<JobItem> store = grid.getStore();
			if (store!=null)
				for (JobItem jobItem: store.getModels()) {
					ComputationStatus status = jobItem.getStatus();
					if (status==null)
						break;
					if (!status.isTerminated())
						updateJobItemStatus(jobItem);
				}
		}
	}


	/**
	 * @param jobItem
	 */
	private void updateJobItemStatus(final JobItem jobItem) {
		StatisticalManager.getService().getComputationStatus(jobItem.getId(), new AsyncCallback<ComputationStatus>() {
			@Override
			public void onSuccess(ComputationStatus computationStatus) {
				jobItem.set("status", computationStatus);
				
				// if the computation is terminated set the end date
				if (computationStatus.isTerminated())
					jobItem.setEndDate(computationStatus.getEndDate());
				
				// fire the dirty event for the tables grid
				if (computationStatus.isComplete())
					EventBusProvider.getInstance().fireEvent(new TablesGridGotDirtyEvent(null));
					
				grid.getStore().update(jobItem);
			}
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		gridUpdaterTimer.cancel();
	}

	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);

		// if the operators classification is not loaded, let's load by an rpc
		if (StatisticalManager.getOperatorsClassifications()==null) {
			this.mask(LOADING_OPERATORS_MESSAGE, Constants.maskLoadingStyle);
			StatisticalManager.getService().getOperatorsClassifications(new AsyncCallback<List<OperatorsClassification>>() {

				@Override
				public void onSuccess(List<OperatorsClassification> result) {
					unmask();
					StatisticalManager.setOperatorsClassifications(result);
					loadGrid();
				}

				@Override
				public void onFailure(Throwable caught) {
					unmask();
					MessageBox.alert("Error", ERROR_GET_OPERATORS, null);
				}
			});
		} else
			loadGrid();
	}

	
	private void loadGrid() {
		// store creation
		RpcProxy<ListLoadResult<JobItem>> proxy = new RpcProxy<ListLoadResult<JobItem>>() {
			@Override
			protected void load(Object loadConfig, AsyncCallback<ListLoadResult<JobItem>> callback) {
				StatisticalManager.getService().getListJobs(callback);
			}
		};
		
		// loader creation
		final BaseListLoader<ListLoadResult<JobItem>> loader = new BaseListLoader<ListLoadResult<JobItem>>(proxy);
		loader.addLoadListener(new LoadListener(){			
			@Override
			public void loaderLoadException(LoadEvent le) {
				Throwable e = le.exception;
				MessageBox.alert("Error", e.getMessage(), null);
//				e.printStackTrace();
			}
		});
		
		loader.setRemoteSort(false);
		ListStore<JobItem> store = new ListStore<JobItem>(loader);
		
		// RENDERERS
		GridCellRenderer<JobItem> renderer = new GridCellRenderer<JobItem>() {
			@Override
			public String render(JobItem jobItem, String property, ColumnData config, int rowIndex, int colIndex, ListStore<JobItem> store, Grid<JobItem> grid) {
				if (property.contentEquals("categoryImage")) {
					jobItem.getOperatorCategory();
					return "<img src='"+GWT.getModuleBaseURL()
							+"../images/categories/"
							+"DEFAULT_IMAGE"+".png' width='25' height='25' border='1'  />";
//					return "<img src='"+GWT.getModuleBaseURL()
//							+"../images/categories/"
//							+(category.hasImage() ? category.getId() : "DEFAULT_IMAGE")+".png' width='25' height='25' border='1' alt='"+category.getName()+"' />";
				} else if (property.contentEquals("operator")) {
					Operator op = jobItem.getOperator();
					return (op==null) ? jobItem.getOperatorId() : op.getName(); // TODO woth tooltip
				}

				return "";
			}
		};
		GridCellRenderer<JobItem> statusRenderer = new GridCellRenderer<JobItem>() {  

			public Object render(final JobItem jobItem, String property, ColumnData config, final int rowIndex,  
					final int colIndex, ListStore<JobItem> store, Grid<JobItem> grid) {
				
				ComputationStatus computationStatus = jobItem.getStatus();
				Status status = computationStatus.getStatus();
				
				ProgressBar progressBar = new ProgressBar();
				switch (status) {
				case PENDING:
					progressBar.updateProgress(1, "Pending");
					progressBar.addStyleName("progressBar-pending");
					break;
				case RUNNING:
					double percentage = computationStatus.getPercentage();
					progressBar.updateProgress(
							computationStatus.getPercentage()/100,
							"Running, " + NumberFormat.getFormat("0.00").format(percentage) + "%");
					break;
				case COMPLETE:
					progressBar.updateProgress(1, "Complete");
					progressBar.addStyleName("progressBar-complete");
					break;
				case FAILED:
					progressBar.updateProgress(1, "Failed");
					progressBar.addStyleName("progressBar-failed");
					break;
				}
				return progressBar;  
			}
		};  

		// COLUMNS		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		// id
		ColumnConfig column = new ColumnConfig("id", "Id", 30);
		configs.add(column);

		// category image
		column = new ColumnConfig("categoryImage", "Cat.", 50);
		column.setWidth(50);
		column.setResizable(false);
		column.setRenderer(renderer);
		configs.add(column);

		// name
		column = new ColumnConfig();
		column.setId("name");
		column.setHeader("Name");
		configs.add(column);

		// operator
		column = new ColumnConfig("operator", "Operator", 200);
		column.setRenderer(renderer);
		configs.add(column);
		
		// infrastructure
		column = new ColumnConfig("infrastructure", "Infrastructure", 150);
		configs.add(column);

		// creation date
		column = new ColumnConfig("creationDate", "Start Date", 67);
		column.setDateTimeFormat(DateTimeFormat.getFormat("MM/dd/yyyy'<br/>'hh:mm:ss"));
		column.setAlignment(HorizontalAlignment.CENTER);		
		column.setResizable(false);
		configs.add(column);

		// end date
		column = new ColumnConfig("endDate", "End Date", 67);
		column.setDateTimeFormat(DateTimeFormat.getFormat("MM/dd/yyyy'<br/>'hh:mm:ss"));
		column.setAlignment(HorizontalAlignment.CENTER);		
		column.setResizable(false);
		configs.add(column);

		// status
		column = new ColumnConfig("status", "Status", 150);
		column.setRenderer(statusRenderer);
		column.setResizable(false);
		configs.add(column);
		
		ColumnConfig columnButton = new ColumnConfig();  
		columnButton.setId("delete");  
		columnButton.setHeader("");  
		columnButton.setWidth(25);
		columnButton.setFixed(true);
		columnButton.setSortable(false);
		columnButton.setRenderer(new GridCellRenderer<JobItem>() {
			@Override
			public Object render(final JobItem j, String property, ColumnData config,
					int rowIndex, int colIndex, ListStore<JobItem> store, Grid<JobItem> grid) {

//				if (j.getStatus().isFailed() || j.getStatus().isTerminated()) {
				
				
				Image img = new Image(ICON_CANCEL);
				img.setTitle("Remove this computation.");
				img.setStyleName("imgCursor");
				img.addClickHandler(new ClickHandler(){
					@Override
					public void onClick(ClickEvent event) {
						event.stopPropagation();
						String message = (j.getStatus().isTerminated()) ? 
								"By removing this computation, the related data will be removed too. Do you want to continue?"
								: "The computation has not finished yet. Do you want to continue?";
						//Message
						MessageBox mbox=MessageBox.confirm("Confirm removing", message, new Listener<MessageBoxEvent>() {
							@Override
							public void handleEvent(MessageBoxEvent be) {
								if (be.getButtonClicked().getText().contentEquals("Yes"))
									removecomputation(j);
							}
						});
						
						List<Component> buttons=mbox.getDialog().getButtonBar().getItems();
						mbox.getDialog().setFocusWidget(buttons.get(0));
						
					}
				});
				return img;  
			}
		});  
		configs.add(columnButton);  

		columnButton = new ColumnConfig();
		columnButton.setId("resubmit");  
		columnButton.setHeader("");  
		columnButton.setWidth(25);
		columnButton.setFixed(true);
		columnButton.setSortable(false);
		columnButton.setRenderer(new GridCellRenderer<JobItem>() {
			@Override
			public Object render(final JobItem j, String property, ColumnData config,
					int rowIndex, int colIndex, ListStore<JobItem> store, Grid<JobItem> grid) {
				
				if (!j.getStatus().isTerminated())
					return null;

//				if (j.getStatus().isFailed() || j.getStatus().isTerminated()) {
				Image img = new Image(ICON_RESUBMIT);
				img.setTitle("Resubmit this job.");
				img.setStyleName("imgCursor");
				img.addClickHandler(new ClickHandler(){
					@Override
					public void onClick(ClickEvent event) {
						event.stopPropagation();
						MessageBox.confirm("Confirm removing", "Are you sure to resubmit this job?", new Listener<MessageBoxEvent>() {
							@Override
							public void handleEvent(MessageBoxEvent be) {
								if (be.getButtonClicked().getText().contentEquals("Yes"))
									resubmit(j);
							}
						});
					}
				});
				return img;  
			}
		});  
		configs.add(columnButton);  

		ColumnModel columnModel = new ColumnModel(configs);

		// GRID
		grid = new Grid<JobItem>(store, columnModel);
		grid.setAutoWidth(true);
		grid.setId("computationGrid");

		grid.setStyleAttribute("borderTop", "none");
		grid.setAutoExpandColumn("name");
		grid.setBorders(false);
		grid.setStripeRows(true);
		grid.setColumnLines(false);
		grid.setColumnReordering(true);
		grid.setLoadMask(true);
		
		grid.addListener(Events.Attach, new Listener<GridEvent<JobItem>>() {
			@Override
			public void handleEvent(GridEvent<JobItem> be) {
				if (dirty) {
					loader.load();
					dirty = false;
				}
				softUpdateGrid();
				gridUpdaterTimer.scheduleRepeating(Constants.TIME_UPDATE_JOBS_GRID);
			}
		});
		
		grid.addListener(Events.RowClick, new Listener<GridEvent<JobItem>>() {
			@Override
			public void handleEvent(GridEvent<JobItem> be) {
				JobItem jobItem = be.getModel();
				JobsGrid.this.handler.jobSelected(jobItem);
			}
		});
		this.add(grid);
		this.layout();
	}

	/**
	 * 
	 */
	private void setToolBar() {
		ToolBar toolBar = new ToolBar();
		toolBar.add(new Label("Tools&nbsp;&nbsp;"));

		Button refreshButton = new Button("Refresh Status", Images.refresh(), new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (grid != null) {
					grid.getStore().getLoader().load();
				}
			}
		});

		toolBar.add(refreshButton);
		this.setTopComponent(toolBar);
	}

	private void removecomputation(final JobItem jobItem) {
		handler.removeComputation(jobItem);
		grid.getStore().remove(jobItem);
	}

	private void resubmit(JobItem jobItem) {
		EventBusProvider.getInstance().fireEvent(new ResubmitJobEvent(jobItem));
	}
}
