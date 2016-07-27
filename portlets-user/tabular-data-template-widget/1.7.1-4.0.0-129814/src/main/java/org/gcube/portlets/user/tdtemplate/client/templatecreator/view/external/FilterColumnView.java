/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external;

import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnElement;

import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 11, 2014
 *
 */
public class FilterColumnView {
	
	private String filterColumnId;
	
	private ExpressionDialogCaller expressionDialogMng;

	private EventBus eventBus;
	
	private SimpleComboBox<String> comboAndOr;
	
	private TextField<String> textFilterDescr;
	
	private Html caption;
	
	private int index;

	private Button filter;
	/**
	 * 
	 */
	public FilterColumnView(String columnKey, boolean addAndOr, final ColumnElement column, EventBus bus) {

		this.eventBus = bus;
		
//		HorizontalPanel hp = new HorizontalPanel();
		comboAndOr = null;
	
		index = column.getColumnIndex()+1;
		if(addAndOr){
//			hp.add(getAndOr(column.getColumnIndex()));
			comboAndOr = getAndOr(index);
		}
		
		filterColumnId = columnKey+(index);
		caption = new Html(filterColumnId);

		
		filter = new Button("Filter", TdTemplateAbstractResources.filter16());
		filter.setScale(ButtonScale.SMALL);
		filter.setIconAlign(IconAlign.LEFT);

		textFilterDescr = new TextField<String>();
		textFilterDescr.setReadOnly(true);
		textFilterDescr.setEmptyText("None");
		
		
		filter.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if(expressionDialogMng==null){
					
					try {
						expressionDialogMng= new ExpressionDialogCaller(filterColumnId, column.getColumnType(), column.getColumnDataType(), eventBus, column.getColumnIndex(), column.getColumnLabel());
					} catch (Exception e) {
						e.printStackTrace();
						MessageBox.alert("Error", e.getMessage(), null);
					}	
					
				}
				expressionDialogMng.getExpressionDialog().show();
				
			}
		});
	}
	
	private SimpleComboBox<String> getAndOr(int index){
		SimpleComboBox<String> comboAndOr = new SimpleComboBox<String>();
		comboAndOr.add("AND");
		comboAndOr.add("OR");
		
		comboAndOr.setFieldLabel("Operator");
		comboAndOr.setTypeAhead(true);
		comboAndOr.setEditable(false);
		comboAndOr.setTriggerAction(TriggerAction.ALL);
		
		comboAndOr.setSimpleValue("AND");
		
		comboAndOr.setWidth(60);
		
		comboAndOr.setData("index-combo", index);
		
		return comboAndOr;
	}

	public String getFilterColumnId() {
		return filterColumnId;
	}

	public ExpressionDialogCaller getExpressionDialogMng() {
		return expressionDialogMng;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public SimpleComboBox<String> getComboAndOr() {
		return comboAndOr;
	}

	public TextField<String> getTextFilterDescr() {
		return textFilterDescr;
	}

	public Html getCaption() {
		return caption;
	}

	public int getIndex() {
		return index;
	}

	public Button getFilter() {
		return filter;
	}

	public void setFilterDescriptionValue(String value){
		textFilterDescr.setValue(value);
	}
}
