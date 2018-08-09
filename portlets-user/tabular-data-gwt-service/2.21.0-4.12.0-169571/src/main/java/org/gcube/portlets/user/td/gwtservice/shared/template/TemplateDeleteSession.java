package org.gcube.portlets.user.td.gwtservice.shared.template;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Delete Template Session
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class TemplateDeleteSession implements Serializable {

	private static final long serialVersionUID = -8834066207159106968L;
	protected ArrayList<TemplateData> templates;

	public ArrayList<TemplateData> getTemplates() {
		return templates;
	}

	public void setTemplates(ArrayList<TemplateData> templates) {
		this.templates = templates;
	}

	@Override
	public String toString() {
		return "TemplateDeleteSession [templates=" + templates + "]";
	}

	
	
	
}
