package org.gcube.datatransfer.portlets.user.server.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class Utils {
	public static String encodeSomeName(String tmp){
		String encodedValue=null;
		try {		    
			encodedValue= URLEncoder.encode(tmp, "UTF-8");
		} catch (UnsupportedEncodingException uee) { }
		return encodedValue;
	}

	public static String decodeSomeNameCompletelly(String tmp){
		if(tmp==null)return null;
		String decodedValue=tmp;
		boolean flag=true;

		while(flag){
			try {
				decodedValue = URLDecoder.decode(tmp, "UTF-8");
			} catch (UnsupportedEncodingException uee) {
				return null;
			}

			if(decodedValue.compareTo(tmp)==0)flag=false;
			else{
				tmp=decodedValue;
			}		
		}
		return decodedValue;
	}
	
	public static String getEncodedWebdavURL(String uri){
		String[] initialParts=uri.split("//");
		String protocol=initialParts[0]+"//";
		String[] parts=(initialParts[1]).split("/");
		String hostname = parts[0];
		String restPart="";
		for(int i =1;i<parts.length;i++){
			restPart=restPart+parts[i];
			if(i!=parts.length-1)restPart=restPart+"/";
		}
		String newRestPart="";

		String[] pieces = restPart.split("/");
		//System.out.println("restPart="+restPart+"\npieces:");
		//for(String tmp:pieces)System.out.println(tmp);
		for(String tmp: pieces){
			newRestPart=newRestPart+"/";
			String decoded = Utils.decodeSomeNameCompletelly(tmp);
			String encoded = Utils.encodeSomeName(decoded);
			newRestPart=newRestPart+encoded;
		}
		//		System.out.println("protocol="+protocol+"\n"+
		//				"hostname="+hostname+"\n"+
		//				"newRestPart="+newRestPart+"\n"+
		//				"everything="+protocol+hostname+newRestPart);
		return protocol+hostname+newRestPart;
	}
	
	public static class URLParamEncoder {

	    public static String encode(String input) {
	        StringBuilder resultStr = new StringBuilder();
	        for (char ch : input.toCharArray()) {
	            if (isUnsafe(ch)) {
	                resultStr.append('%');
	                resultStr.append(toHex(ch / 16));
	                resultStr.append(toHex(ch % 16));
	            } else {
	                resultStr.append(ch);
	            }
	        }
	        return resultStr.toString();
	    }

	    private static char toHex(int ch) {
	        return (char) (ch < 10 ? '0' + ch : 'A' + ch - 10);
	    }

	    private static boolean isUnsafe(char ch) {
	        if (ch > 128 || ch < 0)
	            return true;
	        return " %$&+,/:;=?@<>#^'\"".indexOf(ch) >= 0;
	    }

	    public static String getEncodedWebdavURL(String uri){
			String[] initialParts=uri.split("//");
			String protocol=initialParts[0]+"//";
			String[] parts=(initialParts[1]).split("/");
			String hostname = parts[0];
			String restPart="";
			for(int i =1;i<parts.length;i++){
				restPart=restPart+parts[i];
				if(i!=parts.length-1)restPart=restPart+"/";
			}
			String newRestPart="";

			String[] pieces = restPart.split("/");
			//System.out.println("restPart="+restPart+"\npieces:");
			//for(String tmp:pieces)System.out.println(tmp);
			for(String tmp: pieces){
				newRestPart=newRestPart+"/";
				//String decoded = Utils.URLEncoderFromCommons.decodeWebdavURLCompletelly(tmp);
				String encoded = Utils.URLParamEncoder.encode(tmp);
				newRestPart=newRestPart+encoded;
			}
			//		System.out.println("protocol="+protocol+"\n"+
			//				"hostname="+hostname+"\n"+
			//				"newRestPart="+newRestPart+"\n"+
			//				"everything="+protocol+hostname+newRestPart);
			return protocol+hostname+newRestPart;
		}
	}
	
	public static class URLEncoderFromCommons {
		
		public static String decodeWebdavURLCompletelly(String tmp){
			if(tmp==null)return null;
			String decodedValue=tmp;
			boolean flag=true;

			while(flag){
				try {
					decodedValue = URIUtil.decode(tmp, "UTF-8");
				} catch (URIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(decodedValue.compareTo(tmp)==0)flag=false;
				else{
					tmp=decodedValue;
				}		
			}
			return decodedValue;
		}
		 public static String getEncodedWebdavURL(String uri){
				String[] initialParts=uri.split("//");
				String protocol=initialParts[0]+"//";
				String[] parts=(initialParts[1]).split("/");
				String hostname = parts[0];
				String restPart="";
				for(int i =1;i<parts.length;i++){
					restPart=restPart+parts[i];
					if(i!=parts.length-1)restPart=restPart+"/";
				}
				String newRestPart="";

				String[] pieces = restPart.split("/");
				//System.out.println("restPart="+restPart+"\npieces:");
				//for(String tmp:pieces)System.out.println(tmp);
				for(String tmp: pieces){
					newRestPart=newRestPart+"/";
					String decoded = decodeWebdavURLCompletelly(tmp);
					String encoded = null;
					try {
						encoded = URIUtil.encodePath(decoded,"UTF-8");
					} catch (URIException e) {
						e.printStackTrace();
						return null;
					}
					newRestPart=newRestPart+encoded;
				}
				//		System.out.println("protocol="+protocol+"\n"+
				//				"hostname="+hostname+"\n"+
				//				"newRestPart="+newRestPart+"\n"+
				//				"everything="+protocol+hostname+newRestPart);
				return protocol+hostname+newRestPart;
			}
	}
}
