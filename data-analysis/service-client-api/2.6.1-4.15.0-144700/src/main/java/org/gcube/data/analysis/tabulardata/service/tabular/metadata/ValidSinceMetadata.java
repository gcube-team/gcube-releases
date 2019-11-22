package org.gcube.data.analysis.tabulardata.service.tabular.metadata;

import java.util.Calendar;

public class ValidSinceMetadata implements TabularResourceMetadata<Calendar> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Calendar date;
		
	
	
	public ValidSinceMetadata() {}

	public ValidSinceMetadata(Calendar date) {
		super();
		this.date = date;
	}

	@Override
	public void setValue(Calendar value) {
		this.date = value;
	}

	@Override
	public Calendar getValue() {
		return date;
	}
	
}
