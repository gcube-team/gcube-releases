/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.form;



import java.util.ArrayList;
import java.util.Map.Entry;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.StatisticalManagerExperimentsWidget;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.TableItemSimple;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.ColumnParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.Parameter;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
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
	
	ComboBox<ColumnItem> listBox;
	private String referredTabularParameterName;

	/**
	 * @param parameter
	 */
	public ColumnField(Parameter parameter) {
		super(parameter);

		ColumnParameter p = (ColumnParameter)parameter;
		this.referredTabularParameterName = p.getReferredTabularParameterName();
		this.defaultColumn = p.getDefaultColumn();
		listBox = new ComboBox<ColumnItem>();
		listBox.setAllowBlank(false);
		listBox.setForceSelection(true);
		listBox.setEditable(false);
		listBox.setTriggerAction(TriggerAction.ALL);
		if (p.getDescription()!=null)
			listBox.setTitle(p.getDescription());
		listBox.setFieldLabel(p.getName());
		listBox.setEnabled(false);
		listBox.setAutoWidth(true);
		listBox.setStore(new ListStore<ColumnItem>());
		listBox.setDisplayField(ColumnItem.NAME);
		listBox.setTypeAhead(true);
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
		return listBox.getValue().getCode();
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
			listBox.getStore().removeAll();
			listBox.clear();
			listBox.setEnabled(false);
			listBox.mask();
			showNoSelectionField();
		} else {
			ArrayList<ColumnItem> toAdd=new ArrayList<ColumnItem>();
			TableItemSimple tableItem = (TableItemSimple)message;
			ColumnItem defaultItem=null;
			if(tableItem.getTDFlag()){
				
				for(Entry<String,String> entry:StatisticalManagerExperimentsWidget.instance().getColumns(tableItem.getId()).entrySet()){
					ColumnItem toAddItem=new ColumnItem(entry.getKey(),entry.getValue());
					toAdd.add(toAddItem);
					if(entry.getValue().equals(defaultColumn))
						defaultItem=toAddItem;
				}
			}else{
				for (String columnName: tableItem.getColumnNames()){
					ColumnItem toAddItem=new ColumnItem(columnName,columnName);
					toAdd.add(toAddItem);
					if(columnName.equals(defaultColumn))defaultItem=toAddItem;
				}
			}
			
			listBox.getStore().removeAll();
			listBox.getStore().add(toAdd);
			if (defaultItem!=null)
				listBox.setValue(defaultItem);
			listBox.setEnabled(true);
			listBox.unmask();
			showFieldWithSelection(tableItem);
		}
	}

}
