/**
 * 
 */
package org.gcube.portlets.user.td.excelexportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.Constants;
import org.gcube.portlets.user.td.gwtservice.shared.excel.ExcelExportSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardCard;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.box.AlertMessageBox;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class ExcelTableDetailCard extends WizardCard {

	private final String TABLEDETAILPANELWIDTH = "100%";
	private final String TABLEDETAILPANELHEIGHT = "100%";
	private final String FORMWIDTH = "538px";

	private ExcelExportSession exportSession;
	private ExcelTableDetailCard thisCard;

	private VerticalLayoutContainer p;
	private VerticalPanel tableDetailPanel;

	private TextField id;
	private TextField agencyId;
	private TextField version;
	private TextField measureColumn;

	public ExcelTableDetailCard(final ExcelExportSession exportSession) {
		super("Excel Table Detail", "");

		this.exportSession = exportSession;
		thisCard = this;

		tableDetailPanel = new VerticalPanel();

		tableDetailPanel.setSpacing(4);
		tableDetailPanel.setWidth(TABLEDETAILPANELWIDTH);
		tableDetailPanel.setHeight(TABLEDETAILPANELHEIGHT);

		FramedPanel form = new FramedPanel();
		form.setHeadingText("Details");
		form.setWidth(FORMWIDTH);

		FieldSet fieldSet = new FieldSet();
		fieldSet.setHeadingText("Information");
		fieldSet.setCollapsible(false);
		form.add(fieldSet);
	
		p = new VerticalLayoutContainer();
		switch (exportSession.getExportType()) {
		case DATASET:
			datasetViewConfig();
			break;
		case CODELIST:
		case GENERIC:
		default:
			break;

		}

		fieldSet.add(p);

		tableDetailPanel.add(form);
		setContent(tableDetailPanel);

	}

	private void datasetViewConfig() {
		id = new TextField();
		id.setAllowBlank(false);
		id.setEmptyText("Enter Id...");
		id.setValue(Constants.EXCEL_DATASET_EXPORT_DEFAULT_ID);
		p.add(new FieldLabel(id, "Id"), new VerticalLayoutData(1, -1));

		agencyId = new TextField();
		agencyId.setVisible(true);
		agencyId.setAllowBlank(false);
		agencyId.setEmptyText("Enter Agency Id...");
		if (exportSession.getAgencyId() == null || exportSession.getAgencyId().isEmpty()) {
			agencyId.setValue(Constants.EXCEL_DATASET_EXPORT_DEFAULT_AGENCY_ID);
		} else {
			agencyId.setValue(exportSession.getAgencyId());
		}

		FieldLabel agencyNameLabel = new FieldLabel(agencyId, "Agency Id");
		p.add(agencyNameLabel, new VerticalLayoutData(1, -1));

		version = new TextField();
		version.setAllowBlank(false);
		version.setEmptyText("Enter Version...");
		version.setValue(Constants.EXCEL_DATASET_EXPORT_DEFAULT_VERSION);
		p.add(new FieldLabel(version, "Version"), new VerticalLayoutData(1, -1));

		measureColumn = new TextField();
		measureColumn.setValue(exportSession.getObsValueColumn().getLabel());
		measureColumn.setReadOnly(true);
		p.add(new FieldLabel(measureColumn, "Measure Column"), new VerticalLayoutData(1, -1));

	}

	@Override
	public void setup() {
		Command sayNextCard = new Command() {

			public void execute() {
				try {
					checkData();
				} catch (Exception e) {
					Log.error("Error in checkData :" + e.getLocalizedMessage(),e);
				}
			}

		};

		getWizardWindow().setNextButtonCommand(sayNextCard);

		Command sayPreviousCard = new Command() {
			public void execute() {
				try {
					getWizardWindow().previousCard();
					getWizardWindow().removeCard(thisCard);
					Log.info("Remove ExcelTableDetailCard");
				} catch (Exception e) {
					Log.error("sayPreviousCard :" + e.getLocalizedMessage());
				}
			}
		};

		getWizardWindow().setPreviousButtonCommand(sayPreviousCard);

		setBackButtonVisible(true);
		setEnableBackButton(true);

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

		if (id.getValue() == null || id.getValue().isEmpty() || !id.isValid() || version.getValue() == null
				|| version.getValue().isEmpty() || !version.isValid() || agencyId.getValue() == null
				|| agencyId.getValue().isEmpty() || !agencyId.isValid()) {

			d = new AlertMessageBox("Attention!", "Fill in all fields");
			d.addHideHandler(hideHandler);
			d.show();
		} else {
			if (!version.getValue().matches("[0-9]+\\.[0-9]+")) {
				d = new AlertMessageBox("Attention!", "Version must match the regular expression [0-9]+\\.[0-9]+");
				d.addHideHandler(hideHandler);
				d.show();
			} else {
				id.setReadOnly(true);
				version.setReadOnly(true);
				agencyId.setReadOnly(true);
				goNext();
			}
		}

	}

	protected void goNext() {
		try {
			Log.info("Data:");
			Log.info("Id: "+id.getCurrentValue());
			Log.info("AgencyId: "+agencyId.getCurrentValue());
			Log.info("Version: "+version.getCurrentValue());
			
			exportSession.setId(id.getCurrentValue());
			exportSession.setAgencyId(agencyId.getCurrentValue());
			exportSession.setVersion(version.getCurrentValue());

			ExcelOperationInProgressCard excelOperationInProgressCard = new ExcelOperationInProgressCard(exportSession);
			getWizardWindow().addCard(excelOperationInProgressCard);
			Log.info("NextCard ExcelOperationInProgressCard");
			getWizardWindow().nextCard();
		} catch (Exception e) {
			Log.error("sayNextCard :" + e.getLocalizedMessage());
		}
	}

}
