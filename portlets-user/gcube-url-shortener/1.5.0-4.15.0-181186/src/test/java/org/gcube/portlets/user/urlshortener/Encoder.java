package org.gcube.portlets.user.urlshortener;

import java.util.HashMap;

import org.junit.Test;

public class Encoder {
	
	
	@Test
	public void encode() {

//		System.out.println(UrlEncoderUtil.encodeQuery("request=GetStyles", "layers=test Name", "service=WMS", "version=1.1.1"));

		HashMap<String, String> parameters = new HashMap<String, String>();

		parameters.put("request", "GetStyles");
		parameters.put("layers", "test Name");
		parameters.put("version", "1.1.1");

		System.out.println(UrlEncoderUtil.encodeQuery(parameters));

	}

}
