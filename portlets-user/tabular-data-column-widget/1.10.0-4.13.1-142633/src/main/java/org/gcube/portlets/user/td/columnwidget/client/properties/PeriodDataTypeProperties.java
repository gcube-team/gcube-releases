package org.gcube.portlets.user.td.columnwidget.client.properties;

import org.gcube.portlets.user.td.widgetcommonevent.shared.tr.column.PeriodDataType;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface PeriodDataTypeProperties extends
		PropertyAccess<PeriodDataType> {

	@Path("name")
	ModelKeyProvider<PeriodDataType> name();

	LabelProvider<PeriodDataType> label();

}