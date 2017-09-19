package org.gcube.portlets.user.td.csvimportwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.csvimportwidget.client.csvgrid.CSVGrid;
import org.gcube.portlets.user.td.gwtservice.client.rpc.TDGWTServiceAsync;
import org.gcube.portlets.user.td.gwtservice.shared.csv.AvailableCharsetList;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVImportSession;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVRowError;
import org.gcube.portlets.user.td.gwtservice.shared.csv.CheckCSVSession;
import org.gcube.portlets.user.td.gwtservice.shared.exception.TDGWTSessionExpiredException;
import org.gcube.portlets.user.td.gwtservice.shared.file.HeaderPresence;
import org.gcube.portlets.user.td.widgetcommonevent.client.CommonMessages;
import org.gcube.portlets.user.td.widgetcommonevent.client.event.SessionExpiredEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.SessionExpiredType;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;
import org.gcube.portlets.user.td.wizardwidget.client.util.UtilsGXT3;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.Radio;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.validator.MaxLengthValidator;
import com.sencha.gxt.widget.core.client.tips.ToolTip;
import com.sencha.gxt.widget.core.client.tips.ToolTipConfig;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class CSVConfigCard extends WizardCard {
	private static CSVImportWizardTDMessages msgs= GWT.create(CSVImportWizardTDMessages.class);
	private CommonMessages msgsCommon;
	
	private static final String DEFAULT_DELIMETER = ",";
	private static final String DEFAULT_COMMENT = "#";
	private static final long ERRORLIMIT = 100;
	private CSVImportSession csvImportSession;

	private SimpleComboBox<String> comboEncodings;
	private SimpleComboBox<HeaderPresence> comboHeader;
	private TextField customDelimiterField;
	private Radio radioOtherDelimiter;
	private Radio radioCommaDelimiter;
	private Radio radioSpaceDelimiter;
	private Radio radioTabDelimiter;
	private Radio radioSemicolonDelimiter;
	private TextField commentField;
	private CsvCheckPanel csvCheckPanel;

	private CSVGrid gridCSVSample;

	

	public CSVConfigCard(final CSVImportSession csvImportSession) {
		super(msgs.csvConfiguration(), "");
		if (csvImportSession == null) {
			Log.error("CSVImportSession is null");
		}
		this.csvImportSession = csvImportSession;
		initMessages();
		FormPanel panel = createPanel();
		setCenterWidget(panel,new MarginData(0));
		
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
				updateGrid();
			}
		});

		content.add(new FieldLabel(comboEncodings, msgs.comboEncodingsLabel()));

		LabelProvider<HeaderPresence> labelProvider = new LabelProvider<HeaderPresence>() {

			public String getLabel(HeaderPresence item) {
				return item.getLabel();
			}
		};

		comboHeader = new SimpleComboBox<HeaderPresence>(labelProvider);
		comboHeader.setToolTip(msgs.comboHeaderToolTip());
		comboHeader.setTabIndex(0);
		comboHeader.setEditable(false);
		comboHeader.setForceSelection(true);
		comboHeader.setTriggerAction(TriggerAction.ALL);
		for (HeaderPresence headerPresence : HeaderPresence.values())
			comboHeader.add(headerPresence);
		comboHeader.setValue(HeaderPresence.NONE);
		comboHeader.addSelectionHandler(new SelectionHandler<HeaderPresence>() {

			public void onSelection(SelectionEvent<HeaderPresence> event) {
				updateGrid();
			}
		});

		content.add(new FieldLabel(comboHeader, msgs.comboHeaderLabel()));

		radioCommaDelimiter = new Radio();
		radioCommaDelimiter.setBoxLabel(msgs.radioCommaDelimiterLabel());
		radioCommaDelimiter.setValue(true);

		radioSpaceDelimiter = new Radio();
		radioSpaceDelimiter.setBoxLabel(msgs.radioSpaceDelimiterLabel());

		radioTabDelimiter = new Radio();
		radioTabDelimiter.setBoxLabel(msgs.radioTabDelimiterLabel());

		radioSemicolonDelimiter = new Radio();
		radioSemicolonDelimiter.setBoxLabel(msgs.radioSemicolonDelimiterLabel());

		radioOtherDelimiter = new Radio();
		radioOtherDelimiter.setBoxLabel(msgs.radioOtherDelimiterLabel());

		customDelimiterField = new TextField();
		customDelimiterField.setEnabled(false);
		customDelimiterField.setValue(DEFAULT_DELIMETER);
		customDelimiterField.setAllowBlank(false);
		customDelimiterField.setWidth(20);
		customDelimiterField.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				customDelimiterField.validate();
				
				if (radioOtherDelimiter.getValue()
						&& !customDelimiterField.isValid()){
					UtilsGXT3
					.alert(msgsCommon.attention(),msgs.insertAvalidDelimiterElseCommaIsUsed());
				} 
				
				updateGrid();
				
			}
		});
		ToggleGroup delimitersGroup = new ToggleGroup();
		delimitersGroup.add(radioCommaDelimiter);
		delimitersGroup.add(radioSpaceDelimiter);
		delimitersGroup.add(radioTabDelimiter);
		delimitersGroup.add(radioSemicolonDelimiter);
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
								&& !customDelimiterField.isValid()){
							UtilsGXT3
							.alert(msgsCommon.attention(),msgs.insertAvalidDelimiterElseCommaIsUsed());
						} 
						
						updateGrid();
					}
				});

		HorizontalPanel delimitersPanel = new HorizontalPanel();
		delimitersPanel.add(radioCommaDelimiter);
		delimitersPanel.add(radioSpaceDelimiter);
		delimitersPanel.add(radioTabDelimiter);
		delimitersPanel.add(radioSemicolonDelimiter);
		delimitersPanel.add(radioOtherDelimiter);
		delimitersPanel.add(customDelimiterField);

		new ToolTip(delimitersPanel, new ToolTipConfig(
				msgs.delimitersPanelToolTip()));
		content.add(new FieldLabel(delimitersPanel, msgs.delimitersPanelLabel()));

		commentField = new TextField();
		commentField.setToolTip(msgs.commentFieldToolTip());
		commentField.setValue(DEFAULT_COMMENT);
		commentField.setAllowBlank(false);
		commentField.getValidators().add(new MaxLengthValidator(1));
		commentField.setWidth(20);
		commentField.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				if (commentField.isValid())
					updateGrid();
			}
		});

		content.add(new FieldLabel(commentField, msgs.commentFieldLabel()));

		gridCSVSample = new CSVGrid();
		content.add(gridCSVSample, new VerticalLayoutData(1, -1));

		content.add(new HTML("<BR>"));

		csvCheckPanel = new CsvCheckPanel();
		content.add(csvCheckPanel);

		csvCheckPanel.getCheckConfiguration().addSelectHandler(
				new SelectHandler() {

					public void onSelect(SelectEvent event) {
						checkConfiguration();
					}
				});

		csvCheckPanel.getSkipInvalidCheckBox().addValueChangeHandler(
				new ValueChangeHandler<Boolean>() {

					public void onValueChange(ValueChangeEvent<Boolean> event) {
						boolean skip = csvCheckPanel.getSkipInvalidCheckBox()
								.getValue();
						setEnableNextButton(skip);
						CSVConfigCard.this.csvImportSession
								.setSkipInvalidLines(skip);
					}
				});

		return panel;
	}

	protected void checkConfiguration() {
		
		
		csvCheckPanel.setActiveCheckingPanel();
		csvCheckPanel.getCheckConfiguration().setEnabled(false);

		TDGWTServiceAsync.INSTANCE.checkCSV(ERRORLIMIT,
				new AsyncCallback<CheckCSVSession>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							UtilsGXT3
									.alert(msgs.anErrorOccuredCheckingTheFileHead(),
											msgs.anErrorOccuredCheckingTheFile());
						}
					}

					public void onSuccess(CheckCSVSession checkCSVSession) {
						ArrayList<CSVRowError> errors = checkCSVSession
								.getCsvRowErrorList();
						csvCheckPanel.getCheckConfiguration().setEnabled(true);

						if (errors.size() == 0) {
							if (checkCSVSession.isCsvFileUpperMaxSizeCheck()) {
								CSVConfigCard.this.csvImportSession
										.setSkipInvalidLines(true);
							}
							setCheckCorrectMessage();
						} else {
							setCheckErrorMessage(errors);
						}
					}
				});
	}

	protected void updateGrid() {
		GWT.log("Started updating GRID");

		resetCheckMessage();

		gridCSVSample.mask(msgs.gridCSVSampleMask());

		GWT.log("updating CSV config");

		HeaderPresence headerPresence = comboHeader.getCurrentValue();

		char delimiter = getSelectedDelimiter();

		String encoding = comboEncodings.getCurrentValue();
		char commentChar = commentField.getValue().charAt(0);

		TDGWTServiceAsync.INSTANCE.configureCSVParser(encoding, headerPresence,
				delimiter, commentChar, new AsyncCallback<ArrayList<String>>() {

					public void onFailure(Throwable caught) {
						if (caught instanceof TDGWTSessionExpiredException) {
							getEventBus()
									.fireEvent(
											new SessionExpiredEvent(
													SessionExpiredType.EXPIREDONSERVER));
						} else {
							Log.error("Failed updating CSV config", caught);
							setEnableNextButton(false);
							UtilsGXT3
									.alert(msgs.anErrorOccuredCheckingTheFileHead(),
											msgs.anErrorOccuredCheckingTheFile());

						}
					}

					public void onSuccess(ArrayList<String> result) {
						Log.info("CSV header getted");

						csvImportSession.setHeaders(result);

						for (String name : result)
							Log.info("Column HEADER: " + name);

						// csvImportSession.getId()
						gridCSVSample.configureColumns(result);
						gridCSVSample.unmask();

						setEnableNextButton(false);

					}
				});

	}

	protected char getSelectedDelimiter() {
		if (radioOtherDelimiter.getValue()) {
			String custom=customDelimiterField.getValue();
			if(custom!=null && !custom.isEmpty()){
				return custom.charAt(0);
			} else {
				return DEFAULT_DELIMETER.charAt(0);
				
			}
		} else {
			if (radioCommaDelimiter.getValue()) {
				return ',';
			} else {
				if (radioSpaceDelimiter.getValue()) {
					return ' ';
				} else {
					if (radioTabDelimiter.getValue()) {
						return '\t';
					} else {
						if (radioSemicolonDelimiter.getValue()) {
							return ';';
						}
					}
				}
			}
		}
		return DEFAULT_DELIMETER.charAt(0);
	}

	@Override
	public void setup() {
		setEnableBackButton(false);
		setEnableNextButton(false);
		comboEncodings.focus();

		TDGWTServiceAsync.INSTANCE
				.getAvailableCharset(new AsyncCallback<AvailableCharsetList>() {

					public void onSuccess(AvailableCharsetList result) {
						GWT.log("CharsetInfo: "
								+ result.getCharsetList().size()
								+ " charset, default: "
								+ result.getDefaultCharset());

						for (String charset : result.getCharsetList())
							comboEncodings.add(charset);

						comboEncodings.setValue(result.getDefaultCharset());

						updateGrid();
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
									msgs.errorLoadingCharsetList(), caught.getLocalizedMessage(), caught);
						}
					}
				});

		resetCheckMessage();

		Command sayNextCard = new Command() {

			public void execute() {
				CSVTableDetailCard csvTableDetailCard = new CSVTableDetailCard(
						csvImportSession);
				getWizardWindow().addCard(csvTableDetailCard);
				Log.info("NextCard SDMXTableDetailCard");
				getWizardWindow().nextCard();

			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

	}

	protected void resetCheckMessage() {
		csvCheckPanel.setActiveInfoPanel();
	}

	protected void setCheckErrorMessage(ArrayList<CSVRowError> errors) {
		csvCheckPanel.setActiveFailure(errors);
		setEnableNextButton(false);

	}

	protected void setCheckCorrectMessage() {
		csvCheckPanel.setActiveSuccess();
		setEnableNextButton(true);
	}

	@Override
	public void dispose() {
		csvImportSession
				.setColumnToImportMask(gridCSVSample.getImportColumnsMask());
	}

}
