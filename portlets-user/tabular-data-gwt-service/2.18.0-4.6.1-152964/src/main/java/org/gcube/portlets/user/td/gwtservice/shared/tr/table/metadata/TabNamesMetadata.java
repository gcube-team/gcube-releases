package org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRLocalizedText;


/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class TabNamesMetadata implements TabMetadata {
	
	private static final long serialVersionUID = 7635332011036656032L;
	
	String id="NamesMetadata";
	String title="Names";
	
	ArrayList<TRLocalizedText> listTabLocalizedText;

	public ArrayList<TRLocalizedText> getListTRLocalizedText() {
		return listTabLocalizedText;
	}

	public void setListTRLocalizedText(ArrayList<TRLocalizedText> listTRLocalizedText) {
		this.listTabLocalizedText = listTRLocalizedText;
	}

	@Override
	public String toString() {
		return "TabNamesMetadata [listTabLocalizedText=" + listTabLocalizedText + "]";
	}


	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	 
	 

}
