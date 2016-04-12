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
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.SaveResourceSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.StringResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.tr.resources.TableResourceTD;
import org.gcube.portlets.user.td.gwtservice.shared.uriresolver.UriResolverSession;
import org.gcube.portlets.user.td.gwtservice.shared.user.UserInfo;
import org.gcube.portlets.user.td.resourceswidget.client.charts.ChartViewerDialog;
import org.gcube.portlets.user.td.resourceswidget.client.custom.ResourceTDTypeButtonCell;
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
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BrowserEvents;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoadResultBean;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.RowExpander;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * 
 * ResourcesPanel shows the resources
 * 
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ResourcesPanel extends FramedPanel {
	private static final String GRID_RESOURCE_HEIGHT = "300px";
	private static final String GRID_RESOURCE_WIDTH = "200px";
	private static final String WIDTH = "298px";
	private static final String HEIGHT = "520px";
	
	private CommonMessages msgsCommon;
	private ResourcesMessages msgs;
	
	private ResourcesDialog parent;
	private TRId trId;
	private EventBus eventBus;

	private ListStore<ResourceTDDescriptor> store;
	private ListLoader<ListLoadConfig, ListLoadResult<ResourceTDDescriptor>> loader;
	private Grid<ResourceTDDescriptor> grid;

	private boolean drawed = false;
	private Menu contextMenu;
	private MenuItem openItem;
	private MenuItem saveItem;
	private MenuItem removeItem;

	private RemoveResourceSession removeResourceSession;
	private SaveResourceSession saveResourceSession;

	public ResourcesPanel(ResourcesDialog parent, TRId trId, EventBus eventBus) {
		super();
		this.parent = parent;
		this.trId = trId;
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		initMessages();
		retrieveUserInfo();
	}

	public ResourcesPanel(TRId trId, EventBus eventBus) {
		super();
		this.trId = trId;
		this.eventBus = eventBus;
		forceLayoutOnResize = true;
		initMessages();
		retrieveUserInfo();
	}

	protected void initMessages(){
		msgs = GWT.create(ResourcesMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	protected void retrieveUserInfo() {
		TDGWTServiceAsync.INSTANCE.hello(new AsyncCallback<UserInfo>() {

			public void onFailure(Throwable caught) {
				if (caught instanceof TDGWTSessionExpiredException) {
					eventBus.fireEvent(new SessionExpiredEvent(
							SessionExpiredType.EXPIREDONSERVER));
				} else {

					Log.error("Error Retrieving User Info: "
							+ caught.getLocalizedMessage());
					UtilsGXT3.alert(msgsCommon.error(), msgs.errorRetrievingUserInfo());

				}
			}

			public void onSuccess(UserInfo userInfo) {
				Log.debug("User Info: " + userInfo);
				draw();
			}

		});
	}

	protected void draw() {
		drawed = true;
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
		ResourceTDDescriptorProperties props = GWT
				.create(ResourceTDDescriptorProperties.class);

		IdentityValueProvider<ResourceTDDescriptor> identityProvider = new IdentityValueProvider<ResourceTDDescriptor>();

		final GridSelectionModel<ResourceTDDescriptor> sm = new GridSelectionModel<ResourceTDDescriptor>();
		sm.setSelectionMode(SelectionMode.SINGLE);

		RowExpander<ResourceTDDescriptor> expander = new RowExpander<ResourceTDDescriptor>(
				identityProvider, new AbstractCell<ResourceTDDescriptor>() {

					@Override
					public void render(Context context,
							ResourceTDDescriptor value, SafeHtmlBuilder sb) {
						String data = "<table style='font-family: tahoma, arial, verdana, sans-serif;"
								+ "width: 100%; border: none;"
								+ "font-size: 12px; margin:2px; text-align: left;'>"
								+ "<tr>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"+msgs.nameLabelFixed()+"</b></td>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
								+ SafeHtmlUtils.htmlEscape(value.getName())
								+

								"</td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"+msgs.descriptionLabelFixed()+"</b></td>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
								+ SafeHtmlUtils.htmlEscape(value
										.getDescription())
								+

								"</td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"+msgs.creationDateLabelFixed()+"</b></td>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
								+ SafeHtmlUtils.htmlEscape(value
										.getCreationDate())
								+

								"</td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"+msgs.creatorIdLabelFixed()+"</b></td>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
								+ new SafeHtmlBuilder()
										.append(value.getCreatorId())
										.toSafeHtml().asString()
								+

								"</td>"
								+ "</tr>"
								+ "<tr>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"+msgs.typeLabelFixed()+"</b></td>"
								+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
								+ SafeHtmlUtils.htmlEscape(value
										.getResourceType().toString())
								+ "</td>" + "</tr>";

						ResourceTD resource = value.getResourceTD();
						String resourceData = "";
						if (resource instanceof InternalURITD) {
							/*
							 * InternalURITD internalURITD = (InternalURITD)
							 * resource; if (internalURITD.getStringValue() !=
							 * null &&
							 * !internalURITD.getStringValue().isEmpty()) {
							 * resourceData = "<tr>" +
							 * "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>File Id: </b></td>"
							 * +
							 * "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
							 * + SafeHtmlUtils .htmlEscape(internalURITD
							 * .getStringValue()) + "</td>" + "</tr>"; }
							 */

						} else {
							if (resource instanceof StringResourceTD) {
								StringResourceTD stringResourceTD = (StringResourceTD) resource;
								resourceData = "<tr>"
										+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"+msgs.valueLabelFixed()+"</b></td>"
										+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
										+ SafeHtmlUtils
												.htmlEscape(stringResourceTD
														.getStringValue())
										+ "</td>" + "</tr>";

							} else {
								if (resource instanceof TableResourceTD) {
									TableResourceTD tableResourceTD = (TableResourceTD) resource;
									resourceData = "<tr>"
											+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'><b>"+msgs.tableIdLabelFixed()+"</b></td>"
											+ "<td style='font-size: 12px; padding: 2px 1px 1px 1px;'>"
											+ new SafeHtmlBuilder()
													.append(tableResourceTD
															.getTableId())
													.toSafeHtml().asString() +

											"</td>" + "</tr>";

								} else {

								}
							}
						}
						sb.appendHtmlConstant(data + resourceData + "</table>");

					}

				});

		ColumnConfig<ResourceTDDescriptor, String> nameCol = new ColumnConfig<ResourceTDDescriptor, String>(
				props.name(), 142, msgs.nameCol());

		nameCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				sb.appendHtmlConstant("<span title='"
						+ SafeHtmlUtils.htmlEscape(value) + "'>"
						+ SafeHtmlUtils.htmlEscape(value) + "</span>");

			}

		});

		ColumnConfig<ResourceTDDescriptor, ResourceTDType> typeColumn = new ColumnConfig<ResourceTDDescriptor, ResourceTDType>(
				props.resourceType(), 30, msgs.typeCol());

		ResourceTDTypeButtonCell button = new ResourceTDTypeButtonCell();
		button.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				Log.debug("Button  Pressed");

				Context c = event.getContext();
				int rowIndex = c.getIndex();
				int columnIndex = c.getColumn();

				Element el = grid.getView().getCell(rowIndex, columnIndex);

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

		typeColumn.setCell(button);

		List<ColumnConfig<ResourceTDDescriptor, ?>> l = new ArrayList<ColumnConfig<ResourceTDDescriptor, ?>>();
		l.add(expander);
		l.add(nameCol);
		l.add(typeColumn);

		ColumnModel<ResourceTDDescriptor> cm = new ColumnModel<ResourceTDDescriptor>(
				l);

		store = new ListStore<ResourceTDDescriptor>(props.id());

		RpcProxy<ListLoadConfig, ListLoadResult<ResourceTDDescriptor>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<ResourceTDDescriptor>>() {

			public void load(
					ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<ResourceTDDescriptor>> callback) {
				loadData(loadConfig, callback);
			}

		};

		loader = new ListLoader<ListLoadConfig, ListLoadResult<ResourceTDDescriptor>>(
				proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, ResourceTDDescriptor, ListLoadResult<ResourceTDDescriptor>>(
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
		grid.setColumnReordering(true);
		grid.setColumnResize(true);
		grid.getView().setAutoExpandColumn(nameCol);
		grid.getView().setEmptyText(msgs.gridEmptyText());

		expander.initPlugin(grid);

		createContextMenu();

		add(grid, new MarginData(0));

		onResize();
	}

	protected void createContextMenu() {
		contextMenu = new Menu();

		openItem = new MenuItem();
		openItem.setText(msgs.itemOpenText());
		openItem.setIcon(ResourceBundle.INSTANCE.resources());
		openItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				ResourceTDDescriptor selected = grid.getSelectionModel()
						.getSelectedItem();
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
				ResourceTDDescriptor selected = grid.getSelectionModel()
						.getSelectedItem();
				Log.debug("selected: " + selected);
				requestSave(selected);
			}

		});

		removeItem = new MenuItem();
		removeItem.setText(msgs.itemDeleteText());
		removeItem.setIcon(ResourceBundle.INSTANCE.delete());
		removeItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				ResourceTDDescriptor selected = grid.getSelectionModel()
						.getSelectedItem();
				Log.debug("selected: " + selected);
				requestRemove(selected);
			}

		});

		grid.setContextMenu(contextMenu);

		grid.addBeforeShowContextMenuHandler(new BeforeShowContextMenuHandler() {

			@Override
			public void onBeforeShowContextMenu(BeforeShowContextMenuEvent event) {
				Menu contextMenu = event.getMenu();

				ResourceTDDescriptor selected = grid.getSelectionModel()
						.getSelectedItem();
				ResourceTDType resourceTDType = selected.getResourceType();
				switch (resourceTDType) {
				case CHART:
					contextMenu.clear();
					openItem.setIcon(ResourceBundle.INSTANCE.chart());
					contextMenu.add(openItem);
					contextMenu.add(saveItem);
					contextMenu.add(removeItem);
					grid.setContextMenu(contextMenu);
					break;
				case CODELIST:
					contextMenu.clear();
					contextMenu.add(removeItem);
					grid.setContextMenu(contextMenu);
					break;
				case CSV:
					contextMenu.clear();
					contextMenu.add(saveItem);
					contextMenu.add(removeItem);
					grid.setContextMenu(contextMenu);
					break;
				case GUESSER:
					contextMenu.clear();
					contextMenu.add(removeItem);
					grid.setContextMenu(contextMenu);
					break;
				case JSON:
					contextMenu.clear();
					contextMenu.add(saveItem);
					contextMenu.add(removeItem);
					grid.setContextMenu(contextMenu);
					break;
				case MAP:
					contextMenu.clear();
					openItem.setIcon(ResourceBundle.INSTANCE.gis());
					contextMenu.add(openItem);
					contextMenu.add(removeItem);
					grid.setContextMenu(contextMenu);
					break;
				case SDMX:
					contextMenu.clear();
					contextMenu.add(removeItem);
					grid.setContextMenu(contextMenu);
					break;
				case GENERIC_FILE:
					contextMenu.clear();
					openItem.setIcon(ResourceBundle.INSTANCE.file());
					contextMenu.add(openItem);
					contextMenu.add(saveItem);
					contextMenu.add(removeItem);
					grid.setContextMenu(contextMenu);
					break;
				case GENERIC_TABLE:
					contextMenu.clear();
					contextMenu.add(removeItem);
					grid.setContextMenu(contextMenu);
					break;
				default:
					contextMenu.clear();
					contextMenu.add(removeItem);
					grid.setContextMenu(contextMenu);
					event.setCancelled(true);
					break;

				}

			}
		});

	}

	protected void requestSave(ResourceTDDescriptor resourceTDDescriptor) {
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

	protected void requestSaveResource(
			ResourceTDDescriptor resourceTDDescriptor, MimeTypeSupport mime) {

		saveResourceSession = new SaveResourceSession();
		saveResourceSession.setResourceTDDescriptor(resourceTDDescriptor);
		saveResourceSession.setMime(mime);
		saveResourceSession.setFileName(resourceTDDescriptor.getName());
		saveResourceSession.setFileDescription(resourceTDDescriptor
				.getDescription());

		GWT.runAsync(new RunAsyncCallback() {

			public void onSuccess() {

				SaveResourceWizard saveResourceWizard = new SaveResourceWizard(
						saveResourceSession, msgs.saveResourceWizardHead(), eventBus);

				saveResourceWizard.addListener(new WizardListener() {

					public void failed(String title, String message,
							String details, Throwable throwable) {
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
				eventBus.fireEvent(new SessionExpiredEvent(
						SessionExpiredType.EXPIREDONSERVER));
			}
		});
	}

	protected void requestRemove(ResourceTDDescriptor resourceTDDescriptor) {
		ArrayList<ResourceTDDescriptor> resources=new ArrayList<ResourceTDDescriptor>();
		resources.add(resourceTDDescriptor);
		
		
		removeResourceSession = new RemoveResourceSession(trId,
				resources);

		TDGWTServiceAsync.INSTANCE.removeResource(removeResourceSession,
				new AsyncCallback<Void>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								Log.error("Error removing the resource: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.error(),
										msgs.errorRetrievingResourcesFixed()
												+ caught.getLocalizedMessage());
							}
						}
					}

					public void onSuccess(Void v) {
						Log.debug("Resource removed");
						grid.getLoader().load();
						// UtilsGXT3.info("Resource", "Resource Removed!");
					}

				});

	}

	protected void requestOpen(ResourceTDDescriptor resourceTDDescriptor) {
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
		ChartViewerDialog chartDialog = new ChartViewerDialog(
				resourceTDDescriptor, trId, eventBus);
		chartDialog.show();

	}

	protected void requestOpenMap(
			final ResourceTDDescriptor resourceTDDescriptor) {
		ResourceTD resource = resourceTDDescriptor.getResourceTD();
		if (resource instanceof StringResourceTD) {
			StringResourceTD stringResourceTD = (StringResourceTD) resource;
			UriResolverSession uriResolverSession = new UriResolverSession(
					stringResourceTD.getValue(), ApplicationType.GIS);

			TDGWTServiceAsync.INSTANCE.getUriFromResolver(uriResolverSession,
					new AsyncCallback<String>() {

						public void onFailure(Throwable caught) {
							if (caught instanceof TDGWTSessionExpiredException) {
								eventBus.fireEvent(new SessionExpiredEvent(
										SessionExpiredType.EXPIREDONSERVER));
							} else {
								Log.error("Error with uri resolver: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.error(),
										msgs.errorRetrievingURIFromResolver());
							}
						}

						public void onSuccess(String link) {
							Log.debug("Retrieved link: " + link);
							Window.open(link, resourceTDDescriptor.getName(),
									"");
						}

					});

		} else {
			if (resource instanceof InternalURITD) {
				InternalURITD internalURITD = (InternalURITD) resource;
				UriResolverSession uriResolverSession = new UriResolverSession(
						internalURITD.getId(), ApplicationType.GIS);

				TDGWTServiceAsync.INSTANCE.getUriFromResolver(
						uriResolverSession, new AsyncCallback<String>() {

							public void onFailure(Throwable caught) {
								if (caught instanceof TDGWTSessionExpiredException) {
									eventBus.fireEvent(new SessionExpiredEvent(
											SessionExpiredType.EXPIREDONSERVER));
								} else {
									Log.error("Error with uri resolver: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgsCommon.error(),
													msgs.errorRetrievingURIFromResolver());
								}
							}

							public void onSuccess(String link) {
								Log.debug("Retrieved link: " + link);
								Window.open(link,
										resourceTDDescriptor.getName(), "");
							}

						});

			} else {
				if (resource instanceof TableResourceTD) {

				} else {
					Log.error("Error with resource: no valid resource");
					UtilsGXT3.alert(msgsCommon.error(),
							msgs.errorNoValidInternalUri());

				}

			}
		}
	}

	protected void requestOpenGenericFile(
			final ResourceTDDescriptor resourceTDDescriptor) {
		ResourceTD resource = resourceTDDescriptor.getResourceTD();
		if (resource instanceof InternalURITD) {
			InternalURITD genericFileResourceTD = (InternalURITD) resource;
			UriResolverSession uriResolverSession = new UriResolverSession(
					genericFileResourceTD.getId(), ApplicationType.SMP_ID,
					resourceTDDescriptor.getName(),
					genericFileResourceTD.getMimeType());

			TDGWTServiceAsync.INSTANCE.getUriFromResolver(uriResolverSession,
					new AsyncCallback<String>() {

						public void onFailure(Throwable caught) {
							if (caught instanceof TDGWTSessionExpiredException) {
								eventBus.fireEvent(new SessionExpiredEvent(
										SessionExpiredType.EXPIREDONSERVER));
							} else {
								Log.error("Error with uri resolver: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.error(),
										msgs.errorRetrievingURIFromResolver());
							}
						}

						public void onSuccess(String link) {
							Log.debug("Retrieved link: " + link);
							Window.open(link, resourceTDDescriptor.getName(),
									"");
						}

					});
		} else {
			if (resource instanceof InternalURITD) {

			} else {
				if (resource instanceof TableResourceTD) {

				} else {
					Log.error("Error with resource: no valid resource");
					UtilsGXT3.alert(msgsCommon.error(),
							msgs.errorNoValidInternalUri());

				}

			}
		}
	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<ResourceTDDescriptor>> callback) {

		TDGWTServiceAsync.INSTANCE.getResourcesTD(trId,
				new AsyncCallback<ArrayList<ResourceTDDescriptor>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());
							} else {
								Log.error("Error Retrieving Resources: "
										+ caught.getLocalizedMessage());
								UtilsGXT3.alert(msgs.errorRetrievingResourcesHead(),
										caught.getLocalizedMessage());
							}
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<ResourceTDDescriptor> result) {
						Log.debug("loaded " + result.size());
						try {
							callback.onSuccess(new ListLoadResultBean<ResourceTDDescriptor>(
									result));
						} catch (Throwable e) {
							Log.debug("Error: " + e.getLocalizedMessage());
							e.printStackTrace();
						}
					}

				});

	}

	public void update() {
		retrieveCurrentTR();
		loader.load();
		forceLayout();
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
						UtilsGXT3.alert(msgsCommon.errorLocked(),
								caught.getLocalizedMessage());
					} else {
						Log.error("Error retrieving current TRId: "
								+ caught.getLocalizedMessage());
						UtilsGXT3.alert(msgsCommon.error(),
								msgs.errorRetrievingCurrentTabularResourceId());
					}
				}
			}

			public void onSuccess(TRId result) {
				Log.debug("retrieved " + result);
				trId = result;
				if (!drawed) {
					draw();
				}

			}

		});
	}

	protected void close() {

		if (parent != null) {
			parent.close();
		}

	}

}
