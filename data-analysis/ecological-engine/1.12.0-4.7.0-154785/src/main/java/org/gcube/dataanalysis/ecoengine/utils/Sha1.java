package org.gcube.dataanalysis.ecoengine.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sun.org.apache.regexp.internal.RE;

public class Sha1
{

    static String fixedCachePrefix = "cache_";

    private static String convertToHex(byte[] data)
    {
	StringBuffer buf = new StringBuffer();
	for (int i = 0; i < data.length; i++)
	{
	    int halfbyte = (data[i] >>> 4) & 0x0F;
	    int two_halfs = 0;
	    do
	    {
		if ((0 <= halfbyte) && (halfbyte <= 9))
		    buf.append((char) ('0' + halfbyte));
		else
		    buf.append((char) ('a' + (halfbyte - 10)));
		halfbyte = data[i] & 0x0F;
	    } while (two_halfs++ < 1);
	}
	return buf.toString();
    }

    public static String SHA1(String text)
	throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
	MessageDigest md;
	md = MessageDigest.getInstance("SHA-1");
	byte[] sha1hash = new byte[40];
	md.update(text.getBytes("iso-8859-1"), 0, text.length());
	sha1hash = md.digest();
	return convertToHex(sha1hash);
    }

    public static String calcFilePrefix(String filestring)
    {
	try
	{
	    // prendo i primi 3 caratteri del file
	    int len = filestring.length();
	    int counter = 0;
	    String cacheDir = "";
	    RE regex = new RE("[a-z]");

	    for (int i = 0; i < len; i++)
	    {
		String chars = "" + filestring.charAt(i);
		boolean optioned = regex.match(chars);

		if (optioned)
		{
		    counter++;
		    cacheDir += chars;
		}
		if (counter > 2)
		    break;
	    }

	    return cacheDir + "/";
	}
	catch (Exception e)
	{
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return null;
    }

    public static String calculateDigestMD5(String plainText)

    {

	String hashText = "";
	try
	{
	    MessageDigest m = MessageDigest.getInstance("MD5");
	    m.reset();
	    m.update(plainText.getBytes("UTF-8"));

	    byte[] digestBytes = m.digest();
	    BigInteger digestValue = new BigInteger(1, digestBytes);

	    hashText = digestValue.toString(16);

	    // filling
	    int remain = 32 - hashText.length();

	    for (int i = 0; i < remain; i++)
		hashText = "0" + hashText;

	}
	catch (Exception e)
	{
	    // TTSLogger.getLogger().debug("Exception: " + e.getMessage());

	}
	return hashText;
    }
}
