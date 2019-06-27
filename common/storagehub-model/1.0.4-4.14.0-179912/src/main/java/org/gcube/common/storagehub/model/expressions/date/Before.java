package org.gcube.common.storagehub.model.expressions.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.gcube.common.storagehub.model.expressions.Expression;
import org.gcube.common.storagehub.model.expressions.SearchableField;

public class Before implements Expression<Boolean> {
	
	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
	
	private SearchableField<Calendar> searchableField;
	private Calendar value;
	
	protected Before() {} 
	
	public Before(SearchableField<Calendar> searchableField, Calendar value) {
		super();
		this.searchableField = searchableField;
		this.value = value;
	}

	public SearchableField<Calendar> getSearchableField() {
		return searchableField;
	}

	public Calendar getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Before [searchableField=" + searchableField + ", value=" + dateFormat.format(value.getTime()) + "]";
	}
	
}
