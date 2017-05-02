/**
 * 
 */
package org.gcube.portlets.widgets.dataminermanagerwidget.client.parametersfield;

import java.util.ArrayList;
import java.util.List;

import org.gcube.data.analysis.dataminermanagercl.shared.data.ColumnItem;
import org.gcube.data.analysis.dataminermanagercl.shared.data.TableItemSimple;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.ColumnListParameter;
import org.gcube.data.analysis.dataminermanagercl.shared.parameters.Parameter;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.TabularFldChangeEvent;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.events.TabularFldChangeEvent.TabularFldChangeEventHandler;
import org.gcube.portlets.widgets.dataminermanagerwidget.client.properties.ColumnItemProperties;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.XTemplates;
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
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ColumnListFld extends AbstractFld implements
		TabularFldChangeEventHandler {

	interface LabelTemplates extends XTemplates {
		@XTemplate("<span title=\"{value}\">{value}</span>")
		SafeHtml format(String value);
	}

	// private VerticalLayoutContainer vp;
	private ColumnListParameter columnListParameter;
	private ListStore<ColumnItem> store;
	private Grid<ColumnItem> grid;
	private CheckBoxSelectionModel<ColumnItem> sm;
	private SimpleContainer fieldContainer;
	private SimpleContainer vContainer;
	private TableItemSimple tableItemSimple;

	/**
	 * 
	 * @param parameter
	 */
	public ColumnListFld(Parameter parameter) {
		super(parameter);

		columnListParameter = (ColumnListParameter) parameter;
		// vp = new VerticalLayoutContainer();

		fieldContainer = new SimpleContainer();
		HBoxLayoutContainer horiz = new HBoxLayoutContainer();
		horiz.setPack(BoxLayoutPack.START);
		horiz.setEnableOverflow(false);

		createGrid();

		HtmlLayoutContainer descr;

		if (columnListParameter.getDescription() == null) {
			descr = new HtmlLayoutContainer(
					"<p style='margin-left:5px !important;'></p>");
			descr.addStyleName("workflow-fieldDescription");

		} else {
			// grid.setToolTip(columnListParameter.getDescription());
			descr = new HtmlLayoutContainer(
					"<p style='margin-left:5px !important;'>"
							+ columnListParameter.getDescription() + "</p>");
			descr.addStyleName("workflow-fieldDescription");
		}

		vContainer = new SimpleContainer();
		showNoSelectionField();
		horiz.add(vContainer, new BoxLayoutData(new Margins()));
		horiz.add(descr, new BoxLayoutData(new Margins()));

		fieldContainer.add(horiz);
		fieldContainer.forceLayout();

	}

	private void createGrid() {
		ColumnItemProperties props = GWT.create(ColumnItemProperties.class);

		ColumnConfig<ColumnItem, String> labelCol = new ColumnConfig<ColumnItem, String>(
				props.label());

		labelCol.setCell(new AbstractCell<String>() {

			@Override
			public void render(Context context, String value, SafeHtmlBuilder sb) {
				LabelTemplates labelTemplates = GWT
						.create(LabelTemplates.class);
				sb.append(labelTemplates.format(value));
			}
		});

		IdentityValueProvider<ColumnItem> identity = new IdentityValueProvider<ColumnItem>();
		sm = new CheckBoxSelectionModel<ColumnItem>(identity);

		List<ColumnConfig<ColumnItem, ?>> l = new ArrayList<ColumnConfig<ColumnItem, ?>>();
		l.add(sm.getColumn());
		l.add(labelCol);
		ColumnModel<ColumnItem> cm = new ColumnModel<ColumnItem>(l);

		store = new ListStore<ColumnItem>(props.id());

		grid = new Grid<ColumnItem>(store, cm);

		sm.setSelectionMode(SelectionMode.MULTI);
		grid.setSelectionModel(sm);
		// grid.getView().setAutoExpandColumn(labelCol);
		grid.setSize("180px", "150px");
		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(false);
		grid.disable();

	}

	private void showNoSelectionField() {
		vContainer.clear();
		VerticalLayoutContainer vField = new VerticalLayoutContainer();
		HtmlLayoutContainer typeDescription = new HtmlLayoutContainer(
				"<div class='workflow-parameters-description'><p>Select table from parameter "
						+ Format.ellipse(columnListParameter
								.getReferredTabularParameterName(), 30)
						+ "</p></div>");
		typeDescription.setStylePrimaryName("workflow-parameters-description");
		vField.add(grid, new VerticalLayoutData(-1, -1, new Margins(0)));
		vField.add(typeDescription, new VerticalLayoutData(-1, -1, new Margins(
				0)));
		vContainer.add(vField);
	}

	private void showFieldWithSelection(TableItemSimple tableItem) {
		vContainer.clear();
		VerticalLayoutContainer vField = new VerticalLayoutContainer();
		HtmlLayoutContainer typeDescription = new HtmlLayoutContainer(
				"<div class='workflow-parameters-description'><p>Columns of Table "
						+ Format.ellipse(tableItem.getName(), 30)
						+ "</p></div>");
		typeDescription.setStylePrimaryName("workflow-parameters-description");
		vField.add(grid, new VerticalLayoutData(-1, -1, new Margins(0)));
		vField.add(typeDescription, new VerticalLayoutData(-1, -1, new Margins(
				0)));
		vContainer.add(vField);
	}

	/**
	 * 
	 */
	@Override
	public String getValue() {
		String separator = columnListParameter.getSeparator();
		String value = "";
		boolean first = true;

		if (tableItemSimple == null) {
			return value;
		} else {
			for (ColumnItem columnItem : sm.getSelection()) {
				String columnName;
				if (tableItemSimple.isTabularResource()) {
					columnName = columnItem.getId();
				} else {
					columnName = columnItem.getName();
				}
				value += (first ? "" : separator) + columnName;
				first = false;
			}
			return value;
		}

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
		return (sm.getSelection() != null && sm.getSelection().size() > 0);
	}

	@Override
	public void onChange(TabularFldChangeEvent event) {
		tableItemSimple = event.getTableItemSimple();
		if (tableItemSimple == null) {
			store.clear();
			store.commitChanges();
			grid.disable();
			showNoSelectionField();
		} else {
			store.clear();
			store.commitChanges();
			store.addAll(tableItemSimple.getColumns());
			store.commitChanges();
			grid.enable();
			showFieldWithSelection(tableItemSimple);
		}

	}

}
