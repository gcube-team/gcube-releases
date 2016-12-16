package org.gcube.contentmanager.storageclient.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import org.apache.commons.codec.binary.Base64;

import org.junit.Test;

public class CompressTest {

	String original="pippo pluto paperino e topolino";
	String compressed;
	private static final String HEX_DIGITS = "0123456789abcdef";
	private static final byte[] HEX_CHAR = new byte[] { '0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	
	@Test
	public void gZip() throws IOException{
		String orig = "550c2612e4b0269cbdf3a53e";//"z7PSlllN48hNMXmVpJK81As3reUmPlPZGmbP5+HKCzc=";

	    // compress it
	    ByteArrayOutputStream baostream = new ByteArrayOutputStream();
	    OutputStream outStream = new GZIPOutputStream(baostream);
	    outStream.write(orig.getBytes("UTF-8"));
	    outStream.close();
	    byte[] compressedBytes = baostream.toByteArray(); // toString not always possible
	    // uncompress it
	    InputStream inStream = new GZIPInputStream(
	            new ByteArrayInputStream(compressedBytes));
	    ByteArrayOutputStream baoStream2 = new ByteArrayOutputStream();
	    byte[] buffer = new byte[8192];
	    int len;
	    while ((len = inStream.read(buffer)) > 0) {
	        baoStream2.write(buffer, 0, len);
	    }
	    String uncompressedStr = baoStream2.toString("UTF-8");

	    System.out.println("orig: " + orig);
	    System.out.println("unc:  " + uncompressedStr);
	}
	
	
	public static final String dumpBytes(byte[] buffer) {
        if (buffer == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.setLength(0);
        for (int i = 0; i < buffer.length; i++) {
            sb.append((char) (HEX_CHAR[(buffer[i] & 0x00F0) >> 4]))
                    .append((char) (HEX_CHAR[buffer[i] & 0x000F])).append(' ');
        }
        return sb.toString();
    }
	
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
//	@Test
	public void gzip() throws IOException{
		System.out.println("original: "+original);
		byte[] compressed=compressToByte(original, "UTF8");
		String fromBytes=new String(compressed, "UTF8");
		System.out.println(" l: "+fromBytes.length());
		System.out.println(" fromBytes: "+fromBytes);
		String decompressed= unCompressString(compressed, "UTF8");
		System.out.println("decompressed: "+decompressed);
	}
	public static byte[] compressToByte(final String data, final String encoding)
		    throws IOException
		{
		    if (data == null || data.length() == 0)
		    {
		        return null;
		    }
		    else
		    {
		        byte[] bytes = data.getBytes(encoding);
		        ByteArrayOutputStream baos = new ByteArrayOutputStream();
		        GZIPOutputStream os = new GZIPOutputStream(baos);
		        os.write(bytes, 0, bytes.length);
		        os.close();
		        byte[] result = baos.toByteArray();
		        return result;
		    }
		}
	public static String unCompressString(final byte[] data, final String encoding)
		    throws IOException
		{
		    if (data == null || data.length == 0)
		    {
		        return null;
		    }
		    else
		    {
		        ByteArrayInputStream bais = new ByteArrayInputStream(data);
		        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		        GZIPInputStream is = new GZIPInputStream(bais);
		        byte[] tmp = new byte[256];
		        while (true)
		        {
		            int r = is.read(tmp);
		            if (r < 0)
		            {
		                break;
		            }
		            buffer.write(tmp, 0, r);
		        }
		        is.close();

		        byte[] content = buffer.toByteArray();
		        return new String(content, 0, content.length, encoding);
		    }
		}
	
//	@Test
	public void deflatertest(){
		try {
		     // Encode a String into bytes
		     String inputString = "blahblahblah";
		     System.out.println("inputString: "+inputString+ " length: "+inputString.length());
		     byte[] input = inputString.getBytes("UTF8");
		     System.out.println("inputBytes "+input.length);
		     // Compress the bytes
		     byte[] output = new byte[100];
		     Deflater compresser = new Deflater();
		     compresser.setStrategy(Deflater.HUFFMAN_ONLY);
		     compresser.setInput(input);
		     compresser.finish();
		     int compressedDataLength = compresser.deflate(output);
		     compresser.end();
		     System.out.println("compressed "+compresser);
		     System.out.println("length: "+compresser.toString().length()+"   length "+compressedDataLength);
		     String outString = new String(output);
		     System.out.println("length: "+outString.length()+"   string "+outString);
		     // Decompress the bytes
		     Inflater decompresser = new Inflater();
		     decompresser.setInput(output, 0, compressedDataLength);
		     byte[] result = new byte[100];
		     int resultLength = decompresser.inflate(result);
		     decompresser.end();

		     // Decode the bytes into a String
		     String outputString = new String(result, 0, resultLength, "UTF8");
		     System.out.println("output: "+outputString);
		 } catch(java.io.UnsupportedEncodingException ex) {
		     // handle
		 } catch (java.util.zip.DataFormatException ex) {
		     // handle
		 }
		 
	}
	
	
//	@Test
	public void compressgZipTest() {
		try {
			System.out.println("original: "+original);
			String compressed=compress(original);
			System.out.println("compressed: "+compressed);
			String decompressed= decompress(compressed);
			System.out.println("decompressed: "+decompressed);
		} catch (IOException e) {
			fail("Not yet implemented");
		}
		
	}

	 public static String compress(String str) throws IOException {
	        if (str == null || str.length() == 0) {
	            return str;
	        }
	        System.out.println("String length : " + str.length());
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        GZIPOutputStream gzip = new GZIPOutputStream(out);
	        gzip.write(str.getBytes());
	        gzip.close();
	        String outStr = out.toString("ISO-8859-1");
//	        String outStr = out.toString("UTF-8");
	        System.out.println("Output String lenght : " + outStr.length());
	        return outStr;
	     }
	    
	    public static String decompress(String str) throws IOException {
	        if (str == null || str.length() == 0) {
	            return str;
	        }
	        System.out.println("Input String length : " + str.length());
	        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("ISO-8859-1")));
//	        GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(str.getBytes("UTF-8")));
	        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "ISO-8859-1"));
//	        BufferedReader bf = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
	        String outStr = "";
	        String line;
	        while ((line=bf.readLine())!=null) {
	          outStr += line;
	        }
	        System.out.println("Output String lenght : " + outStr.length());
	        return outStr;
	     }
	  
	
	
}
