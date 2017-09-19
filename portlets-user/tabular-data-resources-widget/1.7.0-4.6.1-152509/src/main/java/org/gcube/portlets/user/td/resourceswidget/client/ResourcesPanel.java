package org.gcube.portlets.user.td.resourceswidget.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.InternalURITD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.RemoveResourceSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDDescriptor;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.ResourceTDType;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.SDMXResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.SaveResourceSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.StringResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.TableResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.uriresolver.UriResolverSession;
import org.gcube.portlets.user.td.resourceswidget.client.charts.ChartViewerDialog;
import org.gcube.portlets.user.td.resourceswidget.client.custom.ResourceTDTypeButtonCell;
import org.gcube.portlets.user.td.resourceswidget.client.custom.ResourcesActionCell;
import org.gcube.portlets.user.td.resourceswidget.client.properties.ResourceTDDescriptorProperties;
import org.gcube.portlets.user.td.resourceswidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.resourceswidget.client.save.SaveResourceWizard;
import org.gcube.portlets.user.td.resourceswidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.mime.MimeTypeSupport;
import org.gcube.portlets.user.td.widgetcommonevent.shared.uriresolver.ApplicationType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardListener;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreFilter;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.RowExpander;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * ResourcesPanel shows the resources
 * 
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ResourcesPanel extends FramedPanel {
	private static final String GRID_RESOURCE_HEIGHT = "300px";
	private static final String GRID_RESOURCE_WIDTH = "200px";
	// private static final String WIDTH = "298px";
	// private static final String HEIGHT = "520px";

	private CommonMessages msgsCommon;
	private ResourcesMessages msgs;

	private ResourcesDialog parent;
	private TRId trId;
	private EventBus eventBus;

	private ExtendedListStore<ResourceTDDescriptor> store;
	private ListLoader<ListLoadConfig, ListLoadResult<ResourceTDDescriptor>> loader;
	private Grid<ResourceTDDescriptor> grid;

	private Menu contextMenu;
	private MenuItem openItem;
	private MenuItem saveItem;
	private MenuItem deleteItem;

	private RemoveResourceSession removeResourceSession;
	private SaveResourceSession saveResourceSession;

	public ResourcesPanel(ResourcesDialog parent, EventBus eventBus) {
		super();
		this.parent = parent;
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		initMessages();
		init();
	}

	public ResourcesPanel(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		initMessages();
		init();

	}

	public ResourcesPanel(TRId trId, EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		initMessages();
		init();
		open(trId);

	}

	public void open(TRId trId) {
		this.trId = trId;
		create();
		forceLayout();
	}

	private void initMessages() {
		msgs = GWT.create(ResourcesMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	private void init() {
		// setWidth(WIDTH);
		// setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		setResize(true);

	}

	private void create() {
		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem(msgsCommon.toolItemSearchLabel()));
		final TextField searchField = new TextField();
		toolBar.add(searchField);

		TextButton btnReload = new TextButton();
		// btnReload.setText("Reload");
		btnReload.setIcon(ResourceBundle.INSTANCE.refresh16());
		btnReload.setToolTip(msgsCommon.toolItemReloadLabel());
		toolBar.add(btnReload);

		ResourceTDDescriptorProperties props = GWT.create(ResourceTDDescriptorProperties.class);

		IdentityValueProvider<ResourceTDDescriptor> identityProvider = new IdentityValueProvider<ResourceTDDescriptor>();

		final GridSelectionModel<ResourceTDDescriptor> sm = new GridSelectionModel<ResourceTDDescriptor>();
		sm.setSelectionMode(SelectionMode.SINGLE);

		RowExpander<ResourceTDDescriptor> expanderColumn = new RowExpander<ResourceTDDescriptor>(identityProvider,
				new AbstractCell<ResourceTDDescriptor>() {

					@Override
					public void render(Context context, ResourceTDDescriptor value, SafeHtmlBuilder sb) {
						String data = "<table style='font-family: tahoma, arial, verdana, sans-serif;"
								+ "width: 100%; border: none;" + "font-size: 12px; margin:2px; text-align: left;'>"
								+ "<tr>" + "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"
								+ msgs.nameLabelFixed() + "</b></td>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
								+ SafeHtmlUtils.htmlEscape(value.getName()) +

								"</td>" + "</tr>" + "<tr>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"
								+ msgs.descriptionLabelFixed() + "</b></td>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
								+ SafeHtmlUtils.htmlEscape(value.getDescription());
								
								/*
								"</td>" + "</tr>" + "<tr>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"
								+ msgs.creationDateLabelFixed() + "</b></td>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
								+ SafeHtmlUtils.htmlEscape(value.getCreationDate()) +

								"</td>" + "</tr>" + "<tr>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>" + msgs.typeLabelFixed()
								+ "</b></td>" + "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
								+ SafeHtmlUtils.htmlEscape(value.getResourceType().toString()) + "</td>" + "</tr>";*/

						ResourceTD resource = value.getResourceTD();
						String resourceData = "";
						if (resource instanceof InternalURITD) {
							InternalURITD internalURITD = (InternalURITD) resource;
							if (internalURITD.getId() != null && !internalURITD.getId().isEmpty()) {

								resourceData = resourceData + "<tr>"
										+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"
										+ msgs.internalURIIdLabelFixed() + "</b></td>"
										+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
										+ SafeHtmlUtils.htmlEscape(internalURITD.getId()) + "</td>" + "</tr>";
							}
							if (internalURITD.getMimeType() != null && !internalURITD.getMimeType().isEmpty()) {
								resourceData = resourceData + "<tr>"
										+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"
										+ msgs.internalURIMimeTypeLabelFixed() + "</b></td>"
										+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
										+ SafeHtmlUtils.htmlEscape(internalURITD.getMimeType()) + "</td>" + "</tr>";
							}

						} else {
							if (resource instanceof StringResourceTD) {
								StringResourceTD stringResourceTD = (StringResourceTD) resource;
								if (stringResourceTD.getStringValue() != null
										&& !stringResourceTD.getStringValue().isEmpty()) {
									resourceData = "<tr>" + "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"
											+ msgs.valueLabelFixed() + "</b></td>"
											+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
											+ SafeHtmlUtils.htmlEscape(stringResourceTD.getStringValue()) + "</td>"
											+ "</tr>";
								}

							} else {
								if (resource instanceof TableResourceTD) {
									TableResourceTD tableResourceTD = (TableResourceTD) resource;
									resourceData = "<tr>" + "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"
											+ msgs.tableIdLabelFixed() + "</b></td>"
											+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
											+ new SafeHtmlBuilder().append(tableResourceTD.getTableId()).toSafeHtml()
													.asString()
											+

											"</td>" + "</tr>";

								} else {
									if (resource instanceof SDMXResourceTD) {
										SDMXResourceTD sdmxResourceTD = (SDMXResourceTD) resource;
										/*
										 * if (sdmxResourceTD.getName() != null
										 * &&
										 * !sdmxResourceTD.getName().isEmpty())
										 * { resourceData = resourceData +
										 * "<tr>" +
										 * "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"
										 * + msgs.sdmxNameIdLabelFixed() +
										 * "</b></td>" +
										 * "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
										 * + "<span>" +
										 * SafeHtmlUtils.htmlEscape(
										 * sdmxResourceTD.getName()) + "</span>"
										 * + "</td>" + "</tr>";
										 * 
										 * }
										 */

										if (sdmxResourceTD.getResourceUrl() != null
												&& !sdmxResourceTD.getResourceUrl().isEmpty()) {

											resourceData = resourceData + "<tr>"
													+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"
													+ msgs.sdmxURLLabelFixed() + "</b></td>"
													+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
													+ "<a href='"
													+ SafeHtmlUtils.htmlEscape(sdmxResourceTD.getResourceUrl()) + "'>"
													+ SafeHtmlUtils.htmlEscape(sdmxResourceTD.getResourceUrl()) + "</a>"
													+ "</td>" + "</tr>";

										}

										if (sdmxResourceTD.getVersion() != null
												&& !sdmxResourceTD.getVersion().isEmpty()) {
											resourceData = resourceData + "<tr>"
													+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"
													+ msgs.sdmxVersionLabelFixed() + "</b></td>"
													+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
													+ SafeHtmlUtils.htmlEscape(sdmxResourceTD.getVersion()) + "</td>"
													+ "</tr>";

										}

										if (sdmxResourceTD.getAgency() != null
												&& !sdmxResourceTD.getAgency().isEmpty()) {
											resourceData = resourceData + "<tr>"
													+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"
													+ msgs.sdmxAgencyLabelFixed() + "</b></td>"
													+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
													+ SafeHtmlUtils.htmlEscape(sdmxResourceTD.getAgency()) + "</td>"
													+ "</tr>";

										}

										if (sdmxResourceTD.getRegistryUrl() != null
												&& !sdmxResourceTD.getRegistryUrl().isEmpty()) {
											resourceData = resourceData + "<tr>"
													+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"
													+ msgs.sdmxRegistryLabelFixed() + "</b></td>"
													+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
													+ "<a href='"
													+ SafeHtmlUtils.htmlEscape(sdmxResourceTD.getRegistryUrl()) + "'>"
													+ SafeHtmlUtils.htmlEscape(sdmxResourceTD.getRegistryUrl()) + "</a>"
													+ "</td>" + "</tr>";

										}

									} else {

									}

								}
							}
						}
						sb.appendHtmlConstant(data + resourceData + "</table>");

					}

				});

		expanderColumn.setHideable(false);
		// SafeStylesBuilder styleB=new SafeStylesBuilder();
		// expanderColumn.setColumnStyle(styleB.margin(5.0,
		// Unit.PX).toSafeStyles());
		expanderColumn.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

		ColumnConfig<ResourceTDDescriptor, String> nameCol = new ColumnConfig<ResourceTDDescriptor, String>(
				props.name(), 140, msgs.nameCol());

		nameCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span title='" + SafeHtmlUtils.htmlEscape(value) + "'>"
						+ SafeHtmlUtils.htmlEscape(value) + "</span>");

			}

		});

		ColumnConfig<ResourceTDDescriptor, String> creationDateColumn = new ColumnConfig<ResourceTDDescriptor, String>(
				props.creationDate(), 70, msgs.creationDateLabel());

		ColumnConfig<ResourceTDDescriptor, ResourceTDType> typeColumn = new ColumnConfig<ResourceTDDescriptor, ResourceTDType>(
				props.resourceType(), 5, msgs.typeCol());

		ResourceTDTypeButtonCell typeButton = new ResourceTDTypeButtonCell();
		/*
		 * typeButton.addSelectHandler(new SelectHandler() {
		 * 
		 * @Override public void onSelect(SelectEvent event) {
		 * Log.debug("Button  Pressed");
		 * 
		 * Context c = event.getContext(); int rowIndex = c.getIndex(); int
		 * columnIndex = c.getColumn();
		 * 
		 * Element el = grid.getView().getCell(rowIndex, columnIndex);
		 * 
		 * NativeEvent contextEvent =
		 * Document.get().createMouseEvent(BrowserEvents.CONTEXTMENU, true,
		 * true, 0, 0, 0, el.getAbsoluteLeft(), el.getAbsoluteTop(), false,
		 * false, false, false, NativeEvent.BUTTON_RIGHT, null);
		 * 
		 * // NativeEvent contextEvent = //
		 * Document.get().createContextMenuEvent();
		 * el.dispatchEvent(contextEvent); // DomEvent } });
		 */

		typeColumn.setCell(typeButton);

		ResourcesActionCell btnOpenActionCell = new ResourcesActionCell("open");
		TextButtonCell btnOpenCell = (TextButtonCell) btnOpenActionCell.getCell();
		btnOpenCell.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				Log.debug("Open Event");
				ResourceTDDescriptor resourceTDDescriptor = store.get(event.getContext().getIndex());
				requestOpen(resourceTDDescriptor);

			}
		});
		btnOpenCell.setText(msgs.btnOpenText());
		btnOpenCell.setIcon(ResourceBundle.INSTANCE.resources());
		btnOpenCell.setIconAlign(IconAlign.RIGHT);

		ResourcesActionCell btnSaveActionCell = new ResourcesActionCell("save");
		TextButtonCell btnSaveCell = (TextButtonCell) btnSaveActionCell.getCell();
		btnSaveCell.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				Log.debug("Save Event");
				ResourceTDDescriptor resourceTDDescriptor = store.get(event.getContext().getIndex());
				requestSave(resourceTDDescriptor);

			}
		});
		btnSaveCell.setText(msgsCommon.btnSaveText());
		btnSaveCell.setIcon(ResourceBundle.INSTANCE.save());
		btnSaveCell.setIconAlign(IconAlign.RIGHT);

		ResourcesActionCell btnDeleteActionCell = new ResourcesActionCell("delete");
		TextButtonCell btnDeleteCell = (TextButtonCell) btnDeleteActionCell.getCell();
		btnDeleteCell.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				Log.debug("Delete Event");
				ResourceTDDescriptor resourceTDDescriptor = store.get(event.getContext().getIndex());
				requestRemove(resourceTDDescriptor);

			}
		});
		btnDeleteCell.setText(msgs.btnDeleteText());
		btnDeleteCell.setIcon(ResourceBundle.INSTANCE.delete());
		btnDeleteCell.setIconAlign(IconAlign.RIGHT);

		List<HasCell<ResourceTDDescriptor, ?>> listOfActionCells = new ArrayList<>();
		listOfActionCells.add(btnOpenActionCell);
		listOfActionCells.add(btnSaveActionCell);
		listOfActionCells.add(btnDeleteActionCell);

		CompositeCell<ResourceTDDescriptor> actionGroupCell = new CompositeCell<ResourceTDDescriptor>(
				listOfActionCells) {
			// override the default layout
			@Override
			protected <X> void render(Cell.Context context, ResourceTDDescriptor value, SafeHtmlBuilder sb,
					HasCell<ResourceTDDescriptor, X> hasCell) {
				// Override individual cell layout
				Cell<X> cell = hasCell.getCell();

				// set the width of the text input cell
				if (value != null && value.getResourceType() != null) {
					switch (value.getResourceType()) {
					case CHART:
						sb.appendHtmlConstant("<div style='display:block;float:left;'>");
						cell.render(context, hasCell.getValue(value), sb);
						sb.appendHtmlConstant("</div>");
						break;
					case CODELIST:
						if (((String) hasCell.getValue(value)).compareTo("delete") == 0) {
							sb.appendHtmlConstant("<div style='display:block;float:left;'>");
							cell.render(context, hasCell.getValue(value), sb);
							sb.appendHtmlConstant("</div>");
						}
						break;
					case CSV:
						if (((String) hasCell.getValue(value)).compareTo("delete") == 0) {
							sb.appendHtmlConstant("<div style='display:block;float:left;'>");
							cell.render(context, hasCell.getValue(value), sb);
							sb.appendHtmlConstant("</div>");
						} else {
							if (((String) hasCell.getValue(value)).compareTo("save") == 0) {
								sb.appendHtmlConstant("<div style='display:block;float:left;'>");
								cell.render(context, hasCell.getValue(value), sb);
								sb.appendHtmlConstant("</div>");
							}
						}
						break;
					case GENERIC_FILE:
						sb.appendHtmlConstant("<div style='display:block;float:left;'>");
						cell.render(context, hasCell.getValue(value), sb);
						sb.appendHtmlConstant("</div>");
						break;
					case GENERIC_TABLE:
						if (((String) hasCell.getValue(value)).compareTo("delete") == 0) {
							sb.appendHtmlConstant("<div style='display:block;float:left;'>");
							cell.render(context, hasCell.getValue(value), sb);
							sb.appendHtmlConstant("</div>");
						}
						break;
					case GUESSER:
						if (((String) hasCell.getValue(value)).compareTo("delete") == 0) {
							sb.appendHtmlConstant("<div style='display:block;float:left;'>");
							cell.render(context, hasCell.getValue(value), sb);
							sb.appendHtmlConstant("</div>");
						}
						break;
					case JSON:
						if (((String) hasCell.getValue(value)).compareTo("delete") == 0) {
							sb.appendHtmlConstant("<div style='display:block;float:left;'>");
							cell.render(context, hasCell.getValue(value), sb);
							sb.appendHtmlConstant("</div>");
						} else {
							if (((String) hasCell.getValue(value)).compareTo("save") == 0) {
								sb.appendHtmlConstant("<div style='display:block;float:left;'>");
								cell.render(context, hasCell.getValue(value), sb);
								sb.appendHtmlConstant("</div>");
							}
						}
						break;
					case MAP:
						if (((String) hasCell.getValue(value)).compareTo("delete") == 0) {
							sb.appendHtmlConstant("<div style='display:block;float:left;'>");
							cell.render(context, hasCell.getValue(value), sb);
							sb.appendHtmlConstant("</div>");
						} else {
							if (((String) hasCell.getValue(value)).compareTo("open") == 0) {
								sb.appendHtmlConstant("<div style='display:block;float:left;'>");
								cell.render(context, hasCell.getValue(value), sb);
								sb.appendHtmlConstant("</div>");
							}
						}
						break;
					case SDMX:
						if (((String) hasCell.getValue(value)).compareTo("delete") == 0) {
							sb.appendHtmlConstant("<div style='display:block;float:left;'>");
							cell.render(context, hasCell.getValue(value), sb);
							sb.appendHtmlConstant("</div>");
						} else {
							if (((String) hasCell.getValue(value)).compareTo("open") == 0) {
								sb.appendHtmlConstant("<div style='display:block;float:left;'>");
								cell.render(context, hasCell.getValue(value), sb);
								sb.appendHtmlConstant("</div>");
							}
						}
						break;
					default:
						if (((String) hasCell.getValue(value)).compareTo("delete") == 0) {
							sb.appendHtmlConstant("<div style='display:block;float:left;'>");
							cell.render(context, hasCell.getValue(value), sb);
							sb.appendHtmlConstant("</div>");
						}
						break;

					}

				}

			}

		};

		ColumnConfig<ResourceTDDescriptor, ResourceTDDescriptor> actionColumn = new ColumnConfig<ResourceTDDescriptor, ResourceTDDescriptor>(
				new IdentityValueProvider<ResourceTDDescriptor>("Action"));

		actionColumn.setHeader("Action");
		actionColumn.setWidth(120);
		actionColumn.setCell(actionGroupCell);
		actionColumn.setHideable(false);

		List<ColumnConfig<ResourceTDDescriptor, ?>> l = new ArrayList<ColumnConfig<ResourceTDDescriptor, ?>>();
		l.add(expanderColumn);
		l.add(nameCol);
		l.add(creationDateColumn);
		l.add(typeColumn);
		l.add(actionColumn);
		// l.add(openColumn);
		// l.add(saveColumn);
		// l.add(deleteColumn);

		ColumnModel<ResourceTDDescriptor> cm = new ColumnModel<ResourceTDDescriptor>(l);

		store = new ExtendedListStore<ResourceTDDescriptor>(props.id());

		searchField.addKeyUpHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				Log.trace("searchTerm: " + searchField.getCurrentValue());
				store.applyFilters();
			}
		});

		store.addFilter(new StoreFilter<ResourceTDDescriptor>() {

			public boolean select(Store<ResourceTDDescriptor> store, ResourceTDDescriptor parent,
					ResourceTDDescriptor item) {
				String searchTerm = searchField.getCurrentValue();
				if (searchTerm == null)
					return true;
				return ResourcesPanel.this.select(item, searchTerm);
			}
		});

		store.setEnableFilters(true);

		RpcProxy<ListLoadConfig, ListLoadResult<ResourceTDDescriptor>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<ResourceTDDescriptor>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<ResourceTDDescriptor>> callback) {
				loadData(loadConfig, callback);
			}

		};

		loader = new ListLoader<ListLoadConfig, ListLoadResult<ResourceTDDescriptor>>(proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(
				new LoadResultListStoreBinding<ListLoadConfig, ResourceTDDescriptor, ListLoadResult<ResourceTDDescriptor>>(
						store) {
				});

		grid = new Grid<ResourceTDDescriptor>(store, cm) {
			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				Scheduler.get().scheduleDeferred(new ScheduledCommand() {

					public void execute() {
						loader.load();
					}
				});
			}

		};

		grid.setLoader(loader);
		grid.setSelectionModel(sm);
		grid.setSize(GRID_RESOURCE_WIDTH, GRID_RESOURCE_HEIGHT);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(false);
		grid.setColumnResize(true);
		grid.getView().setAutoExpandColumn(nameCol);
		grid.getView().setEmptyText(msgs.gridEmptyText());

		expanderColumn.initPlugin(grid);

		createContextMenu();

		SelectHandler sh = new SelectHandler() {
			public void onSelect(SelectEvent event) {
				loader.load();
			}
		};

		btnReload.addSelectHandler(sh);

		VerticalLayoutContainer con = new VerticalLayoutContainer();
		con.add(toolBar, new VerticalLayoutData(1, -1));
		con.add(grid, new VerticalLayoutData(1, 1));

		add(con, new MarginData(0));

	}

	private boolean select(ResourceTDDescriptor item, String searchTerm) {
		if (item.getName() != null && item.getName().toLowerCase().contains(searchTerm.toLowerCase()))
			return true;
		if (item.getCreationDate() != null && item.getCreationDate().toLowerCase().contains(searchTerm.toLowerCase()))
			return true;
		return false;
	}

	private void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<ResourceTDDescriptor>> callback) {

		TDGWTServiceAsync.INSTANCE.getResourcesTD(trId, new AsyncCallback<ArrayList<ResourceTDDescriptor>>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
				} else {
					if (caught instanceof TDGWTIsLockedException) {
						Log.error(caught.getLocalizedMessage());
						UtilsGXT3.alert(msgsCommon.errorLocked(), caught.getLocalizedMessage());
					} else {
						Log.error("Error Retrieving Resources: " + caught.getLocalizedMessage());
						UtilsGXT3.alert(msgs.errorRetrievingResourcesHead(), caught.getLocalizedMessage());
					}
				}
				callback.onFailure(caught);
			}

			public void onSuccess(ArrayList<ResourceTDDescriptor> result) {
				Log.debug("loaded " + result.size());
				try {
					callback.onSuccess(new ListLoadResultBean<ResourceTDDescriptor>(result));
				} catch (Throwable e) {
					Log.debug("Error: " + e.getLocalizedMessage());
					e.printStackTrace();
				}
			}

		});

	}

	private void createContextMenu() {
		contextMenu = new Menu();

		openItem = new MenuItem();
		openItem.setText(msgs.itemOpenText());
		openItem.setIcon(ResourceBundle.INSTANCE.resources());
		openItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				ResourceTDDescriptor selected = grid.getSelectionModel().getSelectedItem();
				Log.debug("selected: " + selected);
				requestOpen(selected);
			}

		});

		saveItem = new MenuItem();
		saveItem.setText(msgs.itemSaveText());
		saveItem.setIcon(ResourceBundle.INSTANCE.save());
		saveItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				ResourceTDDescriptor selected = grid.getSelectionModel().getSelectedItem();
				Log.debug("selected: " + selected);
				requestSave(selected);
			}

		});

		deleteItem = new MenuItem();
		deleteItem.setText(msgs.itemDeleteText());
		deleteItem.setIcon(ResourceBundle.INSTANCE.delete());
		deleteItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				ResourceTDDescriptor selected = grid.getSelectionModel().getSelectedItem();
				Log.debug("selected: " + selected);
				requestRemove(selected);
			}

		});

		grid.setContextMenu(contextMenu);

		grid.addBeforeShowContextMenuHandler(new BeforeShowContextMenuHandler() {

			@Override
			public void onBeforeShowContextMenu(BeforeShowContextMenuEvent event) {
				Menu contextMenu = event.getMenu();

				ResourceTDDescriptor selected = grid.getSelectionModel().getSelectedItem();
				ResourceTDType resourceTDType = selected.getResourceType();
				switch (resourceTDType) {
				case CHART:
					contextMenu.clear();
					openItem.setIcon(ResourceBundle.INSTANCE.chart());
					contextMenu.add(openItem);
					contextMenu.add(saveItem);
					contextMenu.add(deleteItem);
					grid.setContextMenu(contextMenu);
					break;
				case CODELIST:
					contextMenu.clear();
					contextMenu.add(deleteItem);
					grid.setContextMenu(contextMenu);
					break;
				case CSV:
					contextMenu.clear();
					contextMenu.add(saveItem);
					contextMenu.add(deleteItem);
					grid.setContextMenu(contextMenu);
					break;
				case GUESSER:
					contextMenu.clear();
					contextMenu.add(deleteItem);
					grid.setContextMenu(contextMenu);
					break;
				case JSON:
					contextMenu.clear();
					contextMenu.add(saveItem);
					contextMenu.add(deleteItem);
					grid.setContextMenu(contextMenu);
					break;
				case MAP:
					contextMenu.clear();
					openItem.setIcon(ResourceBundle.INSTANCE.gis());
					contextMenu.add(openItem);
					contextMenu.add(deleteItem);
					grid.setContextMenu(contextMenu);
					break;
				case SDMX:
					contextMenu.clear();
					contextMenu.add(openItem);
					contextMenu.add(deleteItem);
					grid.setContextMenu(contextMenu);
					break;
				case GENERIC_FILE:
					contextMenu.clear();
					openItem.setIcon(ResourceBundle.INSTANCE.file());
					contextMenu.add(openItem);
					contextMenu.add(saveItem);
					contextMenu.add(deleteItem);
					grid.setContextMenu(contextMenu);
					break;
				case GENERIC_TABLE:
					contextMenu.clear();
					contextMenu.add(deleteItem);
					grid.setContextMenu(contextMenu);
					break;
				default:
					contextMenu.clear();
					contextMenu.add(deleteItem);
					grid.setContextMenu(contextMenu);
					event.setCancelled(true);
					break;

				}

			}
		});

	}

	private void requestSave(ResourceTDDescriptor resourceTDDescriptor) {
		switch (resourceTDDescriptor.getResourceType()) {
		case CHART:
			requestSaveResource(resourceTDDescriptor, MimeTypeSupport._jpg);
			break;
		case CODELIST:
			break;
		case CSV:
			requestSaveResource(resourceTDDescriptor, MimeTypeSupport._csv);
			break;
		case GUESSER:
			break;
		case JSON:
			requestSaveResource(resourceTDDescriptor, MimeTypeSupport._json);
			break;
		case MAP:
			break;
		case SDMX:
			break;
		case GENERIC_FILE:
			requestSaveResource(resourceTDDescriptor, MimeTypeSupport._unknow);
			break;
		case GENERIC_TABLE:
			// requestSaveResource(resourceTDDescriptor, MimeTypeSupport._csv);
			break;
		default:
			break;

		}

	}

	private void requestSaveResource(ResourceTDDescriptor resourceTDDescriptor, MimeTypeSupport mime) {

		saveResourceSession = new SaveResourceSession();
		saveResourceSession.setResourceTDDescriptor(resourceTDDescriptor);
		saveResourceSession.setMime(mime);
		saveResourceSession.setFileName(resourceTDDescriptor.getName());
		saveResourceSession.setFileDescription(resourceTDDescriptor.getDescription());

		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {

				SaveResourceWizard saveResourceWizard = new SaveResourceWizard(saveResourceSession,
						msgs.saveResourceWizardHead(), eventBus);

				saveResourceWizard.addListener(new WizardListener() {

					public void failed(String title, String message, String details, Throwable throwable) {
						Log.debug(title + ", " + message + " " + details);
					}

					public void completed(TRId id) {
						Log.debug("Save Resource Completed");

					}

					@Override
					public void putInBackground() {
						Log.debug("PutInBakground");
					}

					public void aborted() {
						Log.debug("Save Resource Aborted");
					}
				});

				saveResourceWizard.show();
			}

			public void onFailure(Throwable reason) {
				Log.error("Async code loading failed", reason);
				eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
			}
		});
	}

	private void requestRemove(ResourceTDDescriptor resourceTDDescriptor) {
		ArrayList<ResourceTDDescriptor> resources = new ArrayList<ResourceTDDescriptor>();
		resources.add(resourceTDDescriptor);

		removeResourceSession = new RemoveResourceSession(trId, resources);

		TDGWTServiceAsync.INSTANCE.removeResource(removeResourceSession, new AsyncCallback<Void>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
				} else {
					if (caught instanceof TDGWTIsLockedException) {
						Log.error(caught.getLocalizedMessage());
						UtilsGXT3.alert(msgsCommon.errorLocked(), caught.getLocalizedMessage());
					} else {
						Log.error("Error removing the resource: " + caught.getLocalizedMessage());
						UtilsGXT3.alert(msgsCommon.error(),
								msgs.errorRetrievingResourcesFixed() + caught.getLocalizedMessage());
					}
				}
			}

			public void onSuccess(Void v) {
				Log.debug("Resource removed");
				grid.getLoader().load();
			}

		});

	}

	private void requestOpen(ResourceTDDescriptor resourceTDDescriptor) {
		switch (resourceTDDescriptor.getResourceType()) {
		case CHART:
			repquestOpenChart(resourceTDDescriptor);
			break;
		case CODELIST:
			break;
		case CSV:
			break;
		case GUESSER:
			break;
		case JSON:
			break;
		case MAP:
			requestOpenMap(resourceTDDescriptor);
			break;
		case SDMX:
			requestOpenSDMX(resourceTDDescriptor);
			break;
		case GENERIC_FILE:
			requestOpenGenericFile(resourceTDDescriptor);
			break;
		case GENERIC_TABLE:
			break;
		default:
			break;

		}

	}

	private void repquestOpenChart(ResourceTDDescriptor resourceTDDescriptor) {
		ChartViewerDialog chartDialog = new ChartViewerDialog(resourceTDDescriptor, trId, eventBus);
		chartDialog.show();

	}

	private void requestOpenSDMX(ResourceTDDescriptor resourceTDDescriptor) {
		ResourceTD resource = resourceTDDescriptor.getResourceTD();
		if (resource instanceof SDMXResourceTD) {
			SDMXResourceTD sdmxResourceTD = (SDMXResourceTD) resource;
			String link = sdmxResourceTD.getResourceUrl();
			Log.debug("Retrieved link: " + link);
			Window.open(link, resourceTDDescriptor.getName(), "");

		} else {
			Log.error("Invalid SDMX Resource: " + resource);
			UtilsGXT3.alert(msgsCommon.error(), msgs.errorInvalidResource());
		}
	}

	private void requestOpenMap(final ResourceTDDescriptor resourceTDDescriptor) {
		ResourceTD resource = resourceTDDescriptor.getResourceTD();
		if (resource instanceof StringResourceTD) {
			StringResourceTD stringResourceTD = (StringResourceTD) resource;
			UriResolverSession uriResolverSession = new UriResolverSession(stringResourceTD.getValue(),
					ApplicationType.GIS);

			TDGWTServiceAsync.INSTANCE.getUriFromResolver(uriResolverSession, new AsyncCallback<String>() {

				public void onFailure(Throwable caught) {
					if (caught instanceof TDGWTSessionExpiredException) {
						eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
					} else {
						Log.error("Error with uri resolver: " + caught.getLocalizedMessage());
						UtilsGXT3.alert(msgsCommon.error(), msgs.errorRetrievingURIFromResolver());
					}
				}

				public void onSuccess(String link) {
					Log.debug("Retrieved link: " + link);
					Window.open(link, resourceTDDescriptor.getName(), "");
				}

			});

		} else {
			if (resource instanceof InternalURITD) {
				InternalURITD internalURITD = (InternalURITD) resource;
				UriResolverSession uriResolverSession = new UriResolverSession(internalURITD.getId(),
						ApplicationType.GIS);

				TDGWTServiceAsync.INSTANCE.getUriFromResolver(uriResolverSession, new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Error with uri resolver: " + caught.getLocalizedMessage());
							UtilsGXT3.alert(msgsCommon.error(), msgs.errorRetrievingURIFromResolver());
						}
					}

					public void onSuccess(String link) {
						Log.debug("Retrieved link: " + link);
						Window.open(link, resourceTDDescriptor.getName(), "");
					}

				});

			} else {
				if (resource instanceof TableResourceTD) {

				} else {
					Log.error("Error with resource: no valid resource");
					UtilsGXT3.alert(msgsCommon.error(), msgs.errorNoValidInternalUri());

				}

			}
		}
	}

	private void requestOpenGenericFile(final ResourceTDDescriptor resourceTDDescriptor) {
		ResourceTD resource = resourceTDDescriptor.getResourceTD();
		if (resource instanceof InternalURITD) {

			InternalURITD genericFileResourceTD = (InternalURITD) resource;
			String name = resourceTDDescriptor.getName();

			MimeTypeSupport mts = MimeTypeSupport.getMimeTypeSupportFromMimeName(genericFileResourceTD.getMimeType());
			if (mts != null) {
				name = name + mts.getExtension();
			}

			if (genericFileResourceTD.getId() != null && !genericFileResourceTD.getId().isEmpty()
					&& (genericFileResourceTD.getId().startsWith("http:")
							|| genericFileResourceTD.getId().startsWith("https:"))) {

				Log.debug("Use direct http link");
				Window.open(genericFileResourceTD.getId(), resourceTDDescriptor.getName(), "");

			} else {

				UriResolverSession uriResolverSession = new UriResolverSession(genericFileResourceTD.getId(),
						ApplicationType.SMP_ID, name, genericFileResourceTD.getMimeType());

				TDGWTServiceAsync.INSTANCE.getUriFromResolver(uriResolverSession, new AsyncCallback<String>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Error with uri resolver: " + caught.getLocalizedMessage());
							UtilsGXT3.alert(msgsCommon.error(), msgs.errorRetrievingURIFromResolver());
						}
					}

					public void onSuccess(String link) {
						Log.debug("Retrieved link: " + link);
						Window.open(link, resourceTDDescriptor.getName(), "");
					}

				});
			}
		} else {
			Log.error("Error with resource: no valid resource");
			UtilsGXT3.alert(msgsCommon.error(), msgs.errorNoValidInternalUri());

		}

	}

	public void close() {

		if (parent != null) {
			parent.close();
		}

	}

	protected class ExtendedListStore<M> extends ListStore<M> {

		public ExtendedListStore(ModelKeyProvider<? super M> keyProvider) {
			super(keyProvider);
		}

		public void applyFilters() {
			super.applyFilters();
		}

	}

}
