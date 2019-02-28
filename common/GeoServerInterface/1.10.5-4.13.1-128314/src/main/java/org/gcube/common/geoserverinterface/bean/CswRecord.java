package org.gcube.common.geoserverinterface.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class CswRecord implements Serializable{
	
	private static final long serialVersionUID = -6018344141129825495L;
	
	private String identifier;
	private String title;
	private String type;
	private ArrayList<String> subject;
	private String abstractProperty;
	private String Bbox;
	private ArrayList<String> URI;
	
	public CswRecord(){
		
		this.URI = new ArrayList<String>();
		this.subject = new ArrayList<String>();	
	}
	
	
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public ArrayList<String> getSubject() {
		return subject;
	}
	public void setSubject(ArrayList<String> subject) {
		this.subject = subject;
	}
	public String getAbstractProperty() {
		return abstractProperty;
	}
	public void setAbstractProperty(String abstractProperty) {
		this.abstractProperty = abstractProperty;
	}
	public String getBbox() {
		return Bbox;
	}
	public void setBbox(String bbox) {
		Bbox = bbox;
	}
	public ArrayList<String> getURI() {
		return URI;
	}
	public void setURI(ArrayList<String> URI) {
		this.URI = URI;
	}
}
