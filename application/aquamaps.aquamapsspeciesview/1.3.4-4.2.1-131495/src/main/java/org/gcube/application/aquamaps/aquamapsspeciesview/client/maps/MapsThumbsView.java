package org.gcube.application.aquamaps.aquamapsspeciesview.client.maps;

import java.util.Date;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.Reloadable;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.constants.AquaMapsSpeciesViewConstants;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.data.CompoundMapItem;

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
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;

public class MapsThumbsView extends ContentPanel implements Reloadable{

	private ListView<ModelData> view;  
	private MapsDetailsPanel details=new MapsDetailsPanel();  
	private XTemplate detailTp;  

	private static final String NAME="NAME";
	private static final String ALGORITHM="ALGORITHM";
	private static final String DATE="DATE";
	
	
	public MapsThumbsView(ListStore<ModelData> store,int pageSize) {
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
				model.set(NAME, Format.ellipse((String)model.get(CompoundMapItem.TITLE), 15));  
				model.set(DATE, Format.ellipse(AquaMapsSpeciesViewConstants.timeFormat.format(new Date(Long.parseLong((String)model.get(CompoundMapItem.CREATION_DATE)))),15));
				model.set(ALGORITHM, Format.ellipse((String)model.get(CompoundMapItem.ALGORITHM), 15));
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

	  
		 view.getSelectionModel().addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<ModelData>>() {  
		      public void handleEvent(SelectionChangedEvent<ModelData> be) {  
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
	    listViewToolBar.enable();
		
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
	
	 private void onSelectionChange(SelectionChangedEvent<ModelData> se) {  
		    if (se.getSelection().size() > 0) {		    	
		    	details.show();
		    	details.setData(new CompoundMapItem(se.getSelectedItem().getProperties()));
		    } else {		    	
		      details.hide();  
		    }  
		  }  
	
	private native String getTemplate() /*-{ 
    return ['<tpl for=".">', 
    '<div class="thumb-wrap" id="{creation_date}" style="border: 1px solid white">', 
    '<div class="thumb"><img src="{thumbnail}" title="{title}"></div>', 
   	'<span class="x-editable">{NAME}</span>',
   	'<span class="x-editable">{ALGORITHM}</span>',
   	'<span class="x-editable">{DATE}</span>',
   	'</div>',
    '</tpl>',
    '<div class="x-clear"></div>'].join(""); 
    }-*/;  

	
}