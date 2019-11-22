package org.gcube.portlets.user.td.resourceswidget.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.XTemplates.Formatter;
import com.sencha.gxt.core.client.XTemplates.FormatterFactories;
import com.sencha.gxt.core.client.XTemplates.FormatterFactory;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.data.shared.loader.ListStoreBinding;
import com.sencha.gxt.data.shared.loader.Loader;
import com.sencha.gxt.theme.base.client.listview.ListViewCustomAppearance;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent;
import com.sencha.gxt.widget.core.client.event.BeforeShowContextMenuEvent.BeforeShowContextMenuHandler;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.StoreFilterField;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.SeparatorToolItem;
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
public class ResourcesListViewPanel extends FramedPanel {

	private static final String HEIGHT = "470px";
	private static final String WIDTH = "630px";

	@FormatterFactories(@FormatterFactory(factory = ShortenFactory.class, name = "shorten"))
	public interface Renderer extends XTemplates {
		@XTemplate(source = "ResourcesListView.html")
		public SafeHtml renderItem(ResourceTDDescriptor resourceTDDescriptor, SafeUri thumbnailPath,
				ResourceListViewCSS style);
	}

	public interface ResourcesListViewBundle extends ClientBundle {
		public static final ResourcesListViewBundle INSTANCE = GWT.create(ResourcesListViewBundle.class);

		@Source("ResourcesListView.css")
		ResourceListViewCSS css();
	}

	public interface ResourceListViewCSS extends CssResource {
		String over();

		String select();

		String thumb();

		String thumbWrap();

	}

	static class Shorten implements Formatter<String> {
		private int length;

		public Shorten(int length) {
			this.length = length;
		}

		@Override
		public String format(String data) {
			return Format.ellipse(data, length);
		}
	}

	static class ShortenFactory {
		public static Shorten getFormat(int length) {
			return new Shorten(length);
		}
	}

	public enum ResourcesSortInfo {
		Name("Name"), CreationDate("Creation Date");

		private static ResourcesMessages msgs = GWT.create(ResourcesMessages.class);
		private String id;

		private static List<String> resourcesSortInfoI18NList;

		static {
			resourcesSortInfoI18NList = new ArrayList<String>();
			for (ResourcesSortInfo r : values()) {
				resourcesSortInfoI18NList.add(msgs.resourceSortInfo(r));
			}
		}

		private ResourcesSortInfo(String id) {
			this.id = id;
		}

		public String getId() {
			return id;
		}

		public String toString() {
			return id;
		}

		public String getIdI18N() {
			return msgs.resourceSortInfo(this);
		}

		public static List<ResourcesSortInfo> asList() {
			List<ResourcesSortInfo> list = Arrays.asList(values());
			return list;
		}

		public static List<String> asI18NList() {
			return resourcesSortInfoI18NList;

		}

	}

	private ResourcesListViewDetailPanel details;
	private ResourceListViewCSS style;
	private Renderer renderer;

	private TRId trId;
	private EventBus eventBus;

	private ListStore<ResourceTDDescriptor> store;
	// private ListLoader<Object, ListLoadResult<ResourceTDDescriptor>> loader;
	private Loader<Object, List<ResourceTDDescriptor>> loader;

	private ListView<ResourceTDDescriptor, ResourceTDDescriptor> listView;
	private SimpleComboBox<String> comboSort;
	private Menu contextMenu;
	private MenuItem openItem;
	private MenuItem saveItem;
	private MenuItem deleteItem;
	private RemoveResourceSession removeResourceSession;
	private SaveResourceSession saveResourceSession;
	private ToolBar statusBar;
	private CommonMessages msgsCommon;
	private ResourcesMessages msgs;

	public ResourcesListViewPanel(EventBus eventBus) {
		super();
		this.eventBus = eventBus;
		initMessages();
		init();
		create();
	}

	private void initMessages() {
		msgs = GWT.create(ResourcesMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}

	public void open(TRId trId) {
		try {
			this.trId = trId;
			loader.load();
			details.setDescriptor(null);
			forceLayout();
		} catch (Throwable e) {
			Log.error("Error open resources: " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void init() {
		forceLayoutOnResize = true;
		setHeaderVisible(false);
		setBodyBorder(false);
		setResize(true);
		setWidth(WIDTH);
		setHeight(HEIGHT);

	}

	private void create() {
		Log.debug("Create Resource List View");
		RpcProxy<Object, List<ResourceTDDescriptor>> proxy = new RpcProxy<Object, List<ResourceTDDescriptor>>() {
			@Override
			public void load(Object loadConfig, AsyncCallback<List<ResourceTDDescriptor>> callback) {
				loadData(callback);
			}
		};

		ModelKeyProvider<ResourceTDDescriptor> keyProvider = new ModelKeyProvider<ResourceTDDescriptor>() {
			@Override
			public String getKey(ResourceTDDescriptor item) {
				return String.valueOf(item.getId());
			}
		};

		store = new ListStore<ResourceTDDescriptor>(keyProvider);
		store.addSortInfo(new StoreSortInfo<ResourceTDDescriptor>(new Comparator<ResourceTDDescriptor>() {
			@Override
			public int compare(ResourceTDDescriptor o1, ResourceTDDescriptor o2) {

				String v = comboSort.getCurrentValue();
				if (v.equals(ResourcesSortInfo.Name.getIdI18N())) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				} else if (v.equals(ResourcesSortInfo.CreationDate.getIdI18N())) {
					return o1.getCreationDate().compareTo(o2.getCreationDate());

				}

				return 0;
			}
		}, SortDir.ASC));

		loader = new Loader<Object, List<ResourceTDDescriptor>>(proxy);
		loader.addLoadHandler(new ListStoreBinding<Object, ResourceTDDescriptor, List<ResourceTDDescriptor>>(store));

		// ToolBar

		StoreFilterField<ResourceTDDescriptor> filterField = new StoreFilterField<ResourceTDDescriptor>() {
			@Override
			protected boolean doSelect(Store<ResourceTDDescriptor> store, ResourceTDDescriptor parent,
					ResourceTDDescriptor item, String filter) {
				String name = item.getName().toLowerCase();
				if (name.indexOf(filter.toLowerCase()) != -1) {
					return true;
				}
				return false;
			}

			@Override
			protected void onFilter() {
				super.onFilter();
				listView.getSelectionModel().select(0, false);
			}
		};
		filterField.setWidth(100);
		filterField.bind(store);

		ToolBar toolBar = new ToolBar();
		toolBar.add(new LabelToolItem(msgs.toolBarFilterLabel()));
		toolBar.add(filterField);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(new LabelToolItem(msgs.toolBarSortBy()));

		comboSort = new SimpleComboBox<String>(new StringLabelProvider<String>());
		comboSort.setTriggerAction(TriggerAction.ALL);
		comboSort.setEditable(false);
		comboSort.setForceSelection(true);
		comboSort.setWidth(120);
		comboSort.add(ResourcesSortInfo.asI18NList());
		comboSort.setValue(ResourcesSortInfo.CreationDate.getIdI18N());
		comboSort.addSelectionHandler(new SelectionHandler<String>() {

			@Override
			public void onSelection(SelectionEvent<String> event) {
				store.applySort(false);
			}

		});
		toolBar.add(comboSort);

		//
		statusBar = new ToolBar();

		//
		ResourcesListViewBundle.INSTANCE.css().ensureInjected();

		style = ResourcesListViewBundle.INSTANCE.css();

		renderer = GWT.create(Renderer.class);

		ListViewCustomAppearance<ResourceTDDescriptor> appearance = new ListViewCustomAppearance<ResourceTDDescriptor>(
				"." + style.thumbWrap(), style.over(), style.select()) {
			@Override
			public void renderEnd(SafeHtmlBuilder builder) {
				String markup = new StringBuilder("<div class=\"").append(CommonStyles.get().clear())
						.append("\"></div>").toString();
				builder.appendHtmlConstant(markup);
			}

			@Override
			public void renderItem(SafeHtmlBuilder builder, SafeHtml content) {
				builder.appendHtmlConstant("<div class='" + style.thumbWrap() + "' style='border: 1px solid white'>");
				builder.append(content);
				builder.appendHtmlConstant("</div>");
			}
		};

		listView = new ListView<ResourceTDDescriptor, ResourceTDDescriptor>(store,
				new IdentityValueProvider<ResourceTDDescriptor>() {
					@Override
					public void setValue(ResourceTDDescriptor object, ResourceTDDescriptor value) {
					}
				}, appearance) {

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
		listView.setLoader(loader);
		listView.setCell(
				new SimpleSafeHtmlCell<ResourceTDDescriptor>(new AbstractSafeHtmlRenderer<ResourceTDDescriptor>() {
					@Override
					public SafeHtml render(ResourceTDDescriptor descriptor) {
						return renderCellResource(descriptor);
					}

				}));
		listView.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		listView.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<ResourceTDDescriptor>() {
			@Override
			public void onSelectionChanged(SelectionChangedEvent<ResourceTDDescriptor> event) {
				ResourcesListViewPanel.this.onSelectionChange(event);
			}
		});
		listView.setBorders(false);

		createContextMenu();

		VerticalLayoutContainer main = new VerticalLayoutContainer();
		main.setAdjustForScroll(false);
		main.setScrollMode(ScrollMode.NONE);
		// main.setAdjustForScroll(true);
		main.setBorders(true);
		main.add(toolBar, new VerticalLayoutData(1, -1));
		main.add(listView, new VerticalLayoutData(1, 1));
		main.add(statusBar, new VerticalLayoutData(1, -1));

		details = new ResourcesListViewDetailPanel(this);

		BorderLayoutData centerData = new BorderLayoutData();
		centerData.setMinSize(330);
		centerData.setMargins(new Margins(0, 5, 0, 0));

		BorderLayoutData eastData = new BorderLayoutData(230);
		eastData.setMinSize(180);

		BorderLayoutContainer con = new BorderLayoutContainer();
		con.setCenterWidget(main, centerData);
		con.setEastWidget(details, eastData);

		add(con, new MarginData(0));
	}

	private SafeHtml renderCellResource(ResourceTDDescriptor descriptor) {
		SafeUri thumbnailPath;

		ResourceTDType resourceTDType = descriptor.getResourceType();
		if (resourceTDType == null) {
			thumbnailPath = ResourceBundle.INSTANCE.resources80().getSafeUri();
		} else {

			switch (resourceTDType) {
			case CHART:
				thumbnailPath = ResourceBundle.INSTANCE.chart80().getSafeUri();
				break;
			case CODELIST:
				thumbnailPath = ResourceBundle.INSTANCE.codelist80().getSafeUri();
				break;
			case CSV:
				thumbnailPath = ResourceBundle.INSTANCE.csv80().getSafeUri();
				break;
			case GENERIC_FILE:
				thumbnailPath = ResourceBundle.INSTANCE.file80().getSafeUri();
				break;
			case GENERIC_TABLE:
				thumbnailPath = ResourceBundle.INSTANCE.table80().getSafeUri();
				break;
			case GUESSER:
				thumbnailPath = ResourceBundle.INSTANCE.resources80().getSafeUri();
				break;
			case JSON:
				thumbnailPath = ResourceBundle.INSTANCE.json80().getSafeUri();
				break;
			case MAP:
				thumbnailPath = ResourceBundle.INSTANCE.gis80().getSafeUri();
				break;
			case SDMX:
				thumbnailPath = ResourceBundle.INSTANCE.sdmx80().getSafeUri();
				break;
			default:
				thumbnailPath = ResourceBundle.INSTANCE.resources80().getSafeUri();
				break;
			}
		}

		ResourceTD resourceTD = descriptor.getResourceTD();

		if (resourceTD instanceof InternalURITD) {
			InternalURITD internalURITD = (InternalURITD) resourceTD;
			if (internalURITD.getThumbnailTD() != null && internalURITD.getThumbnailTD().getUrl() != null) {
				thumbnailPath = UriUtils.fromTrustedString(internalURITD.getThumbnailTD().getUrl());
			} else {
				if (internalURITD.getMimeType() != null) {
					if (internalURITD.getMimeType().compareTo(MimeTypeSupport._gif.getMimeName()) == 0
							|| internalURITD.getMimeType().compareTo(MimeTypeSupport._jpg.getMimeName()) == 0
							|| internalURITD.getMimeType().compareTo(MimeTypeSupport._png.getMimeName()) == 0
							|| internalURITD.getMimeType().compareTo(MimeTypeSupport._bmp.getMimeName()) == 0) {
						thumbnailPath = ResourceBundle.INSTANCE.picture80().getSafeUri();
					}
				}
			}
		}

		return renderer.renderItem(descriptor, thumbnailPath, style);
	}

	private void onSelectionChange(SelectionChangedEvent<ResourceTDDescriptor> se) {
		if (se.getSelection().size() > 0) {
			ResourceTDDescriptor descriptor = se.getSelection().get(0);
			details.setDescriptor(descriptor);
		} else {
			details.setDescriptor(null);

		}

	}

	private void loadData(final AsyncCallback<List<ResourceTDDescriptor>> callback) {
		Log.debug("Called loadData");
		if (trId != null) {
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
					Log.debug("Resources loaded " + result.size());
					if (result != null && result.size() > 0) {
						statusBar.clear();
						statusBar.add(new LabelToolItem(msgs.statusBarNumberOfResources(result.size())));
					} else {
						statusBar.clear();
						statusBar.add(new LabelToolItem(msgs.statusBarNoResource()));

					}
					statusBar.forceLayout();
					try {
						callback.onSuccess(result);
					} catch (Throwable e) {
						Log.debug("Error: " + e.getLocalizedMessage());
						e.printStackTrace();
					}
				}

			});
		}
	}

	private void createContextMenu() {
		contextMenu = new Menu();

		openItem = new MenuItem();
		openItem.setText(msgs.itemOpenText());
		openItem.setIcon(ResourceBundle.INSTANCE.resources());
		openItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				List<ResourceTDDescriptor> resources = listView.getSelectionModel().getSelectedItems();
				ResourceTDDescriptor selected = null;
				if (resources != null && resources.size() > 0) {
					selected = resources.get(0);
				}
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
				List<ResourceTDDescriptor> resources = listView.getSelectionModel().getSelectedItems();
				ResourceTDDescriptor selected = null;
				if (resources != null && resources.size() > 0) {
					selected = resources.get(0);
				}
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
				List<ResourceTDDescriptor> selected = listView.getSelectionModel().getSelectedItems();
				Log.debug("selected: " + selected);
				ArrayList<ResourceTDDescriptor> resources = new ArrayList<ResourceTDDescriptor>(selected);
				requestRemove(resources);
			}

		});

		listView.setContextMenu(contextMenu);

		listView.addBeforeShowContextMenuHandler(new BeforeShowContextMenuHandler() {

			@Override
			public void onBeforeShowContextMenu(BeforeShowContextMenuEvent event) {
				Menu contextMenu = event.getMenu();

				ResourceTDDescriptor selected = listView.getSelectionModel().getSelectedItem();
				ResourceTDType resourceTDType = selected.getResourceType();
				switch (resourceTDType) {
				case CHART:
					contextMenu.clear();
					openItem.setIcon(ResourceBundle.INSTANCE.chart());
					contextMenu.add(openItem);
					contextMenu.add(saveItem);
					contextMenu.add(deleteItem);
					listView.setContextMenu(contextMenu);
					break;
				case CODELIST:
					contextMenu.clear();
					contextMenu.add(deleteItem);
					listView.setContextMenu(contextMenu);
					break;
				case CSV:
					contextMenu.clear();
					contextMenu.add(saveItem);
					contextMenu.add(deleteItem);
					listView.setContextMenu(contextMenu);
					break;
				case GUESSER:
					contextMenu.clear();
					contextMenu.add(deleteItem);
					listView.setContextMenu(contextMenu);
					break;
				case JSON:
					contextMenu.clear();
					contextMenu.add(saveItem);
					contextMenu.add(deleteItem);
					listView.setContextMenu(contextMenu);
					break;
				case MAP:
					contextMenu.clear();
					openItem.setIcon(ResourceBundle.INSTANCE.gis());
					contextMenu.add(openItem);
					contextMenu.add(deleteItem);
					listView.setContextMenu(contextMenu);
					break;
				case SDMX:
					contextMenu.clear();
					contextMenu.add(openItem);
					contextMenu.add(deleteItem);
					listView.setContextMenu(contextMenu);
					break;
				case GENERIC_FILE:
					contextMenu.clear();
					openItem.setIcon(ResourceBundle.INSTANCE.file());
					contextMenu.add(openItem);
					contextMenu.add(saveItem);
					contextMenu.add(deleteItem);
					listView.setContextMenu(contextMenu);
					break;
				case GENERIC_TABLE:
					contextMenu.clear();
					contextMenu.add(deleteItem);
					listView.setContextMenu(contextMenu);
					break;
				default:
					contextMenu.clear();
					contextMenu.add(deleteItem);
					listView.setContextMenu(contextMenu);
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

	protected void requestRemove(ArrayList<ResourceTDDescriptor> resourcesList) {
		ArrayList<ResourceTDDescriptor> resources = new ArrayList<ResourceTDDescriptor>(resourcesList);

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
								msgs.errorRemovingTheResource() + caught.getLocalizedMessage());
					}
				}
			}

			public void onSuccess(Void v) {
				Log.debug("Resource removed");
				loader.load();
				details.setDescriptor(null);
				forceLayout();
			}

		});

	}

	protected void requestOpen(ResourceTDDescriptor resourceTDDescriptor) {
		switch (resourceTDDescriptor.getResourceType()) {
		case CHART:
			requestOpenChart(resourceTDDescriptor);
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

	private void requestOpenChart(ResourceTDDescriptor resourceTDDescriptor) {
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
}
