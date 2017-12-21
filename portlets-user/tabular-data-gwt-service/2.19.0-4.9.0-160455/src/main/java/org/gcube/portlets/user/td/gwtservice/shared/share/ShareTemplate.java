package org.gcube.portlets.user.td.gwtservice.shared.share;

import java.io.Serializable;
import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.template.TemplateData;

/**
 * 
 * @author Giancarlo Panichi
 * 
 *
 */
public class ShareTemplate implements Serializable {

	private static final long serialVersionUID = -1860135253322944179L;

	private TemplateData templateData;
	private ArrayList<Contacts> contacts;

	public ShareTemplate() {
		super();
	}

	public ShareTemplate(TemplateData templateData, ArrayList<Contacts> contacts) {
		super();
		this.templateData = templateData;
		this.contacts = contacts;
	}

	public TemplateData getTemplateData() {
		return templateData;
	}

	public void setTemplateData(TemplateData templateData) {
		this.templateData = templateData;
	}

	public ArrayList<Contacts> getContacts() {
		return contacts;
	}

	public void setContacts(ArrayList<Contacts> contacts) {
		this.contacts = contacts;
	}

	@Override
	public String toString() {
		return "ShareTemplate [templateData=" + templateData + ", contacts="
				+ contacts + "]";
	}

}
