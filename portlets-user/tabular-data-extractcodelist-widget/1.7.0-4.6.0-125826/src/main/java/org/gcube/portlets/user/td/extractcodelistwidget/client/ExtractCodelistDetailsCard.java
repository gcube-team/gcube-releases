package org.gcube.portlets.user.td.extractcodelistwidget.client;

import org.gcube.portlets.user.td.expressionwidget.client.properties.ColumnDataPropertiesCombo;
import org.gcube.portlets.user.td.gwtservice.shared.extract.ExtractCodelistSession;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class ExtractCodelistDetailsCard extends WizardCard {

	private static final String TABLEDETAILPANELWIDTH = "100%";
	private static final String TABLEDETAILPANELHEIGHT = "100%";
	private static final String FORMWIDTH = "538px";
	private static ExtractCodelistMessages msgs = GWT
			.create(ExtractCodelistMessages.class);
	private CommonMessages msgsCommon;

	private ExtractCodelistSession extractCodelistSession;
	private ExtractCodelistDetailsCard thisCard;

	private VerticalLayoutContainer p;
	private VerticalPanel tableDetailPanel;

	private TextField nameField;
	private Radio automaticallyAttachTrue;
	private Radio automaticallyAttachFalse;

	private TabResource detail;
	private ComboBox<ColumnData> comboAttachToColumn;
	private ColumnData attachColumn;
	private FieldLabel comboAttachToColumnLabel;

	public ExtractCodelistDetailsCard(
			final ExtractCodelistSession extractCodelistSession) {
		super(msgs.extractCodelistDetailCardHead(), "");

		this.extractCodelistSession = extractCodelistSession;
		thisCard = this;
		attachColumn = null;

		initMessages();
		create();

	}

	protected void initMessages() {
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected void create() {

		tableDetailPanel = new VerticalPanel();

		tableDetailPanel.setSpacing(4);
		tableDetailPanel.setWidth(TABLEDETAILPANELWIDTH);
		tableDetailPanel.setHeight(TABLEDETAILPANELHEIGHT);

		FramedPanel form = new FramedPanel();
		form.setHeadingText(msgs.extractCodelistDetailsCardFormHead());
		form.setWidth(FORMWIDTH);

		FieldSet infoFieldSet = new FieldSet();
		infoFieldSet.setHeadingText(msgs.infoFieldSetHead());
		infoFieldSet.setCollapsible(false);
		form.add(infoFieldSet);

		p = new VerticalLayoutContainer();
		infoFieldSet.add(p);

		nameField = new TextField();
		nameField.setAllowBlank(false);
		nameField.setEmptyText(msgs.nameFieldEmptyText());
		nameField.setAllowBlank(false);
		FieldLabel nameFieldLabel = new FieldLabel(nameField,
				msgs.nameFieldLabel());
		nameFieldLabel.setToolTip(msgs.nameFieldToolTip());
		p.add(nameFieldLabel, new VerticalLayoutData(1, -1, new Margins(0)));

		// /
		automaticallyAttachTrue = new Radio();
		automaticallyAttachTrue
				.setBoxLabel(msgs.automaticallyAttachTrueLabel());
		automaticallyAttachTrue.setValue(true);

		automaticallyAttachFalse = new Radio();
		automaticallyAttachFalse.setBoxLabel(msgs
				.automaticallyAttachFalseLabel());

		ToggleGroup automaticallyAttachGroup = new ToggleGroup();
		automaticallyAttachGroup.add(automaticallyAttachTrue);
		automaticallyAttachGroup.add(automaticallyAttachFalse);

		automaticallyAttachGroup
				.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {

					@Override
					public void onValueChange(
							ValueChangeEvent<HasValue<Boolean>> event) {
						try {
							if (automaticallyAttachTrue.getValue()) {
								comboAttachToColumnLabel.setVisible(true);
							} else {
								comboAttachToColumnLabel.setVisible(false);
							}

							thisCard.forceLayout();

						} catch (Exception e) {
							Log.error("ToggleGroup: onValueChange "
									+ e.getLocalizedMessage());
						}

					}
				});

		HorizontalPanel automaticallyAttachPanel = new HorizontalPanel();
		automaticallyAttachPanel.add(automaticallyAttachTrue);
		automaticallyAttachPanel.add(automaticallyAttachFalse);

		FieldLabel attachFieldLabel = new FieldLabel(automaticallyAttachPanel,
				msgs.attachFieldLabel());
		attachFieldLabel.setToolTip(msgs.attachFieldToolTip());
		p.add(attachFieldLabel, new VerticalLayoutData(-1, -1, new Margins(0)));

		// //

		// Column Data
		ColumnDataPropertiesCombo propsColumnData = GWT
				.create(ColumnDataPropertiesCombo.class);
		ListStore<ColumnData> storeCombo = new ListStore<ColumnData>(
				propsColumnData.id());
		storeCombo.addAll(extractCodelistSession.getSourceColumns());

		comboAttachToColumn = new ComboBox<ColumnData>(storeCombo,
				propsColumnData.label());

		Log.trace("Combo AttachToColumn created");
		addHandlersForComboAttachToColumn(propsColumnData.label());

		comboAttachToColumn.setEmptyText(msgs.comboAttachToColumnEmptyText());
		comboAttachToColumn.setWidth(191);
		comboAttachToColumn.setTypeAhead(false);
		comboAttachToColumn.setEditable(false);
		comboAttachToColumn.setTriggerAction(TriggerAction.ALL);
		comboAttachToColumnLabel = new FieldLabel(comboAttachToColumn,
				msgs.comboAttachToColumnLabel());
		comboAttachToColumnLabel.setToolTip(msgs.comboAttachToColumnToolTip());
		p.add(comboAttachToColumnLabel, new VerticalLayoutData(1, -1,
				new Margins(0)));

		// /
		tableDetailPanel.add(form);

		setContent(tableDetailPanel);

	}

	/**
	 * 
	 * @param label
	 */
	private void addHandlersForComboAttachToColumn(
			final LabelProvider<ColumnData> label) {
		comboAttachToColumn
				.addSelectionHandler(new SelectionHandler<ColumnData>() {
					public void onSelection(SelectionEvent<ColumnData> event) {
						Log.debug("ComboAttachToColumn selected: "
								+ event.getSelectedItem());
						ColumnData column = event.getSelectedItem();
						updateAttachColumn(column);
					}

				});

	}

	private void updateAttachColumn(ColumnData column) {

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				checkData();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove ExtractCodelistDetailsCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);
		getWizardWindow().setEnableNextButton(true);

	}

	protected void checkData() {
		getWizardWindow().setEnableNextButton(false);
		getWizardWindow().setEnableBackButton(false);
		AlertMessageBox d;
		HideHandler hideHandler = new HideHandler() {

			public void onHide(HideEvent event) {
				getWizardWindow().setEnableNextButton(true);
				getWizardWindow().setEnableBackButton(false);

			}
		};

		if (nameField.getValue() == null || nameField.getValue().isEmpty()
				|| !nameField.isValid()) {
			d = new AlertMessageBox(msgsCommon.attention(),
					msgs.attentionFillNameField());
			d.addHideHandler(hideHandler);
			d.show();
		} else {
			if (getAutomaticallyAttach()) {
				attachColumn = comboAttachToColumn.getCurrentValue();
				if (attachColumn == null) {
					d = new AlertMessageBox(msgsCommon.attention(),
							msgs.attentionSelectColumnToAttachCodelist());
					d.addHideHandler(hideHandler);
					d.show();
				} else {
					nameField.setReadOnly(true);
					goNext();
				}
			} else {
				nameField.setReadOnly(true);
				goNext();
			}
		}
	}

	protected boolean getAutomaticallyAttach() {
		if (automaticallyAttachTrue.getValue()) {
			return true;
		} else {
			return false;
		}
	}

	protected void goNext() {
		try {
			detail = new TabResource();
			detail.setName(nameField.getCurrentValue());

			extractCodelistSession.setTabResource(detail);
			extractCodelistSession
					.setAutomaticallyAttach(getAutomaticallyAttach());
			extractCodelistSession.setAttachColumn(attachColumn);
			ExtractCodelistOperationInProgressCard extractCodelistOperationInProgressCard = new ExtractCodelistOperationInProgressCard(
					extractCodelistSession);
			getWizardWindow().addCard(extractCodelistOperationInProgressCard);
			Log.info("NextCard ExtractCodelistOperationInProgressCard");
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
			nameField.setReadOnly(false);
		}
	}

}
