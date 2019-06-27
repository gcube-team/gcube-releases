package org.gcube.portlets.user.speciesdiscovery.server.util;

//import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.apache.log4j.Logger;
import org.gcube.contentmanager.storageclient.model.protocol.smp.Handler;
import org.gcube.contentmanager.storageclient.model.protocol.smp.SMPURLConnection;

public class StorageUtil {
	
	protected static Logger logger = Logger.getLogger(StorageUtil.class);
	
	public static InputStream getInputStreamByStorageClient(String url) throws Exception {

		Handler.activateProtocol();

		URL smsHome = null;
		try {
			
			 smsHome = new URL(null, url, new URLStreamHandler() {
	               
                @Override
                protected URLConnection openConnection(URL u) throws IOException {
                    return new SMPURLConnection(u);
                }
            });
			 
//			smsHome = new URL(url);
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		URLConnection uc = null;
		uc = ( URLConnection ) smsHome.openConnection();
		InputStream is=uc.getInputStream();
		return is;
	}

	public static void main(String[] args) throws Exception {
		
		InputStream is = getInputStreamByStorageClient("smp:/51e1065ee4b0a159b8c25cc8?5ezvFfBOLqb2cBxvyAbVnOhbxBCSqhv+Z4BC5NS/+OwS5RYBeaUL5FS9eDyNubiTI4vSpggUgPA+jm9rQxwbisfhkOW/m6l2IYG9BKb8AEJFLgVvG3FJTk0+4xV9iM/hNQvChZjoJZna0aPXkHN4Eg==");
		
		
		
	}

}
