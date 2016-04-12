package org.gcube.portlets.user.td.csvexportwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.csvexportwidget.client.grid.ColumnDataGridPanel;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.csv.AvailableCharsetList;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVExportSession;
import org.gcube.portlets.user.td.gwtservice.shared.destination.WorkspaceDestination;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTIsLockedException;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.tr.TabResource;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.ColumnData;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.tips.ToolTip;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class CSVExportConfigCard extends WizardCard {
	private static final String ENCODING_EXPORT_FORMAT = "UTF8";
	private static final int LABEL_WIDTH = 128;
	private static final int LABEL_PAD_WIDTH = 2;
	private static final String DEFAULT_DELIMETER = ",";
	private static CSVExportWizardTDMessages msgs = GWT.create(CSVExportWizardTDMessages.class);
	private CommonMessages msgsCommon;
	
	private CSVExportSession exportSession;

	private SimpleComboBox<String> comboEncodings;
	private TextField customDelimiterField;
	private Radio radioOtherDelimiter;
	private Radio radioCommaDelimiter;
	private Radio radioSpaceDelimiter;
	private Radio radioTabDelimiter;
	private Radio radioSemicoloDelimiter;

	private Radio radioViewColumnExportTrue;
	private Radio radioViewColumnExportFalse;

	protected ColumnDataGridPanel csvColumnGridPanel;
	

	public CSVExportConfigCard(final CSVExportSession exportSession) {
		super(msgs.csvExportConfigCardHead(), "");
		initMessages();
		if (exportSession == null) {
			Log.error("CSVExportSession is null");
		}
		this.exportSession = exportSession;

		FormPanel panel = createPanel();
		setCenterWidget(panel, new MarginData(0));

	}
	
	protected void initMessages(){
		msgsCommon = GWT.create(CommonMessages.class);
	}

	protected FormPanel createPanel() {
		FormPanel panel = new FormPanel();
		panel.setLabelWidth(90);
		panel.getElement().setPadding(new Padding(5));

		VerticalLayoutContainer content = new VerticalLayoutContainer();
		panel.add(content);

		comboEncodings = new SimpleComboBox<String>(
				new StringLabelProvider<String>());
		comboEncodings.setToolTip(msgs.comboEncodingsToolTip());
		comboEncodings.setTabIndex(0);
		comboEncodings.setEditable(false);
		comboEncodings.setForceSelection(true);
		comboEncodings.setTriggerAction(TriggerAction.ALL);
		comboEncodings.addSelectionHandler(new SelectionHandler<String>() {

			public void onSelection(SelectionEvent<String> event) {

			}
		});

		comboEncodings.focus();

		TDGWTServiceAsync.INSTANCE
				.getAvailableCharsetForExport(new AsyncCallback<AvailableCharsetList>() {

					public void onSuccess(AvailableCharsetList result) {
						GWT.log("CharsetInfo: "
								+ result.getCharsetList().size()
								+ " charset, default: "
								+ result.getDefaultCharset());

						for (String charset : result.getCharsetList())
							comboEncodings.add(charset);

						comboEncodings.setValue(result.getDefaultCharset());

					}

					/**
					 * {@inheritDoc}
					 */
					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {

							Log.error("Error loading charset list", caught);
							showErrorAndHide(msgs.errorLoadingCharsetListHead(),
									msgs.errorLoadingCharsetList(),caught.getLocalizedMessage() , caught);

						}
					}
				});

		FieldLabel comboEncodingsLabel=new FieldLabel(comboEncodings, msgs.comboEncodingsLabel());
		comboEncodingsLabel.setLabelWidth(LABEL_WIDTH);
		comboEncodingsLabel.setLabelPad(LABEL_PAD_WIDTH);
		//content.add(comboEncodingsLabel);

		// Delimiter
		radioCommaDelimiter = new Radio();
		radioCommaDelimiter.setBoxLabel(msgs.radioCommaDelimiterLabel());
		radioCommaDelimiter.setValue(true);

		radioSpaceDelimiter = new Radio();
		radioSpaceDelimiter.setBoxLabel(msgs.radioSpaceDelimiterLabel());

		radioTabDelimiter = new Radio();
		radioTabDelimiter.setBoxLabel(msgs.radioTabDelimiterLabel());

		radioSemicoloDelimiter = new Radio();
		radioSemicoloDelimiter.setBoxLabel(msgs.radioSemicolonDelimiterLabel());

		radioOtherDelimiter = new Radio();
		radioOtherDelimiter.setBoxLabel(msgs.radioOtherDelimiterLabel());

		customDelimiterField = new TextField();
		customDelimiterField.setEnabled(false);
		customDelimiterField.setValue(DEFAULT_DELIMETER);
		customDelimiterField.setAllowBlank(false);
		customDelimiterField.setWidth(20);

		ToggleGroup delimitersGroup = new ToggleGroup();
		delimitersGroup.add(radioCommaDelimiter);
		delimitersGroup.add(radioSpaceDelimiter);
		delimitersGroup.add(radioTabDelimiter);
		delimitersGroup.add(radioSemicoloDelimiter);
		delimitersGroup.add(radioOtherDelimiter);

		delimitersGroup
				.addValueChangeHandler(new ValueChangeHandler<HasValue<Boolean>>() {

					public void onValueChange(
							ValueChangeEvent<HasValue<Boolean>> event) {

						customDelimiterField.setEnabled(radioOtherDelimiter
								.getValue());

						if (!radioOtherDelimiter.getValue())
							customDelimiterField.clearInvalid();
						else
							customDelimiterField.validate();

						if (radioOtherDelimiter.getValue()
								&& !customDelimiterField.isValid())
							return;
					}
				});

		HorizontalPanel delimitersPanel = new HorizontalPanel();
		delimitersPanel.add(radioCommaDelimiter);
		delimitersPanel.add(radioSpaceDelimiter);
		delimitersPanel.add(radioTabDelimiter);
		delimitersPanel.add(radioSemicoloDelimiter);
		delimitersPanel.add(radioOtherDelimiter);
		delimitersPanel.add(customDelimiterField);

		new ToolTip(delimitersPanel, new ToolTipConfig(
				msgs.delimitersPanelToolTip()));
		FieldLabel fieldDelimeter=new FieldLabel(delimitersPanel, msgs.delimitersPanelLabel());
		fieldDelimeter.setLabelWidth(LABEL_WIDTH);
		fieldDelimeter.setLabelPad(LABEL_PAD_WIDTH);
		content.add(fieldDelimeter);

		// Export View Column
		radioViewColumnExportTrue = new Radio();
		radioViewColumnExportTrue.setBoxLabel(msgs.radioViewColumnExportTrueLabel());
		radioViewColumnExportTrue.setValue(true);

		radioViewColumnExportFalse = new Radio();
		radioViewColumnExportFalse.setBoxLabel(msgs.radioViewColumnExportFalseLabel());

		ToggleGroup exportViewColumnGroup = new ToggleGroup();
		exportViewColumnGroup.add(radioViewColumnExportTrue);
		exportViewColumnGroup.add(radioViewColumnExportFalse);
		
		HorizontalPanel viewColumnExportPanel = new HorizontalPanel();
		viewColumnExportPanel.add(radioViewColumnExportTrue);
		viewColumnExportPanel.add(radioViewColumnExportFalse);
		
		new ToolTip(viewColumnExportPanel, new ToolTipConfig(
				msgs.viewColumnExportPanelToolTip()));
		FieldLabel viewColumnExportPanelLabel=new FieldLabel(viewColumnExportPanel, msgs.viewColumnExportPanelLabel());
		viewColumnExportPanelLabel.setLabelWidth(LABEL_WIDTH);
		viewColumnExportPanelLabel.setLabelPad(LABEL_PAD_WIDTH);
		content.add(viewColumnExportPanelLabel);

		// Column Selection Grid
		csvColumnGridPanel = new ColumnDataGridPanel(this);

		csvColumnGridPanel
				.addSelectionHandler(new SelectionHandler<ColumnData>() {

					public void onSelection(SelectionEvent<ColumnData> event) {

					}

				});

		content.add(csvColumnGridPanel);

		return panel;
	}

	protected char getSelectedDelimiter() {
		if (radioOtherDelimiter.getValue())
			return customDelimiterField.getValue().charAt(0);
		if (radioCommaDelimiter.getValue())
			return ',';
		if (radioSpaceDelimiter.getValue())
			return ' ';
		if (radioTabDelimiter.getValue())
			return '\t';
		if (radioSemicoloDelimiter.getValue())
			return ';';
		return DEFAULT_DELIMETER.charAt(0);
	}
	
	protected boolean getExportViewColumns() {
		if(radioViewColumnExportTrue.getValue()){
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void setup() {
		Log.debug("CSVExportConfigCard Setup");
		Command sayNextCard = new Command() {

			public void execute() {
				Log.debug("CSVExportConfigCard Call sayNextCard");
				checkData();
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);
		setEnableBackButton(false);
		setBackButtonVisible(false);
		setEnableNextButton(true);
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

		ArrayList<ColumnData> columns = csvColumnGridPanel.getSelectedItems();
		if (columns.size() == 0) {
			d = new AlertMessageBox(msgsCommon.attention(), msgs.noColumnSelected());
			d.addHideHandler(hideHandler);
			d.setModal(false);
			d.show();
		} else {
			exportSession.setColumns(columns);
			exportSession.setEncoding(ENCODING_EXPORT_FORMAT);
			exportSession.setSeparator(String.valueOf(getSelectedDelimiter()));
			exportSession.setExportViewColumns(getExportViewColumns());
			useWorkspaceDestination();
		}

	}
	
	protected void useWorkspaceDestination(){
		final WorkspaceDestination workspaceDestination = WorkspaceDestination.INSTANCE;
		exportSession.setDestination(workspaceDestination);
		retrieveTabularResource();
	}
	
	protected void retrieveTabularResource() {
		TDGWTServiceAsync.INSTANCE
				.getTabResourceInformation(new AsyncCallback<TabResource>() {

					public void onSuccess(TabResource result) {
						Log.info("Retrived TR: " + result.getTrId());
						exportSession.setTabResource(result);
						goNext();
					}

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {

							if (caught instanceof TDGWTIsLockedException) {
								Log.error(caught.getLocalizedMessage());
								showErrorAndHide(msgsCommon.errorLocked(),
										caught.getLocalizedMessage(), "",
										caught);
							} else {
								showErrorAndHide(
										msgsCommon.error(),
										msgs.errorRetrievingTabularResourceInfo(),
										caught.getLocalizedMessage(), caught);

							}
						}

					}

				});

	}
	
	

	protected void goNext() {
		try {
			//Enable this for multi destination selection 
			/*DestinationSelectionCard destCard = new DestinationSelectionCard(
					exportSession);
			getWizardWindow().addCard(destCard);
			getWizardWindow().nextCard();*/
			
			Log.info("NextCard CSVWorkspaceSelectionCard");
			CSVWorkSpaceSelectionCard csvWorkspaceSelectionCard = new CSVWorkSpaceSelectionCard(
					exportSession);
			getWizardWindow().addCard(
					csvWorkspaceSelectionCard);
			getWizardWindow().nextCard();
			
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
		}
	}

	@Override
	public void dispose() {

	}

}
