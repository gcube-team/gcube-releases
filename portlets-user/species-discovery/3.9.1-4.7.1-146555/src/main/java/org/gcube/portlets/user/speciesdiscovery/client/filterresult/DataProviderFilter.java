package org.gcube.portlets.user.speciesdiscovery.client.filterresult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.speciesdiscovery.client.ConstantsSpeciesDiscovery;
import org.gcube.portlets.user.speciesdiscovery.client.event.ActiveFilterOnResultEvent;
import org.gcube.portlets.user.speciesdiscovery.client.event.UpdateFilterOnResultEvent;
import org.gcube.portlets.user.speciesdiscovery.client.resources.Resources;
import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.shared.filter.ResultFilter;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class DataProviderFilter implements ResultFilterPanelInterface{

	private static final String SORT_RESULTS = "Sort results";
	private ContentPanel dataProvPanel = new ContentPanel();
	private VerticalPanel vp = new VerticalPanel();
	private HashMap<String, Button> hashButton = new HashMap<String, Button>();
	private EventBus eventBus;
	
	
	public DataProviderFilter(){
		setHeaderTitle();
		vp.setStyleAttribute("margin-left", DEFAULTMARGINLEFT);
		dataProvPanel.setScrollMode(Scroll.AUTO);
		dataProvPanel.add(vp);
		createToolBar();
	}
	
	@Override
	public ContentPanel getPanel() {
		return dataProvPanel;
	}

	@Override
	public String getName() {
		return ResultFilterPanelEnum.DATAPROVIDER.getLabel();
	}

	@Override
	public void setHeaderTitle() {
		dataProvPanel.setHeading(this.getName());
		
	}
	
	public void createToolBar(){
		
		ToolBar toolbar = new ToolBar();
		
		Button buttonSortResult = new Button(SORT_RESULTS);	
		buttonSortResult.setStyleName("button-noimage");
		buttonSortResult.setWidth(100);

		buttonSortResult.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getSortIcon()));
		buttonSortResult.setToolTip(ConstantsSpeciesDiscovery.SORTMESSAGE);
		buttonSortResult.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				reloadDataSort();
			}
		});
		
		toolbar.add(buttonSortResult);
		toolbar.add(new FillToolItem());
		
		Button buttonReload = new Button("");	
		buttonReload.setStyleName("button-noimage");
		buttonReload.setIcon(AbstractImagePrototype.create(Resources.INSTANCE.getReload()));
		buttonReload.setToolTip("Reload filter");
		buttonReload.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				reset();
				eventBus.fireEvent(new UpdateFilterOnResultEvent(SpeciesGridFields.DATAPROVIDER));
			}
		});
		
		toolbar.add(buttonReload);

		
		toolbar.setStyleName("toolbar-filter");
		
		dataProvPanel.setTopComponent(toolbar);
		
	}
	
	public void setEventBus(EventBus eventBus){
		this.eventBus = eventBus;
	}
	

	public void loadDataSource(HashMap<String, Integer> result) {
		
		for(final String key: result.keySet()){
			
			Button butt = hashButton.get(key);
			final Integer counter = result.get(key);
			
			if(butt!=null){
				
				String oldText = butt.getText();
				
				int start = oldText.indexOf("(");
				String description = oldText.substring(0,start);
				butt.setText(description+" ("+counter+")");
				butt.setData("counter", counter);
				
			}
			else{
			
				butt = new Button(key +" ("+counter+")");
				butt.setStyleAttribute("margin", DEFAULTMARGIN);
				butt.setData("counter", counter);
				butt.addSelectionListener(new SelectionListener<ButtonEvent>() {
					
					@Override
					public void componentSelected(ButtonEvent ce) {
						ResultFilter activeFilter = new ResultFilter();
						activeFilter.setByDataProvider(true);
						Integer countValue =  (Integer) ce.getButton().getData("counter");
						activeFilter.setDataProviderName(key, countValue.intValue());
						activeFilter.setFilterValue(key);
						eventBus.fireEvent(new ActiveFilterOnResultEvent(activeFilter));
						
					}
				});

				butt.setStyleName("button-hyperlink");
				butt.setId(key);
				
				hashButton.put(key, butt);
				vp.add(butt);
			
			}
		}
		
		vp.layout();
	}

	public void reset() {
		hashButton.clear();
		vp.removeAll();
		
	}

	public void reloadDataSort(){
		
		vp.removeAll();
		
		if(!hashButton.isEmpty()){
		
			List<String> listKey = new ArrayList<String>(hashButton.keySet());
			Collections.sort(listKey);
			
			for (String key : listKey) 
				vp.add(hashButton.get(key));
		}
		
		vp.layout(true);
	}

}
