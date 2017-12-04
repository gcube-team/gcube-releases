package org.gcube.portlets.user.td.chartswidget.client.properties;

import org.gcube.portlets.user.td.chartswidget.client.store.ChartTypeElement;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface ChartTypePropertiesCombo extends PropertyAccess<ChartTypeElement> {

	@Path("id")
	ModelKeyProvider<ChartTypeElement> id();

	LabelProvider<ChartTypeElement> label();

}
