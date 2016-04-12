package org.gcube.portlets.user.speciesdiscovery.client.detail;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesDetailsFields;
import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.client.util.Util;
import org.gcube.portlets.user.speciesdiscovery.shared.CommonName;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.ui.Image;

public class SpeciesDetailsPanel extends ContentPanel {

	private Image image = new Image(Resources.INSTANCE.getNoPictureAvailable());

	private Grid<SpeciesDetail> grid;

	private GroupingStore<SpeciesDetail> store = new GroupingStore<SpeciesDetail>();  

	public SpeciesDetailsPanel() {

		setLayout(new FlowLayout());
		setHeading("Species Details");
		setScrollMode(Scroll.AUTO);
		
		image.setTitle("Species picture");
		image.setAltText("");
		image.setSize(String.valueOf(Resources.INSTANCE.getNoPictureAvailable().getWidth()), String.valueOf(Resources.INSTANCE.getNoPictureAvailable().getHeight()));

		
		add(image);

		store.groupBy(SpeciesDetailsFields.GROUP.getId());
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();  

		ColumnConfig name = Util.createColumnConfig(SpeciesDetailsFields.NAME, 100);
		columns.add(name);
		ColumnConfig value = Util.createColumnConfig(SpeciesDetailsFields.VALUE, 100);
		columns.add(value);
		ColumnModel cm = new ColumnModel(columns); 

		GroupingView view = new GroupingView();  
		view.setShowGroupedColumn(false);  
		view.setForceFit(true);
		view.setSortingEnabled(false);
		view.setStartCollapsed(true);

		view.setGroupRenderer(new GridGroupRenderer() {  
			public String render(GroupColumnData data) { 
				String l = data.models.size() == 1 ? "Item" : "Items";  
				return data.group + " (" + data.models.size() + " " + l + ")";  
			}  
		});


		grid = new Grid<SpeciesDetail>(store, cm);  
		grid.setView(view);  
		grid.setBorders(true);
		grid.setHeight(318);

		
		add(grid);
	}

	public void setSpeciesData(ModelData data){
//		System.out.println("Setting details");
		mask("Loading details..");
		
		String scientificName = data.get(SpeciesGridFields.MATCHING_NAME.getId());
		setHeading(scientificName+" details.");
		
		
		String imageUrl = data.get(SpeciesGridFields.IMAGE.getId());
		image.setUrl(imageUrl!=null?imageUrl:Resources.INSTANCE.getNoPictureAvailable().getSafeUri().asString());
		image.setAltText("Image for "+scientificName);
		image.setTitle(scientificName);
		image.setPixelSize(Resources.INSTANCE.getNoPictureAvailable().getWidth(), Resources.INSTANCE.getNoPictureAvailable().getHeight());
		
		ArrayList<SpeciesDetail> details = new ArrayList<SpeciesDetail>();
		
		ResultRow row = (ResultRow) data.get(SpeciesGridFields.ROW.getId());
		

		for (CommonName commonName:row.getCommonNames()) {
			details.add(new SpeciesDetail(commonName.getLanguage(), commonName.getName(), SpeciesGridFields.COMMON_NAMES.getName()));
		}

		
		details.add(new SpeciesDetail(SpeciesGridFields.DATASOURCE.getName(), row.getDataSourceName(), SpeciesGridFields.PROVENANCE.getName()));
		details.add(new SpeciesDetail(SpeciesGridFields.DATAPROVIDER.getName(), row.getDataProviderName(), SpeciesGridFields.PROVENANCE.getName()));
		details.add(new SpeciesDetail(SpeciesGridFields.DATASET.getName(), row.getDataSetName(), SpeciesGridFields.PROVENANCE.getName()));
		details.add(new SpeciesDetail(SpeciesGridFields.DATASET_CITATION.getName(), row.getDataSetCitation(), SpeciesGridFields.PROVENANCE.getName()));
		
//		details.add(new SpeciesDetail(SpeciesGridFields.PRODUCT_IMAGES.getName(), String.valueOf(row.getImagesCount()), SpeciesGridFields.PRODUCTS.getName()));
//		details.add(new SpeciesDetail(SpeciesGridFields.PRODUCT_MAPS.getName(), String.valueOf(row.getMapsCount()), SpeciesGridFields.PRODUCTS.getName()));
//		details.add(new SpeciesDetail(SpeciesGridFields.PRODUCT_LAYERS.getName(), String.valueOf(row.getLayersCount()), SpeciesGridFields.PRODUCTS.getName()));
		details.add(new SpeciesDetail(SpeciesGridFields.PRODUCT_OCCURRENCES.getName(), String.valueOf(row.getOccurencesCount()), SpeciesGridFields.PRODUCTS.getName()));
		
		store.removeAll();
		store.add(details);
//		System.out.println("Details added "+details.size());
		//grid.getView().refresh(false);
		unmask();
		//Log.debug("store contains : "+store.getModels().size());
	}


}
