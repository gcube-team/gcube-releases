package org.gcube.portlets.user.td.expressionwidget.client;

import java.util.ArrayList;
import java.util.Date;

import org.gcube.portlets.user.td.expressionwidget.client.exception.MultiColumnExpressionPanelException;
import org.gcube.portlets.user.td.expressionwidget.client.expression.ConditionOnMultiColumnWidget;
import org.gcube.portlets.user.td.expressionwidget.client.notification.ExpressionWrapperNotification;
import org.gcube.portlets.user.td.expressionwidget.client.resources.ExpressionResources;
import org.gcube.portlets.user.td.expressionwidget.shared.exception.ConditionTypeMapException;
import org.gcube.portlets.user.td.expressionwidget.shared.model.logical.C_Not;
import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleColumnPlaceHolderDescriptor;
import org.gcube.portlets.user.td.gwtservice.shared.rule.RuleScopeType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.description.RuleDescriptionData;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDRuleTableType;
import org.gcube.portlets.user.td.gwtservice.shared.rule.type.TDRuleType;
import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.C_ExpressionContainer;
import org.gcube.portlets.user.td.widgetcommonevent.client.expression.ExpressionWrapper;
import org.gcube.portlets.user.td.widgetcommonevent.shared.expression.C_Expression;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnTypeCode;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.ButtonCell.IconAlign;
import com.sencha.gxt.core.client.util.Margins;
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
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.EmptyValidator;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class MultiColumnExpressionPanel extends FramedPanel {

	private static final String WIDTH = "929px";
	private static final String HEIGHT = "352px";
	private static final String CONDITION_FIELD_WIDTH = "910px";
	private static final String RULE_CONDITION_FIELD_WIDTH = "914px";

	private static final String ROWS_DELETE_HEIGHT = "388px";
	private static final String RULE_HEIGHT = "312px";

	private static final String RULE_MULTI_CONDITION_WIDTH = "890px";
	private static final String RULE_MULTI_CONDITION_HEIGHT = "228px";

	private static final String RULE_DESCRIPTION_HEIGHT = "44px";

	
	private enum MultiColumnExpressionPanelType {
		ColumnFilter, RowDeleteByExpression, Template, RuleOnTable;
	}

	private MultiColumnExpressionPanelType type;

	private TemplateMultiColumnExpressionDialog parentTemplateDialog;
	private MultiColumnFilterDialog parentMultiFilterDialog;
	private RowsDeleteByMultiColumnExpressionDialog parentRowsDeleteByExpressionDialog;

	private ColumnData column;
	private ArrayList<ColumnData> columns;

	private TextButton btnApply;
	private TextButton btnClose;

	private FieldSet conditionsField;
	private ConditionOnMultiColumnWidget conditionWidget;
	private ComboBox<ColumnData> comboCols;
	private RuleOnTableCreateDialog parentRuleOnTableCreateDialog;

	private RuleDescriptionData initialRuleDescriptionData;

	private TextField ruleName;
	private TextArea ruleDescription;

	/**
	 * 
	 * @param parent
	 * @param columncreateOnRule
	 * @param columns
	 * @param eventBus
	 */
	public MultiColumnExpressionPanel(MultiColumnFilterDialog parent,
			ArrayList<ColumnData> columns, EventBus eventBus) {
		super();
		setWidth(WIDTH);
		setHeight(HEIGHT);
		type = MultiColumnExpressionPanelType.ColumnFilter;
		this.parentMultiFilterDialog = parent;
		this.columns = columns;
		createOnFilter();
	}

	/**
	 * 
	 * @param parent
	 * @param column
	 * @param columns
	 * @param eventBus
	 */
	public MultiColumnExpressionPanel(
			RowsDeleteByMultiColumnExpressionDialog parent,
			ArrayList<ColumnData> columns, EventBus eventBus) {
		super();
		setWidth(WIDTH);
		setHeight(ROWS_DELETE_HEIGHT);
		type = MultiColumnExpressionPanelType.RowDeleteByExpression;
		this.parentRowsDeleteByExpressionDialog = parent;
		this.columns = columns;
		createOnRowsDeleteByExpression();
	}

	/**
	 * 
	 * @param parent
	 * @param column
	 * @param eventBus
	 */
	public MultiColumnExpressionPanel(
			TemplateMultiColumnExpressionDialog parent,
			ArrayList<ColumnData> columns, EventBus eventBus) {
		super();
		setWidth(WIDTH);
		setHeight(HEIGHT);
		type = MultiColumnExpressionPanelType.Template;
		this.parentTemplateDialog = parent;
		this.columns = columns;
		createOnTemplate();
	}

	/**
	 * 
	 * @param parent
	 * @param eventBus
	 */
	public MultiColumnExpressionPanel(RuleOnTableCreateDialog parent,
			RuleDescriptionData initialRuleDescriptionData)
			throws MultiColumnExpressionPanelException {
		super();
		Log.debug("MultiColumnExpressionPanel");
		setWidth(WIDTH);
		setHeight(RULE_HEIGHT);
		type = MultiColumnExpressionPanelType.RuleOnTable;
		this.parentRuleOnTableCreateDialog = parent;
		this.initialRuleDescriptionData = initialRuleDescriptionData;
		retrieveColumns();
	}

	protected void retrieveColumns() throws MultiColumnExpressionPanelException {
		if (initialRuleDescriptionData == null) {
			Log.error("No rules description data present!");
			throw new MultiColumnExpressionPanelException(
					"No rules description data present!");
		}

		TDRuleType tdRuleType = initialRuleDescriptionData.getTdRuleType();
		if (tdRuleType == null) {
			Log.error("No Rule Type present!");
			throw new MultiColumnExpressionPanelException(
					"No Rule Type present!");
		}

		if (tdRuleType instanceof TDRuleTableType) {
			TDRuleTableType tdRuleTableType = (TDRuleTableType) tdRuleType;
			ArrayList<RuleColumnPlaceHolderDescriptor> listDescriptors = tdRuleTableType
					.getRuleColumnPlaceHolderDescriptors();
			columns = new ArrayList<ColumnData>();
			for (RuleColumnPlaceHolderDescriptor ruleColumnPlaceHolderDescriptor : listDescriptors) {
				ColumnData col = new ColumnData();
				col.setId(ruleColumnPlaceHolderDescriptor.getId());
				col.setColumnId(ruleColumnPlaceHolderDescriptor.getLabel());
				col.setLabel(ruleColumnPlaceHolderDescriptor.getLabel());
				col.setDataTypeName(ruleColumnPlaceHolderDescriptor
						.getColumnDataType().getId());
				columns.add(col);
			}

			createOnRule();

		} else {
			Log.error("No Table Rule Type present!");
			throw new MultiColumnExpressionPanelException(
					"No Table Rule Type present!");
		}
	}

	protected void createOnRule() throws MultiColumnExpressionPanelException {
		try {
			forceLayoutOnResize = true;

			setBodyBorder(false);
			setHeaderVisible(false);

			FieldSet propertiesField = new FieldSet();
			propertiesField.setHeadingText("Properties");
			propertiesField.setCollapsible(false);

			VerticalLayoutContainer propertiesLayout = new VerticalLayoutContainer();
			createRuleOnTableDecription(propertiesLayout);
			propertiesField.add(propertiesLayout);

			conditionsField = new FieldSet();
			conditionsField.setHeadingText("Conditions");
			conditionsField.setCollapsible(false);
			conditionsField.setWidth(RULE_CONDITION_FIELD_WIDTH);

			conditionWidget = new ConditionOnMultiColumnWidget(columns,
					RULE_MULTI_CONDITION_WIDTH, RULE_MULTI_CONDITION_HEIGHT);
			conditionsField.add(conditionWidget);

			HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
			flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
			flowButton.setPack(BoxLayoutPack.CENTER);

			btnApply = new TextButton("Save");
			btnApply.setIcon(ExpressionResources.INSTANCE.ruleColumnAdd());
			btnApply.setIconAlign(IconAlign.RIGHT);
			btnApply.setToolTip("Save rule");
			btnApply.addSelectHandler(new SelectHandler() {

				public void onSelect(SelectEvent event) {
					Log.debug("Pressed Save");
					applySeleceted();

				}
			});

			btnClose = new TextButton("Close");
			btnClose.setIcon(ExpressionResources.INSTANCE.close());
			btnClose.setIconAlign(IconAlign.RIGHT);
			btnClose.setToolTip("Cancel rule");
			btnClose.addSelectHandler(new SelectHandler() {

				public void onSelect(SelectEvent event) {
					Log.debug("Pressed Close");
					close();
				}
			});

			flowButton
					.add(btnApply, new BoxLayoutData(new Margins(2, 4, 2, 4)));

			flowButton
					.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

			VerticalLayoutContainer basicLayout = new VerticalLayoutContainer();
			// basicLayout.setAdjustForScroll(true);

			basicLayout.add(propertiesField, new VerticalLayoutData(1, -1,
					new Margins(1)));

			basicLayout.add(conditionsField, new VerticalLayoutData(-1, -1,
					new Margins(1)));

			if (parentRuleOnTableCreateDialog != null) {
				basicLayout.add(flowButton, new VerticalLayoutData(1, 36,
						new Margins(5, 2, 5, 2)));
			}
			add(basicLayout);

			if (initialRuleDescriptionData == null) {
				conditionWidget.disable();
			} else {
				if (initialRuleDescriptionData.getExpression() == null) {

				} else {

				}
			}
		} catch (Throwable e) {
			Log.debug("Error in createOnRule(): " + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	private void createRuleOnTableDecription(
			VerticalLayoutContainer propertiesLayout) {
		ruleName = new TextField();
		ruleName.addValidator(new EmptyValidator<String>());
		ruleName.setToolTip("Rule Name");
		if (initialRuleDescriptionData != null) {
			ruleName.setValue(initialRuleDescriptionData.getName());
		}
		FieldLabel ruleNameLabel = new FieldLabel(ruleName, "Rule Name");

		ruleDescription = new TextArea();
		ruleDescription.addValidator(new EmptyValidator<String>());
		ruleDescription.setHeight(RULE_DESCRIPTION_HEIGHT);
		ruleDescription.setToolTip("Rule Description");
		if (initialRuleDescriptionData != null) {
			ruleDescription.setValue(initialRuleDescriptionData
					.getDescription());
		}
		FieldLabel ruleDescriptionLabel = new FieldLabel(ruleDescription,
				"Rule Description");

		propertiesLayout.add(ruleNameLabel, new VerticalLayoutData(1, -1,
				new Margins(0)));
		propertiesLayout.add(ruleDescriptionLabel, new VerticalLayoutData(1,
				-1, new Margins(0)));

	}

	protected RuleDescriptionData retrieveRuleDescriptionData(C_Expression exp)
			throws MultiColumnExpressionPanelException {
		if (ruleName.validate()) {
			if (ruleDescription.validate()) {
				if (exp != null) {
					String ruleNameS = ruleName.getCurrentValue();
					String ruleDescriptionS = ruleDescription.getCurrentValue();
					RuleDescriptionData ruleDescriptionData = new RuleDescriptionData(
							0, ruleNameS, ruleDescriptionS, new Date(), null,
							null, RuleScopeType.TABLE, exp,
							initialRuleDescriptionData.getTdRuleType());
					return ruleDescriptionData;
				} else {
					throw new MultiColumnExpressionPanelException(
							"Enter a valid condition!");
				}
			} else {
				throw new MultiColumnExpressionPanelException(
						"Enter a valid description for the rule!");
			}
		} else {
			throw new MultiColumnExpressionPanelException(
					"Enter a valid name for the rule!");
		}

	}

	protected void createOnTemplate() {
		// Important: fixed rendering of widgets
		forceLayoutOnResize = true;

		setBodyBorder(false);
		setHeaderVisible(false);

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		conditionsField = new FieldSet();
		conditionsField.setHeadingText("Conditions");
		conditionsField.setCollapsible(false);
		conditionsField.setWidth(CONDITION_FIELD_WIDTH);

		conditionWidget = new ConditionOnMultiColumnWidget(columns);
		Log.debug("ConditionWidget" + conditionWidget);
		conditionsField.add(conditionWidget);

		btnApply = new TextButton("Add");
		btnApply.setIcon(ExpressionResources.INSTANCE.ruleTableAdd());
		btnApply.setIconAlign(IconAlign.RIGHT);
		btnApply.setToolTip("Add rule");
		btnApply.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Add");
				applySeleceted();

			}
		});

		btnClose = new TextButton("Close");
		btnClose.setIcon(ExpressionResources.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip("Close");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});

		flowButton.add(btnApply, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		VerticalLayoutContainer basicLayout = new VerticalLayoutContainer();
		// basicLayout.setAdjustForScroll(true);
		// basicLayout.setScrollMode(ScrollMode.AUTO); Set In GXT 3.0.1

		basicLayout.add(conditionsField, new VerticalLayoutData(-1, -1,
				new Margins(1)));
		basicLayout.add(flowButton, new VerticalLayoutData(1, 36, new Margins(
				5, 2, 5, 2)));
		add(basicLayout);

	}

	protected void createOnFilter() {
		forceLayoutOnResize = true;

		setBodyBorder(false);
		setHeaderVisible(false);

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		conditionsField = new FieldSet();
		conditionsField.setHeadingText("Conditions");
		conditionsField.setCollapsible(false);
		conditionsField.setWidth(CONDITION_FIELD_WIDTH);

		conditionWidget = new ConditionOnMultiColumnWidget(columns);
		conditionsField.add(conditionWidget);

		btnApply = new TextButton("Apply");
		btnApply.setIcon(ExpressionResources.INSTANCE.applyFilter());
		btnApply.setIconAlign(IconAlign.RIGHT);
		btnApply.setToolTip("Apply Filter");
		btnApply.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Apply");
				applySeleceted();

			}
		});

		btnClose = new TextButton("Close");
		btnClose.setIcon(ExpressionResources.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip("Cancel filter");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});
		flowButton.add(btnApply, new BoxLayoutData(new Margins(2, 4, 2, 4)));
		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		VerticalLayoutContainer basicLayout = new VerticalLayoutContainer();
		// basicLayout.setAdjustForScroll(true);
		basicLayout.add(conditionsField, new VerticalLayoutData(-1, -1,
				new Margins(0)));
		basicLayout.add(flowButton, new VerticalLayoutData(1, 36, new Margins(
				5, 2, 5, 2)));

		add(basicLayout);

	}

	protected void createOnRowsDeleteByExpression() {
		forceLayoutOnResize = true;

		setBodyBorder(false);
		setHeaderVisible(false);

		HBoxLayoutContainer flowButton = new HBoxLayoutContainer();
		flowButton.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		flowButton.setPack(BoxLayoutPack.CENTER);

		conditionsField = new FieldSet();
		conditionsField.setHeadingText("Conditions");
		conditionsField.setCollapsible(false);
		conditionsField.setWidth(CONDITION_FIELD_WIDTH);

		conditionWidget = new ConditionOnMultiColumnWidget(columns);
		Log.debug("ConditionWidget" + conditionWidget);
		conditionsField.add(conditionWidget);

		btnApply = new TextButton("Delete");
		btnApply.setIcon(ExpressionResources.INSTANCE
				.tableRowDeleteByExpression());
		btnApply.setIconAlign(IconAlign.RIGHT);
		btnApply.setToolTip("Delete rows");
		btnApply.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Delete");
				applySeleceted();

			}
		});

		btnClose = new TextButton("Close");
		btnClose.setIcon(ExpressionResources.INSTANCE.close());
		btnClose.setIconAlign(IconAlign.RIGHT);
		btnClose.setToolTip("Cancel");
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				Log.debug("Pressed Close");
				close();
			}
		});
		flowButton.add(btnApply, new BoxLayoutData(new Margins(2, 4, 2, 4)));
		flowButton.add(btnClose, new BoxLayoutData(new Margins(2, 4, 2, 4)));

		VerticalLayoutContainer basicLayout = new VerticalLayoutContainer();

		basicLayout.add(conditionsField, new VerticalLayoutData(-1, -1,
				new Margins(1)));
		basicLayout.add(flowButton, new VerticalLayoutData(1, 36, new Margins(
				5, 2, 5, 2)));

		add(basicLayout);

	}

	protected void setSelectedColumn() {
		if (column == null) {

		} else {
			ColumnTypeCode typeCode = ColumnTypeCode
					.getColumnTypeCodeFromId(column.getTypeCode());
			HTML errorMessage;
			if (typeCode == null) {
				errorMessage = new HTML(
						"This column has column data type null!");
				UtilsGXT3.alert("Error",
						"This column has column data type null!!");
				return;
			}

			switch (typeCode) {
			case ANNOTATION:
			case ATTRIBUTE:
			case CODE:
			case CODEDESCRIPTION:
			case CODENAME:
			case MEASURE:
				comboCols.setValue(column);
				break;
			case DIMENSION:
			case TIMEDIMENSION:
				errorMessage = new HTML(
						"This type of column is not supported for now!");
				add(errorMessage);
				UtilsGXT3.alert("Error",
						"This type of column is not supported for now!");
				break;
			default:
				errorMessage = new HTML(
						"This type of column is not supported for now!");
				add(errorMessage);
				UtilsGXT3.alert("Error",
						"This type of column is not supported for now!");
				break;

			}
		}
	}

	protected SelectionHandler<ColumnData> comboColsSelection() {
		SelectionHandler<ColumnData> selectionHandler = new SelectionHandler<ColumnData>() {

			@Override
			public void onSelection(SelectionEvent<ColumnData> event) {
				if (event.getSelectedItem() != null) {
					ColumnData col = event.getSelectedItem();
					Log.debug("Col selected:" + col.toString());
					column = col;
					btnApply.enable();
					conditionWidget.update(columns);
				} else {
					column = null;
					btnApply.disable();
					conditionWidget.update(null);
				}
			}
		};

		return selectionHandler;
	}

	protected void applySeleceted() {

		C_Expression exp;
		C_ExpressionContainer condContainer = null;
		ExpressionWrapper exWrapper = null;

		switch (type) {
		case Template:
			try {
				exp = conditionWidget.getExpression();
			} catch (ConditionTypeMapException e) {
				Log.debug(e.getLocalizedMessage());
				UtilsGXT3.alert("Attention", e.getLocalizedMessage());
				return;
			}
			condContainer = new C_ExpressionContainer();
			condContainer.setId(C_ExpressionContainer.Contains.C_Expression);
			condContainer.setExp(exp);
			exWrapper = new ExpressionWrapper(null, null, condContainer);

			ExpressionWrapperNotification expressionWrapperNotification = new ExpressionWrapperNotification(
					exWrapper);
			Log.debug("Apply: " + expressionWrapperNotification);
			parentTemplateDialog.onExpression(expressionWrapperNotification);
			break;
		case RuleOnTable:
			if (!conditionWidget.isEnabled()) {
				UtilsGXT3.alert("Attention", "Fill all field!");
				return;
			}
			try {
				exp = conditionWidget.getExpression();
			} catch (ConditionTypeMapException e) {
				Log.debug(e.getLocalizedMessage());
				UtilsGXT3.alert("Attention", e.getLocalizedMessage());
				return;
			}

			RuleDescriptionData ruleDescriptionData = null;
			try {
				ruleDescriptionData = retrieveRuleDescriptionData(exp);
			} catch (MultiColumnExpressionPanelException e1) {
				Log.debug(e1.getLocalizedMessage());
				UtilsGXT3.alert("Attention", e1.getLocalizedMessage());
				return;
			}
			if (ruleDescriptionData != null) {
				if (parentRuleOnTableCreateDialog != null) {
					parentRuleOnTableCreateDialog.addRule(ruleDescriptionData);
				}
			}
			break;
		case ColumnFilter:
			try {
				exp = conditionWidget.getExpression();
			} catch (ConditionTypeMapException e) {
				Log.debug(e.getLocalizedMessage());
				UtilsGXT3.alert("Attention", e.getLocalizedMessage());
				return;
			}
			Log.debug("Apply: " + exp);
			parentMultiFilterDialog.applyFilter(exp);
			break;
		case RowDeleteByExpression:
			try {
				exp = conditionWidget.getExpression();
			} catch (ConditionTypeMapException e) {
				Log.debug(e.getLocalizedMessage());
				UtilsGXT3.alert("Attention", e.getLocalizedMessage());
				return;
			}
			C_Expression notExp = new C_Not(exp);
			Log.debug("Apply: " + exp);
			parentRowsDeleteByExpressionDialog.deleteRowsByExpression(notExp);

			break;

		default:
			break;
		}

	}

	public RuleDescriptionData getRuleOnTable()
			throws MultiColumnExpressionPanelException {
		if (!conditionWidget.isEnabled()) {
			throw new MultiColumnExpressionPanelException("Fill all field!");
		}

		C_Expression exp;

		try {
			exp = conditionWidget.getExpression();
		} catch (ConditionTypeMapException e) {
			Log.debug(e.getLocalizedMessage());
			throw new MultiColumnExpressionPanelException(
					e.getLocalizedMessage());
		}
		RuleDescriptionData ruleDescriptionData = retrieveRuleDescriptionData(exp);
		return ruleDescriptionData;

	}

	protected void close() {
		switch (type) {
		case Template:
			parentTemplateDialog.close();
			break;
		case RuleOnTable:
			if (parentRuleOnTableCreateDialog != null) {
				parentRuleOnTableCreateDialog.close();
			}
			break;
		case ColumnFilter:
			parentMultiFilterDialog.close();
			break;
		case RowDeleteByExpression:
			parentRowsDeleteByExpressionDialog.close();
			break;
		default:
			break;
		}

	}

}
