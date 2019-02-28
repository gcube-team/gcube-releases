/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.form;



import java.util.Map.Entry;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.StatisticalManagerExperimentsWidget;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.TableItemSimple;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.ColumnListParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.Parameter;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.CheckBoxListView;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ceras
 *
 */
public class ColumnListField extends AbstractField {

	private CheckBoxListView<BeanModel> view;
	private VerticalPanel vp = new VerticalPanel();
	String value = null;
	//	TableItem selectedColumn = null; 
	private String tableId;
	private ColumnListParameter columnListParameter;

	/**
	 * @param parameter
	 */
	public ColumnListField(Parameter parameter) {
		super(parameter);

		this.columnListParameter = (ColumnListParameter)parameter;

		view = new CheckBoxListView<BeanModel>();
		ListStore<BeanModel> store = new ListStore<BeanModel>();
		view.setStore(store);
		view.setDisplayProperty(ColumnItem.NAME);
		view.setSize(150, 150);
		view.mask();
		view.setAutoWidth(true);
		showNoSelectionField();		
	}

	/**
	 * 
	 */
	private void showNoSelectionField() {
		vp.removeAll();
		vp.add(view);
		vp.add(new Html("<div class='workflow-parameters-description'>Select table from parameter " + 
				this.columnListParameter.getReferredTabularParameterName()+"</div>"));
		vp.layout();
	}

	/**
	 * 
	 */
	private void showFieldWithSelection(TableItemSimple tableItem) {
		vp.removeAll();
		vp.add(getSelectAllButton());
		vp.add(view);
		vp.add(new Html("<div class='workflow-parameters-description'>Columns of Data Set "+tableItem.getName()+"</div>"));
		vp.layout();
	}

	private HorizontalPanel getSelectAllButton() {
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new Html("<div class='workflow-parameters-description' style='margin-right:5px'>Select all</div>"));
		final CheckBox check = new CheckBox();
		check.addListener(Events.Change, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				toggleSelectAll(check.getValue());
			}
		});
		hp.add(check);
		return hp;
	}

	/**
	 * @param value2
	 */
	protected void toggleSelectAll(Boolean select) {
		if (view!=null && view.getStore()!=null)
			for (BeanModel beanModel: view.getStore().getModels())
				view.setChecked(beanModel, select);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		String separator = columnListParameter.getSeparator();
		String value = "";
		boolean first = true;

		for (BeanModel beanModel: view.getChecked()) {
			String columnId= ((ColumnItem)beanModel).getCode();
			value += (first ? "" : separator) + columnId;
			first = false;
		}
		return value;
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
			view.getStore().removeAll();
			view.mask();
			showNoSelectionField();
		} else {
			TableItemSimple tableItem = (TableItemSimple)message;
			view.getStore().removeAll();

			if(tableItem.getTDFlag()){
				
				for(Entry<String,String> entry:StatisticalManagerExperimentsWidget.instance().getColumns(tableItem.getId()).entrySet())
					view.getStore().add(new ColumnItem(entry.getKey(),entry.getValue()));
			}else{
				for (String columnName: tableItem.getColumnNames())
					view.getStore().add(new ColumnItem(columnName,columnName));
			}
			view.unmask();
			view.setAutoHeight(true);
			//			Resizable r = new Resizable(view);
			showFieldWithSelection(tableItem);
		}
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#isValid()
	 */
	@Override
	public boolean isValid() {
		return (view.getChecked().size()>0);
	}
}
