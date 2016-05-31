package org.gcube.portlets.user.td.client.template;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.td.client.resource.TabularDataResources;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateData;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.IdentityValueProvider; 
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Margins;
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
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class TemplateSharePanel extends FramedPanel {
	private static final String WIDTH = "840px";
	private static final String HEIGHT = "520px";
	private static final DateTimeFormat sdf=DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
	
	private EventBus eventBus;
	private TemplateShareDialog parent;
	
	private TextButton btnClose;
	private TextButton btnShare;

	private ListLoader<ListLoadConfig, ListLoadResult<TemplateData>> loader;
	private Grid<TemplateData> grid;
	private ExtendedListStore<TemplateData> store;

	public TemplateSharePanel(TemplateShareDialog parent, EventBus eventBus) {
		this.parent = parent;
		Log.debug("TemplateSharePanel");
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		this.eventBus = eventBus;

		create();

	}

	protected void create() {
		ToolBar toolBarHead = new ToolBar();
		toolBarHead.add(new LabelToolItem("Search: "));
		final TextField searchField = new TextField();
		toolBarHead.add(searchField);

		TextButton btnReload = new TextButton();
		// btnReload.setText("Reload");
		btnReload.setIcon(TabularDataResources.INSTANCE.refresh());
		btnReload.setToolTip("Reload");
		toolBarHead.add(btnReload);

		IdentityValueProvider<TemplateData> identity = new IdentityValueProvider<TemplateData>();
		CheckBoxSelectionModel<TemplateData> sm = new CheckBoxSelectionModel<TemplateData>(
				identity);

		TemplateDataProperties props = GWT.create(TemplateDataProperties.class);

		ColumnConfig<TemplateData, String> nameCol = new ColumnConfig<TemplateData, String>(
				props.name(), 120, "Name");
		ColumnConfig<TemplateData, String> categoryCol = new ColumnConfig<TemplateData, String>(
				props.category(), 50, "Category");

		ColumnConfig<TemplateData, String> ownerCol = new ColumnConfig<TemplateData, String>(
				props.ownerLogin(), 70, "Owner");

		ColumnConfig<TemplateData, String> agencyCol = new ColumnConfig<TemplateData, String>(
				props.agency(), 90, "Agency");

		ColumnConfig<TemplateData, String> descriptionCol = new ColumnConfig<TemplateData, String>(
				props.description(), 90, "Description");
		
		ColumnConfig<TemplateData, Date> creationDateCol = new ColumnConfig<TemplateData, Date>(
				props.creationDate(), 60, "Creation Date");
		
		creationDateCol.setCell(new DateCell(sdf));
		
		
		List<ColumnConfig<TemplateData, ?>> l = new ArrayList<ColumnConfig<TemplateData, ?>>();
		l.add(nameCol);
		l.add(categoryCol);
		l.add(ownerCol);
		l.add(agencyCol);
		l.add(descriptionCol);
		l.add(creationDateCol);

		ColumnModel<TemplateData> cm = new ColumnModel<TemplateData>(l);

		store = new ExtendedListStore<TemplateData>(props.id());

		searchField.addKeyUpHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				Log.trace("searchTerm: " + searchField.getCurrentValue());
				store.applyFilters();
			}
		});

		store.addFilter(new StoreFilter<TemplateData>() {

			@Override
			public boolean select(Store<TemplateData> store,
					TemplateData parent, TemplateData item) {
				String searchTerm = searchField.getCurrentValue();
				if (searchTerm == null)
					return true;
				return TemplateSharePanel.this.select(item, searchTerm);
			}
		});

		store.setEnableFilters(true);

		RpcProxy<ListLoadConfig, ListLoadResult<TemplateData>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<TemplateData>>() {

			public void load(ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<TemplateData>> callback) {
				loadData(loadConfig, callback);
			}
		};
		loader = new ListLoader<ListLoadConfig, ListLoadResult<TemplateData>>(
				proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, TemplateData, ListLoadResult<TemplateData>>(
				store) {
		});

		SelectHandler sh = new SelectHandler() {
			public void onSelect(SelectEvent event) {
				loader.load();
			}
		};

		btnReload.addSelectHandler(sh);

		grid = new Grid<TemplateData>(store, cm) {
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

		sm.setSelectionMode(SelectionMode.SINGLE);
		grid.setLoader(loader);
		grid.setSelectionModel(sm);
		grid.setHeight("384px");
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(true);
		grid.getView().setAutoExpandColumn(descriptionCol);

		ToolBar toolBar = new ToolBar();
		toolBar.add(grid);
		toolBar.addStyleName(ThemeStyles.get().style().borderTop());
		toolBar.getElement().getStyle().setProperty("borderBottom", "none");

		

		btnClose = new TextButton("Close");
		btnClose.setIcon(TabularDataResources.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip("Close");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});

		btnShare = new TextButton("Share");
		btnShare.setIcon(TabularDataResources.INSTANCE.templateShare());
		btnShare.setIconAlign(IconAlign.RIGHT);
		btnShare.setToolTip("Share");
		btnShare.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Share");
				share();
			}
		});

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		BoxLayoutData boxLayoutData = new BoxLayoutData(new Margins(2, 4, 2, 4));
		flowButton.add(btnShare, boxLayoutData);
		flowButton.add(btnClose, boxLayoutData);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.add(toolBarHead, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(grid, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(toolBar, new VerticalLayoutData(1, 25, new Margins(0)));
		v.add(flowButton,
				new VerticalLayoutData(1, 36, new Margins(5, 2, 5, 2)));
		add(v);

	}

	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<TemplateData>> callback) {

		TDGWTServiceAsync.INSTANCE
				.getTemplates(new AsyncCallback<ArrayList<TemplateData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Load templates failure:"
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert("Error retrieving templates",
									"Error retrieving templates");
						}
						callback.onFailure(caught);
					}

					public void onSuccess(ArrayList<TemplateData> result) {
						Log.trace("loaded " + result.size() + " Occurences");
						callback.onSuccess(new ListLoadResultBean<TemplateData>(
								result));

					}

				});

	}

	

	protected ArrayList<TemplateData> getSelectedItem() {
		ArrayList<TemplateData> templates = new ArrayList<TemplateData>();
		for (TemplateData template : grid.getSelectionModel()
				.getSelectedItems()) {
			templates.add(template);
		}
		return templates;
	}


	protected void share() {

		ArrayList<TemplateData> templates = getSelectedItem();
		if (templates == null || templates.size() == 0) {
			UtilsGXT3.info("Attention", "Select the template");
		} else {
			TemplateData template = templates.get(0);
			Log.debug("templateShare: " + template);
			parent.templateShare(template);

		}

	}

	protected void close() {
		parent.close();
	}

	protected class ExtendedListStore<M> extends ListStore<M> {

		public ExtendedListStore(ModelKeyProvider<? super M> keyProvider) {
			super(keyProvider);
		}

		public void applyFilters() {
			super.applyFilters();
		}

	}

	public void gridReload() {
		grid.getLoader().load();
	}

	protected boolean select(TemplateData item, String searchTerm) {
		if (item.getName() != null
				&& item.getName().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		if (item.getCategory() != null
				&& item.getCategory().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		if (item.getAgency() != null
				&& item.getAgency().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		if (item.getDescription() != null
				&& item.getDescription().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		return false;
	}

}
