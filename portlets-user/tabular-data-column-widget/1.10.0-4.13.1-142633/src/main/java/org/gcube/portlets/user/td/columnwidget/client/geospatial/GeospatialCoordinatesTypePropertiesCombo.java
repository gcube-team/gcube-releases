package org.gcube.portlets.user.td.columnwidget.client.geospatial;

import org.gcube.portlets.user.td.widgetcommonevent.shared.geospatial.GeospatialCoordinatesType;

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
public interface GeospatialCoordinatesTypePropertiesCombo extends
		PropertyAccess<GeospatialCoordinatesType> {
	
	@Path("id")
	ModelKeyProvider<GeospatialCoordinatesType> id();
	
	LabelProvider<GeospatialCoordinatesType> label();
	

}
