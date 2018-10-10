/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.client.properties;

import org.gcube.portlets.user.tdtemplateoperation.shared.TdColumnData;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * The Interface TdColumnDataPropertiesCombo.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Mar 25, 2015
 */
public interface TdColumnDataPropertiesAccess extends PropertyAccess<TdColumnData> {

	/**
	 * Id.
	 *
	 * @return the model key provider
	 */
	@Path("id")
	ModelKeyProvider<TdColumnData> id();
	
	ValueProvider<TdColumnData,String> label();
}
