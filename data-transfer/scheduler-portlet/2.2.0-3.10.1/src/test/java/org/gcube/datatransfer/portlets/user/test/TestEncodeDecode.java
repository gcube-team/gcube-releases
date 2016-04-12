package org.gcube.datatransfer.portlets.user.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.gcube.datatransfer.portlets.user.server.utils.Utils;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestEncodeDecode {


	public static void main(String[] args) {
		String uri = "http://node11.d.d4science.research-infrastructures.eu:8080/jackrabbit-custom-webapp-2.4.1/repository/default/Home/nikolaos.drakopoulos/Workspace/Wikipedia logo silver##::^^@@!!~~&&(())++==;;''__e spaces.png";
	//	String uri = "webdav://nick:pass@hostname.com/ena_dyo/sdsd / trio :p/jhjh";
		//	String uri = "webdav%3A%2F%2Fnick%3Apass%40hostname.com%2Fena+dyo+trio+%3Ap";
		URI uriUri=null;
		try {
			uriUri = new URI(Utils.getEncodedWebdavURL(uri).replaceAll("\\+","%20"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		System.out.println(uriUri+"\n"+uriUri.getPath());
		//System.out.println(Utils.getEncodedWebdavURL(uri));
		//String encoded = Utils.getEncodedWebdavURL(uri).replaceAll("\\+","%20");
		//System.out.println(encoded);
		//System.out.println(Utils.decodeSomeNameCompletelly(encoded));
	}

}
