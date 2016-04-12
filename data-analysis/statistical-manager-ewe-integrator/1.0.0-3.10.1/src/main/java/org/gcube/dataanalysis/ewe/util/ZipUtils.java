package org.gcube.dataanalysis.ewe.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;

class ZipArchive {

	private File root;
	private Collection<String> exclude;
	
	
	public ZipArchive(File root) {
		this.root = root;
		this.exclude = new ArrayList<>();
	}

	public void exclude(String[] exclude) {
		for(String e:exclude) {
			AnalysisLogger.getLogger().debug("The zip file will NOT contain '" + e + "'");
			this.exclude.add(e);
		}
	}
	
    private String generateZipEntry(String file){
    	if(file.equals(root.getAbsoluteFile().toString()))
    		return "";
    	else
    		return file.substring(root.getAbsoluteFile().toString().length()+1, file.length());
    }
    
	private List<String> generateFileList(File node) {
		List<String> out = new ArrayList<>();

		String zipEntry = generateZipEntry(node.getAbsoluteFile().toString()); 

		if(!this.exclude.contains(zipEntry)) {
			// add file only
			if (node.isFile()) {
				out.add(zipEntry);
			}
	
			if (node.isDirectory()) {
				String[] subNote = node.list();
				for (String filename : subNote) {
					out.addAll(generateFileList(new File(node, filename)));
				}
			}
		}
		else {
			AnalysisLogger.getLogger().debug("Excluding " + zipEntry);
		}
		return out;

	}

    public void zipToFile(File zipFile){

        byte[] buffer = new byte[1024];
       	
        try{
       		
       	FileOutputStream fos = new FileOutputStream(zipFile);
       	ZipOutputStream zos = new ZipOutputStream(fos);
       		
       	AnalysisLogger.getLogger().debug("Output to Zip : " + zipFile);
       		
       	for(String file : this.generateFileList(this.root)) {
       			
       		AnalysisLogger.getLogger().debug("File Added : " + file);
       		ZipEntry ze= new ZipEntry(file);
           	zos.putNextEntry(ze);
                  
           	FileInputStream in = 
                          new FileInputStream(this.root + File.separator + file);
          	   
           	int len;
           	while ((len = in.read(buffer)) > 0) {
           		zos.write(buffer, 0, len);
           	}
                  
           	in.close();
       	}
       		
       	zos.closeEntry();
       	//remember close it
       	zos.close();
             
       	AnalysisLogger.getLogger().debug("Done");
       }catch(IOException ex){
          ex.printStackTrace();   
       }
      }	
	
}

public final class ZipUtils {

	public static void unzipFile(File zipped, File directory) throws IOException {
		AnalysisLogger.getLogger().debug("Unzipping file '"+zipped+"' into '"+directory+"'");

	    ZipFile zipFile = new ZipFile(zipped);
	    try {
	      Enumeration<? extends ZipEntry> entries = zipFile.entries();
	      while (entries.hasMoreElements()) {
	        ZipEntry entry = entries.nextElement();
	        File entryDestination = new File(directory, entry.getName());
	        if (entry.isDirectory()) {
	            entryDestination.mkdirs();
	        } else {
	            entryDestination.getParentFile().mkdirs();
	            InputStream in = zipFile.getInputStream(entry);
	            OutputStream out = new FileOutputStream(entryDestination);
	            IOUtils.copy(in, out);
	            IOUtils.closeQuietly(in);
	            out.close();
	        }
	      }
	    } finally {
	      zipFile.close();
	    }		
		
	    AnalysisLogger.getLogger().debug("Unzipped.");
	}
	
	public static void zipFolder(File root, File zipFile) {
		new ZipArchive(root).zipToFile(zipFile);
	}

	public static void zipFolder(File root, File zipFile, String[] exclude) {
		ZipArchive za = new ZipArchive(root);
		za.exclude(exclude);
		za.zipToFile(zipFile);
	}

}