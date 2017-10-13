package org.gcube.portlets.admin.sepeditor.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class RRAccessPoint implements Serializable {

	private String desc;
	private String interfaceEntryNameAttr;
	private String interfaceEndPoint;
	private String username;
	private String password;
	
	private ArrayList<Property> properties;

	public RRAccessPoint() {
		super();
	}

	public RRAccessPoint(String desc, String interfaceEntryNameAttr,
			String interfaceEndPoint, String username, String password,
			ArrayList<Property> properties) {
		super();
		this.desc = desc;
		this.interfaceEntryNameAttr = interfaceEntryNameAttr;
		this.interfaceEndPoint = interfaceEndPoint;
		this.username = username;
		this.password = password;
		this.properties = properties;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getInterfaceEntryNameAttr() {
		return interfaceEntryNameAttr;
	}

	public void setInterfaceEntryNameAttr(String interfaceEntryNameAttr) {
		this.interfaceEntryNameAttr = interfaceEntryNameAttr;
	}

	public String getInterfaceEndPoint() {
		return interfaceEndPoint;
	}

	public void setInterfaceEndPoint(String interfaceEndPoint) {
		this.interfaceEndPoint = interfaceEndPoint;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public ArrayList<Property> getProperties() {
		return properties;
	}

	public void setProperties(ArrayList<Property> properties) {
		this.properties = properties;
	}

	
}
