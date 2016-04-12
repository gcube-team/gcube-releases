package org.gcube.portlets.user.speciesdiscovery.client.advancedsearch;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.NumberField;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public interface AdvancedSearchInterface {
	
	//Bounds filter
	public NumberField getUpperBoundLatitudeField();
	public NumberField getUpperBoundLongitudeField();
	public NumberField getLowerBoundLatitudeField();
	public NumberField getLowerBoundLongitudeField();
	
	//Date filter
	public DateField getFromDate();
	public DateField getToDate();
}
