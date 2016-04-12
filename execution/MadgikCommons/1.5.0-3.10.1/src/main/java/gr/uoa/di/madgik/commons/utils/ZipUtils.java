package gr.uoa.di.madgik.commons.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterInputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZipUtils
{
	public static byte[] ZipBytes(byte []input) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedOutputStream bufos = new BufferedOutputStream(new GZIPOutputStream(bos));
		bufos.write(input);
		bufos.close();
		byte[] retval = bos.toByteArray();
		bos.close();
		return retval;
	}

	public static byte[] ZipBytes(byte []input,int length) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedOutputStream bufos = new BufferedOutputStream(new GZIPOutputStream(bos));
		bufos.write(input,0,length);
		bufos.close();
		byte[] retval = bos.toByteArray();
		bos.close();
		return retval;
	}

	public static byte[] ZipBytes(byte []input,int offset, int length) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedOutputStream bufos = new BufferedOutputStream(new GZIPOutputStream(bos));
		bufos.write(input,offset,length);
		bufos.close();
		byte[] retval = bos.toByteArray();
		bos.close();
		return retval;
	}

	public static byte[] UnzipBytes(byte []input) throws IOException
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(input);
		BufferedInputStream bufis = new BufferedInputStream(new GZIPInputStream(bis));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len;
		while ((len = bufis.read(buf)) > 0)
		{
			bos.write(buf, 0, len);
		}
		bos.flush();
		bis.close();
		bufis.close();
		bos.close();
		return bos.toByteArray();
	}
	
	public static byte[] ZipString(String input) throws IOException
	{
		return ZipUtils.ZipString(input, "UTF-8");
	}

	public static byte[] ZipString(String input, String CharSet) throws IOException
	{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		BufferedOutputStream bufos = new BufferedOutputStream(new GZIPOutputStream(bos));
		bufos.write(input.getBytes());
		bufos.close();
		byte[] retval = bos.toByteArray();
		bos.close();
		return retval;
	}

	public static String UnzipString(byte[] bytes) throws IOException
	{
		return ZipUtils.UnzipString(bytes, "UTF-8");
	}
	
	public static String UnzipString(byte[] bytes,String CharSet) throws IOException
	{
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		BufferedInputStream bufis = new BufferedInputStream(new GZIPInputStream(bis));
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int len;
		while ((len = bufis.read(buf)) > 0)
		{
			bos.write(buf, 0, len);
		}
		String retval = new String(bos.toByteArray(),CharSet);
		bis.close();
		bufis.close();
		bos.close();
		return retval;
	}
	
	public static void UnzipFile(File infile, File outfile) throws FileNotFoundException, IOException {
//		GZIPInputStream in = new GZIPInputStream(new FileInputStream(infile));
		DeflaterInputStream in = new DeflaterInputStream(new FileInputStream(infile), new Deflater(3), 4*1024);
		
		
		FileOutputStream out = new FileOutputStream(outfile);
		
        byte[] buf = new byte[4*1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
	}
}
