/**
 * 
 */
package org.gcube.portlets.user.csvimportwizard.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.csvimportwizard.client.csvgrid.CSVGridSample;
import org.gcube.portlets.user.csvimportwizard.client.data.AvailableCharsetList;
import org.gcube.portlets.user.csvimportwizard.client.data.CSVRowError;
import org.gcube.portlets.user.csvimportwizard.client.general.WizardCard;
import org.gcube.portlets.user.csvimportwizard.client.rpc.CSVImportService;
import org.gcube.portlets.user.csvimportwizard.client.util.ErrorMessageBox;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.MultiField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class CSVConfigCard extends WizardCard {

	private static final String DEFAULT_DELIMETER = ",";
	private static final String DEFAULT_COMMENT = "#";
	protected CSVImportSession importStatus;

	protected SimpleComboBox<String> encodings;
	protected CheckBox header;
	protected TextField<String> delimiterField;
	protected Radio otherDelimiter;
	protected Radio commaDelimiter;
	protected Radio spaceDelimiter;
	protected Radio tabDelimiter;
	protected Radio semicoloDelimiter;
	protected TextField<String> commentField;
	protected CsvCheckPanel csvCheckPanel;
	protected ImportWizard importWizard;

	protected CSVGridSample csvSample;

	public CSVConfigCard(final CSVImportSession importStatus, ImportWizard importWizard) {
		super("CSV configuration", "Step 3 of 4");

		this.importStatus = importStatus;
		this.importWizard = importWizard;

		FormPanel panel = createPanel();
		setContent(panel);

	}
	
	protected FormPanel createPanel()
	{
		FormPanel panel = new FormPanel();
		panel.setHeaderVisible(false);
		panel.setLabelWidth(90);

		encodings = new SimpleComboBox<String>();
		encodings.setFieldLabel("File encoding");
		encodings.setToolTip("The CSV file encoding");
		encodings.setTabIndex(0);
		encodings.setEditable(false);
		encodings.setForceSelection(true);
		encodings.setTriggerAction(TriggerAction.ALL);
		encodings.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
			
			public void selectionChanged(SelectionChangedEvent<SimpleComboValue<String>> se) {
				updateGrid();
			}
		});
		
		panel.add(encodings);

		header = new CheckBox();
		header.setFieldLabel("Has header");
		header.setToolTip("Check it if the first line is the CSV header");
		header.setTabIndex(1);
		header.addListener(Events.OnClick, new Listener<FieldEvent>() {

			
			public void handleEvent(FieldEvent be) {
				GWT.log("On change header");
				updateGrid();
			}
		});
		
		panel.add(header, new FormData(20, 20));

		Listener<FieldEvent> clickHandler = new Listener<FieldEvent>() {
			
			public void handleEvent(FieldEvent be) {
				updateGrid();
			}
		};

		commaDelimiter = new Radio();
		commaDelimiter.setBoxLabel("Comma");
		commaDelimiter.addListener(Events.OnClick, clickHandler);
		commaDelimiter.setValue(true);

		spaceDelimiter = new Radio();
		spaceDelimiter.setBoxLabel("Space");
		spaceDelimiter.addListener(Events.OnClick, clickHandler);

		tabDelimiter = new Radio();
		tabDelimiter.setBoxLabel("Tab");
		tabDelimiter.addListener(Events.OnClick, clickHandler);

		semicoloDelimiter = new Radio();
		semicoloDelimiter.setBoxLabel("Semicolon");
		semicoloDelimiter.addListener(Events.OnClick, clickHandler);

		otherDelimiter = new Radio();
		otherDelimiter.setBoxLabel("Other delimiter");
		otherDelimiter.addListener(Events.OnClick, clickHandler);
		
		delimiterField = new TextField<String>(); 
		delimiterField.setEnabled(false);
		delimiterField.setHideLabel(true);
		delimiterField.setValue(DEFAULT_DELIMETER);
		delimiterField.setAllowBlank(false);
		delimiterField.setWidth(20);
		delimiterField.addListener(Events.OnChange, new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent be) {
				
				if (delimiterField.isValid()) updateGrid();
			}
		});
		
		otherDelimiter.addListener(Events.Change, new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent be) {
				delimiterField.setEnabled(otherDelimiter.getValue());
				
				if (!otherDelimiter.getValue()) delimiterField.clearInvalid();
				else delimiterField.validate();
			}
		});
		
		RadioGroup delimiters = new RadioGroup();
		delimiters.setTabIndex(2);
		delimiters.add(commaDelimiter);
		delimiters.add(spaceDelimiter);
		delimiters.add(tabDelimiter);
		delimiters.add(semicoloDelimiter);
		delimiters.add(otherDelimiter);
		
		MultiField<Boolean> mf = new MultiField<Boolean>("Delimiter", delimiters, delimiterField);
		mf.setToolTip("The delimiter use to delimit the CSV fields");
		panel.add(mf, new FormData("100%"));
		
		commentField = new TextField<String>(); 
		commentField.setFieldLabel("Comment");
		commentField.setToolTip("The character used as comment line prefix");
		commentField.setValue(DEFAULT_COMMENT);
		commentField.setAllowBlank(false);
		commentField.setMaxLength(1);
		commentField.setWidth(20);
		commentField.addListener(Events.OnChange, new Listener<FieldEvent>() {

			public void handleEvent(FieldEvent be) {
				
				if (commentField.isValid()) updateGrid();
			}
		});
		
		panel.add(commentField, new FormData(20, 20));

		csvSample = new CSVGridSample();
		panel.add(csvSample, new FormData("100%"));
		
		panel.add(new Html("<BR>"));
		
		csvCheckPanel = new CsvCheckPanel();
		panel.add(csvCheckPanel, new FormData("100%"));
		
		csvCheckPanel.getCheckConfiguration().addListener(Events.OnClick, new Listener<ButtonEvent>() {

			public void handleEvent(ButtonEvent be) {
				checkConfiguration();
			}
		});
		
		csvCheckPanel.getSkipInvalidCheckBox().addListener(Events.Change, new Listener<FieldEvent>() {

			
			public void handleEvent(FieldEvent be) {
				boolean skip = csvCheckPanel.getSkipInvalidCheckBox().getValue();
				setEnableNextButton(skip);
				CSVConfigCard.this.importStatus.setSkipInvalidLines(skip);
				
			}
		});
		
	    return panel;
	}

	protected void checkConfiguration()
	{
		csvCheckPanel.setActiveCheckingPanel();
		csvCheckPanel.getCheckConfiguration().setEnabled(false);

		CSVImportService.Util.getInstance().checkCSV(importStatus.getId(), 100, new AsyncCallback<ArrayList<CSVRowError>>() {

			
			public void onFailure(Throwable caught) {
				ErrorMessageBox.showError("An error occured checking the file", "Please retry, if the error perstists change the CSV configuration", "", new Listener<MessageBoxEvent>() {

					
					public void handleEvent(MessageBoxEvent be) {
					}
				});
			}

			
			public void onSuccess(ArrayList<CSVRowError> errors) {
				csvCheckPanel.getCheckConfiguration().setEnabled(true);
				
				if (errors.size() == 0) {
					setCheckCorrectMessage();
				}else{
					setCheckErrorMessage(errors);
				}
			}
		});
	}

	protected void updateGrid()
	{
		GWT.log("Started updating GRID");

		resetCheckMessage();
		
		csvSample.mask("Updating...");

		GWT.log("updating CSV config");
		
		boolean hasHeader = header.getValue();
		char delimiter = getSelectedDelimiter();
		List<SimpleComboValue<String>> selectedValues = encodings.getSelection();
		
		String encoding = (selectedValues.size()>0)?encodings.getSelection().get(0).getValue():"";
		char commentChar = commentField.getValue().charAt(0);
		
		CSVImportService.Util.getInstance().configureCSVParser(importStatus.getId(), encoding, hasHeader, delimiter, commentChar, new AsyncCallback<ArrayList<String>>() {

			
			public void onFailure(Throwable caught) {
				GWT.log("Failed updating CSV config",caught);
				setEnableNextButton(false);
				ErrorMessageBox.showError("An error occured checking the file", "Please retry, if the error perstists change the CSV configuration", "", new Listener<MessageBoxEvent>() {

					
					public void handleEvent(MessageBoxEvent be) {
					}
				});

			}

			
			public void onSuccess(ArrayList<String> result) {
				GWT.log("CSV header getted");

				importStatus.setHeaders(result);

				for (String name:result) GWT.log("Column HEADER: "+name);

				csvSample.configureColumns(importStatus.getId(), result);
				csvSample.unmask();

				setEnableNextButton(false);
				
			}
		});
	
	}
	
	protected char getSelectedDelimiter()
	{
		if (otherDelimiter.getValue()) return delimiterField.getValue().charAt(0);
		if (commaDelimiter.getValue()) return ',';
		if (spaceDelimiter.getValue()) return ' ';
		if (tabDelimiter.getValue()) return '\t';
		if (semicoloDelimiter.getValue()) return ';';
		return DEFAULT_DELIMETER.charAt(0);
	}

	
	
	public void setup() {
		setEnableBackButton(false);
		setEnableNextButton(false);
		encodings.focus();

		/*TimeSeriesPortlet.csvService.getImportedFileName(importStatus.ticketId, new AsyncCallback<String>(){

			public void onFailure(Throwable caught) {
				GWT.log("Error retrieving file import name",caught);
			}

			public void onSuccess(String result) {
				importStatus.setServerlFileName(result);
				addToWindowTitle(" ("+result+")");
			}

		});*/

		CSVImportService.Util.getInstance().getAvailableCharset(importStatus.getId(), new AsyncCallback<AvailableCharsetList>() {
			
			
			public void onSuccess(AvailableCharsetList result) {
				GWT.log("CharsetInfo: "+result.getCharsetList().size()+" charset, default: "+result.getDefaultCharset());
				
				for (String charset:result.getCharsetList()) encodings.add(charset);
				
				encodings.setSimpleValue(result.getDefaultCharset());

				updateGrid();
			}
			
			/**
			 * {@inheritDoc}
			 */
			
			public void onFailure(Throwable caught) {
				GWT.log("Error loading charset list",caught);
				showErrorAndHide("Error loading charset list", "Error loading charset list", "", caught);
			}
		});
	
		resetCheckMessage();
	}

	
	protected void resetCheckMessage()
	{
		csvCheckPanel.setActiveInfoPanel();
	}

	protected void setCheckErrorMessage(ArrayList<CSVRowError> errors)
	{
		csvCheckPanel.setActiveFailure(errors);
		setEnableNextButton(false);
	}

	protected void setCheckCorrectMessage()
	{
		csvCheckPanel.setActiveSuccess();
		setEnableNextButton(true);
	}

	

	
	public void dispose() {
		importStatus.setColumnToImportMask(csvSample.getImportColumnsMask());
	}

}
