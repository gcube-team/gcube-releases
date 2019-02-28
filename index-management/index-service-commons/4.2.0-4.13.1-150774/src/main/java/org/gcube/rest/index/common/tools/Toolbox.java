package org.gcube.rest.index.common.tools;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

public class Toolbox {

	public static void main(String []args) throws DecoderException{
//		String a = "[{\"operation\":\"remove\",\"spec\":{\"metadata\":{\"oai_dc:dc\":{\"xsi:*\":\"\"}}}},{\"operation\":\"remove\",\"spec\":{\"metadata\":{\"oai_dc:dc\":{\"xmln*\":\"\"}}}},{\"operation\":\"remove\",\"spec\":{\"metadata\":{\"oai_dc:dc\":{\"dc:*\":{\"xml:lang\":\"\"}}}}},{\"operation\":\"remove\",\"spec\":{\"metadata\":{\"oai_dc:dc\":{\"dc:*\":{\"*\":{\"xml*\":\"\"}}}}}},{\"operation\":\"shift\",\"spec\":{\"metadata\":{\"oai_dc:dc\":\"\"}}},{\"operation\":\"shift\",\"spec\":{\"*\":{\"content\":\"&1\",\"*\":{\"content\":\"&2\"}},\"dc:format\":\"&\",\"dc:identifier\":\"&\",\"dc:language\":\"&\",\"dc:publisher\":\"&\"}}]";
//		System.out.println(decode(encode(a)));
	}
	
	public static String encode(String plain){
		String hexString = Hex.encodeHexString(plain.getBytes(StandardCharsets.UTF_8));
		return hexString;
	}
	
	public static String decode(String encoded) throws DecoderException{
		byte[] hexArray = Hex.decodeHex(encoded.toCharArray());
		return new String(hexArray, StandardCharsets.UTF_8);
	}
	
	public static String toUnicode(String whatever) throws UnsupportedEncodingException{
		byte[] utf8Bytes = whatever.getBytes("UTF-8");
		return new String(utf8Bytes, "UTF8"); 
	}
	
	public static String keepUnicodeCharacters(String dirtyStr){
		return dirtyStr.replaceAll("\\p{S}", " ").replaceAll("\\p{P}", "").replaceAll("\\p{Po}", "").replaceAll("\\p{C}", "");
	}
	
}
