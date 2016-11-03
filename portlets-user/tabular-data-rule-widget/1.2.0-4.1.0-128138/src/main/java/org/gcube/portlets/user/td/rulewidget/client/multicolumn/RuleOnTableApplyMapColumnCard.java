package org.gcube.portlets.user.td.rulewidget.client.multicolumn;

import java.util.ArrayList;
import java.util.HashMap;

import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.expressionwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.rule.ApplyTableRuleSession;
import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleColumnPlaceHolderDescriptor;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDRuleTableType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDRuleType;
import org.gcube.portlets.user.td.rulewidget.client.multicolumn.data.MapPlaceHolderToColumnRow;
import org.gcube.portlets.user.td.rulewidget.client.multicolumn.data.MapPlaceHolderToColumnRowProperties;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnDataType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent;
import com.sencha.gxt.widget.core.client.event.BeforeStartEditEvent.BeforeStartEditHandler;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent;
import com.sencha.gxt.widget.core.client.event.CancelEditEvent.CancelEditHandler;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.Grid.GridCell;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.grid.editing.GridRowEditing;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class RuleOnTableApplyMapColumnCard extends WizardCard {
	private static RuleOnTableApplyMessages msgs=GWT.create(RuleOnTableApplyMessages.class);
	private CommonMessages msgsCommon;
	
	private RuleOnTableApplyMapColumnCard thisCard;
	private ApplyTableRuleSession applyTableRuleSession;
	private Grid<MapPlaceHolderToColumnRow> grid;
	private ListStore<MapPlaceHolderToColumnRow> store;
	private ArrayList<MapPlaceHolderToColumnRow> rows;
	private ArrayList<ColumnData> columns;
	private ListStore<ColumnData> storeComboColumnData;
	
	public RuleOnTableApplyMapColumnCard(
			ApplyTableRuleSession applyTableRuleSession) {
		super(msgs.ruleOnTableApplyMapColumnCardHead(), "");
		this.thisCard = this;
		this.applyTableRuleSession = applyTableRuleSession;
		initMessages();
		createData();
		retrieveColumns();

	}
	
	protected void initMessages(){
		msgsCommon=GWT.create(CommonMessages.class);
	}

	protected void createData() {
		RuleDescriptionData ruleDescriptionData = applyTableRuleSession
				.getRuleDescriptionData();
		TDRuleType tdRuleType = ruleDescriptionData.getTdRuleType();
		if (tdRuleType instanceof TDRuleTableType) {
			TDRuleTableType tdRuleTableType = (TDRuleTableType) tdRuleType;
			ArrayList<RuleColumnPlaceHolderDescriptor> rulePlaceHolderDescriptorList = tdRuleTableType
					.getRuleColumnPlaceHolderDescriptors();
			rows = new ArrayList<MapPlaceHolderToColumnRow>();

			for (int i = 0; i < rulePlaceHolderDescriptorList.size(); i++) {
				RuleColumnPlaceHolderDescriptor placeHolder = rulePlaceHolderDescriptorList
						.get(i);
				rows.add(new MapPlaceHolderToColumnRow(i, placeHolder, null));
			}
		} else {
			getWizardWindow().setEnableNextButton(false);
			getWizardWindow().setEnableBackButton(false);

			HideHandler hideHandler = new HideHandler() {

				public void onHide(HideEvent event) {
					getWizardWindow().setEnableNextButton(false);
					getWizardWindow().setEnableBackButton(true);

				}
			};

			AlertMessageBox d = new AlertMessageBox(msgsCommon.attention(),
					msgs.thisIsNotARuleOnTable());
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
			return;

		}

	}

	protected void create() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));
		setCenterWidget(panel, new MarginData(0));

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		panel.add(v);

		// Grid
		MapPlaceHolderToColumnRowProperties props = GWT
				.create(MapPlaceHolderToColumnRowProperties.class);

		ColumnConfig<MapPlaceHolderToColumnRow, String> placeHolderCol = new ColumnConfig<MapPlaceHolderToColumnRow, String>(
				props.placeHolderLabel(), 220, msgs.placeHolderCol());
		ColumnConfig<MapPlaceHolderToColumnRow, ColumnData> columnCol = new ColumnConfig<MapPlaceHolderToColumnRow, ColumnData>(
				props.column(), 220, msgs.columnCol());

		columnCol.setCell(new AbstractCell<ColumnData>() {

			@Override
			public void render(Context context, ColumnData value,
					SafeHtmlBuilder sb) {
				if (value == null) {
					sb.appendHtmlConstant("");
				} else {
					sb.appendHtmlConstant("<span title='"
							+ SafeHtmlUtils.htmlEscape(value.getLabel()) + "'>"
							+ SafeHtmlUtils.htmlEscape(value.getLabel())
							+ "</span>");
				}
			}
		});

		ArrayList<ColumnConfig<MapPlaceHolderToColumnRow, ?>> l = new ArrayList<ColumnConfig<MapPlaceHolderToColumnRow, ?>>();
		l.add(placeHolderCol);
		l.add(columnCol);

		ColumnModel<MapPlaceHolderToColumnRow> gridColumns = new ColumnModel<MapPlaceHolderToColumnRow>(
				l);

		store = new ListStore<MapPlaceHolderToColumnRow>(props.id());
		store.addAll(rows);

		final GridSelectionModel<MapPlaceHolderToColumnRow> sm = new GridSelectionModel<MapPlaceHolderToColumnRow>();
		sm.setSelectionMode(SelectionMode.SINGLE);

		grid = new Grid<MapPlaceHolderToColumnRow>(store, gridColumns);
		grid.setSelectionModel(sm);
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(false);
		grid.getView().setAutoExpandColumn(columnCol);

		// EDITING //
		ColumnDataPropertiesCombo columnDataPropertiesCombo = GWT
				.create(ColumnDataPropertiesCombo.class);

		storeComboColumnData = new ListStore<ColumnData>(
				columnDataPropertiesCombo.id());
		

		ComboBox<ColumnData> comboColumnData = new ComboBox<ColumnData>(
				storeComboColumnData, columnDataPropertiesCombo.label());
		comboColumnData.setClearValueOnParseError(false);
		comboColumnData.setAllowBlank(false);
		comboColumnData.setTriggerAction(TriggerAction.ALL);

		final GridRowEditing<MapPlaceHolderToColumnRow> editing = new GridRowEditing<MapPlaceHolderToColumnRow>(
				grid);

		TextButton btnSave=editing.getSaveButton();
		btnSave.setText(msgsCommon.btnSaveText());
		btnSave.setToolTip(msgsCommon.btnSaveToolTip());
		
		TextButton btnCancel=editing.getCancelButton();
		btnCancel.setText(msgsCommon.btnCancelText());
		btnCancel.setToolTip(msgsCommon.btnCancelToolTip());
	
		editing.addEditor(columnCol, comboColumnData);
		editing.addBeforeStartEditHandler(new BeforeStartEditHandler<MapPlaceHolderToColumnRow>() {

			@Override
			public void onBeforeStartEdit(
					BeforeStartEditEvent<MapPlaceHolderToColumnRow> event) {
				setEnableNextButton(false);
				GridCell cell=event.getEditCell();
				int rowIndex=cell.getRow();
				MapPlaceHolderToColumnRow row=store.get(rowIndex);
				RuleColumnPlaceHolderDescriptor descriptor=row.getRuleColumnPlaceHolderDescriptor();
				ColumnDataType columnDataType=descriptor.getColumnDataType();
				ArrayList<ColumnData> usableColumns=new ArrayList<ColumnData>();
				for(ColumnData column:columns){
					ColumnDataType cdt=ColumnDataType.getColumnDataTypeFromId(column.getDataTypeName());
					if(column.getDataTypeName()!=null && cdt.compareTo(columnDataType)==0){
						usableColumns.add(column);
					}
				}
				
				storeComboColumnData.clear();
				storeComboColumnData.commitChanges();
				storeComboColumnData.addAll(usableColumns);
				storeComboColumnData.commitChanges();
				
				
			}
		});

		editing.addCancelEditHandler(new CancelEditHandler<MapPlaceHolderToColumnRow>() {

			@Override
			public void onCancelEdit(
					CancelEditEvent<MapPlaceHolderToColumnRow> event) {
				store.rejectChanges();
				checkAllFill();

			}

		});

		editing.addCompleteEditHandler(new CompleteEditHandler<MapPlaceHolderToColumnRow>() {

			@Override
			public void onCompleteEdit(
					CompleteEditEvent<MapPlaceHolderToColumnRow> event) {
				try {

					store.commitChanges();
					checkAllFill();

				} catch (Throwable e) {
					Log.error("Error in RuleOnTableNewDefinitionCard: "
							+ e.getLocalizedMessage());
					e.printStackTrace();
				}
			}

			
		});

		//
		v.add(grid, new VerticalLayoutData(1, 1, new Margins(0)));

		forceLayout();
		return;
	}
	
	protected void checkAllFill() {
		boolean fillAll = true;
		for (MapPlaceHolderToColumnRow row : store.getAll()) {
			if (row.getColumn() == null) {
				fillAll = false;
				break;
			}
		}
		
		if (fillAll) {
			setEnableNextButton(true);
		} else {
			setEnableNextButton(false);
		}
	}
	

	protected void retrieveColumns() {
		TDGWTServiceAsync.INSTANCE.getColumns(applyTableRuleSession.getTrId(),
				new AsyncCallback<ArrayList<ColumnData>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								UtilsGXT3.alert(msgsCommon.errorLocked(),
										caught.getLocalizedMessage());

							} else {
								Log.error("Error retrieving column: "
										+ caught.getMessage());
								UtilsGXT3.alert(msgs.errorRetrievingColumnsHead(),
										caught.getMessage());
							}
						}
					}

					public void onSuccess(ArrayList<ColumnData> result) {
						Log.debug("Retrived column: " + result);
						columns = result;
						sanitizesColumns();
						create();
					}

				});

	}

	protected void sanitizesColumns() {
		ArrayList<ColumnData> removableColumn = new ArrayList<ColumnData>();
		for (ColumnData c : columns) {
			if (c.getTypeCode().compareTo(ColumnTypeCode.DIMENSION.toString()) == 0
					|| c.getTypeCode().compareTo(
							ColumnTypeCode.TIMEDIMENSION.toString()) == 0) {
				removableColumn.add(c);
			} else {

			}
		}
		columns.removeAll(removableColumn);
	}

	@Override
	public void setup() {
		Log.debug("RuleOnTableApplyMapColumnCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("RuleOnTableApplyMapColumnCard Call sayNextCard");
				checkData();
			}

		};

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove RuleOnTableApplyMapColumnCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setNextButtonCommand(sayNextCard);

		setEnableBackButton(true);
		setBackButtonVisible(true);
		setEnableNextButton(false);
	}

	protected void checkData() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);

		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(false);
				getWizardWindow().setEnableBackButton(true);

			}
		};

		if (store == null || store.size() <= 0) {
			AlertMessageBox d = new AlertMessageBox(msgsCommon.attention(),
					msgs.errorNoMappingForThisTableRule());
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
			return;
		}
		
		
		HashMap<String, String> placeHolderToColumnMap=new HashMap<String,String>();
		for(MapPlaceHolderToColumnRow m: store.getAll()){
			placeHolderToColumnMap.put(m.getRuleColumnPlaceHolderDescriptor().getId(),
					m.getColumn().getColumnId());
		}
		
		applyTableRuleSession.setPlaceHolderToColumnMap(placeHolderToColumnMap);
		goNext();
	}

	protected void goNext() {
		try {
			RuleOnTableApplyOperationInProgressCard createRuleOnTableExpressionCard = new RuleOnTableApplyOperationInProgressCard(
					applyTableRuleSession);
			getWizardWindow().addCard(createRuleOnTableExpressionCard);
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void dispose() {

	}

}
