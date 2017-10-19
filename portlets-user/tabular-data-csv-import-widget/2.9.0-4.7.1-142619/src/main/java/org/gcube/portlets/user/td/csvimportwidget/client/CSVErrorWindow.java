package org.gcube.portlets.user.td.csvimportwidget.client;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.csv.CSVRowError;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class CSVErrorWindow extends Window {

	private static final CSVRowErrorProperties props = GWT
			.create(CSVRowErrorProperties.class);
	private CSVImportWizardTDMessages msgs;

	private Grid<CSVRowError> gridErrors;
	private ListStore<CSVRowError> storeGridErrors;

	public CSVErrorWindow() {
		initMessages();
		setHeadingText(msgs.csvErrorWindowHead());
		setModal(true);
		setBlinkModal(true);
		setWidth(600);
		setHeight(350);

		createGrid();
		add(gridErrors);

		TextButton btnClose = new TextButton(msgs.btnCloseText());
		btnClose.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				hide();
			}
		});

		addButton(btnClose);
		setButtonAlign(BoxLayoutPack.CENTER);
	}

	protected void initMessages() {
		msgs = GWT.create(CSVImportWizardTDMessages.class);
	}

	protected void createGrid() {
		ArrayList<ColumnConfig<CSVRowError, ?>> columns = new ArrayList<ColumnConfig<CSVRowError, ?>>();

		columns.add(new ColumnConfig<CSVRowError, Integer>(props.lineNumber(),
				30, msgs.gridErrorColumnNLine()));
		columns.add(new ColumnConfig<CSVRowError, String>(props.lineValue(),
				60, msgs.gridErrorCololumnLine()));
		columns.add(new ColumnConfig<CSVRowError, String>(props
				.errorDescription(), 160, msgs.gridErrorCololumnError()));

		ColumnModel<CSVRowError> columnModel = new ColumnModel<CSVRowError>(
				columns);

		storeGridErrors = new ListStore<CSVRowError>(props.id());

		gridErrors = new Grid<CSVRowError>(storeGridErrors, columnModel);
		gridErrors.getView().setForceFit(true);
	}

	public void updateGrid(ArrayList<CSVRowError> errors) {
		storeGridErrors.clear();
		storeGridErrors.addAll(errors);
	}

	protected interface CSVRowErrorProperties extends
			PropertyAccess<CSVRowError> {

		ModelKeyProvider<CSVRowError> id();

		ValueProvider<CSVRowError, Integer> lineNumber();

		ValueProvider<CSVRowError, String> lineValue();

		ValueProvider<CSVRowError, String> errorDescription();
	}

}
