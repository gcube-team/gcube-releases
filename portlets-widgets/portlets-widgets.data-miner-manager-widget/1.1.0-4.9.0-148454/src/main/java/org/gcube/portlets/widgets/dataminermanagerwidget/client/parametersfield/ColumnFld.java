/**
 * 
 */
package org.gcube.portlets.widgets.dataminermanagerwidget.client.parametersfield;

import java.util.ArrayList;

import org.gcube.data.analysis.dataminermanagercl.shared.data.ColumnItem;
import org.gcube.data.analysis.dataminermanagercl.shared.data.TableItemSimple;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ColumnParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.TabularFldChangeEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.TabularFldChangeEvent.TabularFldChangeEventHandler;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.properties.ColumnItemPropertiesCombo;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HtmlLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.ComboBox;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ColumnFld extends AbstractFld implements TabularFldChangeEventHandler {

	private String defaultColumn;
	private ComboBox<ColumnItem> comboBox;
	private ListStore<ColumnItem> store;
	private String referredTabularParameterName;

	private SimpleContainer fieldContainer;
	private SimpleContainer vContainer;
	private TableItemSimple tableItemSimple;

	/**
	 * @param parameter
	 *            parameter
	 */
	public ColumnFld(Parameter parameter) {
		super(parameter);

		ColumnParameter columnParameter = (ColumnParameter) parameter;

		referredTabularParameterName = columnParameter.getReferredTabularParameterName();
		defaultColumn = columnParameter.getDefaultColumn();

		fieldContainer = new SimpleContainer();
		HBoxLayoutContainer horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);

		ColumnItemPropertiesCombo props = GWT.create(ColumnItemPropertiesCombo.class);

		store = new ListStore<ColumnItem>(props.id());

		comboBox = new ComboBox<ColumnItem>(store, props.label());
		comboBox.setAllowBlank(false);
		comboBox.setForceSelection(true);
		comboBox.setEditable(false);
		comboBox.setTriggerAction(TriggerAction.ALL);
		comboBox.setEnabled(false);
		HtmlLayoutContainer descr;

		if (columnParameter.getDescription() == null) {
			descr = new HtmlLayoutContainer("<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");

		} else {
			// comboBox.setToolTip(columnParameter.getDescription());
			descr = new HtmlLayoutContainer(
					"<p style='margin-left:5px !important;'>" + columnParameter.getDescription() + "</p>");
			descr.addStyleName("workflow-fieldDescription");
		}

		vContainer = new SimpleContainer();
		showNoSelectionField();
		horiz.add(vContainer, new BoxLayoutData(new Margins()));
		horiz.add(descr, new BoxLayoutData(new Margins()));

		fieldContainer.add(horiz);
		fieldContainer.forceLayout();

	}

	private void showNoSelectionField() {
		vContainer.clear();
		VerticalLayoutContainer vField = new VerticalLayoutContainer();
		HtmlLayoutContainer typeDescription = new HtmlLayoutContainer(
				"<div class='workflow-parameters-description'><p>Select table from parameter "
						+ Format.ellipse(referredTabularParameterName, 30) + "</p></div>");
		typeDescription.setStylePrimaryName("workflow-parameters-description");
		vField.add(comboBox, new VerticalLayoutData(1, -1, new Margins(0)));
		vField.add(typeDescription, new VerticalLayoutData(-1, -1, new Margins(0)));
		vContainer.add(vField);
	}

	private void showFieldWithSelection(TableItemSimple tableItem) {
		vContainer.clear();
		VerticalLayoutContainer vField = new VerticalLayoutContainer();
		HtmlLayoutContainer typeDescription = new HtmlLayoutContainer(
				"<div class='workflow-parameters-description'><p>Columns of Table "
						+ Format.ellipse(tableItem.getName(), 30) + "</p></div>");
		typeDescription.setStylePrimaryName("workflow-parameters-description");
		vField.add(comboBox, new VerticalLayoutData(1, -1, new Margins(0)));
		vField.add(typeDescription, new VerticalLayoutData(-1, -1, new Margins(0)));
		vContainer.add(vField);
	}

	@Override
	public String getValue() {
		if (tableItemSimple == null) {
			return null;
		} else {
			ColumnItem columnItem = comboBox.getCurrentValue();
			if (columnItem == null) {
				return null;
			} else {
				if (tableItemSimple.isTabularResource()) {
					return columnItem.getId();
				} else {
					return columnItem.getName();
				}
			}
		}
	}

	@Override
	public Widget getWidget() {
		return fieldContainer;
	}

	@Override
	public boolean isValid() {
		return comboBox.isValid();
	}

	@Override
	public void onChange(TabularFldChangeEvent event) {
		tableItemSimple = event.getTableItemSimple();
		if (tableItemSimple == null) {
			store.clear();
			store.commitChanges();
			comboBox.clear();
			comboBox.setEnabled(false);
			showNoSelectionField();
		} else {
			store.clear();
			store.commitChanges();
			comboBox.clear();
			ArrayList<ColumnItem> columns = tableItemSimple.getColumns();
			if (columns != null) {
				store.addAll(columns);
				store.commitChanges();
				Log.debug("DefaultColumn: " + defaultColumn);
				for (ColumnItem columnItem : columns) {
					Log.debug("ColumnItem: " + columnItem);
					if (columnItem.getName().compareToIgnoreCase(defaultColumn) == 0) {
						comboBox.setValue(columnItem);
						break;
					}
				}

			}
			comboBox.setEnabled(true);
			showFieldWithSelection(tableItemSimple);
		}
		fieldContainer.forceLayout();
	}

}
