package org.gcube.portlets.user.td.rulewidget.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gcube.portlets.user.td.expressionwidget.client.rpc.ExpressionServiceAsync;
import org.gcube.portlets.user.td.expressionwidget.shared.rule.RuleDescriptionDataProperties;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.rule.AppliedRulesResponseData;
import org.gcube.portlets.user.td.gwtservice.shared.rule.DetachTableRulesSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.rulewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
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
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
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
public class RuleActiveOnTablePanel extends FramedPanel {
	private static final String WIDTH = "760px";
	private static final String HEIGHT = "418px";
	private static final DateTimeFormat sdf=DateTimeFormat.getFormat("yyyy-MM-dd HH:mm");
	
	interface RuleActiveTemplates extends XTemplates {
		@XTemplate("<span title=\"{value}\">{value}</span>")
		SafeHtml format(String value);
	}
	
	private RuleActiveMessages msgs;
	
	private EventBus eventBus;
	private TRId trId;

	private AppliedRulesResponseData appliedRuleResponseData;
	
	private Grid<RuleDescriptionData> gridAppliedRulesOnTable;
	private ListStore<RuleDescriptionData> storeAppliedRulesOnTable;
	private VerticalLayoutContainer mainLayoutContainer;
	
	
	public RuleActiveOnTablePanel(TRId trId, EventBus eventBus) {
		super();
		this.trId = trId;
		this.eventBus = eventBus;
		Log.debug("RuleActiveOnTablePanel");
		initMessages();
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
		getActiveRules();

	}
	
	protected void initMessages(){
		msgs = GWT.create(RuleActiveMessages.class);
	}
	
	protected void create() {
		Log.debug("Create RuleActiveOnTablePanel(): " + trId);

		mainLayoutContainer = new VerticalLayoutContainer();
		mainLayoutContainer.setScrollMode(ScrollMode.AUTO);
		mainLayoutContainer.setAdjustForScroll(true);

	
		ArrayList<RuleDescriptionData> appliedTableRules=appliedRuleResponseData.getTableRules();
		if (appliedTableRules == null || appliedTableRules.size() <=0) {
			FieldLabel noRulesLabel = new FieldLabel(null, msgs.noRulesOnTableApplied());
			noRulesLabel.setLabelSeparator("");
			noRulesLabel.setLabelWidth(200);
			mainLayoutContainer.add(noRulesLabel, new VerticalLayoutData(
					1, -1, new Margins(0)));
			add(mainLayoutContainer);
			return;
		}
	
		//Grid

		RuleDescriptionDataProperties propsRules = GWT
				.create(RuleDescriptionDataProperties.class);

		ColumnConfig<RuleDescriptionData, String> nameCol = new ColumnConfig<RuleDescriptionData, String>(
				propsRules.name(), 120, msgs.nameCol());

		nameCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				RuleActiveTemplates ruleActiveTemplates = GWT
						.create(RuleActiveTemplates.class);
				sb.append(ruleActiveTemplates.format(value));
			}
		});

		ColumnConfig<RuleDescriptionData, String> descriptionCol = new ColumnConfig<RuleDescriptionData, String>(
				propsRules.description(), 120, msgs.descriptionCol());
		descriptionCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				RuleActiveTemplates ruleActiveTemplates = GWT
						.create(RuleActiveTemplates.class);
				sb.append(ruleActiveTemplates.format(value));
			}
		});

		ColumnConfig<RuleDescriptionData, String> ownerCol = new ColumnConfig<RuleDescriptionData, String>(
				propsRules.ownerLogin(), 70, msgs.ownerCol());
		ownerCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				RuleActiveTemplates ruleActiveTemplates = GWT
						.create(RuleActiveTemplates.class);
				sb.append(ruleActiveTemplates.format(value));
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

		// Applies Rules
		storeAppliedRulesOnTable = new ListStore<RuleDescriptionData>(
				propsRules.id());

	
		storeAppliedRulesOnTable.addAll(appliedTableRules);
		

		gridAppliedRulesOnTable = new Grid<RuleDescriptionData>(
				storeAppliedRulesOnTable, cm);
		//gridAppliedRules.setHeight(RULES_GRID_HEIGHT);
		gridAppliedRulesOnTable.getView().setStripeRows(true);
		gridAppliedRulesOnTable.getView().setColumnLines(true);
		gridAppliedRulesOnTable.getView().setAutoFill(true);
		gridAppliedRulesOnTable.setBorders(false);
		gridAppliedRulesOnTable.setLoadMask(true);
		gridAppliedRulesOnTable.setColumnReordering(true);
		gridAppliedRulesOnTable.setColumnResize(true);
		gridAppliedRulesOnTable.getView().setAutoExpandColumn(descriptionCol);

		GridFilters<RuleDescriptionData> filtersAppliesRulesOnTable = new GridFilters<RuleDescriptionData>();
		filtersAppliesRulesOnTable.initPlugin(gridAppliedRulesOnTable);
		filtersAppliesRulesOnTable.setLocal(true);
		filtersAppliesRulesOnTable.addFilter(nameFilter);
		filtersAppliesRulesOnTable.addFilter(descriptionFilter);

		createContextMenu(gridAppliedRulesOnTable);

		mainLayoutContainer.add(gridAppliedRulesOnTable, new VerticalLayoutData(
				1, 1, new Margins(0)));
		add(mainLayoutContainer);
		return;
	}

	protected void getActiveRules() {

		ExpressionServiceAsync.INSTANCE.getActiveRulesByTabularResourceId(trId,
				new AsyncCallback<AppliedRulesResponseData>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error(caught.getLocalizedMessage());
							UtilsGXT3.alert(msgs.errorRetrievingActiveRulesHead(),
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

	protected void updateActiveRules() {

		ExpressionServiceAsync.INSTANCE.getActiveRulesByTabularResourceId(trId,
				new AsyncCallback<AppliedRulesResponseData>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error(caught.getLocalizedMessage());
							UtilsGXT3.alert(msgs.errorRetrievingAppliedRulesHead(),
									caught.getLocalizedMessage());
						}

					}

					@Override
					public void onSuccess(AppliedRulesResponseData result) {
						Log.trace("AppliedRuleResponseData: " + result);
						appliedRuleResponseData = result;
						recreate();
					}
				});

	}

	protected void requestInfo(RuleDescriptionData rule) {
		final RuleInfoDialog infoRuleDialog = new RuleInfoDialog(rule);
		infoRuleDialog.show();

	}

	protected void createContextMenu(final Grid<RuleDescriptionData> grid) {
		Menu contextMenu = new Menu();

		MenuItem infoItem = new MenuItem();
		infoItem.setText(msgs.infoItemText());
		infoItem.setToolTip(msgs.infoItemToolTip());
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

		MenuItem detachItem = new MenuItem();
		detachItem.setText(msgs.detachItemText());
		detachItem.setToolTip(msgs.detachItemToolTip());
		detachItem.setIcon(ResourceBundle.INSTANCE.ruleColumnDetach());
		detachItem.addSelectionHandler(new SelectionHandler<Item>() {

			@Override
			public void onSelection(SelectionEvent<Item> event) {
				RuleDescriptionData selected = grid.getSelectionModel()
						.getSelectedItem();
				Log.debug(selected.toString());
				requestDetach(selected, grid);
			}

		});

		contextMenu.add(infoItem);
		contextMenu.add(detachItem);

		grid.setContextMenu(contextMenu);

	}

	protected void requestDetach(RuleDescriptionData selected,
			final Grid<RuleDescriptionData> grid) {

		ArrayList<RuleDescriptionData> detachRules = new ArrayList<RuleDescriptionData>();
		detachRules.add(selected);

		DetachTableRulesSession detachTableRulesSession = new DetachTableRulesSession(
				trId, detachRules);

		ExpressionServiceAsync.INSTANCE.setDetachTableRules(
				detachTableRulesSession, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							eventBus.fireEvent(new SessionExpiredEvent(
									SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error(caught.getLocalizedMessage());
							UtilsGXT3.alert(msgs.errorInDetachRulesHead(),
									caught.getLocalizedMessage());
						}

					}

					@Override
					public void onSuccess(Void result) {
						updateActiveRules();
						Log.debug("The rule is detached!");
						UtilsGXT3.info(msgs.ruleIsDetachedHead(), msgs.ruleIsDetached());

					}
				});

	}

	protected void recreate() {
		remove(mainLayoutContainer);
		create();
		forceLayout();

	}

}
