package org.gcube.portlets.user.gisviewer.client.commons.beans;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LayerDescriptor implements Comparable<LayerDescriptor>, IsSerializable {

	private String id = "";
	private boolean hasLegend = false;
	private String name = "";
	private String father = null;
	private boolean visible = false;
	private boolean baseLayer = false;
	private boolean container = false;
	private int level_node = 0;
	
	public LayerDescriptor() {
		this("ROOT");
	}
	
	public LayerDescriptor(String id, boolean hasLegend, String name,
			String father, boolean visible, boolean baseLayer,
			boolean container, int levelNode) {
		super();
		this.id = id;
		this.hasLegend = hasLegend;
		this.name = name;
		this.father = father;
		this.visible = visible;
		this.baseLayer = baseLayer;
		this.container = container;
		level_node = levelNode;
	}
	
	public LayerDescriptor(String id, boolean hasLegend, String name,
			String father, boolean visible, 
			boolean container, int levelNode) {
		super();
		this.id = id;
		this.hasLegend = hasLegend;
		this.name = name;
		this.father = father;
		this.visible = visible;
		this.container = container;
		level_node = levelNode;
	}
	
	public LayerDescriptor(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public boolean isHasLegend() {
		return hasLegend;
	}

	public void setHasLegend(boolean hasLegend) {
		this.hasLegend = hasLegend;
	}

	public boolean isBaseLayer() {
		return baseLayer;
	}

	public void setBaseLayer(boolean baseLayer) {
		this.baseLayer = baseLayer;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public boolean isContainer() {
		return container;
	}
	public void setContainer(boolean container) {
		this.container = container;
	}
	public boolean isVisible() {
		return visible;
	}
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	public String getFather() {
		return father;
	}
	public void setFather(String father) {
		this.father = father;
	}
	public int getLevel_node() {
		return level_node;
	}
	public void setLevel_node(int level_node) {
		this.level_node = level_node;
	}
	
	public int compareTo(LayerDescriptor o) {
        // compara i due contatori
        return this.getName().compareToIgnoreCase(o.getName());
	}
}