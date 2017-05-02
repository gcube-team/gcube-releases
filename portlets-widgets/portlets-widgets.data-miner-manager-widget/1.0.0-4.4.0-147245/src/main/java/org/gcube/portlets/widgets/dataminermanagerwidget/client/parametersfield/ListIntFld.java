/**
 * 
 */
package org.gcube.portlets.widgets.dataminermanagerwidget.client.parametersfield;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ListParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.DataMinerManagerPanel;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.IntegerField;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class ListIntFld extends AbstractFld {

	private List<Item> items;
	private SimpleContainer simpleContainer;
	private VerticalLayoutContainer vp;
	private ListParameter listParameter;
	private SimpleContainer listContainer;
	private SimpleContainer fieldContainer;
	private HBoxLayoutContainer horiz;

	/**
	 * @param parameter
	 */
	public ListIntFld(Parameter parameter) {
		super(parameter);
		this.listParameter = (ListParameter) parameter;
		
		listContainer = new SimpleContainer();
		vp = new VerticalLayoutContainer();
		items = new ArrayList<Item>();
		addField(null);
		listContainer.add(vp, new MarginData(new Margins()));
		
		fieldContainer = new SimpleContainer();
		horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);
		
		HtmlLayoutContainer descr;

		if (listParameter.getDescription() == null) {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");

		} else {
			//listContainer.setToolTip(listParameter.getDescription());
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'>"
					+ listParameter.getDescription() + "</p>");
			descr.addStyleName("workflow-fieldDescription");
		}

		horiz.add(listContainer, new BoxLayoutData(new Margins(0)));
		horiz.add(descr, new BoxLayoutData(new Margins(0)));

		fieldContainer.add(horiz);
		fieldContainer.forceLayout();
		
		
	}

	private void addField(Item upperItem) {

		ObjectParameter objPar = new ObjectParameter(listParameter.getName(),
				listParameter.getDescription(), listParameter.getType(), null);

		if (upperItem == null) {
			Item item = new Item(objPar, true);
			items.add(item);
			vp.add(item, new VerticalLayoutData(1, -1, new Margins()));
		} else {
			// search the position of the upper item
			int pos = 0;
			for (int i = 0; i < items.size(); i++)
				if (items.get(i) == upperItem) {
					pos = i;
					break;
				}

			upperItem.showCancelButton();
			Item item = new Item(objPar, false);
			items.add(pos + 1, item);
			vp.insert(item, pos + 1);
		}

	}

	/**
	 * @param item
	 */
	private void removeField(Item item) {
		items.remove(item);

		vp.remove(item);
		
		if (items.size() == 1) {
			items.get(0).hideCancelButton();
		}
		
		simpleContainer.forceLayout();

	}

	/**
	 * 
	 */
	@Override
	public String getValue() {
		String separator = listParameter.getSeparator();
		String value = "";
		boolean first = true;
		for (Item item : items) {
			Integer itemValue = item.getValue();
			if (itemValue != null) {
				value += (first ? "" : separator) + itemValue;
				first = false;
			}
		}
		return value;
	}

	/**
	 * 
	 */
	@Override
	public Widget getWidget() {
		return fieldContainer;
	}

	/**
	 * 
	 */
	@Override
	public boolean isValid() {
		for (Item item : items)
			if (!item.isValid()) {
				return false;
			}
		return true;
	}

	private class Item extends HBoxLayoutContainer {

		private IntegerField field;
		private TextButton addBtn;
		private TextButton removeBtn;

		/**
		 * @param objPar
		 */
		public Item(ObjectParameter objectParameter, boolean first) {
			super();
			
			field = new IntegerField();
			field.setAllowBlank(false);
			
			addBtn = new TextButton("");

			addBtn.setIcon(DataMinerManagerPanel.resources.add());

			addBtn.addSelectHandler(new SelectEvent.SelectHandler() {

				@Override
				public void onSelect(SelectEvent event) {
					addField(Item.this);
					forceLayout();
					vp.forceLayout();
					fieldContainer.forceLayout();
				}
			});

			removeBtn = new TextButton("");

			removeBtn.setIcon(DataMinerManagerPanel.resources.cancel());

			removeBtn.addSelectHandler(new SelectEvent.SelectHandler() {

				@Override
				public void onSelect(SelectEvent event) {
					removeField(Item.this);
					forceLayout();
					vp.forceLayout();
					fieldContainer.forceLayout();

				}
			});

			removeBtn.setVisible(!first);

			setPack(BoxLayoutPack.START);
			setEnableOverflow(false);
			add(field, new BoxLayoutData(new Margins()));
			add(addBtn, new BoxLayoutData(new Margins()));
			add(removeBtn, new BoxLayoutData(new Margins()));

			forceLayout();
		}

		public void showCancelButton() {
			removeBtn.setVisible(true);
		}

		public void hideCancelButton() {
			removeBtn.setVisible(false);
		}

		public Integer getValue() {
			return field.getCurrentValue();
		}

		public boolean isValid() {
			return field.isValid();
		}
	}

}
