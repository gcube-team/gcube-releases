package org.gcube.portlets.user.td.tablewidget.client.type;


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
public interface TableTypeProperties  extends PropertyAccess<TableTypeElement> {
	
	@Path("id")
	ModelKeyProvider<TableTypeElement> id();

	LabelProvider<TableTypeElement> label();

}
 