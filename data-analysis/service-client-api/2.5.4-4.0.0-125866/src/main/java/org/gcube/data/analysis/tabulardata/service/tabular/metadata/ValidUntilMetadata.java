package org.gcube.data.analysis.tabulardata.service.tabular.metadata;

import java.util.Calendar;

public class ValidUntilMetadata implements TabularResourceMetadata<Calendar> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7456782447897136151L;

	private Calendar date;
	
	protected ValidUntilMetadata(){}
	
	public ValidUntilMetadata(Calendar date) {
		super();
		this.date = date;
	}

	@Override
	public void setValue(Calendar value) {
		this.date = value;
	}

	@Override
	public Calendar getValue() {
		return this.date;
	}

}
