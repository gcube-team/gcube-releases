package org.gcube.portlets.user.td.monitorwidget.client.details;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.shared.monitor.OperationMonitor;
import org.gcube.portlets.user.td.gwtservice.shared.task.JobSClassifier;
import org.gcube.portlets.user.td.gwtservice.shared.task.WorkerState;
import org.gcube.portlets.user.td.monitorwidget.client.custom.ExtendedTreeGridView;
import org.gcube.portlets.user.td.monitorwidget.client.details.tree.MonitorBaseDto;
import org.gcube.portlets.user.td.monitorwidget.client.details.tree.MonitorFolderDto;
import org.gcube.portlets.user.td.monitorwidget.client.details.tree.MonitorJobSDto;
import org.gcube.portlets.user.td.monitorwidget.client.details.tree.MonitorTaskSDto;
import org.gcube.portlets.user.td.monitorwidget.client.details.tree.MonitorTreeDataGenerator;
import org.gcube.portlets.user.td.monitorwidget.client.details.tree.MonitorValidationJobSDto;
import org.gcube.portlets.user.td.monitorwidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ProgressBarCell;
import com.sencha.gxt.core.client.ToStringValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.CollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.ExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

public class MonitorDetailPanel extends FramedPanel {
	private static final String WIDTH = "618px";
	private static final String HEIGHT = "256px";
	private static final String GRIDHEIGHT = "208px";

	private static final int CC1WIDTH = 164;
	private static final int CC2WIDTH = 100;
	private static final int CC3WIDTH = 140;
	private static final int CC4WIDTH = 104;

	interface DtoTemplates extends XTemplates {
		@XTemplate("<span title=\"{id} - {value}\">{value}</span>")
		SafeHtml format(String id, String value);

		/*
		 * "<span title='" + SafeHtmlUtils.htmlEscape(value) + "'>" +
		 * SafeHtmlUtils.htmlEscape(value) + "</span>"
		 */

	}

	private VerticalLayoutContainer con;

	private MonitorTreeDataGenerator gen;
	private TreeStore<MonitorBaseDto> store;
	private TreeGrid<MonitorBaseDto> tree;
	private OperationMonitor operationMonitor;
	private ExtendedTreeGridView<MonitorBaseDto> gridView;

	class KeyProvider implements ModelKeyProvider<MonitorBaseDto> {
		@Override
		public String getKey(MonitorBaseDto item) {
			return (item instanceof MonitorFolderDto ? "f-" : "v-")
					+ item.getId();
		}
	}

	/**
	 * 
	 * @param eventBus
	 */
	public MonitorDetailPanel(EventBus eventBus) {
		super();
		Log.debug("MonitorDetailPanel");
		forceLayoutOnResize = true;
		gen = new MonitorTreeDataGenerator();
		init();
		create();

	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);

	}

	protected void create() {
		con = new VerticalLayoutContainer();
		// con.setScrollMode(ScrollMode.AUTO);

		store = new TreeStore<MonitorBaseDto>(new KeyProvider());

		addChildrensToStore();

		ColumnConfig<MonitorBaseDto, String> cc1 = new ColumnConfig<MonitorBaseDto, String>(
				new ToStringValueProvider<MonitorBaseDto>("task"), CC1WIDTH,
				"Task");
		cc1.setHeader("Task");
		cc1.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				String key = (String) context.getKey();
				MonitorBaseDto d = store.findModelWithKey(key);
				DtoTemplates dtoTemplates = GWT.create(DtoTemplates.class);

				if (d instanceof MonitorTaskSDto) {
					sb.append(dtoTemplates.format("Task", value));
				} else {
					if (d instanceof MonitorJobSDto) {
						MonitorJobSDto jobSDto = (MonitorJobSDto) d;

						String label = jobSDto.getJobClassfier().getLabel();
						String response;
						if (label == null || label.isEmpty()) {
							response = "Job";
						} else {
							response = "Job " + label;
						}

						sb.append(dtoTemplates.format(response, value));
					} else {
						if (d instanceof MonitorValidationJobSDto) {
							sb.append(dtoTemplates.format("Validation", value));
						} else {
							sb.append(dtoTemplates.format("", value));
						}
					}
				}

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

		gridView = new ExtendedTreeGridView<MonitorBaseDto>();

		tree = new TreeGrid<MonitorBaseDto>(store, cm, cc1);
		tree.setView(gridView);
		tree.getView().setAutoFill(true);
		tree.setBorders(false);
		tree.setLoadMask(true);
		tree.setColumnResize(true);
		tree.getView().setAutoExpandColumn(cc1);
		tree.setHeight(GRIDHEIGHT);

		IconProvider<MonitorBaseDto> iconProvider = new IconProvider<MonitorBaseDto>() {

			@Override
			public ImageResource getIcon(MonitorBaseDto model) {
				ImageResource img = null;
				if (model instanceof MonitorFolderDto) {
					if (model instanceof MonitorJobSDto) {
						MonitorJobSDto jobSDto = (MonitorJobSDto) model;
						JobSClassifier jobClassifier = jobSDto
								.getJobClassfier();
						if (jobClassifier == null) {
							img = ResourceBundle.INSTANCE.cog();
						} else {
							switch (jobClassifier) {
							case DATAVALIDATION:
								img = ResourceBundle.INSTANCE
										.cogDataValidation();
								break;
							case POSTPROCESSING:
								img = ResourceBundle.INSTANCE
										.cogPostprocessing();
								break;
							case PREPROCESSING:
								img = ResourceBundle.INSTANCE
										.cogPreprocessing();
								break;
							case PROCESSING:
								img = ResourceBundle.INSTANCE.cog();
								break;
							case UNKNOWN:
								img = ResourceBundle.INSTANCE.cog();
								break;
							default:
								img = ResourceBundle.INSTANCE.cog();
								break;

							}

						}

					} else {
						if (model instanceof MonitorTaskSDto) {
							img = ResourceBundle.INSTANCE.basket();
						} else {

						}
					}
				} else {
					if (model instanceof MonitorValidationJobSDto) {
						img = ResourceBundle.INSTANCE.tableValidation();
					} else {
						img = ResourceBundle.INSTANCE.tableValidation();
					}
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

		// createContextMenu();

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

		con.add(toolBar, new VerticalLayoutData(1, -1, new Margins(0)));
		con.add(tree, new VerticalLayoutData(1, -1, new Margins(0)));

		add(con, new MarginData(0));

		tree.expandAll();
		forceLayout();

	}

	protected void requestOperationOnKey(Context context) {
		String key = (String) context.getKey();
		@SuppressWarnings("unused")
		MonitorBaseDto d = store.findModelWithKey(key);

	}

	public void update(OperationMonitor operationMonitor) {
		this.operationMonitor = operationMonitor;

		if (!gen.isCacheValid(operationMonitor)) {
			store.clear();
			store.commitChanges();
			// con.remove(tree);
			// con.add(tree, new VerticalLayoutData(1, -1, new Margins(0)));
			addChildrensToStore();
			store.commitChanges();
			tree.expandAll();
			forceLayout();
		}
		

	}

	private void addChildrensToStore() {
		MonitorFolderDto root = gen.getRootNoTask(operationMonitor);
		if (root != null) {
			for (MonitorBaseDto base : root.getChildrens()) {
				try {
					store.add(base);
					if (base instanceof MonitorFolderDto) {
						processFolder(store, (MonitorFolderDto) base);
					}
				} catch (Throwable e) {
					Log.error(e.getLocalizedMessage());
					e.printStackTrace();
				}

			}

		} else {
			Log.error("Error generating the task informations");
			UtilsGXT3.alert("Error", "Error generating task informations");
		}
	}

	private void processFolder(TreeStore<MonitorBaseDto> store,
			MonitorFolderDto folder) {

		for (MonitorBaseDto child : folder.getChildrens()) {
			try {
				store.add(folder, child);
				if (child instanceof MonitorFolderDto) {
					processFolder(store, (MonitorFolderDto) child);
				}

			} catch (Throwable e) {
				Log.error(e.getLocalizedMessage());
				e.printStackTrace();
			}
		}

	}

}
