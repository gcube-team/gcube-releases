package org.gcube.portlets.widgets.dataminermanagerwidget.client.parametersfield;

import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ObjectParameter;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.DataMinerManagerPanel;

import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class StringItem extends HBoxLayoutContainer {
	private ListStringFld parent;
	private TextField field;
	private TextButton addBtn;
	private TextButton removeBtn;

	/**
	 * @param objPar
	 */
	public StringItem(ListStringFld parent, ObjectParameter objectParameter, boolean first) {
		super();
		this.parent=parent;
		create(objectParameter,first);	
	}
	
	private void create( ObjectParameter objectParameter, boolean first){
		field = new TextField();
		field.setAllowBlank(false);

		addBtn = new TextButton("");

		addBtn.setIcon(DataMinerManagerPanel.resources.add());

		addBtn.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				parent.addField(StringItem.this);
				

			}
		});

		removeBtn = new TextButton("");

		removeBtn.setIcon(DataMinerManagerPanel.resources.cancel());

		removeBtn.addSelectHandler(new SelectEvent.SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				parent.removeField(StringItem.this);
				

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

	public String getValue() {
		return field.getCurrentValue();
	}

	public boolean isValid() {
		return field.isValid();
	}

}
