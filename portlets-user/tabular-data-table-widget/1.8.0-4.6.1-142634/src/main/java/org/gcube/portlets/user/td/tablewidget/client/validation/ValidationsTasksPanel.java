package org.gcube.portlets.user.td.tablewidget.client.validation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.task.InvocationS;
import org.gcube.portlets.user.td.gwtservice.shared.task.JobSClassifier;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskResumeSession;
import org.gcube.portlets.user.td.gwtservice.shared.task.ValidationsTasksMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.ConditionCode;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialog;
import org.gcube.portlets.user.td.monitorwidget.client.MonitorDialogListener;
import org.gcube.portlets.user.td.tablewidget.client.custom.ValidationCell;
import org.gcube.portlets.user.td.tablewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.tablewidget.client.validation.tree.BaseDto;
import org.gcube.portlets.user.td.tablewidget.client.validation.tree.FolderDto;
import org.gcube.portlets.user.td.tablewidget.client.validation.tree.JobSDto;
import org.gcube.portlets.user.td.tablewidget.client.validation.tree.TaskSDto;
import org.gcube.portlets.user.td.tablewidget.client.validation.tree.TreeDataGenerator;
import org.gcube.portlets.user.td.tablewidget.client.validation.tree.ValidationDto;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.ChangeTableRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.WidgetRequestEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.ChangeTableWhy;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.WidgetRequestType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.OperationResult;
import org.gcube.portlets.user.td.widgetcommonevent.shared.RequestProperties;
import org.gcube.portlets.user.td.widgetcommonevent.shared.RequestPropertiesParameterType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.operations.OperationsId;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.ToStringValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.IconProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.CollapseItemEvent.CollapseItemHandler;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.ExpandItemEvent.ExpandItemHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ValidationsTasksPanel extends FramedPanel implements
		MonitorDialogListener {
	interface DtoTemplates extends XTemplates {
		@XTemplate("<span title=\"{id} - {value}\">{value}</span>")
		SafeHtml format(String id, String value);

		/*"<span title='"
		+ SafeHtmlUtils.htmlEscape(value) + "'>" + SafeHtmlUtils.htmlEscape(value)
		+ "</span>"*/
		
		
	}

	private static final String WIDTH = "298px";
	private static final String HEIGHT = "520px";

	private Menu contextMenu;
	private MenuItem resumeItem;
	private MenuItem resolveItem;

	private TRId trId;
	private VerticalLayoutContainer con;
	private EventBus eventBus;
	private ValidationsTasksMetadata validationsTasksMetadata;

	private TreeDataGenerator gen;
	private TreeStore<BaseDto> store;
	private TreeGrid<BaseDto> tree;
	private boolean updateTR;

	class KeyProvider implements ModelKeyProvider<BaseDto> {
		@Override
		public String getKey(BaseDto item) {
			return (item instanceof FolderDto ? "f-" : "v-") + item.getId();
		}
	}

	/**
	 * 
	 * @param eventBus
	 */
	public ValidationsTasksPanel(EventBus eventBus) {
		super();
		Log.debug("ValidationsTasksPanel");
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		updateTR = false;
		init();
		retrieveCurrentTR();

	}

	/**
	 * 
	 * @param trId
	 * @param eventBus
	 */
	public ValidationsTasksPanel(TRId trId, EventBus eventBus) {
		super();
		Log.debug("ValidationsTasksPanel: " + trId);
		this.trId = trId;
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		updateTR = false;
		init();
		retrieveValidations();
	}

	protected void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);

	}

	protected void createTreeData() {
		if (updateTR) {
			store.clear();
			store.commitChanges();
			addChildrensToStore();

		} else {
			create();
		}
		store.commitChanges();
		
		try{
			tree.expandAll();
		} catch (Throwable caught){
			Log.error(caught.getLocalizedMessage());
		}
		
		forceLayout();

	}

	protected void create() {
		con = new VerticalLayoutContainer();
		con.setScrollMode(ScrollMode.AUTO);

		store = new TreeStore<BaseDto>(new KeyProvider());

		addChildrensToStore();

		ColumnConfig<BaseDto, String> cc1 = new ColumnConfig<BaseDto, String>(
				new ToStringValueProvider<BaseDto>("task"), 168, "Task");
		cc1.setHeader("Task");
		cc1.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				String key = (String) context.getKey();
				BaseDto d = store.findModelWithKey(key);
				DtoTemplates dtoTemplates = GWT.create(DtoTemplates.class);

				if (d instanceof TaskSDto) {
					sb.append(dtoTemplates.format("Task", value));
				} else {
					if (d instanceof JobSDto) {
						JobSDto jobSDto = (JobSDto) d;

						String label = jobSDto.getJobClassfier().getLabel();
						String response;
						if (label == null||label.isEmpty()) {
							response = "Job";
						} else {
							response = "Job "+label;
						}
					
						sb.append(dtoTemplates.format(response, value));
					} else {
						if (d instanceof ValidationDto) {
							sb.append(dtoTemplates.format("Validation", value));
						} else{
							sb.append(dtoTemplates.format("", value));
						}
					}
				}
			}
		});

		ColumnConfig<BaseDto, Boolean> cc2 = new ColumnConfig<BaseDto, Boolean>(
				new ValueProvider<BaseDto, Boolean>() {

					@Override
					public Boolean getValue(BaseDto object) {
						return object instanceof ValidationDto ? ((ValidationDto) object).getValid()
								: null;
					}

					@Override
					public void setValue(BaseDto object, Boolean value) {
						if (object instanceof ValidationDto) {
							((ValidationDto) object).setValid(value);
						}
					}

					@Override
					public String getPath() {
						return "valid";
					}
				}, 38, "Valid");
		cc2.setHeader("Valid");

		ValidationCell validationButton = new ValidationCell();
		validationButton.setTrueIcon(ResourceBundle.INSTANCE.ok());
		validationButton.setFalseIcon(ResourceBundle.INSTANCE.error());
		validationButton.setTrueTitle("Valid");
		validationButton.setFalseTitle("Error");

		validationButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				Log.debug("Button  Pressed");

				Context c = event.getContext();
				int rowIndex = c.getIndex();
				int columnIndex = c.getColumn();

				Element el = tree.getView().getCell(rowIndex, columnIndex);

				NativeEvent contextEvent = Document.get().createMouseEvent(
						BrowserEvents.CONTEXTMENU, true, true, 0, 0, 0,
						el.getAbsoluteLeft(), el.getAbsoluteTop(), false,
						false, false, false, NativeEvent.BUTTON_RIGHT, null);

				// NativeEvent contextEvent =
				// Document.get().createContextMenuEvent();
				el.dispatchEvent(contextEvent);
				// DomEvent
			}
		});

		cc2.setCell(validationButton);

		List<ColumnConfig<BaseDto, ?>> l = new ArrayList<ColumnConfig<BaseDto, ?>>();
		l.add(cc1);
		l.add(cc2);
		ColumnModel<BaseDto> cm = new ColumnModel<BaseDto>(l);

		tree = new TreeGrid<BaseDto>(store, cm, cc1);
		tree.getView().setAutoFill(true);
		tree.setBorders(false);
		tree.setLoadMask(true);
		tree.setColumnResize(true);
		tree.setAutoExpand(true);
		tree.getView().setAutoExpandColumn(cc1);

		IconProvider<BaseDto> iconProvider = new IconProvider<BaseDto>() {

			@Override
			public ImageResource getIcon(BaseDto model) {
				ImageResource img = null;
				if (model instanceof FolderDto) {
					if (model instanceof JobSDto) {
						JobSDto jobSDto = (JobSDto) model;
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
						if (model instanceof TaskSDto) {
							img = ResourceBundle.INSTANCE.basket();
						} else {

						}
					}
				} else {
					if (model instanceof ValidationDto) {
						img = ResourceBundle.INSTANCE.tableValidation();
					} else {
						img = ResourceBundle.INSTANCE.tableValidation();
					}
				}
				return img;
			}
		};
		tree.setIconProvider(iconProvider);

		tree.addExpandHandler(new ExpandItemHandler<BaseDto>() {

			@Override
			public void onExpand(ExpandItemEvent<BaseDto> event) {
				forceLayout();

			}
		});

		tree.addCollapseHandler(new CollapseItemHandler<BaseDto>() {

			@Override
			public void onCollapse(CollapseItemEvent<BaseDto> event) {
				forceLayout();

			}
		});

		createContextMenu();

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
	}

	private void addChildrensToStore() {
		gen = new TreeDataGenerator(validationsTasksMetadata);

		FolderDto root = gen.getRoot();
		for (BaseDto base : root.getChildrens()) {
			store.add(base);
			if (base instanceof FolderDto) {
				processFolder(store, (FolderDto) base);
			}
		}

	}

	protected void requestSolution(Context context) {
		String key = (String) context.getKey();
		BaseDto d = store.findModelWithKey(key);
		requestResolve(d);
	}

	protected void requestResolve(BaseDto d) {
		if (d != null) {
			Log.debug(d.toString());
			if (d instanceof ValidationDto) {
				ValidationDto v = (ValidationDto) d;
				Log.debug("ValidationDto: [" + v.getId() + ", "
						+ v.getDescription() + ", " + v.getValid() + ", "
						+ v.getConditionCode() + ", "
						+ v.getValidationColumnColumnId() + ", "
						+ v.getInvocation() + "]");
				if (v.getValid()) {

				} else {
					if (v.getInvocation() != null) {
						InvocationS invocationS = v.getInvocation();

						Long op = invocationS.getOperationId();
						if (op.compareTo(OperationsId.ChangeToDimensionColumn
								.toLong()) == 0) {
							if (v.getConditionCode() == ConditionCode.MissingValueOnExternalReference
									|| v.getConditionCode() == ConditionCode.AmbiguousValueOnExternalReference) {
								WidgetRequestEvent widgetRequestEvent = new WidgetRequestEvent(
										WidgetRequestType.CURATIONBYREPLACEBATCHDIALOG);
								widgetRequestEvent.setTrId(trId);
								HashMap<RequestPropertiesParameterType, Object> map = new HashMap<RequestPropertiesParameterType, Object>();
								map.put(RequestPropertiesParameterType.InvocationS,
										invocationS);
								map.put(RequestPropertiesParameterType.ConditionCode,
										v.getConditionCode());
								map.put(RequestPropertiesParameterType.ValidationColumnColumnId,
										v.getValidationColumnColumnId());

								RequestProperties props = new RequestProperties(
										map);
								widgetRequestEvent.setRequestProperties(props);
								eventBus.fireEvent(widgetRequestEvent);
							}
						} else {
							// UtilsGXT3.info("Invocation Info", title);

						}
					}
				}

			}
		}

	}

	protected void requestResume(BaseDto selected) {
		if (selected instanceof ValidationDto) {
			ValidationDto v = (ValidationDto) selected;
			Log.debug("ValidationDto: [" + v.getId() + ", "
					+ v.getDescription() + ", " + v.getValid() + ", "
					+ v.getInvocation() + "]");
			if (v.getInvocation() != null) {
				if (v.getValid()) {
					UtilsGXT3.info("Resume", "Is valid, resume not applicable");
				} else {
					InvocationS invocationS = v.getInvocation();
					String taskId = invocationS.getTaskId();
					if (taskId == null || taskId.isEmpty()) {
						UtilsGXT3.alert("Resume",
								"TaskId is null, resume not applicable");
					} else {
						TaskResumeSession taskResumeSession = new TaskResumeSession(
								trId, taskId);
						startTaskResume(taskResumeSession);
					}
				}
			} else {
				UtilsGXT3.alert("Resume",
						"Invocation is null, resume not applicable");
			}
		}

	}

	protected void createContextMenu() {
		contextMenu = new Menu();

		resumeItem = new MenuItem();
		resumeItem.setText("Resume");
		resumeItem.setIcon(ResourceBundle.INSTANCE.refresh());
		resumeItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				BaseDto selected = tree.getSelectionModel().getSelectedItem();
				Log.debug(selected.toString());
				requestResume(selected);
			}
		});

		resolveItem = new MenuItem();
		resolveItem.setText("Resolve");
		resolveItem.setIcon(ResourceBundle.INSTANCE.plaster());
		resolveItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				BaseDto selected = tree.getSelectionModel().getSelectedItem();
				Log.debug(selected.toString());
				requestResolve(selected);

			}

		});

		tree.setContextMenu(contextMenu);

		tree.addBeforeShowContextMenuHandler(new BeforeShowContextMenuHandler() {

			@Override
			public void onBeforeShowContextMenu(BeforeShowContextMenuEvent event) {
				Menu contextMenu = event.getMenu();

				BaseDto selected = tree.getSelectionModel().getSelectedItem();
				if (selected instanceof ValidationDto) {
					ValidationDto v = (ValidationDto) selected;
					Log.debug("ValidationDto: [" + v.getId() + ", "
							+ v.getDescription() + ", " + v.getValid() + ", "
							+ v.getConditionCode() + ", "
							+ v.getValidationColumnColumnId() + ", "
							+ v.getInvocation() + "]");
					if (v.getInvocation() != null) {
						if (v.getValid()) {
							contextMenu.clear();
							contextMenu.add(resumeItem);
							tree.setContextMenu(contextMenu);
							event.setCancelled(true);
						} else {
							InvocationS invocationS = v.getInvocation();
							Long op = invocationS.getOperationId();
							// TODO Add all cases
							if (op.compareTo(OperationsId.ChangeToDimensionColumn
									.toLong()) == 0
									&& (v.getConditionCode() == ConditionCode.MissingValueOnExternalReference || v
											.getConditionCode() == ConditionCode.AmbiguousValueOnExternalReference)) {
								contextMenu.clear();
								contextMenu.add(resolveItem);
								contextMenu.add(resumeItem);
								tree.setContextMenu(contextMenu);
							} else {

								contextMenu.clear();
								contextMenu.add(resumeItem);
								tree.setContextMenu(contextMenu);

							}
						}
					} else {
						contextMenu.clear();
						tree.setContextMenu(contextMenu);
						event.setCancelled(true);
					}

				} else {
					contextMenu.clear();
					tree.setContextMenu(contextMenu);
					event.setCancelled(true);
				}

			}
		});
	}

	protected void retrieveValidations() {

		TDGWTServiceAsync.INSTANCE.getValidationsTasksMetadata(trId,
				new AsyncCallback<ValidationsTasksMetadata>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								Log.error("Load validations metadata failure: "
										+ caught.getLocalizedMessage());
								UtilsGXT3
										.alert("Error retrieving validations metadata",
												"Error retrieving validations metadata");
							}
						}
					}

					public void onSuccess(ValidationsTasksMetadata result) {
						Log.debug("Loaded " + result.getId());
						validationsTasksMetadata = result;
						createTreeData();

					}

				});

	}

	public void update() {
		updateTR = true;
		retrieveCurrentTR();
	}

	public void update(TRId trId) {
		this.trId = trId;
		updateTR = true;
		retrieveValidations();
	}

	protected void retrieveCurrentTR() {
		TDGWTServiceAsync.INSTANCE.getCurrentTRId(new AsyncCallback<TRId>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(
							SessionExpiredType.EXPIREDONSERVER));
				} else {
					if (caught instanceof TDGWTIsLockedException) {
						Log.error(caught.getLocalizedMessage());
						UtilsGXT3.alert("Error Locked",
								caught.getLocalizedMessage());
					} else {
						Log.error("Error retrieving current TRId: "
								+ caught.getLocalizedMessage());
						UtilsGXT3.alert("Error",
								"Error retrieving current tabular resource id");
					}
				}
			}

			public void onSuccess(TRId result) {
				Log.debug("retrieved " + result);
				trId = result;
				retrieveValidations();

			}

		});
	}

	private void processFolder(TreeStore<BaseDto> store, FolderDto folder) {
		for (BaseDto child : folder.getChildrens()) {
			store.add(folder, child);
			if (child instanceof FolderDto) {
				processFolder(store, (FolderDto) child);
			}
		}
	}

	protected void startTaskResume(TaskResumeSession taskResumeSession) {
		Log.debug(taskResumeSession.toString());
		TDGWTServiceAsync.INSTANCE.startTaskResume(taskResumeSession,
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert("Error Locked",
										caught.getLocalizedMessage());
							} else {
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert("Error Final",
											caught.getLocalizedMessage());
								} else {
									Log.debug(caught.getLocalizedMessage());
									UtilsGXT3.alert("Error in Resume",
											caught.getLocalizedMessage());
								}
							}
						}

					}

					@Override
					public void onSuccess(String taskId) {
						openMonitorDialog(taskId);

					}

				});
	}

	protected void close() {
		/*
		 * if (parent != null) { parent.close(); }
		 */
	}

	// /
	protected void openMonitorDialog(String taskId) {
		MonitorDialog monitorDialog = new MonitorDialog(taskId, eventBus);
		monitorDialog.addProgressDialogListener(this);
		monitorDialog.show();
	}

	@Override
	public void operationComplete(OperationResult operationResult) {
		ChangeTableWhy why = ChangeTableWhy.TABLEUPDATED;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.TASKRESUME, operationResult.getTrId(),
				why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();
	}

	@Override
	public void operationFailed(Throwable caught, String reason, String details) {
		UtilsGXT3.alert(reason, details);
		close();

	}

	@Override
	public void operationStopped(OperationResult operationResult,
			String reason, String details) {
		ChangeTableWhy why = ChangeTableWhy.TABLECURATION;
		ChangeTableRequestEvent changeTableRequestEvent = new ChangeTableRequestEvent(
				ChangeTableRequestType.TASKRESUME, operationResult.getTrId(),
				why);
		eventBus.fireEvent(changeTableRequestEvent);
		close();

	}

	@Override
	public void operationAborted() {
		close();

	}

	@Override
	public void operationPutInBackground() {
		close();

	}

}
