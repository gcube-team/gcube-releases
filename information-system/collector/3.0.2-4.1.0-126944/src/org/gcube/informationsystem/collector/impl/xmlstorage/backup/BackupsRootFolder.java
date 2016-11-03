package org.gcube.informationsystem.collector.impl.xmlstorage.backup;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.contexts.ICServiceContext;

/**
 * An abstract representation of the backups root folder
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class BackupsRootFolder {

    private static final long serialVersionUID = 6054398926161291140L;
    
    private static File rootFolder;  
    
    private static int maxBackups = Integer.valueOf((String) ICServiceContext.getContext().getProperty("maxBackups", true));
		
    private static GCUBELog logger = new GCUBELog(BackupsRootFolder.class);
    
    static {
	String backupDir = (String) ICServiceContext.getContext().getProperty("backupDir", true);	
	rootFolder = new File(backupDir);	
        if (! rootFolder.isAbsolute()) {//make it absolute
            backupDir = ICServiceContext.getContext().getPersistenceRoot().getAbsolutePath() + File.separator + backupDir + File.separator;
            rootFolder = new File(backupDir);
        }
    }
    
    /**
     * Creates a new backup folder in the BackupsRoot folder. <br/>
     * The root folder is specified as value of the <em>backupDir</em> variable in the service's JNDI file
     * @return the new backup Folder
     * @throws IOException if the creation fails
     */
    public static BackupFolder createBackupFolder() throws IOException {
	
	final String backupFolder = rootFolder.getAbsolutePath() +  File.separator + buildBackupFolderName(); 
	File backup = new File(backupFolder);
	if (!backup.mkdirs())
	    throw new IOException("Unable to create the backup folder");
	
	return new BackupFolder(backup.getAbsolutePath());	   
    }
    
    /**
     * Manages the backup folder as a circular queue by maintaining only the latest N backups. <br/>
     * N is specified as value of the <em>maxBackups</em> variable in the service's JNDI file
     * @param filter the file filter to identify valid backups in the BackupsRoot
     */
    public static void manageOldBackups(FileFilter ... filter) throws IOException {
	
	if (maxBackups <= 0)// all the backups will be maintained
	    return;
	
	if (!rootFolder.exists()) //what am I supposed to delete?
	    throw new IOException("Unable to read the backup folder");
	
	File[] files = (filter != null && filter.length > 0) ? rootFolder.listFiles(filter[0]) : rootFolder.listFiles();
	logger.trace("Number of backups found:" + files.length);
	
	//sort by modification date descending
	Arrays.sort(files, new Comparator<File>() {
		public int compare(File file1, File file2) {					
		    return new Long(file2.lastModified()).compareTo(new Long(file1.lastModified()));
		}
	}); 
	//bye bye old guys
	for (int i = maxBackups; i < files.length; i++) deleteBackup(files[i]);	
    }
    
    /**
     * Gets the last modified backup in the BackupsRoot folder
     * @param <T> the class of the backup folder
     * @param backupType the type class
     * @param filter the file filter to identify valid backups in the BackupsRoot
     * @return
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public static <T extends BackupFolder> T getLastBackup(Class<T>  backupType, FileFilter ... filter) throws IOException {
	
	if (!rootFolder.exists())
	    throw new IOException("Unable to read the backup folder");
	
	//look for the most recent backup
	File[] files = (filter != null && filter.length > 0) ? rootFolder.listFiles(filter[0]) : rootFolder.listFiles();
	
	long lastModified = 0;
	T lastBackup = null;
	for (File file : files) {
	    logger.trace("Checking backup content: " + file.getAbsolutePath());
	    if (file.lastModified() >= lastModified) {
		try {		    	    			
		    Class<T> clazz = (Class<T>) Class.forName(backupType.getName());		    
		    Constructor<T> constructor = clazz.getConstructor(new Class[] {String.class});		    
		    lastBackup = constructor.newInstance(file.getAbsolutePath());				    		
		}catch (Exception e) {
		    logger.warn("invalid backup folder ("+ file.getAbsolutePath() +")", e);
		}		
	    }
	}
	if (lastBackup == null)
	    throw new IOException("Unable to find a valid backup folder");
	
	logger.info("last backup found in " + lastBackup.getAbsolutePath());
	return lastBackup;	
	
    }
    
    private static String buildBackupFolderName() {
	Calendar date = Calendar.getInstance();
	StringBuilder name = new StringBuilder();
	name.append(date.get(Calendar.DAY_OF_MONTH));
	name.append("-");
	name.append(Calendar.getInstance().get(Calendar.MONTH) + 1 );
	name.append("-");
	name.append(Calendar.getInstance().get(Calendar.YEAR));
	name.append("-");
	name.append(System.currentTimeMillis());
	
	return name.toString();
    }
    
    /**
     * Deletes the given backup
     * @param backup the backup to delete
     * @return true if the deletion worked, false otherwise
     */
    private static boolean deleteBackup(File backup) {
        if (backup.isDirectory()) {
            String[] children = backup.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteBackup(new File(backup, children[i]));
                if (!success) return false;
            }
        }    
        // the backup directory is now empty or the file can now be deleted 
        return backup.delete();
    }

}
