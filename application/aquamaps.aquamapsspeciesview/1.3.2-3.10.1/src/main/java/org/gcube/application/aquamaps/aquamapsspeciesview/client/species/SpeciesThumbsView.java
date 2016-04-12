package org.gcube.application.aquamaps.aquamapsspeciesview.client.species;

import java.util.List;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.AquaMapsSpeciesViewConstants;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.Tags;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;

public class SpeciesThumbsView extends ContentPanel implements SpeciesView{

	private ListView<ModelData> view;  
	private SpeciesDetailsPanel details=new SpeciesDetailsPanel();  
	private XTemplate detailTp;  

	private static final String NAME="NAME";
	public static final String PATH="PATH";

	
	
	public SpeciesThumbsView(ListStore<ModelData> store,int pageSize) {
		this.setBorders(true);
		this.setHeight(400);
		this.setWidth(500);
		this.setBodyBorder(false);  
		this.setLayout(new BorderLayout());  
		this.setHeaderVisible(false);	    
		this.setBodyBorder(false); 

		ContentPanel viewPanel=new ContentPanel(new FitLayout());
		viewPanel.setHeaderVisible(false);
		viewPanel.setHeight(400);
		viewPanel.setWidth(500);

		view=new ListView<ModelData>(store);
		



		view = new ListView<ModelData>() {  
			@Override  
			protected ModelData prepareData(ModelData model) { 
				String scientific=(String)model.get(SpeciesFields.scientific_name+"");
				if(scientific==null||scientific.length()==0) scientific=model.get(SpeciesFields.genus+"")+" "+model.get(SpeciesFields.species+"");
				model.set(NAME, Format.ellipse(scientific, 15));  
				model.set(PATH, AquaMapsSpeciesViewConstants.servletUrl.get(Tags.imageServlet)+"?"+Tags.PIC_NAME+"=tn_"+model.get(SpeciesFields.picname+""));  
				return model;  
			}  
		};  
		view.setId("img-chooser-view");  


		//	    view.setTemplate(XTemplate.create("<tpl for=\".\">" +
		//				"<div class=\"thumb-wrap\" id=\"{"+SpeciesFields.speciesid+"}\">"+
		//				"<IMG SRC=\""+AquaMapsSpeciesViewConstants.servletUrl.get(Tags.imageServlet)+"?"+
		//						Tags.PIC_NAME+"=tn_{"+SpeciesFields.picname+"}\" TITLE=\"{"+SpeciesFields.scientific_name+"}\">" +
		//								"<span class=\"x-editable\">{"+SpeciesFields.scientific_name+"}</span></div>"+
		//				"</tpl>"));

		view.setTemplate(getTemplate());

		view.setBorders(false);  
		view.setStore(store);  
		view.setItemSelector("div.thumb-wrap");
		view.setLoadingText("Retrieving data from server..");
		view.setOverStyle("");


		view.getSelectionModel().setSelectionMode(SelectionMode.MULTI);  

		//	    view.getSelectionModel().addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<BeanModel>>() {  
		//	      public void handleEvent(SelectionChangedEvent<BeanModel> be) {  
		//	        onSelectionChange(be);  
		//	      }  
		//	    });  
		 view.getSelectionModel().addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<BeanModel>>() {  
		      public void handleEvent(SelectionChangedEvent<BeanModel> be) {  
		        onSelectionChange(be);  
		      }  
		    });  
		viewPanel.add(view);


		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(0));  
	  
	    BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 350);  
	    eastData.setSplit(false);  
	    eastData.setCollapsible(true);  
	    eastData.setMargins(new Margins(0,0,0,5));  
	    
	    PagingToolBar listViewToolBar=new PagingToolBar(pageSize);
	    
	    listViewToolBar.bind((PagingLoader<PagingLoadResult<ModelData>>) store.getLoader());

		
		setBottomComponent(listViewToolBar);
	    
	    
		this.add(viewPanel,centerData);
		this.add(details,eastData);
		details.hide();
	}
	@Override
	protected void onShow() {		
		super.onShow();
		reload();
	}
	public void reload(){
		view.getStore().getLoader().load();
	}
	
	 private void onSelectionChange(SelectionChangedEvent<BeanModel> se) {  
		    if (se.getSelection().size() > 0) {		    	
		    	details.show();
		    	details.setSpeciesData(se.getSelectedItem());
		    } else {		    	
		      details.hide();  
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
	
	@Override
	public void bindToSelection(final Button toBind) {
		view.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				if(se.getSelection().size()>0)toBind.enable();
				else toBind.disable();
			}
		});
	}
	@Override
	public List<ModelData> getSelection() {
		return view.getSelectionModel().getSelectedItems();
	}  

	
}