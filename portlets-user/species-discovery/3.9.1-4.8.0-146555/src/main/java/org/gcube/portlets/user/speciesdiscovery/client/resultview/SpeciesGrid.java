package org.gcube.portlets.user.speciesdiscovery.client.resultview;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.UpdateRowSelectionEvent;
import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.client.util.TaxonomyGridField;
import org.gcube.portlets.user.speciesdiscovery.client.util.Util;
import org.gcube.portlets.user.speciesdiscovery.client.view.SpeciesViewInterface;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.AggregationRowConfig;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.extjs.gxt.ui.client.widget.grid.SummaryType;
import com.extjs.gxt.ui.client.widget.layout.AnchorData;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.NumberFormat;

public class SpeciesGrid extends ContentPanel implements SpeciesViewInterface {

	public enum ColumnModelType{
		SCIENTIFIC,EXPANDED 
	}

	private EditorGrid<ModelData> grid;
	private EventBus eventBus;
	
	private boolean isMouseDown = false;
	private List<ColumnConfig> columns;
	private ColumnConfig columnProductOccurences;
	private final CheckBoxSelectionModel<ModelData> sm;

	public SpeciesGrid(ListStore<ModelData> store, final EventBus eventBus) {
		this.eventBus = eventBus;
		
		setLayout(new AnchorLayout());
		setHeaderVisible(false);

		columns = new ArrayList<ColumnConfig>();  

	    sm = new CheckBoxSelectionModel<ModelData>(){

	    	  @Override
	    	  protected void handleMouseDown(GridEvent<ModelData> e){
	       		  isMouseDown = true;
//	       		  Log.trace("on handleMouseDown");
	    		  super.handleMouseDown(e);
//	    		  Window.alert("mouse down");
	    	  }
	    	  
	    	  @Override
	    	  protected void onHeaderClick(GridEvent<ModelData> e){
	    		  isMouseDown = true;
//	    		  Log.trace("on header click");
	    		  super.onHeaderClick(e);
	    	  }

	    	protected void onAdd(List<? extends ModelData> models){
	    		super.onAdd(models);

	    		for (ModelData md : models) {
	    			if(md.get(SpeciesGridFields.ROW.getId()) instanceof ResultRow){
	    				
		    			ResultRow rs = (ResultRow) md.get(SpeciesGridFields.ROW.getId());
		    			if(rs.isSelected()){
		    				isMouseDown = false;
		    				select(md, true);
		    			}
	    			}
				}
	    		
	    	}
	       	
	      	@Override
	      	protected  void onSelectChange(ModelData model, boolean select){
	      		super.onSelectChange(model, select);
//	      		 Log.trace("onSelectChange");
	      		if(isMouseDown){
//	         		 Window.alert("onSelectChange");
		      		int rowId = ((ResultRow) model.get(SpeciesGridFields.ROW.getId())).getId();
		    		eventBus.fireEvent(new UpdateRowSelectionEvent(rowId, select));
	      		}
	      	}
     
	    };  

		columns.add(sm.getColumn()); 

		ColumnConfig dataSource = Util.createColumnConfig(SpeciesGridFields.DATASOURCE, 120); 
		columns.add(dataSource);

		ColumnConfig dataProvider = Util.createColumnConfig(SpeciesGridFields.DATAPROVIDER, 310);
		dataProvider.setHidden(true);
		columns.add(dataProvider);

		ColumnConfig dataSet = Util.createColumnConfig(SpeciesGridFields.DATASET, 200);  
		columns.add(dataSet);

		ColumnConfig dataSetCitation = Util.createColumnConfig(SpeciesGridFields.DATASET_CITATION, 360);
		dataSetCitation.setHidden(true);
		columns.add(dataSetCitation);

		ColumnConfig matchingName = Util.createColumnConfig(SpeciesGridFields.MATCHING_NAME, 180);
		columns.add(matchingName);
		
		ColumnConfig author = Util.createColumnConfig(TaxonomyGridField.SCIENTIFICNAMEAUTHORSHIP, 150);
		columns.add(author);

		ColumnConfig accordingTo = Util.createColumnConfig(SpeciesGridFields.MATCHING_AUTHOR, 200);
		columns.add(accordingTo);

		ColumnConfig matchingCategory = Util.createColumnConfig(SpeciesGridFields.MATCHING_RANK, 80);
		columns.add(matchingCategory);

		ColumnConfig matchingCredits = Util.createColumnConfig(SpeciesGridFields.MATCHING_CREDITS, 80);
		matchingCredits.setHidden(true);
		columns.add(matchingCredits);

		
		columnProductOccurences = Util.createColumnConfig(SpeciesGridFields.PRODUCT_OCCURRENCES, 90);
//		productOccurences.setHidden(true);
		columns.add(columnProductOccurences);

		final ColumnModel classicColumnModel = new ColumnModel(columns);

		classicColumnModel.addHeaderGroup(0, 1, new HeaderGroupConfig(SpeciesGridFields.PROVENANCE.getName(), 1, 3));  
		classicColumnModel.addHeaderGroup(0, 5, new HeaderGroupConfig("Matching", 1, 4));
		classicColumnModel.addHeaderGroup(0, 9, new HeaderGroupConfig(SpeciesGridFields.PRODUCTS.getName(), 1, 4));

		AggregationRowConfig<ModelData> counts = new AggregationRowConfig<ModelData>();
		counts.setHtml(SpeciesGridFields.DATASOURCE.getId(), "Count");  

		counts.setSummaryType(SpeciesGridFields.PRODUCT_OCCURRENCES.getId(), SummaryType.SUM);  
		counts.setSummaryFormat(SpeciesGridFields.PRODUCT_OCCURRENCES.getId(), NumberFormat.getDecimalFormat()); 

		classicColumnModel.addAggregationRow(counts);

		grid = new EditorGrid<ModelData>(store, classicColumnModel);
		grid.setLoadMask(true);  
		grid.setBorders(true);  
		grid.setStripeRows(true);
//		grid.getView().setForceFit(true);
		grid.getView().setAutoFill(true);
		grid.setClicksToEdit(ClicksToEdit.ONE);
		grid.getView().setEmptyText(ConstantsSpeciesDiscovery.NORESULTS);
	    grid.setSelectionModel(sm); 
		grid.addPlugin(sm);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SIMPLE);

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
		//FIXME for (ModelData selected: selectionModel.getSelectedItems()) selectedRows.add((ResultRow) selected.get(SpeciesGridFields.ROW.getId()));
		return selectedRows;
	}
	
	@Override
	public void setBodyStyleAsFiltered(boolean isFiltered) {

		if (this.getElement("body") != null) {

			if (isFiltered) {
				this.getElement("body").getStyle().setBorderColor("#32CD32");
			} else
				this.getElement("body").getStyle().setBorderColor("#99BBE8");

		}
	}

	public void setOccurencesVisible(boolean b) {
		
		ColumnConfig productOccurences = grid.getColumnModel().getColumnById(SpeciesGridFields.PRODUCT_OCCURRENCES.getId());
		
		grid.getColumnModel().setHidden(grid.getColumnModel().getColumnCount()-1, !b); //TODO Temporary
	}
	
	@Override
	public ContentPanel getPanel() {
		return this;
	}
	
	public void selectAll(){
		sm.selectAll();
	}
	
	public void deselectAll(){
		sm.deselectAll();
	}

}
