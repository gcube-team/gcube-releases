package org.gcube.portlets.user.tdtemplateoperation.client;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.td.monitorwidget.client.utils.UtilsGXT3;
import org.gcube.portlets.user.tdtemplateoperation.client.event.ActionCompletedEvent;
import org.gcube.portlets.user.tdtemplateoperation.client.properties.TdColumnDataPropertiesAccess;
import org.gcube.portlets.user.tdtemplateoperation.client.resources.ResourceBundleTemplateOperation;
import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectId;
import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;
import org.gcube.portlets.user.tdtemplateoperation.shared.action.NormalizeColumnsAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 10, 2014
 * 
 */
public class NormalizeColumnPanel extends FramedPanel {
	private static final int GRID_HEIGHT = 250;
	protected String WIDTH = "640px";
	protected String HEIGHT = "520px";
	protected EventBus eventBus;
	protected TextButton normalizeButton;
	
	protected ListLoader<ListLoadConfig, ListLoadResult<TdColumnData>> gridLoader;
	protected Grid<TdColumnData> grid;
	protected CheckBoxSelectionModel<TdColumnData> sm;

	private ListStore<TdColumnData> gridStore;
	private List<TdColumnData> listColumns;
	
	private HTML error = new HTML();
	private boolean errorCase = false;
	private ServerObjectId serverObjectId;
	private TextField normalizeField;
	private TextField quantityField;
	private Command onClose;

	public NormalizeColumnPanel(ServerObjectId serverObjectId, EventBus eventBus, Command onCloseCommand) {
		this.serverObjectId = serverObjectId;
		this.eventBus = eventBus;
		this.onClose = onCloseCommand;
		init();
		build();
		setEnableNormalizeButton(false);
	}

	public void init() {
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setHeaderVisible(false);
		setBodyBorder(false);
	}

	public void errorText(String text, boolean visible) {
		String html = "<p><img src=\""
				+ ResourceBundleTemplateOperation.INSTANCE.alert().getSafeUri()
						.asString()
				+ "\"/><span style=\"color:red; font-size:11px; margin-left:1px; vertical-align:middle;\">"
				+ text + "</span></p>";
		error.setHTML(html);
	}

	protected void build() {

		TdColumnDataPropertiesAccess props = GWT.create(TdColumnDataPropertiesAccess.class);

		ColumnConfig<TdColumnData, String> labelCol = new ColumnConfig<TdColumnData, String>(props.label());
		
		IdentityValueProvider<TdColumnData> identity = new IdentityValueProvider<TdColumnData>();
		sm = new CheckBoxSelectionModel<TdColumnData>(identity);

		List<ColumnConfig<TdColumnData, ?>> l = new ArrayList<ColumnConfig<TdColumnData, ?>>();
		l.add(sm.getColumn());
		l.add(labelCol);
		ColumnModel<TdColumnData> cm = new ColumnModel<TdColumnData>(l);

		gridStore = new ListStore<TdColumnData>(props.id());

//		gridStore.addStoreDataChangeHandler(new StoreDataChangeHandler<TdColumnData>() {
//
//					@Override
//					public void onDataChange(
//							StoreDataChangeEvent<TdColumnData> event) {
//						List<TdColumnData> cols = event.getSource().getAll();
//						Log.debug("Columns:" + cols.size());
//						if (columnName != null) {
//							for (TdColumnData c : cols) {
//								if (c.getName().compareTo(columnName) == 0) {
//									sm.select(c, false);
//									sm.refresh();
//									break;
//								}
//							}
//						}
//
//					}
//				});
		grid = new Grid<TdColumnData>(gridStore, cm);

		sm.setSelectionMode(SelectionMode.MULTI);
		grid.setLoader(gridLoader);
		grid.setSelectionModel(sm);
		grid.setHeight(GRID_HEIGHT);

		grid.getView().setStripeRows(true);
		grid.getView().setColumnLines(true);
		grid.getView().setAutoFill(true);
		grid.setBorders(false);
		grid.setLoadMask(true);
		grid.setColumnReordering(true);
		grid.setColumnResize(false);

		// Delete Button
		normalizeButton = new TextButton("Normalize");
		normalizeButton.setIcon(ResourceBundleTemplateOperation.INSTANCE.timeaggregate());

		normalizeButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(SelectEvent event) {
				boolean isValidForm = validateNormalizeForm();

				if (isValidForm)
					callStartNormalize();

			}
		});

		sm.addSelectionHandler(new SelectionHandler<TdColumnData>() {

			@Override
			public void onSelection(SelectionEvent<TdColumnData> event) {
				if (getSelectedItems().size() > 0) {
					// verticalFunctionsLayoutEnable(true);
					if (!errorCase)
						setEnableNormalizeButton(true);
				} else {
					// verticalFunctionsLayoutEnable(false);
					setEnableNormalizeButton(false);
				}
			}
		});

		FieldLabel columnsLabel = new FieldLabel(null, "Column/s to Normalize:");
		columnsLabel.setLabelWidth(150);

		VerticalLayoutContainer v = new VerticalLayoutContainer();
		v.setScrollMode(ScrollMode.AUTOY);
		v.setAdjustForScroll(true);
		v.add(columnsLabel, new VerticalLayoutData(1, -1, new Margins(2, 1, 5,1)));
		v.add(grid, new VerticalLayoutData(1, -1, new Margins(0)));
		
		v.add(new FieldLabel(null, "Normalize Label"), new VerticalLayoutData(1, -1, new Margins(5, 1, 2,1)));
		normalizeField = new TextField();
		normalizeField.setAllowBlank(false);
		v.add(normalizeField, new VerticalLayoutData(1,-1));
		
		v.add(new FieldLabel(null, "Quantity Label"), new VerticalLayoutData(1, -1, new Margins(5, 1, 2,1)));
		quantityField = new TextField();
		quantityField.setAllowBlank(false);
		v.add(quantityField, new VerticalLayoutData(1,-1));

		HBoxLayoutContainer hBox = new HBoxLayoutContainer();
		hBox.add(normalizeButton, new BoxLayoutData(new Margins(2, 5, 2,
				5)));
		v.add(hBox, new VerticalLayoutData(-1, -1, new Margins(10, 0, 10, 0)));

		v.add(error, new VerticalLayoutData(1, -1, new Margins(0, 1, 10, 1)));

		add(v, new VerticalLayoutData(1, -1, new Margins(0)));

	}

	protected void setEnableNormalizeButton(boolean bool) {
		normalizeButton.setEnabled(bool);
	}

	protected ArrayList<TdColumnData> getSelectedItems() {
		return new ArrayList<TdColumnData>(grid.getSelectionModel()
				.getSelectedItems());

	}
	
	protected void loadListTdColumnData(List<TdColumnData> result){
		listColumns = (ArrayList<TdColumnData>) result;
		gridStore.clear();
		gridStore.addAll(listColumns);
	}

	protected boolean validateNormalizeForm() {

		ArrayList<TdColumnData> selectedColumns = getSelectedItems();
		if (selectedColumns == null || selectedColumns.size() < 1) {
			UtilsGXT3.alert("Attention", "Attention no column selected!");
			return false;
		} else if(normalizeField.getValue()==null || normalizeField.getValue().isEmpty()){
				UtilsGXT3.alert("Attention", "Field Normalize Label is empty!");
				normalizeField.markInvalid("");
				return false;
		}else if(quantityField.getValue()==null || quantityField.getValue().isEmpty()){
			UtilsGXT3.alert("Attention", "Field Quantity Label is empty!");
			quantityField.markInvalid("");
			return false;
		}
		return true;
	}

	private void callStartNormalize() {
		GWT.log("Building NormalizeColumnsAction..");
		NormalizeColumnsAction normAction = new NormalizeColumnsAction(null, getSelectedItems(), normalizeField.getValue(), quantityField.getValue());
		GWT.log("Builded: " + normAction);
		eventBus.fireEvent(new ActionCompletedEvent(normAction));
		
		if(onClose!=null)
			onClose.execute();
		
	}
	
	public boolean isErrorCase() {
		return errorCase;
	}

	public void errorHandler(boolean error) {
		this.errorCase = error;
		setEnableNormalizeButton(!error);
	}

}
