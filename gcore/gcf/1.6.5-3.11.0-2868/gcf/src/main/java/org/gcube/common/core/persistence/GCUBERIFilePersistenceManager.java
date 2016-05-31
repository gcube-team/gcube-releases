package org.gcube.common.core.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.gcube.common.core.contexts.GCUBEServiceContext;

/**
 * A {@link GCUBERIPersistenceManager} for Running Instances that persist their state on the file system.
 * 
 * @author Fabio Simeoni (University of Strathclyde)
 *
 */
public abstract class GCUBERIFilePersistenceManager extends GCUBERIPersistenceManager {

	/** Single-file state serialisation .*/
	private File stateFile;
	
	/** Auxiliary buffer for I/O operations. */
	private static byte[] buffer = new byte[8192];
    
	/** 
	 * Creates a new instance for a given service and from a given configuration profile.
	 * @param ctxt the context of the service.
	 * @param profile the configuration profile.
	 */
	public GCUBERIFilePersistenceManager(GCUBEServiceContext ctxt, GCUBERIPersistenceManagerProfile profile){
		super(ctxt,profile);
	}
	
	/**{@inheritDoc} */
	@Override protected void commitState() throws Exception {
		this.packageState();
		this.storeState();
    }
	
	/**{@inheritDoc} */
	@Override protected void recoverState() throws StateNotFoundException, Exception {
		if (!isEmpty(ctxt.getPersistenceRoot())) {logger.info("local state is not empty, retaining it");return;}
		this.loadState();
		this.unpackageState();
	}
	
	/**
	 * Used internally to asses whether the storage directory contains some actual state.
	 * @param dir a directory.
	 * @return <code>true</code> if the directory does not contain any files, recursively, <code>false</code> otherwise. 
	 */
	private boolean isEmpty(File dir) {return this.getState(dir).size()>0;}
	
	/**
	 * Used internally to enumerate the files that comprise the state to persist (i.e. filtered by excludes directives),
	 * starting from a given directory.
	 * @param dir the start directory.
	 * @param files (optional) the list of files to persist accumulated during recursive invocations.
	 * @return the files to persist.
	 */
	protected List<File> getState(File dir, List<File> ... files) {
		if (files.length==0) files = new List[]{new ArrayList<File>()};
		if (dir.exists()) 
			for (File f : dir.listFiles(new FilenameFilter() {public boolean accept(File dir, String name) {return isExclude(name);}})) 
				if (f.isFile()) files[0].add(f); 
				else this.getState(f,files); //ignore return value, which is only for top-level invocations
		return files[0];
	}
	
	/**
	 * Used internally to check whether a given string matches at least one of the exclude directives.
	 * @param name the name to match.
	 * @return <code>true</code> if the string matches, <code>false</code> otherwise.
	 */
	public boolean isExclude(String name) {
		for (Pattern p : excludes) if (p.matcher(name).matches()) return true;
		return false;
	}
	/**
	 * Returns the file in which state is to be packaged. 
	 * @return the file.
	 */
	protected File getStateFile() throws IOException {
		if (stateFile==null) stateFile= File.createTempFile(ctxt.getInstance().getID(),".state");
		return stateFile;
	}
	
	/**
	 * Packages the state of the running instance in a single file.
	 * @throws Exception if the state could not be packaged.
	 */
	protected void packageState() throws Exception {
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(this.getStateFile())));
		packageState(this.getState(ctxt.getPersistenceRoot()),out);
		out.close();
	}
	
	/**
	 * Recursively packages the contents of a folder into zipped output streamed.
	 * @param dir the folder.
	 * @param zstream the stream.
	 * @throws Exception if the folder could not be packaged.
	 */
	private void packageState(List<File> files, ZipOutputStream zstream) throws Exception { 
		int read = 0;
	    for (File f : files) {
	        FileInputStream in = new FileInputStream(f);
	        ZipEntry entry = new ZipEntry(f.getPath().substring(ctxt.getPersistenceRoot().getPath().length() + 1));
	        zstream.putNextEntry(entry);
	        logger.debug("adding:"+entry.getName());
	        while (-1 != (read = in.read(buffer))) zstream.write(buffer, 0, read);
	        zstream.closeEntry();
	        in.close();
	     }
	}
	
	/**
	 * Unpackages the state of the running instances from a single file.
	 * @throws Exception if the state could not be unpackaged.
	 */
	protected void unpackageState() throws Exception {
		   FileInputStream fileInputStream = new FileInputStream(getStateFile());
		   ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
	      
		   ZipEntry entry;
	       while((entry = zipInputStream.getNextEntry()) != null){
	    	   File file=new File(ctxt.getPersistenceRoot(),entry.getName());
	    	
	    	   if(file.getParentFile()!=null && !file.getParentFile().exists()){
	    		   file.getParentFile().mkdirs();
	    	   }
	          
	          FileOutputStream fileOutputStream = new FileOutputStream(file);
	          BufferedOutputStream bufferedOutputStream= new BufferedOutputStream(fileOutputStream);
	          int count; 
	          
	          while ((count = zipInputStream.read(buffer, 0, buffer.length)) != -1)
	             bufferedOutputStream.write(buffer, 0, count);
	          bufferedOutputStream.flush();
	          bufferedOutputStream.close();
	       }
	       
	       zipInputStream.close();	
		
	}
	
	/**
	 * Loads the state of the running instance from persistent storage.
	 * @throws Exception if the state could not be loaded.
	 */
	protected abstract void loadState() throws StateNotFoundException, Exception;
	/**
	 * Stores the state of the running instance into persistent storage.
	 * @throws Exception if the state could not be persisted.
	 */
	protected abstract void storeState() throws Exception;

	
}
