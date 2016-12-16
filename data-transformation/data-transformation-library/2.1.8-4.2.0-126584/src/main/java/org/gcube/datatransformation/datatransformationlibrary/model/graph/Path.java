package org.gcube.datatransformation.datatransformationlibrary.model.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.gcube.datatransformation.datatransformationlibrary.model.Parameter;

/**
 * @author Dimitris Katris, NKUA
 * <p>
 * A transformations path includes an ordered list of transformationUnit edges which denote transformationUnit units.
 * </p>
 */
public class Path {
	
	private ArrayList<TEdge> path = new ArrayList<TEdge>();
	protected HashMap<TEdge, List<Parameter>> sunbound = new HashMap<TEdge, List<Parameter>>();
	private HashMap<TEdge, List<Parameter>> tunbound = new HashMap<TEdge, List<Parameter>>();
	
	private double cost=0.0;
	
	protected double getCost() {
		return cost;
	}
	protected void setCost(double cost) {
		this.cost = cost;
	}
	protected void addCost(double cost){
		this.cost+=cost;
	}
	protected void subCost(double cost){
		this.cost-=cost;
	}
	protected ArrayList<TEdge> getPath() {
		return path;
	}
	protected boolean add(TEdge edge) {
		return path.add(edge);
	}
	protected boolean addAll(Collection<? extends TEdge> edges) {
		return path.addAll(edges);
	}
	protected boolean contains(Object edge) {
		return path.contains(edge);
	}
	protected boolean remove(Object edge) {
		return path.remove(edge);
	}
	protected int size() {
		return path.size();
	}
	
	protected void clearSUnbound(){
		sunbound.clear();
	}
	
	protected List<Parameter> getSUnbound(Object key) {
		return (sunbound.get(key)==null ? new ArrayList<Parameter>() : sunbound.get(key));
	}
	
	protected List<Parameter> putSUnbound(TEdge key, List<Parameter> value) {
		if(value==null)value=new ArrayList<Parameter>();
		return sunbound.put(key, value);
	}
	
	protected void removeSUnbound(TEdge key){
		sunbound.remove(key);
	}
	
	protected void clearTUnbound(){
		tunbound.clear();
	}
	
	protected List<Parameter> getTUnbound(Object key) {
		return (tunbound.get(key)==null ? new ArrayList<Parameter>() : tunbound.get(key));
	}
	
	protected List<Parameter> putTUnbound(TEdge key, List<Parameter> value) {
		if(value==null)value=new ArrayList<Parameter>();
		return tunbound.put(key, value);
	}
	
	protected void removeTUnbound(TEdge key){
		tunbound.remove(key);
	}
	
	@SuppressWarnings("unchecked")
	protected Path clone(){
		Path newpath = new Path();
		newpath.setCost(this.cost);
		newpath.path = (ArrayList<TEdge>)this.path.clone();
		newpath.sunbound=(HashMap<TEdge, List<Parameter>>)this.sunbound.clone();
		newpath.tunbound=(HashMap<TEdge, List<Parameter>>)this.tunbound.clone();
//		System.out.println("size: "+newpath.sunbound.size());
		return newpath;
	}
	
}
