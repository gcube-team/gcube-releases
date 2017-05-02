package org.gcube.common.geoserverinterface.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class GroupRest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2028151067656254608L;
	/**
	 * @uml.property  name="name"
	 */
	private String name = null;
	/**
	 * @uml.property  name="layers"
	 * @uml.associationEnd  multiplicity="(0 -1)" elementType="java.lang.String"
	 */
	private ArrayList<String> layers = null;
	/**
	 * @uml.property  name="styles"
	 * @uml.associationEnd  qualifier="layer:java.lang.String java.lang.String"
	 */
	private Map<String, String> styles = null;
	/**
	 * @uml.property  name="bounds"
	 * @uml.associationEnd  
	 */
	private BoundsRest bounds = null;
	
	public GroupRest() {
		super();
		layers = new ArrayList<String>();
		styles = new HashMap<String, String>();
	}
	/**
	 * @return
	 * @uml.property  name="name"
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name
	 * @uml.property  name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String> getLayers() {
		return layers;
	}
	public void setLayers(ArrayList<String> layers) {
		this.layers = layers;
	}
	public Collection<String> getStyles() {
		
		ArrayList<String> result = new ArrayList<String>();
		for (String l: this.layers) {
			result.add(this.getStyle(l));
		}
		
		return result;
	}
	/**
	 * @return
	 * @uml.property  name="bounds"
	 */
	public BoundsRest getBounds() {
		return bounds;
	}
	/**
	 * @param bounds
	 * @uml.property  name="bounds"
	 */
	public void setBounds(BoundsRest bounds) {
		this.bounds = bounds;
	}
	public void addLayer(String layer) {
		this.layers.add(layer);
	}
	public String getStyle(String layer) {
		return this.styles.get(layer);
	}
	public void addStyle(String layer, String style) {
		this.styles.put(layer, style);
	}
	public void setStyles(Map<String, String> styles) {
		this.styles = styles;
	}
}
