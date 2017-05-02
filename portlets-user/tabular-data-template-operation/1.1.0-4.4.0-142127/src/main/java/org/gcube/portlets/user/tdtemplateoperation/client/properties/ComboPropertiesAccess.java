/**
 * 
 */
package org.gcube.portlets.user.tdtemplateoperation.client.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * The Interface TdPropertiesAccessCombo.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @param <T> the generic type
 * @May 19, 2014
 * @Path("id") ModelKeyProvider<T> id();
 * @Path("label") LabelProvider<T> label();
 */
public interface ComboPropertiesAccess<T> extends PropertyAccess<T>{
	
	@Path("id")
	ModelKeyProvider<T> id();
	
	@Path("label")
	LabelProvider<T> label();

}
