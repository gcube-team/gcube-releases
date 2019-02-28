package org.gcube.portlets.user.gisviewer.client.commons.beans;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class GroupInfo implements IsSerializable{

	private String name;
	private ArrayList<LayerItem> layers;
	private BoundsMap bounds;
	
	public GroupInfo() {
		super();
	}

	public GroupInfo(String name, ArrayList<LayerItem> layers, BoundsMap bounds) {
		super();
		this.name = name;
		this.layers = layers;
		this.bounds = bounds;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<LayerItem> getLayers() {
		return layers;
	}

	public void setLayers(ArrayList<LayerItem> layers) {
		this.layers = layers;
	}

	public BoundsMap getBounds() {
		return bounds;
	}

	public void setBounds(BoundsMap bounds) {
		this.bounds = bounds;
	}
}
