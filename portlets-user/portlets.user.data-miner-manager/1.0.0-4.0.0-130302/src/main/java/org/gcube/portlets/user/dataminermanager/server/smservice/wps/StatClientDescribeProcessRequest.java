package org.gcube.portlets.user.dataminermanager.server.smservice.wps;

import org.n52.wps.client.AbstractClientGETRequest;

/**
 * 
 * @author Giancarlo Panichi
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class StatClientDescribeProcessRequest extends AbstractClientGETRequest {
	
	private static String IDENTIFIER_REQ_PARAM_NAME = "identifier";
	private static String REQUEST_REQ_PARAM_VALUE = "DescribeProcess";
	
	public StatClientDescribeProcessRequest() {
		super();
		setRequestParamValue(REQUEST_REQ_PARAM_VALUE);
	}
	
	public void setIdentifier(String[] ids) {
		String idsString = "";
			for(int i = 0; i < ids.length; i++) {
				idsString = idsString + ids[i];
				if(i != ids.length -1) {
					idsString = idsString + ",";
			}	
		}
		requestParams.put(IDENTIFIER_REQ_PARAM_NAME, idsString);
	}
	
	public boolean valid() {
		return true;
	}

}
