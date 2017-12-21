package org.gcube.portlets.widgets.wsmail.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.gcube.portlets.widgets.wsmail.client.forms.MailForm;

import com.google.gwt.core.client.EntryPoint;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WsMail_Widget implements EntryPoint {
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
//		show();
	}
	/**
	 * 
	 */
	public void show() {
		ArrayList<String> listContactsId = new ArrayList<String>();
		listContactsId.add("massimiliano.assante");
//		listContactsId.add("pino.pinetti");
//		listContactsId.add("rino.gattuso");
	

		new MailForm(listContactsId);
	}
}
