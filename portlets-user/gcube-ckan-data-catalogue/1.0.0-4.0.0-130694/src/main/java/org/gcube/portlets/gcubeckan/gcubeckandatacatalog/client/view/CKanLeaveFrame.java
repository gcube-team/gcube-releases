package org.gcube.portlets.gcubeckan.gcubeckandatacatalog.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Frame;

/**
 * This iframe handles the onLeave page event in order to call the logout servlet
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CKanLeaveFrame extends Frame{

	public CKanLeaveFrame(){

		try{

//			GCubeCkanDataCatalogPanel.print("Instancing new IFRAME with uri: "+ url);
			getElement().setId("i-frame-logout");

			// make it smaller
			getElement().getStyle().setWidth(1, Unit.PX);
			getElement().getStyle().setHeight(1, Unit.PX);

		}catch(Exception e){
			GWT.log("exception " + e);
		}

	}
}
