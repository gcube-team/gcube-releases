package org.gcube.spatial.data.sdi.engine.impl;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetUtils {

	public static boolean isUp(String url) throws IOException {
		String finalUrl=resolveRedirects(url);
		log.debug("Checking {} availability .. ",finalUrl);
		URL urlObj=new URL(finalUrl);
		HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
		int status=connection.getResponseCode();
		log.trace("HTTP Status response code for {} is {} ",finalUrl,status);
		return status>=200&&status<300;		
	}
	
	
	public static String resolveRedirects(String url) throws IOException{
		log.debug("Resolving redirect for url {} ",url);
		URL urlObj=new URL(url);
		HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
		int status=connection.getResponseCode();
		if(status>=300&&status<400){
			String newUrl=connection.getHeaderField("Location");
			log.debug("Following redirect from {} to {} ",url,newUrl);
			return resolveRedirects(newUrl);
		}else return url;
	}
	
	
}
