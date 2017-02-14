package org.gcube.portlets.user.speciesdiscovery.client.resultview;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.UpdateRowSelectionEvent;
import org.gcube.portlets.user.speciesdiscovery.client.util.TaxonomyGridField;
import org.gcube.portlets.user.speciesdiscovery.client.util.Util;
import org.gcube.portlets.user.speciesdiscovery.client.view.SpeciesViewInterface;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.extjs.gxt.ui.client.widget.layout.AnchorData;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.google.gwt.event.shared.EventBus;

public class TaxonomyGrid extends ContentPanel implements SpeciesViewInterface {

	private EditorGrid<ModelData> grid;
	private EventBus eventBus;
	
	private boolean isMouseDown = false;
	private List<ColumnConfig> columns;
	
	private CheckBoxSelectionModel<ModelData> sm;

	public TaxonomyGrid(ListStore<ModelData> store, final EventBus eventBus) {
		this.eventBus = eventBus;
		
		setLayout(new AnchorLayout());
		setHeaderVisible(false);

		columns = new ArrayList<ColumnConfig>();  

	    sm = new CheckBoxSelectionModel<ModelData>(){
	    	
	    	  @Override
	    	  protected void handleMouseDown(GridEvent<ModelData> e){
	    		  
	       		  isMouseDown = true;
	    		  super.handleMouseDown(e);
	    	  }
	    	  
	    	  @Override
	    	  protected void onHeaderClick(GridEvent<ModelData> e){
	    		  isMouseDown = true;
	    		  super.onHeaderClick(e);
	    	  }

	    	protected void onAdd(List<? extends ModelData> models){
	    		super.onAdd(models);

	    		for (ModelData md : models) {
	    			
		    			if(md.get(TaxonomyGridField.ROW.getId()) instanceof TaxonomyRow){
		    			
		    			TaxonomyRow rs = (TaxonomyRow) md.get(TaxonomyGridField.ROW.getId());
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

	      		if(isMouseDown){
		      		int rowId = ((TaxonomyRow) model.get(TaxonomyGridField.ROW.getId())).getId();
		    		eventBus.fireEvent(new UpdateRowSelectionEvent(rowId, select));
	      		}
	      	}
     
	    };  
	    
		columns.add(sm.getColumn()); 

		
		ColumnConfig matchingName = Util.createColumnConfig(TaxonomyGridField.SCIENTIFIC_NAME, 180);
		columns.add(matchingName);
		
		ColumnConfig author = Util.createColumnConfig(TaxonomyGridField.SCIENTIFICNAMEAUTHORSHIP, 150);
		columns.add(author);
		
		ColumnConfig dataProvider = Util.createColumnConfig(TaxonomyGridField.DATASOURCE, 250);
		columns.add(dataProvider);

		ColumnConfig dataSetCitation = Util.createColumnConfig(TaxonomyGridField.CITATION, 180);
		columns.add(dataSetCitation);

		ColumnConfig rank = Util.createColumnConfig(TaxonomyGridField.MATCHING_RANK, 80);
		columns.add(rank);
		
		ColumnConfig status = Util.createColumnConfig(TaxonomyGridField.STATUSREFNAME, 60);
		columns.add(status);
		
		ColumnConfig statusRemarks = Util.createColumnConfig(TaxonomyGridField.STATUS_REMARKS, 90);
		columns.add(statusRemarks);

		final ColumnModel classicColumnModel = new ColumnModel(columns);

		classicColumnModel.addHeaderGroup(0, 1, new HeaderGroupConfig(TaxonomyGridField.PROVENANCE.getName(), 1, 3));  


		grid = new EditorGrid<ModelData>(store, classicColumnModel);
		grid.setLoadMask(true);
		grid.setBorders(true);  
		grid.setStripeRows(true);
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
