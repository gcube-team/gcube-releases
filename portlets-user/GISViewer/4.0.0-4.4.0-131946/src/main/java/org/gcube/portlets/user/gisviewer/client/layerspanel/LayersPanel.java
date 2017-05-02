package org.gcube.portlets.user.gisviewer.client.layerspanel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.portlets.user.gisviewer.client.Constants;
import org.gcube.portlets.user.gisviewer.client.commons.beans.LayerItem;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.dnd.DragSource;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.TableData;


public class LayersPanel extends VerticalPanel {
	List<LayerItem> layerItems = new ArrayList<LayerItem>();
	Map<LayerItem, LayerToolsPanel> map = new HashMap<LayerItem, LayerToolsPanel>();
	static final int spacing = 1;
	private LayersPanelHandler layersPanelHandler;
	private boolean isEmpty = true;
	private LayerItem layerItemTransect;
	private boolean firstTimeOver = true;

	public LayersPanel(LayersPanelHandler layersPanelHandler) {
		super();
		this.layersPanelHandler = layersPanelHandler;
//		this.setTableHeight("100%");
		this.setSpacing(spacing);

		if (Constants.layersDragEnabled) {
			DropTarget target = new LayersDropTarget(this);
			target.setGroup("layersPanel");
		}

		setTableWidth("100%");

		this.addListener(Events.OnMouseOver, new Listener<DomEvent>(){
			@Override
			public void handleEvent(DomEvent be) {
				if (firstTimeOver) {
					Info.display("Layers Panel", "Now it's possible to move layers to ordering change.");
					firstTimeOver = false;
				}
			}
		});
	}

	public void showEmptyMessage() {
		this.removeAll();
		Label l = new Label("No layers");
		l.setStyleAttribute("font-size", "12px");
		this.add(l);
		this.layout();
	}

	public void addLayer(LayerItem layerItem, boolean toTop) {
		// add layer item object
		if (toTop)
			this.layerItems.add(0, layerItem);
		else
			this.layerItems.add(layerItem);

		final LayerToolsPanel layerToolsPanel = new LayerToolsPanel(layerItem, layersPanelHandler);

		if (Constants.layersDragEnabled) {
			DragSource source = new DragSource(layerToolsPanel) {
				@Override
				protected void onDragStart(DNDEvent event) {
					if (event.getClientY()<layerToolsPanel.getAbsoluteTop()+20) {
						event.setData(layerToolsPanel);
						event.getStatus().update(El.fly(layerToolsPanel.getElement()).cloneNode(true));
					} else
						event.setData(null);
				}
			};
			source.setGroup("layersPanel");
		}

		if (isEmpty) {
			this.removeAll();
			isEmpty = false;
		}
		map.put(layerItem, layerToolsPanel);

		if (toTop)
			this.insert(layerToolsPanel, 0, new TableData("100%", ""));
		else
			this.add(layerToolsPanel, new TableData("100%", ""));
	}

	public void addLayerItems(List<LayerItem> layers, boolean toTop) {
		for (LayerItem layerItem : layers)
			this.addLayer(layerItem, toTop);
		this.layout();
	}

	//PATCHED BY FRANCESCO M.
	public List<LayerItem> getVisibleLayers() {

		List<Integer> positions = new ArrayList<Integer>(this.layerItems.size());
 		for (LayerItem layerItem : this.layerItems){
			if (layerItem.isVisible()){
				//PATCHED BY FRANCESCO M.
//				GWT.log("VisibleLayer order: "+layerItem.getOrder()+  " title: "+layerItem.getTitle());
				int pos = (int)layerItem.getOrder();
				positions.add(pos-1 < 0 ? 0: pos-1);
			}
			else
				positions.add(-1);
		}

 		LayerItem[] positionArrays = new LayerItem[layerItems.size()];

 		for (int i=0; i<positions.size(); i++) {
 			if(positions.get(i)!=-1) //IS GOOD ORDER
 				positionArrays[positions.get(i)] = this.layerItems.get(i);
		}

// 		orderedArraysLItems.removeAll(Collections.singleton(null));  //NOT IMPLEMENTED

 		List<LayerItem> ordered  = new ArrayList<LayerItem>(layerItems.size());

 		for (LayerItem layerItem : positionArrays) {
			if(layerItem!=null)
				ordered.add(layerItem);
		}

//	 	for (LayerItem layerItem : ordered) {
//	 		GWT.log("Returning: "+layerItem.getTitle());
//		}

		return ordered;
	}

	public void updateLayersOrder() {
		//		System.out.println("LAYERS ORDER");
		int i=0;
		for (Component c : this.getItems()) {
			LayerToolsPanel layerToolPanel = (LayerToolsPanel)c;
			LayerItem layerItem = layerToolPanel.getLayerItem();
			layerItem.setOrder(++i);
			//			System.out.println(""+(++i)+") " + layerItem.getLayer() + " ("+layerItem.getId()+")");
		}
		this.layersPanelHandler.updateLayersOrder();
	}

	public void setCqlTip(LayerItem layerItem, boolean show) {
		LayerToolsPanel layerToolsPanel = map.get(layerItem);
		layerToolsPanel.setCqlTip(show);
	}

	public void setTransectTip(LayerItem layerItem, boolean show) {
		// delete previous transet tip
		if (show && this.layerItemTransect!=null) {
			LayerToolsPanel layerToolsPanelTransect = map.get(layerItemTransect);
			if (layerToolsPanelTransect!=null)
				layerToolsPanelTransect.setTransectTip(false);
		}

		LayerToolsPanel layerToolsPanel = map.get(layerItem);
		if (layerToolsPanel!=null) {
			layerToolsPanel.setTransectTip(show);
			this.layerItemTransect = show ? layerItem : null;
		}
	}

	/**
	 * @param layerItem
	 */
	public void removeLayer(LayerItem layerItem) {
		LayerToolsPanel layertoolsPanel = map.get(layerItem);
		map.remove(layerItem);
		layerItems.remove(layerItem);
		this.remove(layertoolsPanel);
		this.layout();
	}

	/**
	 * @return
	 */
	public List<LayerItem> getLayerItems() {
		return this.layerItems;
	}

	public void setLayerVisible(LayerItem layerItem, boolean isVisible) {
		LayerToolsPanel layertoolsPanel = map.get(layerItem);
		if (layertoolsPanel!=null)
			layertoolsPanel.setCheckVisible(isVisible);
	}
}

