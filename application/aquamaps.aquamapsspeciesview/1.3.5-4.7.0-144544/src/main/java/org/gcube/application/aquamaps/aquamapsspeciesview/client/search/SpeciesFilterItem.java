package org.gcube.application.aquamaps.aquamapsspeciesview.client.search;

import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.FilterCategory;
import org.gcube.application.aquamaps.aquamapsspeciesview.client.rpc.fields.SpeciesFields;

import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public class SpeciesFilterItem extends MenuItem {

	private SpeciesFields field; 
	private FilterCategory category;
	
	public SpeciesFilterItem(String label, SpeciesFields field,FilterCategory category) {
		super(label);
		setCategory(category);
		setField(field);
	}
	
	public void setField(SpeciesFields field) {
		this.field = field;
	}
	public void setCategory(FilterCategory category) {
		this.category = category;
	}
	public SpeciesFields getField() {
		return field;
	}
	public FilterCategory getCategory() {
		return category;
	}
}
