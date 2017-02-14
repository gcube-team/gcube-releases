package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.tar.TarEntry;
import org.apache.commons.compress.tar.TarInputStream;
import org.apache.commons.compress.tar.TarOutputStream;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.porttypes.ServiceContext;


/**
 * Archive Management Class
 * @author Luca Frosini (ISTI-CNR)
 */
public class ArchiveManagement {

	/** 
	 * Class logger. 
	 */
	protected static final GCUBELog logger = new GCUBELog(ArchiveManagement.class);

	/**
	 * Uncompress Archive Method ()
	 * @param sourceArchive source archive
	 * @throws Exception if the uncompress operation fails
	 */
	public static void unTarGz(final File sourceArchive) throws Exception {

		logger.debug("Uncompressing TAR GZ archive : " + sourceArchive.getAbsolutePath());

		try {
			final GZIPInputStream in = new GZIPInputStream(new FileInputStream(sourceArchive));
			String name = sourceArchive.getName();
			if(name.contains(".tar.gz")){
				name = name.replace(".gz", "");
			}else if(name.contains(".tgz")){
				name = name.replace("tgz", "tar");
			}
			final File tarArchive = new File(sourceArchive.getParentFile(),name);
			final OutputStream out = new FileOutputStream(tarArchive);			
			final byte[] buf = new byte[1024];
			int len;
			logger.debug("Unzipping tar.gz file");
			while((len = in.read(buf)) > 0) {
				out.write(buf,0, len);
			}
			in.close();
			out.close();
			logger.debug("Untar TAR file");
			final TarInputStream tis = new TarInputStream(new FileInputStream(tarArchive));
			TarEntry te = null;
			while((te = tis.getNextEntry()) != null) {
				logger.debug("Processing " + (te.isDirectory()?"directory : ":"file : ") + te.getName());
				File file = new File(sourceArchive.getParent(),te.getName());
				if (te.isDirectory()) {
					file.mkdirs();
				} else {
					tis.copyEntryContents(new FileOutputStream(file));
				}
			}
			tarArchive.delete();
			tis.close();
		} catch (Exception e) {
			logger.error("Unable to uncompress tar.gz",e);
			throw e;
		}

		logger.debug("TAR GZ file uncompressed successfully");

				
	}

	/**
	 * Compress Archive Method
	 * @param targetArchive archive file
	 * @param archiveFiles List of files to include in TAR GZ
	 * @throws Exception if the compression fails
	 */
	public static void createTarGz(final File targetArchive ,final List<File> archiveFiles) throws Exception {
		File[] arrayArchiveFiles = new File[archiveFiles.size()];
		for(int i=0; i<archiveFiles.size(); i++){
			arrayArchiveFiles[i] = archiveFiles.get(i);
		}
		createTarGz(targetArchive,arrayArchiveFiles);
	}
	
	
	/**
	 * Compress Archive Method. The first entry in the 
	 * @param targetArchive archive file
	 * @param archiveFiles Array of files to include in TAR GZ. The first entry 
	 * in the array is the source root directory. It is used to calculate relative path.
	 * If it is not supplied the archive will not have directory tree.
	 * @throws Exception if the compression fails
	 */
	public static void createTarGz(final File targetArchive ,final File[] archiveFiles) throws Exception {

		logger.debug("Creating TAR GZ file");

		final File parentDirectory = targetArchive.getParentFile();

		
		File rootDirectory = null;
		if(archiveFiles[0].isDirectory()){
			rootDirectory = archiveFiles[0];
		}
		
		String name = targetArchive.getName();
		if(name.contains(".tar.gz")){
			name = name.replace(".gz", "");
		}else if(name.contains(".tgz")){
			name = name.replace("tgz", "tar");
		} else {
			Exception e = new Exception("The archive should have .tar.gz or .tgz extention");
			logger.error(e);
			throw e;
		}

		final File tarFile = new File(parentDirectory,name);

		logger.debug("Opening TAR file: " + tarFile.getName());

		final FileOutputStream stream = new FileOutputStream(tarFile);
		final TarOutputStream out = new TarOutputStream(stream);
		out.setLongFileMode(TarOutputStream.LONGFILE_GNU);

		byte buffer[] = new byte[1024];

		for(File item : archiveFiles) {
					
			if(rootDirectory!=null && item==rootDirectory){
				continue;
			}
			logger.debug("Adding " + item.getName() + " to TAR");
			final TarEntry tarEntry = new TarEntry(item);
			//tarAdd.setModTime(file.lastModified());
			String relativePath = item.getName();
			if(rootDirectory!=null){
				relativePath = item.getAbsolutePath().replace(rootDirectory.getAbsolutePath()+File.separator, "");
			}
			tarEntry.setName(relativePath);
			tarEntry.setSize(item.length());
			out.putNextEntry(tarEntry);
			FileInputStream in = new FileInputStream(item);
			while (true) {
				int nRead = in.read(buffer, 0, buffer.length);
				if (nRead <= 0)
					break;
				out.write(buffer, 0, nRead);
			}
			in.close();				
			out.closeEntry();
		}
		logger.debug("Closing TAR file: " + tarFile.getName());
		out.close();
		stream.close();
		logger.debug ("Compressing tar file = " + tarFile.getName());
		final String outFileName = tarFile.getAbsoluteFile() + ".gz";
		final GZIPOutputStream outGZ = new GZIPOutputStream(new FileOutputStream(outFileName));
		final FileInputStream inTAR = new FileInputStream(tarFile);
		int len;
		while((len = inTAR.read(buffer)) > 0) {
			outGZ.write(buffer, 0, len);
		}
		inTAR.close();
		outGZ.finish();
		outGZ.close();
		logger.debug("Deleting TAR file");
		logger.debug(tarFile.getName() + " " + (tarFile.delete()?"deleted":"not deleted"));
		logger.debug(targetArchive.getName() + " successfull created");

	}

	public File extractFileFromArchive(File tar, String pathToFile) throws IOException{
//		URL url = new URL(tarUrl);
		logger.debug("extractFileFromArchive method");
		GZIPInputStream gis=new GZIPInputStream(new FileInputStream(tar));
		TarInputStream tin = new TarInputStream(gis);
		TarEntry te = tin.getNextEntry();
		while (!te.getName().equals(pathToFile)) {
//		    tin.closeEntry(); // not sure whether this is necessary
			logger.debug("processing file: "+te.getName());
		    te = tin.getNextEntry();
		}
		logger.debug("file in archive founded: "+te.getName());
		byte[] bytes = new byte[(int) te.getSize()];
		tin.read(bytes);
    	String cfgDir= (String)ServiceContext.getContext().getProperty("configDir", false);
		return new NexusRestConnector().byteToFile(cfgDir, pathToFile, bytes);
	}
	
	public void insertFileToArchive(File tar, String pathToFile) throws IOException{
		int buffersize = 1024;  
		byte[] buf = new byte[buffersize];
		TarOutputStream tos = new TarOutputStream(new FileOutputStream(tar));
		File profile=new File(pathToFile);
		FileInputStream fis = new FileInputStream(profile);  
	 //BufferedInputStream bis = new BufferedInputStream(fis,buffersize);  
		TarEntry te = new TarEntry(profile.getPath());  
		tos.putNextEntry(te);  
		int count = 0;  
		while((count = fis.read(buf,0,buffersize)) != -1)       
		{  
			tos.write(buf,0,count);      
		}  
		tos.closeEntry();  
		fis.close(); 
	}
	
	public void replaceFilesToTarGz(File source, File file)
	{
		logger.debug("replaceFilesToTarGz method");
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
}
