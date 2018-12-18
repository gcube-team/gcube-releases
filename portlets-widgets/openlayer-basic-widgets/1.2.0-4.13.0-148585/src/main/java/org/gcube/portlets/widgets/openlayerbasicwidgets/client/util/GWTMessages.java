package org.gcube.portlets.widgets.openlayerbasicwidgets.client.util;

import com.google.gwt.core.client.Callback;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class GWTMessages {

	public static void alert(String title, String text, int zIndex) {
		AlertDialog alertDialog = new AlertDialog(title, text, zIndex);
		
		alertDialog.show();

	}

	public static void alert(String title, String text,int zIndex,
			Callback<Void, Void> callback) {
		AlertDialog alertDialog = new AlertDialog(title, text, zIndex, callback);
		alertDialog.show();

	}

}
