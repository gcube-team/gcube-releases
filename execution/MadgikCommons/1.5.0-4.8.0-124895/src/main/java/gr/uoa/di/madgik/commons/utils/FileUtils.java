package gr.uoa.di.madgik.commons.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FileUtils
{
	public static String ReadFileToString(File file) throws IOException
	{
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = new BufferedReader(new FileReader(file));

		char[] chars = new char[1024];
		while (true)
		{
			int numRead = reader.read(chars);
			if (numRead <= -1) break;
			sb.append(String.valueOf(chars));
		}
		reader.close();
		return sb.toString();
	}
	
	public static void MakeFileExecutable(File file) throws Exception
	{
		if(!file.exists()) return;
		ProcessBuilder build=new ProcessBuilder(new String[]{"/bin/chmod","u+x",file.getAbsolutePath()});
		Process p = build.start();
		p.waitFor();
	}
	
	public static void MakeFileReadWriteOwner(File file) throws Exception
	{
		if(!file.exists()) return;
		ProcessBuilder build=new ProcessBuilder(new String[]{"/bin/chmod","0600",file.getAbsolutePath()});
		Process p = build.start();
		p.waitFor();
	}
	
	public static void MakeFilePermissions(File file, String perms) throws Exception
	{
		if(!file.exists()) return;
		ProcessBuilder build=new ProcessBuilder(new String[]{"/bin/chmod",perms,file.getAbsolutePath()});
		Process p = build.start();
		p.waitFor();
	}
	
	public static void Copy(File source, OutputStream stream) throws IOException
	{
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
		BufferedOutputStream out = new BufferedOutputStream(stream);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) 
		{
			out.write(buf, 0, len);
		}
		in.close();
		out.flush();
		out.close();
	}
	
	public static void Copy(InputStream stream, File target) throws IOException
	{
		BufferedOutputStream out=new BufferedOutputStream(new FileOutputStream(target));
		BufferedInputStream in=new BufferedInputStream(stream);
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) 
		{
			out.write(buf, 0, len);
			out.flush();
		}
		in.close();
		out.flush();
		out.close();
	}

	public static void Copy(File src, File dest) throws IOException
	{
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dest).getChannel();
		try
		{
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e)
		{
			throw e;
		} finally
		{
			if (inChannel != null)
			{
				inChannel.close();
			}
			if (outChannel != null)
			{
				outChannel.close();
			}
		}
	}
	
	public static void Copy(File src, RandomAccessFile dest,boolean Compress) throws IOException
	{
		FileInputStream fin = new FileInputStream(src);
		BufferedInputStream bin = new BufferedInputStream(fin);
		long length = src.length();
		long offset = dest.getFilePointer();
		long count = 0;
		dest.writeLong(length);
		byte[] buf = new byte[1024 * 4];
		while (true)
		{
			int n = bin.read(buf);
			if (n < 0)
			{
				break;
			}
			count += n;
			if(!Compress) dest.write(buf, 0, n);
			else
			{
				byte[] b=ZipUtils.ZipBytes(buf, n);
				dest.writeInt(b.length);
				dest.write(b);
			}
		}
		long endoffset = dest.getFilePointer();
		dest.seek(offset);
		dest.writeLong(count);
		dest.seek(endoffset);
		bin.close();
		fin.close();
		buf=null;
	}
	
	public static void Copy(RandomAccessFile src, File dest,boolean Uncompress) throws IOException
	{
		FileOutputStream fout = new FileOutputStream(dest, false);
		BufferedOutputStream bout = new BufferedOutputStream(fout);
		long length = src.readLong();
		byte[] buf = new byte[1024 * 4];
		long readSoFar = 0;
		int readnow = buf.length;
		while (true)
		{
			readnow = buf.length;
			long remaining = length - readSoFar;
			if (remaining <= 0)
			{
				break;
			}
			if (remaining < buf.length)
			{
				readnow = (int) remaining;
			}
			if(!Uncompress)
			{
				src.readFully(buf, 0, readnow);
				bout.write(buf, 0, readnow);
				readSoFar += readnow;
			}
			else
			{
				int lenCur=src.readInt();
				byte[] b=new byte[lenCur];
				src.readFully(b);
				b=ZipUtils.UnzipBytes(b);
				bout.write(b);
				readSoFar+=b.length;
			}
		}
		bout.flush();
		bout.close();
		fout.close();
		buf=null;
	}
	
	public static void CleanUp(File target)
	{
		if(!target.exists()) return;
		if(target.isFile()) target.delete();
		else if(target.isDirectory())
		{
			for(File content : target.listFiles()) FileUtils.CleanUp(content);
			target.delete();
		}
	}
}
