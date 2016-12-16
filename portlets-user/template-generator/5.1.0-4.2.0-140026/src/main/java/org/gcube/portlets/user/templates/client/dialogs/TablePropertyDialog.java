package org.gcube.portlets.user.templates.client.dialogs;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.user.templates.client.components.GenericTable;
import org.gcube.portlets.user.templates.client.presenter.Presenter;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;


/**
 * <code> TablePropertyDialog </code> class is is the Dialog for changing template properties
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version October 2011 (0.1) 
 */


public class TablePropertyDialog extends Dialog {

	private FormData formData;  
	/**
	 * 
	 * @param controller
	 */
	public TablePropertyDialog(final Presenter controller) {

		setHeading("Add Table");
		formData = new FormData("-20");

		FormPanel simple = new FormPanel();  
		simple.setHeaderVisible(false);
		simple.setFrame(true);  

		final SpinnerField numRows = new SpinnerField();  
		numRows.setIncrement(1);  
		numRows.getPropertyEditor().setType(Double.class);  
		numRows.getPropertyEditor().setFormat(NumberFormat.getFormat("0"));  
		numRows.setFieldLabel("Number of Rows");  
		numRows.setMinValue(2);  
		numRows.setMaxValue(50);
		numRows.setMaxLength(2);
		numRows.setAllowBlank(false);
		numRows.setValue(GenericTable.DEFAULT_ROWS_NUM);
		numRows.setAutoWidth(true);
		simple.add(numRows, formData);  

		final SpinnerField numCols= new SpinnerField();  
		numCols.setIncrement(1);  
		numCols.getPropertyEditor().setType(Double.class);  
		numCols.getPropertyEditor().setFormat(NumberFormat.getFormat("0"));  
		numCols.setFieldLabel("Number of Columns");  
		numCols.setMinValue(2);  
		numCols.setMaxValue(10); 
		numCols.setMaxLength(2);
		numCols.setAllowBlank(false);
		numCols.setValue(GenericTable.DEFAULT_COLS_NUM);
		numCols.setAutoWidth(true);
		simple.add(numCols, formData);  

		simple.add(new Label("Note: The lock in tables locks the structure not the content (during reporting phase)."));
		
		add(simple);		


		setButtons(Dialog.OKCANCEL);
		ButtonBar buttons = this.getButtonBar();

		Button okbutton = (Button) buttons.getItem(0);
		setHideOnButtonClick(false);

		okbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				final int rows = numRows.getValue().intValue();
				final int cols = numCols.getValue().intValue();
				hide();
				final MessageBox box = MessageBox.wait("Adding Table",  
						"constructing table", "please wait...");  
				Timer t = new Timer() {  
					@Override  
					public void run() {  
						box.close();  
						controller.insertTable(ComponentType.FLEX_TABLE, rows, cols);	
					}  
				};  
				t.schedule(1000);  

				
			}  
		});  

		Button cancelbutton = (Button) buttons.getItem(1);

		cancelbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
			public void componentSelected(ButtonEvent ce) {  
				hide();
			}  
		});  

	}

}

