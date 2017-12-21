package org.gcube.portlets.user.td.rulewidget.client.multicolumn;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.td.expressionwidget.client.rpc.ExpressionServiceAsync;
import org.gcube.portlets.user.td.expressionwidget.shared.rule.RuleDescriptionDataProperties;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyTableRuleSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleScopeType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.rulewidget.client.RuleInfoDialog;
import org.gcube.portlets.user.td.rulewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
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
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.LabelToolItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class RuleOnTableApplySelectRuleCard extends WizardCard {
	private static final String GRID_HEIGHT = "334px";
	private static final DateTimeFormat sdf=DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
	private static RuleOnTableApplyMessages msgs=GWT.create(RuleOnTableApplyMessages.class);
	private CommonMessages msgsCommon;
	
	interface RuleOnTableSelectTemplates extends XTemplates {
		@XTemplate("<span title=\"{value}\">{value}</span>")
		SafeHtml format(String value);
	}
	
	private RuleOnTableApplySelectRuleCard thisCard;
	private ApplyTableRuleSession applyTableRuleSession;
	private Grid<RuleDescriptionData> grid;
	private ExtendedListStore<RuleDescriptionData> store;
	private ListLoader<ListLoadConfig, ListLoadResult<RuleDescriptionData>> loader;
	private RuleDescriptionData ruleDescriptionData;
	
	
	public RuleOnTableApplySelectRuleCard(ApplyTableRuleSession applyTableRuleSession) {
		super(msgs.ruleOnTableApplySelectRuleCardHead(),
				"");
		this.thisCard = this;
		this.applyTableRuleSession=applyTableRuleSession;
		initMessages();
		FormPanel panel = createPanel();
		setCenterWidget(panel, new MarginData(0));

	}
	
	protected void initMessages(){
		msgsCommon=GWT.create(CommonMessages.class);
	}


	protected FormPanel createPanel() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		panel.add(v);
		
		
		//ToolBarHead
		ToolBar toolBarHead = new ToolBar();
		toolBarHead.add(new LabelToolItem(msgsCommon.toolItemSearchLabel()));
		final TextField searchField = new TextField();
		toolBarHead.add(searchField);

		TextButton btnReload = new TextButton();
		// btnReload.setText("Reload");
		btnReload.setIcon(ResourceBundle.INSTANCE.refresh());
		btnReload.setToolTip(msgsCommon.toolItemReloadLabel());
		toolBarHead.add(btnReload);

		IdentityValueProvider<RuleDescriptionData> identity = new IdentityValueProvider<RuleDescriptionData>();
		CheckBoxSelectionModel<RuleDescriptionData> sm = new CheckBoxSelectionModel<RuleDescriptionData>(
				identity);

		RuleDescriptionDataProperties props = GWT
				.create(RuleDescriptionDataProperties.class);

		ColumnConfig<RuleDescriptionData, String> nameCol = new ColumnConfig<RuleDescriptionData, String>(
				props.name(), 120, msgs.nameCol());

		nameCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				RuleOnTableSelectTemplates ruleOnTableSelectTemplates = GWT
						.create(RuleOnTableSelectTemplates.class);
				sb.append(ruleOnTableSelectTemplates.format(value));
			}
		});

		ColumnConfig<RuleDescriptionData, String> scopeCol = new ColumnConfig<RuleDescriptionData, String>(
				props.scopeLabel(), 40, msgs.scopeCol());

		nameCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				RuleOnTableSelectTemplates ruleOnTableSelectTemplates = GWT
						.create(RuleOnTableSelectTemplates.class);
				sb.append(ruleOnTableSelectTemplates.format(value));
			}
		});
		

		ColumnConfig<RuleDescriptionData, String> descriptionCol = new ColumnConfig<RuleDescriptionData, String>(
				props.description(), 120, msgs.descriptionCol());
		descriptionCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				RuleOnTableSelectTemplates ruleOnTableSelectTemplates = GWT
						.create(RuleOnTableSelectTemplates.class);
				sb.append(ruleOnTableSelectTemplates.format(value));
			}
		});

		ColumnConfig<RuleDescriptionData, String> ownerCol = new ColumnConfig<RuleDescriptionData, String>(
				props.ownerLogin(), 70, msgs.ownerCol());
		ownerCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				RuleOnTableSelectTemplates ruleDeleteTemplates = GWT
						.create(RuleOnTableSelectTemplates.class);
				sb.append(ruleDeleteTemplates.format(value));
			}
		});
		
		ColumnConfig<RuleDescriptionData, Date> creationDateCol = new ColumnConfig<RuleDescriptionData, Date>(
				props.creationDate(), 56, msgs.creationDateCol());
		
		creationDateCol.setCell(new DateCell(sdf));

		List<ColumnConfig<RuleDescriptionData, ?>> l = new ArrayList<ColumnConfig<RuleDescriptionData, ?>>();
		l.add(nameCol);
		l.add(scopeCol);
		l.add(descriptionCol);
		l.add(ownerCol);
		l.add(creationDateCol);
		
		
		ColumnModel<RuleDescriptionData> cm = new ColumnModel<RuleDescriptionData>(
				l);

		store = new ExtendedListStore<RuleDescriptionData>(props.id());

		searchField.addKeyUpHandler(new KeyUpHandler() {

			public void onKeyUp(KeyUpEvent event) {
				Log.trace("searchTerm: " + searchField.getCurrentValue());
				store.applyFilters();
			}
		});

		store.addFilter(new StoreFilter<RuleDescriptionData>() {

			@Override
			public boolean select(Store<RuleDescriptionData> store,
					RuleDescriptionData parent, RuleDescriptionData item) {
				String searchTerm = searchField.getCurrentValue();
				if (searchTerm == null)
					return true;
				return thisCard.select(item, searchTerm);
			}
		});

		store.setEnableFilters(true);

		RpcProxy<ListLoadConfig, ListLoadResult<RuleDescriptionData>> proxy = new RpcProxy<ListLoadConfig, ListLoadResult<RuleDescriptionData>>() {

			public void load(
					ListLoadConfig loadConfig,
					final AsyncCallback<ListLoadResult<RuleDescriptionData>> callback) {
				loadData(loadConfig, callback);
			}
		};
		loader = new ListLoader<ListLoadConfig, ListLoadResult<RuleDescriptionData>>(
				proxy);

		loader.setRemoteSort(false);
		loader.addLoadHandler(new LoadResultListStoreBinding<ListLoadConfig, RuleDescriptionData, ListLoadResult<RuleDescriptionData>>(
				store) {
		});

		SelectHandler sh = new SelectHandler() {
			public void onSelect(SelectEvent event) {
				loader.load();
			}
		};

		btnReload.addSelectHandler(sh);

		grid = new Grid<RuleDescriptionData>(store, cm) {
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
		grid.setHeight(GRID_HEIGHT);
		grid.setSelectionModel(sm);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(true);
		grid.getView().setAutoExpandColumn(descriptionCol);
		
		createContextMenu();

		ToolBar toolBar = new ToolBar();
		toolBar.add(grid);
		toolBar.addStyleName(ThemeStyles.get().style().borderTop());
		toolBar.getElement().getStyle().setProperty("borderBottom", "none");
		
		v.add(toolBarHead, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(grid, new VerticalLayoutData(1, -1, new Margins(0)));
		v.add(toolBar, new VerticalLayoutData(1, 25, new Margins(0)));
		return panel;
	}
	
	protected void loadData(ListLoadConfig loadConfig,
			final AsyncCallback<ListLoadResult<RuleDescriptionData>> callback) {

		ExpressionServiceAsync.INSTANCE.getRules(RuleScopeType.TABLE,
				new AsyncCallback<ArrayList<RuleDescriptionData>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus().fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Load rules failure:"
									+ caught.getLocalizedMessage());
							UtilsGXT3.alert(msgsCommon.error(),
									msgs.errorRetrievingRules());
						}
						callback.onFailure(caught);

					}

					@Override
					public void onSuccess(ArrayList<RuleDescriptionData> result) {
						Log.trace("loaded " + result.size() + " Rules");
						callback.onSuccess(new ListLoadResultBean<RuleDescriptionData>(
								result));

					}
				});
	}

	protected RuleDescriptionData getSelectedItem() {
		RuleDescriptionData rule = grid.getSelectionModel()
				.getSelectedItem();
		return rule;
	}

	

	
	

	@Override
	public void setup() {
		Log.debug("RuleOnTableApplySelectRuleCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("RuleOnTableApplySelectRuleCard Call sayNextCard");
				checkData();
			}

		};

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove RuleOnTableNewDefinitionCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setNextButtonCommand(sayNextCard);

		setEnableBackButton(false);
		setBackButtonVisible(false);
		setEnableNextButton(true);
	}

	protected void checkData() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);

		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(false);

			}
		};
		
		

		ruleDescriptionData = getSelectedItem();
		//TODO x Test
		/*ArrayList<RuleColumnPlaceHolderDescriptor> ruleColumnPlaceHolderDescriptors=new ArrayList<RuleColumnPlaceHolderDescriptor>();
		RuleColumnPlaceHolderDescriptor r1=new RuleColumnPlaceHolderDescriptor("Test1", "Test1", ColumnDataType.Text);
		RuleColumnPlaceHolderDescriptor r2=new RuleColumnPlaceHolderDescriptor("Test2", "Test2", ColumnDataType.Integer);
		ruleColumnPlaceHolderDescriptors.add(r1);
		ruleColumnPlaceHolderDescriptors.add(r2);
		TDRuleType tdRuleType=new TDRuleTableType(ruleColumnPlaceHolderDescriptors);
		
		ruleDescriptionData =new RuleDescriptionData(1, "Test", "Test Description", "2015-01-01 18:45", null, null, RuleScopeType.TABLE, new C_Expression(), 
				tdRuleType);
		*/
		if (ruleDescriptionData == null) {
			AlertMessageBox d = new AlertMessageBox(msgsCommon.attention(),
					msgs.selectARule());
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
			return;
		}

		applyTableRuleSession.setRuleDescriptionData(ruleDescriptionData);
		
		goNext();
	}

	protected void goNext(){
		try {
			RuleOnTableApplyMapColumnCard ruleOnTableApplyMapColumnCard = new RuleOnTableApplyMapColumnCard(
					applyTableRuleSession);
			getWizardWindow().addCard(ruleOnTableApplyMapColumnCard);
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	

	@Override
	public void dispose() {

	}
	
	protected void createContextMenu() {
		Menu contextMenu = new Menu();

		MenuItem infoItem = new MenuItem();
		infoItem.setText(msgs.infoItemText());
		infoItem.setIcon(ResourceBundle.INSTANCE.information());
		infoItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				RuleDescriptionData selected = grid.getSelectionModel()
						.getSelectedItem();
				Log.debug(selected.toString());
				requestInfo(selected);
			}
		});

		contextMenu.add(infoItem);

		grid.setContextMenu(contextMenu);

	}
	
	protected void requestInfo(RuleDescriptionData rule) {

		final RuleInfoDialog infoRuleDialog = new RuleInfoDialog(rule);
		infoRuleDialog.show();


	}

	
	protected boolean select(RuleDescriptionData item, String searchTerm) {
		if (item.getName() != null
				&& item.getName().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		if (item.getDescription() != null
				&& item.getDescription().toLowerCase()
						.contains(searchTerm.toLowerCase()))
			return true;
		return false;
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
