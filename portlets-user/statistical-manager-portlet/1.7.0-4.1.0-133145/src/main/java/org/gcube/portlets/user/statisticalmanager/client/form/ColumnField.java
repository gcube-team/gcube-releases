/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.form;

import org.gcube.portlets.user.statisticalmanager.client.bean.TableItemSimple;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.ColumnParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.Parameter;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ceras
 *
 */
public class ColumnField extends AbstractField {
	
	private VerticalPanel vp = new VerticalPanel();
	String value = null;
//	TableItem selectedColumn = null; 
	private String defaultColumn;
	
	SimpleComboBox<String> listBox;
	private String referredTabularParameterName;

	/**
	 * @param parameter
	 */
	public ColumnField(Parameter parameter) {
		super(parameter);

		ColumnParameter p = (ColumnParameter)parameter;
		this.referredTabularParameterName = p.getReferredTabularParameterName();
		this.defaultColumn = p.getDefaultColumn();
		listBox = new SimpleComboBox<String>();
		listBox.setAllowBlank(false);
		listBox.setForceSelection(true);
		listBox.setEditable(false);
		listBox.setTriggerAction(TriggerAction.ALL);
		if (p.getDescription()!=null)
			listBox.setTitle(p.getDescription());
		listBox.setFieldLabel(p.getName());
		listBox.setEnabled(false);
		listBox.mask();
		showNoSelectionField();
	}
	
	/**
	 * 
	 */
	private void showNoSelectionField() {
		vp.removeAll();
		vp.add(listBox);
		vp.add(new Html("<div class='workflow-parameters-description'>Select table from parameter "+referredTabularParameterName+"</div>"));
		vp.layout();
	}

	/**
	 * 
	 */
	private void showFieldWithSelection(TableItemSimple tableItem) {
		vp.removeAll();
		vp.add(listBox);
		vp.add(new Html("<div class='workflow-parameters-description'>Columns of Table "+tableItem.getName()+"</div>"));
		vp.layout();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		return listBox.getValue().getValue();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getComponent()
	 */
	@Override
	public Widget getWidget() {
		return vp;
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#fireEvent(java.lang.Object)
	 */
	@Override
	public void fireEvent(Object message) {
		if (message==null) {
			listBox.removeAll();
			listBox.clear();
			listBox.setEnabled(false);
			listBox.mask();
			showNoSelectionField();
		} else {
			TableItemSimple tableItem = (TableItemSimple)message;
			listBox.removeAll();
			listBox.add(tableItem.getColumnNames());
			if (tableItem.getColumnNames().contains(this.defaultColumn))
				listBox.setSimpleValue(this.defaultColumn);
			listBox.setEnabled(true);
			listBox.unmask();
			showFieldWithSelection(tableItem);
		}
	}

}
