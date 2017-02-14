package org.gcube.informationsystem.collector.impl.xmlstorage.backup;

import java.io.File;
import java.io.IOException;

/**
 * An abstract representation of a folder
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class BackupFolder extends File {

    private static final long serialVersionUID = 4774672121228681814L;
    
    /**
     * @param pathname
     * @throws IOException 
     */
    public BackupFolder(String pathname) throws IOException {
	super(pathname);
	if (! this.isDirectory()) 
	    throw new IOException();
	
	if (! this.isValid())
	    throw new IOException();
    }

    /**
     * Validates the backup folder
     * @return true if this is a valid backup folder
     */
    //gives a chance to subclasses to perform custom validation
     protected boolean isValid() { return true;}

}
