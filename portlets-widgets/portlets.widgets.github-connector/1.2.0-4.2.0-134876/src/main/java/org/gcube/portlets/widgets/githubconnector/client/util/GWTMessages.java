package org.gcube.portlets.widgets.githubconnector.client.util;

import com.google.gwt.core.client.Callback;

/**
 * 
 * @author Giancarlo Panichi email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
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
