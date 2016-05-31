package gr.uoa.di.madgik.urlresolutionlibrary.test;

import gr.uoa.di.madgik.urlresolutionlibrary.helpers.Helpers;
import gr.uoa.di.madgik.urlresolutionlibrary.url.urlconnections.LocatorURLConnection;

import java.net.URL;

/**
 * 
 * @author Alex Antoniadis
 * 
 */
public class ContentFactoryTest {

	public static void main(String[] args) throws Exception {
		LocatorURLConnection.activateProtocol();
		URL url = new URL("https://www.amazon.co.uk");
		String str = Helpers.convertStreamToString(url.openConnection().getInputStream(), 1024, "UTF-8");
		
		System.out.println("Data : \n" + str);

	}

}
