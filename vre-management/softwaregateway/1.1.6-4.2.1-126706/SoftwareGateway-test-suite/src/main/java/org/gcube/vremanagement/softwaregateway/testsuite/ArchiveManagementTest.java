package org.gcube.vremanagement.softwaregateway.testsuite;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.compress.tar.TarEntry;
import org.apache.commons.compress.tar.TarInputStream;
import org.apache.commons.compress.tar.TarOutputStream;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.util.NexusRestConnector;
import org.junit.Test;
import org.junit.Before;

public class ArchiveManagementTest {

	File tar= null;
	File tarCopy= null;
	String pathToFile=null;
	String pathToFile2=null;
	File source=null;
	File file=null;
	 // 4MB buffer
    private static final byte[] BUFFER = new byte[4096 * 1024];

    @Before
	public void initialize(){
		tar= new File("settings/org.gcube.content-management.ecological-modelling-servicearchive-1.3.0-0.tar.gz");
		tarCopy= new File("settings/org.gcube.content-management.ecological-modelling-servicearchive-1.3.0-0_copy.tar.gz");
		pathToFile="org.gcube.content-management.ecological-modelling-servicearchive-1.3.0-0/profile.xml";
		pathToFile2="settings/profile.xml";
		source=new File("settings/org.gcube.content-management.ecological-modelling-servicearchive-1.3.0-0.tar.gz");
		file=new File("settings/profile.xml");
	}
	
	
	public void extractFileFromArchive() throws IOException{
		GZIPInputStream gis=new GZIPInputStream(new FileInputStream(tar));
		TarInputStream tin = new TarInputStream(gis);
		TarEntry te = tin.getNextEntry();
		while (!te.getName().equals(pathToFile)) {
//		    tin.closeEntry(); // not sure whether this is necessary
		    te = tin.getNextEntry();
		    System.out.println("entry found: "+te.getName());
		}
		byte[] bytes = new byte[(int) te.getSize()];
		tin.read(bytes);
		new NexusRestConnector().byteToFile("settings", "profileextracted.xml", bytes);
	}

	
	@Test
	public void addFilesToTar()
	{
	    try
	    {
	        File tmpZip = File.createTempFile(source.getName(), null);
	        tmpZip.delete();
	        if(!source.renameTo(tmpZip))
	        {
	            throw new Exception("Could not make temp file (" + source.getName() + ")");
	        }
	        byte[] buffer = new byte[1024];
	        GZIPInputStream gis=new GZIPInputStream(new FileInputStream(tmpZip));
			TarInputStream tin = new TarInputStream(gis);
	    	GZIPOutputStream gos=new GZIPOutputStream(new FileOutputStream(source));
			TarOutputStream out = new TarOutputStream(gos);
			out.setLongFileMode(TarOutputStream.LONGFILE_GNU);  

//	        for(int i = 0; i < files.length; i++)
//	        {
            InputStream in = new FileInputStream(file);
            TarEntry te=new TarEntry(file.getName());
            te.setSize(file.length());
            out.putNextEntry(te);
            for(int read = in.read(buffer); read > -1; read = in.read(buffer))
            {
                out.write(buffer, 0, read);
            }
            out.closeEntry();
            in.close();
//	        }

	        for(TarEntry tentry = tin.getNextEntry(); tentry != null; tentry = tin.getNextEntry())
	        {
	            if(!tentry.getName().equals(file.getName())){
	            	out.putNextEntry(tentry);
	            	for(int read = tin.read(buffer); read > -1; read = tin.read(buffer))
		            {
		                out.write(buffer, 0, read);
		            }
	            	out.closeEntry();
	            }
	        }
	        out.close();
	        tmpZip.delete();
	    }
	    catch(Exception e)
	    {
	        e.printStackTrace();
	    }
	}
	
	
//	@Test
	public void insertFileToArchive() throws IOException{
		int buffersize = 1024;  
		byte[] buf = new byte[buffersize];
		GZIPOutputStream gos=new GZIPOutputStream(new FileOutputStream(tarCopy));
		TarOutputStream tos = new TarOutputStream(gos);
		tos.setLongFileMode(TarOutputStream.LONGFILE_GNU);  
		File profile=new File(pathToFile2);
		FileInputStream fis = new FileInputStream(profile);  
		TarEntry te = new TarEntry(profile);  
		tos.putNextEntry(te);  
		int count = 0;  
		while((count = fis.read(buf,0,buffersize)) != -1)       
		{ 
			System.out.println("count: "+count);
			tos.write(buf,0,count);      
		}
		tos.closeEntry();
	 //BufferedInputStream bis = new BufferedInputStream(fis,buffersize);  
		GZIPInputStream gis=new GZIPInputStream(new FileInputStream(tar));
		TarInputStream tin = new TarInputStream(gis);
		TarEntry tei = tin.getNextEntry();
		while (tei!=null) {
			if(!tei.getName().contains("profile.xml")){
			    System.out.println("entry found: "+tei.getName());
			    File f=tei.getFile();
			    if((f != null) && (f.isFile()) && (f.length() > 100)){
			    	tei.setSize(f.length());
			    }
				tos.putNextEntry(tei);  
				count = 0;  
			}
			tei = tin.getNextEntry();
		}
		tos.close();
		gos.close();
		fis.close();
	}
}
