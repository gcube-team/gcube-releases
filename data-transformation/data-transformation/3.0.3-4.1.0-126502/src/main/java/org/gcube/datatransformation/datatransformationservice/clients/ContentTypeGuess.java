//package org.gcube.datatransformation.datatransformationservice.clients;
//
//import javax.activation.MimetypesFileTypeMap;
//import java.io.File;
//import java.net.MalformedURLException;
//import java.net.URL;
//
//public class ContentTypeGuess {
//
//	/**
//	 * @param args
//	 */
//	public static void main(String[] args) {
//
//		try {
//			URL url = new URL("http://151.1.148.222/foto/high/DIAL/D036/D36-09.JPG");
//		File f = new File("gumby.gif");
//		System.out.println("Mime Type of " + url.toString() + " is "
//				+ new MimetypesFileTypeMap().getContentType(url.toString()));
//		// expected output :
//		// "Mime Type of gumby.gif is image/gif"
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}
