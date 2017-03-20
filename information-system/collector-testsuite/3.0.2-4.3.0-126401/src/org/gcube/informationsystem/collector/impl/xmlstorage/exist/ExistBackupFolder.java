package org.gcube.informationsystem.collector.impl.xmlstorage.exist;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.xmlstorage.backup.BackupFolder;

/**
 * An abstract representation of an eXist backup folder
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class ExistBackupFolder extends BackupFolder {
   
    private static final long serialVersionUID = -6676338091088430159L;

    private static final GCUBELog logger = new GCUBELog(ExistBackupFolder.class);
    
    /** {@inheritDoc} */
    public ExistBackupFolder(String pathname) throws IOException {
	super(pathname);
    }

    protected static FileFilter getBackupFolderFilter() {
	return new FileFilter() {	    
	    public boolean accept(File dir) {
		logger.trace("Checking eXist root folder: " + dir.getName());
		File archive = lookForZipArchive(dir);
		if (archive != null)
		    return true;
		else //TODO: should check the existence of "db/__contents__.xml" ??
		    return false;
	    }};
    }

    private static File lookForZipArchive(final File filein) {
	File[] files = filein.listFiles();
	for (File file : files) { //check if there is a full*.zip file inside
	    logger.trace("Checking eXist backup file: " + file.getName());
		if ((file.getName().endsWith(".zip") || file.getName().endsWith(".ZIP"))
			&& ((file.getName().startsWith("full") || (file.getName().startsWith("FULL")))))        		
		    return file;        		
	}	
	return null;
    }
    
    /**
     * Gets the backup file from the folder
     * @return the backup file
     */
    protected File getBackupFile() throws IOException {
	File archive = lookForZipArchive(this);	
	if (archive != null) {
	    logger.trace("Valid ZIP archive found in " + archive.getAbsolutePath() );
	    return archive;
	}
	    
	throw new IOException("the eXist backup in " + this.getAbsolutePath() +" is not valid: no ZIP archive has been found inside");
    }
     
}
