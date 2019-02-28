package org.gcube.portlets.user.speciesdiscovery.client.view;

import java.util.LinkedList;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.detail.SpeciesDetailsPanel;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class SpeciesThumbsView extends ContentPanel implements SpeciesViewInterface {

	private ListView<ModelData> view;  
	private SpeciesDetailsPanel detailsPanel;  
	private XTemplate detailTp;  

	private static final String NAME = "NAME";
	public static final String PATH = "PATH";

	public SpeciesThumbsView(ListStore<ModelData> store, int pageSize) {
		setBorders(true);
		setBodyBorder(false);  
		setLayout(new BorderLayout());  
		this.setHeight(400);
		this.setWidth(500);
		
		final BorderLayout layout = new BorderLayout();  
		setLayout(layout);  
		setHeaderVisible(false);

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
		centerData.setMargins(new Margins(0));		

		ContentPanel viewPanel = new ContentPanel(new FitLayout());
		viewPanel.setHeaderVisible(false);
		viewPanel.setHeight(400);
		viewPanel.setWidth(500);
		viewPanel.setId("images-view");

		view = new ListView<ModelData>(store) {  
			@Override  
			protected ModelData prepareData(ModelData model) { 
				String scientificName = model.get(SpeciesGridFields.MATCHING_NAME.getId());

				model.set(NAME, Format.ellipse(scientificName, 15));  

				String imageUrl = model.get(SpeciesGridFields.IMAGE.getId());
				model.set(PATH, imageUrl!=null?imageUrl:Resources.INSTANCE.getNoPictureAvailable().getSafeUri().asString());  
				return model;  
			}  
		};  
		view.setId("img-chooser-view");  


		view.setTemplate(getTemplate());

		view.setBorders(false);  
		view.setStore(store);  
		view.setItemSelector("div.thumb-wrap");
		view.setLoadingText("Retrieving data from server..");
		view.setOverStyle("");

		view.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);  
		view.getSelectionModel().addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<BeanModel>>() {  
			public void handleEvent(SelectionChangedEvent<BeanModel> be) {  
				onSelectionChange(be);  
			}  
		});
		viewPanel.add(view);
		
		add(viewPanel, centerData);


		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 350);  
		eastData.setSplit(false);  
		eastData.setCollapsible(true);  

		detailsPanel = new SpeciesDetailsPanel();

		add(detailsPanel, eastData);
		
		//FIXME detailsPanel.hide();
	}
	@Override
	protected void onShow() {		
		super.onShow();
		reload();
	}
	public void reload(){
		//view.getStore().getLoader().load();
	}

	private void onSelectionChange(SelectionChangedEvent<BeanModel> se) {
//		System.out.println("Selection changed");
		if (se.getSelection().size() > 0) {    	
			detailsPanel.show();
			detailsPanel.setSpeciesData(se.getSelectedItem());
		} else {		    	
			detailsPanel.hide();  
		}  
	}  

	private native String getTemplate() /*-{ 
    return ['<tpl for=".">', 
    '<div class="thumb-wrap" id="{speciesid}" style="border: 1px solid white">', 
    '<div class="thumb"><img src="{PATH}" title="{scientific_name}"></div>', 
   	'<span class="x-editable">{NAME}</span></div>',
    '</tpl>',
    '<div class="x-clear"></div>'].join(""); 

    }-*/;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ResultRow> getSelectedRows() {
		List<ResultRow> selectedRows = new LinkedList<ResultRow>();
		for (ModelData selected: view.getSelectionModel().getSelectedItems()) selectedRows.add((ResultRow) selected.get(SpeciesGridFields.ROW.getId()));
		return selectedRows;
	}
	

	@Override
	public void setBodyStyleAsFiltered(boolean isFiltered) {
		
		if(this.getElement("body")!=null){
			
			if(isFiltered){
				this.getElement("body").getStyle().setBorderColor("#32CD32");
			}
			else
				this.getElement("body").getStyle().setBorderColor("#99BBE8");
		
		}
		
	}
	@Override
	public ContentPanel getPanel() {
		return this;
	}


}