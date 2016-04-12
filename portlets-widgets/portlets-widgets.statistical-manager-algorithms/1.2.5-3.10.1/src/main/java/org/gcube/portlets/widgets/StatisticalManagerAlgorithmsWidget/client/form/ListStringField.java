/**
 * 
 */
package org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.form;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.ListParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.ObjectParameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.bean.parameters.Parameter;
import org.gcube.portlets.widgets.StatisticalManagerAlgorithmsWidget.client.resources.Images;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.Widget;





/**
 * @author ceras
 *
 */
public class ListStringField extends AbstractField {
	
	private List<Item> items = new ArrayList<Item>();
	private VerticalPanel vp = new VerticalPanel();
	private ListParameter listParameter;

	/**
	 * @param parameter
	 */
	public ListStringField(Parameter parameter) {
		super(parameter);
		
		this.listParameter = (ListParameter)parameter;
		
		addField(null);
	}
	
	private void addField(Item upperItem) {		
		
		ObjectParameter objPar = new ObjectParameter(listParameter.getName(), listParameter.getDescription(), listParameter.getType(), null);
		
		if (upperItem==null) {
			Item item = new Item(objPar, true);
			items.add(item);
			vp.add(item);
		} else {
			// search the position of the upper item
			int pos=0;
			for (int i=0; i<items.size(); i++)
				if (items.get(i) == upperItem) {
					pos = i;
					break;
				}
			
			upperItem.showCancelButton();
			Item item = new Item(objPar, false);
			items.add(pos+1, item);
			vp.insert(item, pos+1);
		}
		
		vp.layout();
	}

	/**
	 * @param item
	 */
	protected void removeField(Item item) {
		items.remove(item);
			
		vp.remove(item);
		vp.layout();
		
		if (items.size()==1) {
			items.get(0).hideCancelButton();
		}

	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		String separator = listParameter.getSeparator();
		String value = "";
		boolean first = true;
		for (Item item: items) {
			String itemValue = item.getValue();
			if (itemValue!=null && !itemValue.contentEquals("")) {
				value += (first ? "" : separator) + itemValue;
				first = false;
			}
		}
		return value;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getWidget()
	 */
	@Override
	public Widget getWidget() {
		return vp;
	}
	
	
	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#isValid()
	 */
	@Override
	public boolean isValid() {
		boolean valid = false;
		for (Item item: items)
			if (item.getField().getValue()!=null) {
				valid = true;
				break;
			}
		return valid;
	}
	
	
	private class Item extends HorizontalPanel {
		
		private StringField field;
		private Button addButton = new Button("", Images.addl());
		private Button removeButton = new Button("", Images.cancel());
		
		/**
		 * @param objPar
		 */
		public Item(ObjectParameter objectParameter, boolean first) {
			super();
			this.field = new StringField(objectParameter);
			this.add(field.getWidget());
			
			addButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					addField(Item.this);
				}
			});
			
			removeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					removeField(Item.this);
				}
			});
			removeButton.setVisible(!first);

			this.add(addButton);
			this.add(removeButton);			
		}
		
		public void showCancelButton() {
			removeButton.setVisible(true);
		}
		
		public void hideCancelButton() {
			removeButton.setVisible(false);
		}
		
		public String getValue() {
			return field.getValue();
		}
		
		public StringField getField() {
			return field;
		}
	}

}
