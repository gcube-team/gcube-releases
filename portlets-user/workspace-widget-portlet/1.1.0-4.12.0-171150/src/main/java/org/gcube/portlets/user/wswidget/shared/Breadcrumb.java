package org.gcube.portlets.user.wswidget.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Breadcrumb implements Serializable {
	String id;
	String name;
	Breadcrumb child = null;
	public Breadcrumb(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Breadcrumb getChild() {
		return child;
	}
	public void setChild(Breadcrumb child) {
		this.child = child;
	}	
	public boolean hasChild() {
		return this.child != null;
	}
	@Override
	public String toString() {
		return "Breadcrumb [id=" + id + ", name=" + name + ", child=" + ((child == null) ? "" : child.toString()) + "]";
	}
	
	
}
