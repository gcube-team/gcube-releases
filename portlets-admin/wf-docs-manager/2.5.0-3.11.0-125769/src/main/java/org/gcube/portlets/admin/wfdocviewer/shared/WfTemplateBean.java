package org.gcube.portlets.admin.wfdocviewer.shared;

import com.extjs.gxt.ui.client.data.BaseModel;

@SuppressWarnings("serial")
public class WfTemplateBean extends BaseModel {

	public WfTemplateBean() {	}

	public WfTemplateBean(String templateid,  String templatename,  String author, String dateCreated) {
		set("id", templateid);
		set("name", templatename);
		set("author", author);
		set("dateCreated", dateCreated);
	}
	public String getId() {
		return (String) get("id");
	}

	public String getName() {
		return (String) get("name");
	}

	public String getAuthor() {
		return (String) get("author");
	}
	public String getDate() {
		return (String) get("dateCreated");
	}
}
