package org.gcube.portlets.user.td.rulewidget.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.rpc.ExpressionServiceAsync;
import org.gcube.portlets.user.td.expressionwidget.shared.rule.RuleDescriptionDataProperties;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsFinalException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.rule.AppliedRulesResponseData;
import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyAndDetachColumnRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.rulewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.dnd.core.client.GridDragSource;
import com.sencha.gxt.dnd.core.client.GridDropTarget;
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
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.sencha.gxt.widget.core.client.grid.filters.StringFilter;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class RuleOnColumnApplyPanel extends FramedPanel {
	private static final String WIDTH = "770px";
	private static final String HEIGHT = "520px";
	private static final String RULES_GRID_HEIGHT = "184px";
	private static final String SET_RULES = "SetRules";
	private static final DateTimeFormat sdf=DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
	
	interface RuleApplyTemplates extends XTemplates {
		@XTemplate("<span title=\"{value}\">{value}</span>")
		SafeHtml format(String value);
	}

	private EventBus eventBus;
	private RuleOnColumnApplyDialog parent;
	private TRId trId;
	private ArrayList<ColumnData> columns;
	private ColumnData column;
	private AppliedRulesResponseData appliedRuleResponseData;
	private ArrayList<RuleDescriptionData> applicableRules;
	private ArrayList<RuleDescriptionData> appliedRules;

	private TextButton btnApply;
	private TextButton btnClose;

	private ComboBox<ColumnData> comboColumns;
	private ListStore<RuleDescriptionData> applicableRulesStore;
	private Grid<RuleDescriptionData> gridApplicableRules;
	private ListStore<RuleDescriptionData> selectedRulesStore;
	private Grid<RuleDescriptionData> gridSelectedRules;
	private RuleOnColumnApplyMessages msgs;
	private CommonMessages msgsCommon;

	public RuleOnColumnApplyPanel(RuleOnColumnApplyDialog parent, TRId trId, EventBus eventBus) {
		this.parent = parent;
		this.trId = trId;
		this.eventBus = eventBus;
		applicableRules = new ArrayList<RuleDescriptionData>();
		Log.debug("RuleOnColumnApplyPanel");
		initMessages();
		initPanel();
		retrieveColumns();

	}
	
	protected void initMessages(){
		msgs= GWT.create(RuleOnColumnApplyMessages.class);
		msgsCommon = GWT.create(CommonMessages.class);
	}
	
	protected void initPanel(){
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}
	

	protected void retrieveColumns() {
		TDGWTServiceAsync.INSTANCE.getColumns(trId,
				new AsyncCallback<ArrayList<ColumnData>>() {

					@Override
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
								if (caught instanceof TDGWTIsFinalException) {
									Log.error(caught.getLocalizedMessage());
									UtilsGXT3.alert(msgsCommon.errorFinal(),
											caught.getLocalizedMessage());
								} else {
									Log.debug("Error retrieving columns: "
											+ caught.getLocalizedMessage());
									UtilsGXT3
											.alert(msgsCommon.errorRetrievingColumnsHead(),
													msgsCommon.errorRetrievingColumns());
								}
							}
						}

					}

					@Override
					public void onSuccess(ArrayList<ColumnData> result) {
						Log.debug("Retrieved Columns");
						if (result == null) {
							UtilsGXT3.alert(msgsCommon.error(),
									msgs.columnIsNull());
						}
						columns = result;

						getAppliedBaseColumnRules();

					}
				});
	}

	protected void create() {

		Log.debug("Create RuleApplyPanel(): " + trId);

		FieldSet configurationFieldSet = new FieldSet();
		configurationFieldSet.setHeadingText(msgs.configurationFieldSetHead());
		configurationFieldSet.setCollapsible(false);

		VerticalLayoutContainer configurationFieldSetLayout = new VerticalLayoutContainer();
		configurationFieldSet.add(configurationFieldSetLayout);

		// Column Data
		ColumnDataPropertiesCombo propsColumnData = GWT
				.create(ColumnDataPropertiesCombo.class);
		ListStore<ColumnData> storeCombo = new ListStore<ColumnData>(
				propsColumnData.id());
		storeCombo.addAll(columns);

		Log.trace("StoreCombo created");
		comboColumns = new ComboBox<ColumnData>(storeCombo,
				propsColumnData.label());

		Log.trace("Combo ColumnData created");

		addHandlersForComboColumn(propsColumnData.label());
		comboColumns.setEmptyText(msgs.comboColumnsEmptyText());
		comboColumns.setWidth(191);
		comboColumns.setTypeAhead(false);
		comboColumns.setEditable(false);
		comboColumns.setTriggerAction(TriggerAction.ALL);

		FieldLabel comboColumnsLabel = new FieldLabel(comboColumns, msgs.comboColumnsLabel());
		configurationFieldSetLayout.add(comboColumnsLabel,
				new VerticalLayoutData(1, -1, new Margins(0)));

		// Rules

		RuleDescriptionDataProperties propsRules = GWT
				.create(RuleDescriptionDataProperties.class);

		ColumnConfig<RuleDescriptionData, String> nameCol = new ColumnConfig<RuleDescriptionData, String>(
				propsRules.name(), 120, msgs.nameCol());

		nameCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				RuleApplyTemplates ruleApplyTemplates = GWT
						.create(RuleApplyTemplates.class);
				sb.append(ruleApplyTemplates.format(value));
			}
		});

		ColumnConfig<RuleDescriptionData, String> descriptionCol = new ColumnConfig<RuleDescriptionData, String>(
				propsRules.description(), 120, msgs.descriptionCol());
		descriptionCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				RuleApplyTemplates ruleApplyTemplates = GWT
						.create(RuleApplyTemplates.class);
				sb.append(ruleApplyTemplates.format(value));
			}
		});

		ColumnConfig<RuleDescriptionData, String> ownerCol = new ColumnConfig<RuleDescriptionData, String>(
				propsRules.ownerLogin(), 70, msgs.ownerCol());
		ownerCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				RuleApplyTemplates ruleApplyTemplates = GWT
						.create(RuleApplyTemplates.class);
				sb.append(ruleApplyTemplates.format(value));
			}
		});
		
		ColumnConfig<RuleDescriptionData, Date> creationDateCol = new ColumnConfig<RuleDescriptionData, Date>(
				propsRules.creationDate(), 56, msgs.creationDateCol());

		creationDateCol.setCell(new DateCell(sdf));
				

		List<ColumnConfig<RuleDescriptionData, ?>> l = new ArrayList<ColumnConfig<RuleDescriptionData, ?>>();
		l.add(nameCol);
		l.add(descriptionCol);
		l.add(ownerCol);
		l.add(creationDateCol);
		
		ColumnModel<RuleDescriptionData> cm = new ColumnModel<RuleDescriptionData>(
				l);

		StringFilter<RuleDescriptionData> nameFilter = new StringFilter<RuleDescriptionData>(
				propsRules.name());
		StringFilter<RuleDescriptionData> descriptionFilter = new StringFilter<RuleDescriptionData>(
				propsRules.description());

		// Applicable Rules
		applicableRulesStore = new ListStore<RuleDescriptionData>(
				propsRules.id());

		gridApplicableRules = new Grid<RuleDescriptionData>(
				applicableRulesStore, cm);
		gridApplicableRules.setHeight(RULES_GRID_HEIGHT);
		gridApplicableRules.getView().setStripeRows(true);
		gridApplicableRules.getView().setColumnLines(true);
		gridApplicableRules.getView().setAutoFill(true);
		gridApplicableRules.setBorders(false);
		gridApplicableRules.setLoadMask(true);
		gridApplicableRules.setColumnReordering(true);
		gridApplicableRules.setColumnResize(true);
		gridApplicableRules.getView().setAutoExpandColumn(descriptionCol);

		GridFilters<RuleDescriptionData> filtersApplicableRules = new GridFilters<RuleDescriptionData>();
		filtersApplicableRules.initPlugin(gridApplicableRules);
		filtersApplicableRules.setLocal(true);
		filtersApplicableRules.addFilter(nameFilter);
		filtersApplicableRules.addFilter(descriptionFilter);

		createContextMenu(gridApplicableRules);

		// Selected Rules
		selectedRulesStore = new ListStore<RuleDescriptionData>(propsRules.id());

		gridSelectedRules = new Grid<RuleDescriptionData>(selectedRulesStore,
				cm);
		gridSelectedRules.setHeight(RULES_GRID_HEIGHT);
		gridSelectedRules.getView().setStripeRows(true);
		gridSelectedRules.getView().setColumnLines(true);
		gridSelectedRules.getView().setAutoFill(true);
		gridSelectedRules.setBorders(false);
		gridSelectedRules.setLoadMask(true);
		gridSelectedRules.setColumnReordering(true);
		gridSelectedRules.setColumnResize(true);
		gridSelectedRules.getView().setAutoExpandColumn(descriptionCol);

		GridFilters<RuleDescriptionData> filtersSelectedRules = new GridFilters<RuleDescriptionData>();
		filtersSelectedRules.initPlugin(gridSelectedRules);
		filtersSelectedRules.setLocal(true);
		filtersSelectedRules.addFilter(nameFilter);
		filtersSelectedRules.addFilter(descriptionFilter);

		createContextMenu(gridSelectedRules);

		//

		new GridDragSource<RuleDescriptionData>(gridApplicableRules)
				.setGroup(SET_RULES);
		new GridDragSource<RuleDescriptionData>(gridSelectedRules)
				.setGroup(SET_RULES);

		new GridDropTarget<RuleDescriptionData>(gridApplicableRules)
				.setGroup(SET_RULES);
		new GridDropTarget<RuleDescriptionData>(gridSelectedRules)
				.setGroup(SET_RULES);

		//
		FieldLabel rulesApplicableLabel = new FieldLabel(gridApplicableRules,
				msgs.rulesApplicableLabel());

		configurationFieldSetLayout.add(rulesApplicableLabel,
				new VerticalLayoutData(1, -1, new Margins(0)));

		FieldLabel rulesSelectedLabel = new FieldLabel(gridSelectedRules,
				msgs.rulesSelectedLabel());

		configurationFieldSetLayout.add(rulesSelectedLabel,
				new VerticalLayoutData(1, -1, new Margins(0)));

		//
		HTML rulesNote = new HTML(msgs.ruleTip());
		configurationFieldSetLayout.add(rulesNote,
				new VerticalLayoutData(-1, -1, new Margins(0)));
		// Button
		btnApply = new TextButton(msgs.btnApplyText());
		btnApply.setIcon(ResourceBundle.INSTANCE.ruleColumnApply());
		btnApply.setIconAlign(IconAlign.RIGHT);
		btnApply.setToolTip(msgs.btnApplyToolTip());
		btnApply.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Apply");
				apply();

			}
		});

		btnClose = new TextButton(msgsCommon.btnCloseText());
		btnClose.setIcon(ResourceBundle.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip(msgsCommon.btnCloseToolTip());
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		flowButton.add(btnApply, new BoxLayoutData(new Margins(2, 4, 2, 4)));
		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.add(configurationFieldSet, new VerticalLayoutData(-1, -1,
				new Margins(0)));
		v.add(flowButton,
				new VerticalLayoutData(1, 36, new Margins(5, 2, 5, 2)));
		add(v);

	}

	private void addHandlersForComboColumn(
			final LabelProvider<ColumnData> labelProvider) {
		comboColumns.addSelectionHandler(new SelectionHandler<ColumnData>() {
			public void onSelection(SelectionEvent<ColumnData> event) {
				Log.debug("ComboColumn selected: " + event.getSelectedItem());
				ColumnData columnData = event.getSelectedItem();
				updateRulesInGrids(columnData);
			}

		});

	}

	protected void updateRulesInGrids(ColumnData columnData) {
		column = columnData;
		applicableRulesStore.clear();
		selectedRulesStore.clear();
		HashMap<String, ArrayList<RuleDescriptionData>> columnRuleMapping = appliedRuleResponseData
				.getColumnRuleMapping();

		if (columnRuleMapping != null) {
			ArrayList<RuleDescriptionData> applied = columnRuleMapping
					.get(columnData.getColumnId());

			if (applied != null) {
				appliedRules = applied;
				selectedRulesStore.addAll(new ArrayList<RuleDescriptionData>(
						applied));

			}
		}
		getApplicableBaseColumnRules();

	}

	protected void getApplicableBaseColumnRules() {
		ExpressionServiceAsync.INSTANCE.getApplicableBaseColumnRules(column,
				new AsyncCallback<ArrayList<RuleDescriptionData>>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error(caught.getLocalizedMessage());
							UtilsGXT3.alert(
									msgs.errorRetrievingApplicableRulesHead(),
									caught.getLocalizedMessage());
						}

					}

					@Override
					public void onSuccess(ArrayList<RuleDescriptionData> result) {
						Log.trace("loaded " + result.size() + " Rules");
						if (appliedRules != null && appliedRules.size() > 0) {
							ArrayList<RuleDescriptionData> removableRules = new ArrayList<RuleDescriptionData>();
							for (RuleDescriptionData ruleApplied : appliedRules) {
								for (RuleDescriptionData ruleApplicable : result) {
									if (ruleApplicable.equals(ruleApplied)) {
										removableRules.add(ruleApplicable);
										break;
									}
								}

							}
							result.removeAll(removableRules);

						}
						applicableRules = result;
						applicableRulesStore
								.addAll(new ArrayList<RuleDescriptionData>(
										applicableRules));

						forceLayout();

					}
				});

	}

	protected void getAppliedBaseColumnRules() {

		ExpressionServiceAsync.INSTANCE.getActiveRulesByTabularResourceId(trId,
				new AsyncCallback<AppliedRulesResponseData>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error(caught.getLocalizedMessage());
							UtilsGXT3.alert(msgs.errorRetrievingSelectedRulesHead(),
									caught.getLocalizedMessage());
						}

					}

					@Override
					public void onSuccess(AppliedRulesResponseData result) {
						Log.trace("AppliedRuleResponseData: " + result);
						appliedRuleResponseData = result;
						create();
					}
				});

	}

	protected ArrayList<RuleDescriptionData> getSelectedItems() {
		List<RuleDescriptionData> selectedItems = selectedRulesStore.getAll();
		ArrayList<RuleDescriptionData> rulesSelected = new ArrayList<RuleDescriptionData>(
				selectedItems);
		return rulesSelected;
	}

	protected void apply() {
		ColumnData column = comboColumns.getCurrentValue();
		if (column != null) {
			ArrayList<RuleDescriptionData> selectedRules = getSelectedItems();
			if(selectedRules==null){
				selectedRules=new ArrayList<RuleDescriptionData>();
			}
			ArrayList<RuleDescriptionData> rulesThatWillBeDetach = new ArrayList<RuleDescriptionData>();
			ArrayList<RuleDescriptionData> rulesThatWillBeApplied = new ArrayList<RuleDescriptionData>();
			HashMap<String, ArrayList<RuleDescriptionData>> columnRuleMapping = appliedRuleResponseData
					.getColumnRuleMapping();
			if (columnRuleMapping != null) {
				ArrayList<RuleDescriptionData> rulesApplied = columnRuleMapping
						.get(column.getColumnId());
				if (rulesApplied != null && rulesApplied.size() > 0) {
					for (RuleDescriptionData ruleApplied : rulesApplied) {
						boolean ruleStillApplied = false;
						for (RuleDescriptionData ruleSelected : selectedRules) {
							if (ruleSelected.getId() == ruleApplied.getId()) {
								ruleStillApplied = true;
								break;
							}
						}

						if (!ruleStillApplied) {
							rulesThatWillBeDetach.add(ruleApplied);
						}
					}
				}
			}

			for (RuleDescriptionData ruleSelected : selectedRules) {
				boolean ruleApplied = false;

				if (columnRuleMapping != null) {
					ArrayList<RuleDescriptionData> rulesApplied = columnRuleMapping
							.get(column.getColumnId());
					if (rulesApplied != null) {
						for (RuleDescriptionData ruleAlreadyApplied : rulesApplied) {
							if (ruleSelected.getId() == ruleAlreadyApplied
									.getId()) {
								ruleApplied = true;
								break;
							}
						}
					} else {

					}
				} else {

				}

				if (ruleApplied == false) {
					rulesThatWillBeApplied.add(ruleSelected);
				}
			}
			
			if(rulesThatWillBeApplied.size()<=0&&rulesThatWillBeDetach.size()<=0){
				Log.error("Select a rule!");
				UtilsGXT3.alert(msgsCommon.attention(), msgs.selectARule());
				return;
			}
			
			ApplyAndDetachColumnRulesSession applyColumnRulesSession = new ApplyAndDetachColumnRulesSession(
					trId, column, rulesThatWillBeApplied, rulesThatWillBeDetach);
			parent.applyRules(applyColumnRulesSession);
			
		} else {
			Log.error("No column selected");
			UtilsGXT3.alert(msgsCommon.attention(), msgs.selectAColumn());
		}
	}

	protected void close() {
		parent.close();
	}

	protected void requestInfo(RuleDescriptionData rule) {
		final RuleInfoDialog infoRuleDialog = new RuleInfoDialog(rule);
		infoRuleDialog.show();


	}

	protected void createContextMenu(final Grid<RuleDescriptionData> grid) {
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

}
