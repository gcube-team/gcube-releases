package org.gcube.portlets.user.statisticalalgorithmsimporter.client.properties;

import org.gcube.portlets.user.statisticalalgorithmsimporter.shared.input.IOType;

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

public interface IOTypePropertiesCombo extends PropertyAccess<IOType> {

	@Path("id")
	ModelKeyProvider<IOType> id();

	LabelProvider<IOType> label();

}
