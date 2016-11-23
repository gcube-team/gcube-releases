package org.gcube.portlets.user.td.gwtservice.shared.tr.table.metadata;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.tr.metadata.TRLocalizedText;


/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 * 
 */
public class TabDescriptionsMetadata implements TabMetadata {
	private static final long serialVersionUID = -2663624208642658528L;

	
	String id="DescriptionsMetadata";
	String title="Descriptions";
	
	 ArrayList<TRLocalizedText> listTabLocalizedText;

	public ArrayList<TRLocalizedText> getListTRLocalizedText() {
		return listTabLocalizedText;
	}

	public void setListTRLocalizedText(ArrayList<TRLocalizedText> listTRLocalizedText) {
		this.listTabLocalizedText = listTRLocalizedText;
	}

	@Override
	public String toString() {
		return "TabDescriptionsMetadata [listTabLocalizedText=" + listTabLocalizedText
				+ "]";
	}

	
	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	 
	 

}
