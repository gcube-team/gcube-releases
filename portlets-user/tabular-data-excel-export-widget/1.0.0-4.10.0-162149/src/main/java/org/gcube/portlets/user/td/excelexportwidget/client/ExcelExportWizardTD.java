package org.gcube.portlets.user.td.excelexportwidget.client;

import org.gcube.portlets.user.td.gwtservice.shared.excel.ExcelExportSession;
import org.gcube.portlets.user.td.wizardwidget.client.WizardWindow;

import com.google.web.bindery.event.shared.EventBus;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 * 
 */
public class ExcelExportWizardTD extends WizardWindow {

	protected ExcelExportSession exportSession;

	/**
	 * 
	 * 
	 * @param title
	 *            Title
	 * @param eventBus
	 *            Event bus
	 */
	public ExcelExportWizardTD(String title, final EventBus eventBus) {
		super(title, eventBus);
		setWidth(550);
		setHeight(520);

		exportSession = new ExcelExportSession();

		
		MeasureColumnSelectionCard sdmxRegistrySelectionCard = new MeasureColumnSelectionCard(exportSession);
		addCard(sdmxRegistrySelectionCard);
		sdmxRegistrySelectionCard.setup();

	}

	

}