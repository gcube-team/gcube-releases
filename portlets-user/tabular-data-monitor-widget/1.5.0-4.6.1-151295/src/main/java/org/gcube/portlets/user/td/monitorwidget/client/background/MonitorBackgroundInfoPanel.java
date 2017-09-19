package org.gcube.portlets.user.td.monitorwidget.client.background;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.shared.monitor.BackgroundOperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitorSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.WorkerState;
import org.gcube.portlets.user.td.monitorwidget.client.details.tree.MonitorBaseDto;
import org.gcube.portlets.user.td.monitorwidget.client.details.tree.MonitorFolderDto;
import org.gcube.portlets.user.td.monitorwidget.client.details.tree.MonitorTreeDataGenerator;
import org.gcube.portlets.user.td.monitorwidget.client.details.tree.MonitorValidationJobSDto;
import org.gcube.portlets.user.td.monitorwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.core.client.ToStringValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.CollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.ExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class MonitorBackgroundInfoPanel extends FramedPanel implements
		MonitorBackgroundInfoUpdaterListener {
	private DateTimeFormat sdf = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm:ss");

	private static final int STATUS_POLLING_DELAY = 1000;

	private static final String WIDTH = "618px";
	private static final String HEIGHT = "420px";
	private static final String HEIGHTGRID = "290px";

	private static final int CC1WIDTH = 164;
	private static final int CC2WIDTH = 100;
	private static final int CC3WIDTH = 140;
	private static final int CC4WIDTH = 104;

	private MonitorBackgroundInfoDialog parent;

	private VerticalLayoutContainer con;

	private MonitorTreeDataGenerator gen;
	private TreeStore<MonitorBaseDto> store;
	private TreeGrid<MonitorBaseDto> tree;
	private OperationMonitor operationMonitor;
	private OperationMonitorSession operationMonitorSession;
	private BackgroundOperationMonitor backgroundOperationMonitor;

	private MonitorBackgroundInfoUpdater monitorBackgroundInfoUpdater;
	private TextButton btnClose;

	private TextField startDateField;
	private FieldLabel startLabel;

	class KeyProvider implements ModelKeyProvider<MonitorBaseDto> {
		@Override
		public String getKey(MonitorBaseDto item) {
			return (item instanceof MonitorFolderDto ? "f-" : "v-")
					+ item.getId();
		}
	}

	public MonitorBackgroundInfoPanel(MonitorBackgroundInfoDialog parent,
			BackgroundOperationMonitor backgroundOperationMonitor,
			EventBus eventBus) {
		super();
		gen = new MonitorTreeDataGenerator();
		this.parent = parent;
		this.backgroundOperationMonitor = backgroundOperationMonitor;
		this.operationMonitorSession = new OperationMonitorSession(
				backgroundOperationMonitor.getTaskId());

		operationMonitorSession.setInBackground(true);
		forceLayoutOnResize = true;
		init();
		create();
	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		setResize(true);

	}

	protected void create() {
		con = new VerticalLayoutContainer();
		// con.setScrollMode(ScrollMode.AUTO);

		// StartDate
		startDateField = new TextField();
		if (backgroundOperationMonitor != null
				&& backgroundOperationMonitor.getStartTime() != null) {
			startDateField.setValue(sdf.format(backgroundOperationMonitor
					.getStartTime()));
		}
		startLabel = new FieldLabel(startDateField, "Start Date");

		// Tree
		store = new TreeStore<MonitorBaseDto>(new KeyProvider());

		addChildrensToStore();

		ColumnConfig<MonitorBaseDto, String> cc1 = new ColumnConfig<MonitorBaseDto, String>(
				new ToStringValueProvider<MonitorBaseDto>("task"), CC1WIDTH,
				"Task");
		cc1.setHeader("Task");
		cc1.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span title='"
						+ SafeHtmlUtils.htmlEscape(value) + "'>"
						+ SafeHtmlUtils.htmlEscape(value) + "</span>");

			}
		});

		ColumnConfig<MonitorBaseDto, String> cc2 = new ColumnConfig<MonitorBaseDto, String>(
				new ValueProvider<MonitorBaseDto, String>() {

					@Override
					public String getValue(MonitorBaseDto object) {
						String state = null;
						if (object instanceof MonitorValidationJobSDto) {
							state = ((MonitorValidationJobSDto) object)
									.getWorkerState().toString();

						}
						if (object instanceof MonitorFolderDto) {
							state = ((MonitorFolderDto) object).getState();
						}
						return state;
					}

					@Override
					public void setValue(MonitorBaseDto object, String value) {
						if (object instanceof MonitorValidationJobSDto) {
							WorkerState workerState = WorkerState.get(value);
							((MonitorValidationJobSDto) object).setWorkerState(workerState);

						}
						if (object instanceof MonitorFolderDto) {
							((MonitorFolderDto) object).setState(value);
						}
					}

					@Override
					public String getPath() {
						return "status";
					}
				}, CC2WIDTH, "Status");
		cc2.setHeader("Status");
		cc2.setWidth(CC2WIDTH);

		ColumnConfig<MonitorBaseDto, String> cc3 = new ColumnConfig<MonitorBaseDto, String>(
				new ValueProvider<MonitorBaseDto, String>() {

					@Override
					public String getValue(MonitorBaseDto object) {
						String state = null;
						if (object instanceof MonitorValidationJobSDto) {
							state = ((MonitorValidationJobSDto) object).getHumanReadableStatus();
						}
						if (object instanceof MonitorFolderDto) {
							state = ((MonitorFolderDto) object)
									.getHumanReadableStatus();
						}
						return state;
					}

					@Override
					public void setValue(MonitorBaseDto object, String value) {
						if (object instanceof MonitorValidationJobSDto) {
							((MonitorValidationJobSDto) object)
									.setHumanReadableStatus(value);

						}
						if (object instanceof MonitorFolderDto) {
							((MonitorFolderDto) object)
									.setHumanReadableStatus(value);
						}
					}

					@Override
					public String getPath() {
						return "humanreadablestatus";
					}
				}, CC3WIDTH, "Human Readble");
		cc3.setHeader("Human Readble");
		cc3.setWidth(CC3WIDTH);

		ColumnConfig<MonitorBaseDto, Double> cc4 = new ColumnConfig<MonitorBaseDto, Double>(
				new ValueProvider<MonitorBaseDto, Double>() {

					@Override
					public Double getValue(MonitorBaseDto object) {
						Double d = null;
						if (object instanceof MonitorValidationJobSDto) {
							Float f = ((MonitorValidationJobSDto) object).getProgress();
							d = new Double(f);
						}
						if (object instanceof MonitorFolderDto) {
							Float f = ((MonitorFolderDto) object).getProgress();
							d = new Double(f);
						}

						return d;
					}

					@Override
					public void setValue(MonitorBaseDto object, Double value) {
						if (object instanceof MonitorValidationJobSDto) {
							((MonitorValidationJobSDto) object)
									.setProgress(value.floatValue());
						}
						if (object instanceof MonitorFolderDto) {
							((MonitorFolderDto) object).setProgress(value
									.floatValue());
						}
					}

					@Override
					public String getPath() {
						return "progress";
					}
				}, CC4WIDTH, "Progress");
		cc4.setHeader("Progress");
		cc4.setWidth(CC4WIDTH);
		
		ProgressBarCell progress = new ProgressBarCell() {
			@Override
			public boolean handlesSelection() {
				return true;
			}
		};
		progress.setProgressText("{0}% Complete");
		progress.setWidth(100);

		cc4.setCell(progress);

		List<ColumnConfig<MonitorBaseDto, ?>> l = new ArrayList<ColumnConfig<MonitorBaseDto, ?>>();
		l.add(cc1);
		l.add(cc2);
		l.add(cc3);
		l.add(cc4);
		ColumnModel<MonitorBaseDto> cm = new ColumnModel<MonitorBaseDto>(l);

		tree = new TreeGrid<MonitorBaseDto>(store, cm, cc1);
		tree.getView().setAutoFill(true);
		tree.setBorders(false);
		tree.setLoadMask(true);
		tree.setColumnResize(true);
		tree.getView().setAutoExpandColumn(cc1);
		tree.setHeight(HEIGHTGRID);
		tree.setAutoExpand(true);

		IconProvider<MonitorBaseDto> iconProvider = new IconProvider<MonitorBaseDto>() {

			@Override
			public ImageResource getIcon(MonitorBaseDto model) {
				ImageResource img = null;
				if (model instanceof MonitorFolderDto) {
					String type = ((MonitorFolderDto) model).getType();
					if (type.compareTo("job") == 0) {
						img = ResourceBundle.INSTANCE.cog();
					} else {
						if (type.compareTo("task") == 0) {
							img = ResourceBundle.INSTANCE.basket();
						} else {

						}
					}
				} else {
					img = ResourceBundle.INSTANCE.tableValidation();
				}
				return img;
			}
		};
		tree.setIconProvider(iconProvider);

		tree.addExpandHandler(new ExpandItemHandler<MonitorBaseDto>() {

			@Override
			public void onExpand(ExpandItemEvent<MonitorBaseDto> event) {
				forceLayout();

			}
		});

		tree.addCollapseHandler(new CollapseItemHandler<MonitorBaseDto>() {

			@Override
			public void onCollapse(CollapseItemEvent<MonitorBaseDto> event) {
				forceLayout();

			}
		});

		ToolBar toolBar = new ToolBar();

		TextButton btnExpandAll = new TextButton();
		btnExpandAll.setIcon(ResourceBundle.INSTANCE.magnifierZoomIn());
		btnExpandAll.setToolTip("Expand All");
		btnExpandAll.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				tree.expandAll();
				forceLayout();
			}
		});
		toolBar.add(btnExpandAll);

		TextButton btnCollapseAll = new TextButton();
		btnCollapseAll.setIcon(ResourceBundle.INSTANCE.magnifierZoomOut());
		btnCollapseAll.setToolTip("Collapse All");
		btnCollapseAll.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				tree.collapseAll();
				forceLayout();
			}
		});
		toolBar.add(btnCollapseAll);

		//
		btnClose = new TextButton("Close");
		btnClose.setWidth("70px");
		// btnAbort.setIcon(ResourceBundle.INSTANCE.abort());
		// btnAbort.setIconAlign(IconAlign.RIGHT);
		btnClose.setTitle("Close");

		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();

			}
		});

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		flowButton.add(btnClose, new BoxLayoutData(new Margins(0, 4, 0, 4)));

		//
		con.add(startLabel, new VerticalLayoutData(1, -1, new Margins(0)));
		con.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		con.add(tree, new VerticalLayoutData(1, -1, new Margins(0)));
		con.add(flowButton, new VerticalLayoutData(1, 36, new Margins(5, 5, 5,
				5)));

		add(con, new MarginData(0));

		addMonitor();

		forceLayout();
		tree.expandAll();

	}

	protected void addMonitor() {
		monitorBackgroundInfoUpdater = new MonitorBackgroundInfoUpdater(
				operationMonitorSession);
		monitorBackgroundInfoUpdater.addListener(this);
		monitorBackgroundInfoUpdater.scheduleRepeating(STATUS_POLLING_DELAY);
		monitorBackgroundInfoUpdater.run();
	}
	
	public void cancelMonitorBackgroundInfoUpdater(){
		if(monitorBackgroundInfoUpdater!=null){
			monitorBackgroundInfoUpdater.cancel();
		}
	}
	

	protected void close() {
		if (parent != null) {
			parent.close();
		}

	}

	private void addChildrensToStore() {

		MonitorFolderDto root = gen.getRoot(operationMonitor);
		if (root != null) {
			Log.debug("root childrens " + root.getChildrens().size());
			for (MonitorBaseDto base : root.getChildrens()) {
				try {
					Log.debug("Check children: " + base);

					store.add(base);
					if (base instanceof MonitorFolderDto) {
						processFolder((MonitorFolderDto) base);
					}
				} catch (Throwable e) {
					Log.error("Error adding childrens to store :"
							+ e.getLocalizedMessage());
					e.printStackTrace();
				}
			}
		} else {
			Log.error("Error generating the task informations");
			UtilsGXT3.alert("Error", "Error generating task informations");
		}

	}

	public void update(OperationMonitor operationMonitor) {
		this.operationMonitor = operationMonitor;

		if (!gen.isCacheValid(operationMonitor)) {
			store.clear();
			store.commitChanges();
			addChildrensToStore();
			store.commitChanges();
			tree.expandAll();
			// StartDate
			if (backgroundOperationMonitor != null
					&& backgroundOperationMonitor.getStartTime() != null) {
				startDateField.setValue(sdf.format(backgroundOperationMonitor
						.getStartTime()));
			}

			forceLayout();

		}

		

	}

	private void processFolder(MonitorFolderDto folder) {
		for (MonitorBaseDto child : folder.getChildrens()) {
			try {
				store.add(folder, child);
				if (child instanceof MonitorFolderDto) {
					processFolder((MonitorFolderDto) child);
				}
			} catch (Throwable e) {
				Log.error("Error adding childrens to store :"
						+ e.getLocalizedMessage());
				e.printStackTrace();
			}

		}
	}

	@Override
	public void backgroundOperationMonitorUpdated(
			OperationMonitor operationMonitor) {
		update(operationMonitor);

	}

	@Override
	public void retrieveBackgroundOperationMonitorFailed(Throwable caught) {
		Log.error("Error retrieving background operation monitor: "
				+ caught.getLocalizedMessage());
		UtilsGXT3.alert(
				"Error",
				"Error generating informations: "
						+ caught.getLocalizedMessage());
		close();
	}

}