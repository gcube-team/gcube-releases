package org.gcube.application.aquamaps.aquamapsspeciesview.client.maps;

import java.util.ArrayList;
import java.util.List;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.ExtendedLiveGridView;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.PortletCommon;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.Reloadable;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.AquaMapsSpeciesViewConstants;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.Tags;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.AnchorData;
import com.extjs.gxt.ui.client.widget.layout.AnchorLayout;
import com.extjs.gxt.ui.client.widget.toolbar.LiveToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class DescriptiveMapsView extends ContentPanel implements Reloadable{

	ExtendedLiveGridView liveGridView = new ExtendedLiveGridView(); 

	private Grid<ModelData> grid;

	public DescriptiveMapsView(ListStore<ModelData> store) {
		setLayout(new AnchorLayout());
		setHeaderVisible(false);
		setHeight(300);
		List<ColumnConfig> descriptiveColumns = new ArrayList<ColumnConfig>();  

		ColumnConfig descriptiveName = new ColumnConfig(SpeciesFields.scientific_name+"", "Names", 100);  
		descriptiveColumns.add(descriptiveName);
		descriptiveName.setRenderer(new GridCellRenderer<ModelData>() {
			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				String scientific=(String)model.get(SpeciesFields.scientific_name+"");
				if(scientific==null||scientific.length()==0) scientific=model.get(SpeciesFields.genus+"")+" "+model.get(SpeciesFields.species+"");
				return "<p><b style=\"color: #385F95;\">"+scientific+"</b><br/>" +
				"aka : "+
				"<table>"+
				"<tr><td><b>FishBase name:</b></td><td>"+model.get(SpeciesFields.fbname+"")+"</td></tr>"+
				"<tr><td><b>English:</b></td><td>"+model.get(SpeciesFields.english_name+"")+"</td></tr>"+
				"<tr><td><b>French:</b></td><td>"+model.get(SpeciesFields.french_name+"")+"</td></tr>"+
				"<tr><td><b>Spanish:</b></td><td>"+model.get(SpeciesFields.english_name+"")+"</td></tr>"+
				"<tr><td><b>English:</b></td><td>"+model.get(SpeciesFields.english_name+"")+"</td></tr>"+
				"<tr></tr>"+
				"<tr><td><b>Inserted by: </b></td><td>"+model.get(SpeciesFields.authname+"")+"</td></tr>"+
				"</table></p>";
			}
		});

		ColumnConfig descriptivePicture = new ColumnConfig(SpeciesFields.picname+"", "Picture", 100);  
		descriptiveColumns.add(descriptivePicture);
		descriptivePicture.setRenderer(new GridCellRenderer<ModelData>() {
			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				return "<div text-align:center;><IMG SRC=\""+AquaMapsSpeciesViewConstants.servletUrl.get(Tags.imageServlet)+"?"+Tags.PIC_NAME+"=tn_"+model.get(SpeciesFields.picname+"")+"\" width=\"130\" height=\"130\"></div>";
			}
		});


		ColumnConfig descriptiveTaxonomy = new ColumnConfig(SpeciesFields.kingdom+"", "Taxonomy", 100);  
		descriptiveColumns.add(descriptiveTaxonomy);
		descriptiveTaxonomy.setRenderer(new GridCellRenderer<ModelData>() {
			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				return "<p><table><tr><td><b>Kingdom:</b></td><td>"+model.get(SpeciesFields.kingdom+"")+"</td></tr>" +
				"<tr><td><b>Phylum:</b></td><td>"+model.get(SpeciesFields.phylum+"")+"</td></tr>"+
				"<tr><td><b>Class:</b></td><td>"+model.get(SpeciesFields.classcolumn+"")+"</td></tr>"+
				"<tr><td><b>Order:</b></td><td>"+model.get(SpeciesFields.ordercolumn+"")+"</td></tr>"+
				"<tr><td><b>Family:</b></td><td>"+model.get(SpeciesFields.familycolumn+"")+"</td></tr>"+
				"</table></p>";
			}
		});

		ColumnConfig descriptiveCharacteristics = new ColumnConfig(SpeciesFields.m_mammals+"", "Characteristics", 100);  
		descriptiveColumns.add(descriptiveCharacteristics);
		descriptiveCharacteristics.setRenderer(new GridCellRenderer<ModelData>() {
			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {

				return "<p><table><tr><td><b>Mammal:</b></td><td>"+PortletCommon.evaluateBoolean(model.get(SpeciesFields.m_mammals+""))+"</td></tr>" +
				"<tr><td><b>Deepwater:</b></td><td>"+PortletCommon.evaluateBoolean(model.get(SpeciesFields.deepwater+""))+"</td></tr>"+
				"<tr><td><b>Invertebrate:</b></td><td>"+PortletCommon.evaluateBoolean(model.get(SpeciesFields.m_invertebrates+""))+"</td></tr>"+
				"<tr><td><b>Algae:</b></td><td>"+PortletCommon.evaluateBoolean(model.get(SpeciesFields.algae+""))+"</td></tr>"+
				"<tr><td><b>Seabird:</b></td><td>"+PortletCommon.evaluateBoolean(model.get(SpeciesFields.seabirds+""))+"</td></tr>"+
				"<tr><td><b>Freshwater:</b></td><td>"+PortletCommon.evaluateBoolean(model.get(SpeciesFields.freshwater+""))+"</td></tr>"+						
				"</table></p>";
			}
		});


		ColumnConfig descriptiveRelevance = new ColumnConfig(SpeciesFields.angling+"", "Relevance", 100);  
		descriptiveColumns.add(descriptiveRelevance);
		descriptiveRelevance.setRenderer(new GridCellRenderer<ModelData>() {
			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				
				return "<p><table><tr><td><b>Angling:</b></td><td>"+PortletCommon.evaluateBoolean(model.get(SpeciesFields.angling+""))+"</td></tr>" +
				"<tr><td><b>Diving:</b></td><td>"+PortletCommon.evaluateBoolean(model.get(SpeciesFields.diving+""))+"</td></tr>"+
				"<tr><td><b>Dangerous:</b></td><td>"+PortletCommon.evaluateBoolean(model.get(SpeciesFields.dangerous+""))+"</td></tr>"+
				"</table></p>";
			}
		});



		final ColumnModel descriptiveColumnModel = new ColumnModel(descriptiveColumns);

		grid=new Grid<ModelData>(store,descriptiveColumnModel);
		grid.setLoadMask(true);  
		grid.setBorders(true);  
		grid.setStripeRows(true);
		grid.setAutoExpandColumn(SpeciesFields.scientific_name+"");  
		grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);  

		liveGridView.setRowHeight(160);
		grid.setView(liveGridView);



		add(grid,new AnchorData("100% 100%"));
		ToolBar gridBottomToolbar=new ToolBar();
		LiveToolItem item = new LiveToolItem();
		item.bindGrid(grid);
		gridBottomToolbar.add(item); 
		setBottomComponent(gridBottomToolbar);
		Log.debug("LiveToolbar id is "+gridBottomToolbar.getId());
	}
	@Override
	protected void onShow() {		
		super.onShow();
		reload();
	}

	public void reload(){
		grid.getStore().getLoader().load();
	}
//	@Override
//	public void bindToSelection(final Button toBind) {
//		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
//			
//			@Override
//			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
//				if(se.getSelection().size()>0)toBind.enable();
//				else toBind.disable();
//			}
//		});		
//	}
//	@Override
//	public List<ModelData> getSelection() {
//		return grid.getSelectionModel().getSelectedItems(); 
//	}
	
}
