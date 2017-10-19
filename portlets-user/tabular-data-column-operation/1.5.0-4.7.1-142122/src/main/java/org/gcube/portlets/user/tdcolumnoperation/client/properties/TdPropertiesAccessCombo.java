/**
 * 
 */
package org.gcube.portlets.user.tdcolumnoperation.client.properties;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @May 19, 2014
 * 
 * @Path("id")
 * ModelKeyProvider<T> id();
 * @Path("label")
 * LabelProvider<T> label();
 *
 */
public interface TdPropertiesAccessCombo<T> extends PropertyAccess<T>{
	
	@Path("id")
	ModelKeyProvider<T> id();
	
	@Path("label")
	LabelProvider<T> label();

}
