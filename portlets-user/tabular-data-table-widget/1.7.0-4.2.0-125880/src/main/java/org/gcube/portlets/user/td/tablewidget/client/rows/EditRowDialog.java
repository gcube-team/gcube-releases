package org.gcube.portlets.user.td.tablewidget.client.rows;

import java.util.ArrayList;

import org.gcube.portlets.user.td.tablewidget.client.resources.ResourceBundle;
import org.gcube.portlets.user.td.tablewidget.client.util.UtilsGXT3;
import org.gcube.portlets.user.td.widgetcommonevent.shared.TRId;
import org.gcube.portlets.user.td.widgetcommonevent.shared.grid.model.RowRaw;

import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/**
 * Edit Row
 * 
 * @author "Giancarlo Panichi"
 * 
 */
public class EditRowDialog extends Window {
	private static final String WIDTH = "690px";
	private static final String HEIGHT = "456px";
	private boolean addRow;

	/**
	 * Edit selected rows
	 * 
	 * @param trId
	 * @param rows
	 * @param eventBus
	 */
	public EditRowDialog(TRId trId, ArrayList<RowRaw> rows, EventBus eventBus) {
		addRow = false;
		if (rows == null) {
			UtilsGXT3.alert("Attentions", "No row selected");
		} else {

			initWindow();
			EditRowPanel editRowPanel = new EditRowPanel(this, trId, rows,
					eventBus);
			add(editRowPanel);
		}
	}

	/**
	 * Add a new row to the table
	 * 
	 * @param trId
	 * @param eventBus
	 */
	public EditRowDialog(TRId trId, EventBus eventBus) {
		addRow = true;
		initWindow();
		EditRowPanel editRowPanel = new EditRowPanel(this, trId, eventBus);
		add(editRowPanel);

	}

	protected void initWindow() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setBodyBorder(false);
		setResizable(false);
		
		setClosable(true);
		setModal(true);
		forceLayoutOnResize = true;
		if (addRow) {
			getHeader().setIcon(ResourceBundle.INSTANCE.rowInsert());
			setHeadingText("Add Row");
		} else {
			getHeader().setIcon(ResourceBundle.INSTANCE.rowEdit());
			setHeadingText("Edit");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initTools() {
		super.initTools();

		closeBtn.addSelectHandler(new SelectHandler() {

			public void onSelect(SelectEvent event) {
				close();
			}
		});

	}

	protected void close() {
		hide();

	}

}
