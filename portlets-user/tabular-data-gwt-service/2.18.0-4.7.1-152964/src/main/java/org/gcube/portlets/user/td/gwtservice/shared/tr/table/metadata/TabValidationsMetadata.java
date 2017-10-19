package org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.table.Validations;



/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class TabValidationsMetadata implements TabMetadata {
	
	private static final long serialVersionUID = 3321995330377334019L;
	String id="TabValidationsMetadata";
	String title="Validations";
	ArrayList<Validations> validations;
	
	
	public ArrayList<Validations> getValidations() {
		return validations;
	}

	public void setValidations(ArrayList<Validations> validations) {
		this.validations = validations;
	}

	
	@Override
	public String toString() {
		return "TabValidationsMetadata [id=" + id + ", title=" + title
				+ ", validations=" + validations + "]";
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	
}
