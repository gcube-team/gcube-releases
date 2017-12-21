package org.gcube.application.datamanagementfacilityportlet.client.forms;

import java.util.List;

import org.gcube.application.datamanagementfacilityportlet.client.DataManagementFacilityConstants;
import org.gcube.application.datamanagementfacilityportlet.client.resources.Resources;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.ClientTinyResource;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientResourceType;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class MultiSourceSelector extends ContentPanel {

	
	private TinyResourceGrid grid=new TinyResourceGrid();
	private ResourcePickerComboBox combo;
	private Button add;
	private Button remove;
	
	public MultiSourceSelector(ClientResourceType type,String title) {
		setLayout(new FitLayout());
		setHeading(title);
		combo=new ResourcePickerComboBox(type);
		combo.setHideLabel(true);
		combo.setWidth(Common.defaultResourcePickupWidth);
		combo.addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				if(se.getSelection().size()>0){
					ClientTinyResource toAdd=new ClientTinyResource(
							Integer.parseInt((String)combo.getValue().get(ClientResource.SEARCH_ID)), 
							(String)combo.getValue().get(ClientResource.TYPE), (String)combo.getValue().get(ClientResource.TITLE),
							(String)combo.getValue().get(ClientResource.SOURCE_HCAF));
					boolean alreadyInserted=false;
					for(ClientTinyResource inStore:grid.getStore().getModels())
						if(inStore.getId().equals(toAdd.getId())){
							alreadyInserted=true;
							break;
						}
					if(!alreadyInserted) add.enable();
					else add.disable();
				}else add.disable();				
			}
		});
		ToolBar toolbar=new ToolBar();
		add=new Button("Add selected "+type, Resources.ICONS.add(), new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				ClientTinyResource toAdd=new ClientTinyResource(
						Integer.parseInt((String)combo.getValue().get(ClientResource.SEARCH_ID)), 
						(String)combo.getValue().get(ClientResource.TYPE), (String)combo.getValue().get(ClientResource.TITLE),
						(String)combo.getValue().get(ClientResource.SOURCE_HCAF));
				if(!grid.getStore().contains(toAdd)) grid.getStore().add(toAdd);
			}
		});
		add.disable();
		toolbar.add(add);
		toolbar.add(combo);
		toolbar.add(new SeparatorToolItem());
		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ClientTinyResource>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<ClientTinyResource> se) {
				if(se.getSelection().size()>0)remove.enable();
				else remove.disable();
			}
		});
		remove=new Button("Remove selected", Resources.ICONS.delete(), new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				for(ClientTinyResource r:grid.getSelectionModel().getSelectedItems())
					grid.getStore().remove(r);
			}
		});
		remove.disable();
		toolbar.add(remove);
		setTopComponent(toolbar);
		add(grid);
	}
	public MultiSourceSelector (ClientResourceType type){
		this(type,DataManagementFacilityConstants.resourceNames.get(type)+" ("+type+")");
	}
	
	public List<ClientTinyResource> getSelection(){
		return grid.getStore().getModels();
	}
}
