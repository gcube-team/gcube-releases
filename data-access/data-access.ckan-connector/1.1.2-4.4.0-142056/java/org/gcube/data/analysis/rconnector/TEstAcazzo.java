package org.gcube.data.analysis.rconnector;

import java.security.MessageDigest;

import org.junit.Test;
import org.python.core.PyCode;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import java.io.*;
public class TEstAcazzo {

	@Test
	public void test() throws Exception{
		String secret = "Ew2DCqvvavzBx7fKFPWwKhNKN";
		String digest0 = encodeTimestamp("0.0.0.0", 1458818509l)+secret+"user_vre_two_editor"+'\0'+'\0'+"userid_type:unicode";
		String timestamp16 = Long.toString(1458818509l, 16);
		System.out.println(digest0);
		System.out.println(digest0.length());
		MessageDigest mg = MessageDigest.getInstance("MD5");
		String digestResult = toHexString(mg.digest(digest0.getBytes()));
		System.out.println(digestResult);
		String finalDigest = toHexString(mg.digest((digestResult+secret).getBytes()));
		System.out.println(finalDigest);
		System.out.println(finalDigest.length());
		System.out.println(timestamp16);
	}

	@Test
	public void python() throws Exception{
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.execfile(new FileInputStream(new File(this.getClass().getClassLoader().getResource("digest.py").getFile())));
		PyObject someFunc = interpreter.get("calculate_digest");
		System.out.println(" someFunc is null ?"+(someFunc==null));
		PyObject ret = someFunc.__call__(new PyObject[]{new PyString("0.0.0.0"), new PyString("1458818509"), new PyString("Ew2DCqvvavzBx7fKFPWwKhNKN"), 
				new PyString("user_vre_two_editor"), new PyString(""), new PyString("userid_type:unicode")} );
		String realResult = (String) ret.__tojava__(String.class);
		System.out.println("result is "+realResult);
	}
	
	
	public String encodeTimestamp(String ip, long timestamp ){
		String[] splitIp = ip.split("\\.");
		
		//bho
		String toConvertIp ="";
		for (String ipPart: splitIp)
			toConvertIp += UnicodeFormatter.byteToHex((byte)Integer.parseInt(ipPart));
		
		
		int t = (int) timestamp;
		String tsString = UnicodeFormatter.byteToHex((byte)((t & 0xff000000) >> 24))+
				UnicodeFormatter.byteToHex((byte)((t & 0xff0000) >> 16))+
			    UnicodeFormatter.byteToHex((byte)((t & 0xff00) >> 8))+
			    UnicodeFormatter.byteToHex((byte)((t & 0xff)));
		System.out.println("ts string is "+toConvertIp+tsString);
		return toConvertIp+tsString;
	}
	
	public static String toHexString(byte[] bytes) {
	    StringBuilder hexString = new StringBuilder();

	    for (int i = 0; i < bytes.length; i++) {
	        String hex = Integer.toHexString(0xFF & bytes[i]);
	        if (hex.length() == 1) {
	            hexString.append('0');
	        }
	        hexString.append(hex);
	    }

	    return hexString.toString();
	}
	
	
	 
	public static class UnicodeFormatter  {
	 
	   static public String byteToHex(byte b) {
	      // Returns hex String representation of byte b
	      char hexDigit[] = {
	         '0', '1', '2', '3', '4', '5', '6', '7',
	         '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
	      };
	      char[] array = { hexDigit[(b >> 4) & 0x0f], hexDigit[b & 0x0f] };
	      return new String(array);
	   }
	 
	   static public String charToHex(char c) {
	      // Returns hex String representation of char c
	      byte hi = (byte) (c >>> 8);
	      byte lo = (byte) (c & 0xff);
	      return byteToHex(hi) + byteToHex(lo);
	   }
	 
	}
}
