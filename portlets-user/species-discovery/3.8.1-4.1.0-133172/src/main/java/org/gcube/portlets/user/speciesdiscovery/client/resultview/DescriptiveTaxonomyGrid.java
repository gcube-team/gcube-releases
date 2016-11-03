package org.gcube.portlets.user.speciesdiscovery.client.resultview;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.util.JavascriptInjector;
import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.client.util.TaxonomyGridField;
import org.gcube.portlets.user.speciesdiscovery.client.util.Util;
import org.gcube.portlets.user.speciesdiscovery.client.view.SpeciesViewInterface;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.AnchorData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class DescriptiveTaxonomyGrid extends ContentPanel implements SpeciesViewInterface {

	protected static final String TOGGLE_CLASS = "SPECIES_TOGGLE";

	private Grid<ModelData> grid;

	public DescriptiveTaxonomyGrid(ListStore<ModelData> store) {

		setLayout(new FitLayout());
		setHeaderVisible(false);

		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
		
		ColumnConfig descriptiveName = Util.createColumnConfig(TaxonomyGridField.SCIENTIFIC_NAME, 250);  
		columns.add(descriptiveName);

		JavascriptInjector.inject(Resources.INSTANCE.getToggleJavaScript().getText());

		ColumnConfig descriptiveTaxonomy = Util.createColumnConfig(TaxonomyGridField.TAXONOMY, 350);  
		columns.add(descriptiveTaxonomy);

		ColumnConfig datasourceColumn = Util.createColumnConfig(TaxonomyGridField.PROVENANCE,  350);  
		columns.add(datasourceColumn);
		
		ColumnConfig productsColumn = Util.createColumnConfig(TaxonomyGridField.PRODUCTS,  350);  
		columns.add(productsColumn);

		final ColumnModel descriptiveColumnModel = new ColumnModel(columns);

		grid = new Grid<ModelData>(store,descriptiveColumnModel);
		grid.setBorders(true);  
		grid.setStripeRows(true);

		add(grid, new AnchorData("100% 100%"));

	}
	@Override
	protected void onShow() {		
		super.onShow();
		reload();
	}

	public void reload(){
		//grid.getStore().getLoader().load();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ResultRow> getSelectedRows() {
		List<ResultRow> selectedRows = new LinkedList<ResultRow>();
		for (ModelData selected: grid.getSelectionModel().getSelectedItems()) selectedRows.add((ResultRow) selected.get(SpeciesGridFields.ROW.getId()));
		return selectedRows;
	}
	
	public void setBodyStyleAsFiltered(boolean isFiltered){
		
		if (this.getElement("body") != null) {

			if (isFiltered) {
				this.getElement("body").getStyle().setBorderColor("#32CD32");
			} else
				this.getElement("body").getStyle().setBorderColor("#99BBE8");

		}
	}
	
	@Override
	public ContentPanel getPanel() {
		return this;
	}

}
