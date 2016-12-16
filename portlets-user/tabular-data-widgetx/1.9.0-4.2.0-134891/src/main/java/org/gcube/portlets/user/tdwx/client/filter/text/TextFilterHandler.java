package org.gcube.portlets.user.tdwx.client.filter.text;

import com.sencha.gxt.data.shared.loader.FilterHandler;


/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TextFilterHandler extends FilterHandler<String> {

	@Override
	public String convertToObject(String value) {
		return value;
	}

	@Override
	public String convertToString(String object) {
		return object;
	}

}
