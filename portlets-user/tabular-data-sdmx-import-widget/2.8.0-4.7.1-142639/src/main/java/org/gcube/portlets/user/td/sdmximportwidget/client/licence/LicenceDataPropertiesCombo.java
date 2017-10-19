package org.gcube.portlets.user.td.sdmximportwidget.client.licence;

import org.gcube.portlets.user.td.gwtservice.shared.licenses.LicenceData;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;


/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface LicenceDataPropertiesCombo extends
		PropertyAccess<LicenceData> {
	
	@Path("id")
	ModelKeyProvider<LicenceData> id();
	
	LabelProvider<LicenceData> licenceName();
	

}