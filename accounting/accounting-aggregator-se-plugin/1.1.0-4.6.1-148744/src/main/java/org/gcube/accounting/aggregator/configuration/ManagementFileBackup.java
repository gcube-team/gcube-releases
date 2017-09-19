package org.gcube.accounting.aggregator.configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.couchbase.client.java.document.JsonDocument;

/**
 * @author Alessandro Pieve (ISTI - CNR) 
 *
 */

public class ManagementFileBackup {

	private static Logger logger = LoggerFactory.getLogger(ManagementFileBackup.class);
	/**
	 * The singleton instance of the ConfigurationFileBackup.
	 */
	private static ManagementFileBackup instance;

	/**
	 * Construct a new ConfigurationFileBackup
	 */
	private ManagementFileBackup(){
		File DirIns = new File(Constant.PATH_DIR_BACKUP_INSERT);
		if (!DirIns.exists()) {
			DirIns.mkdir();
		}
		File DirDel = new File(Constant.PATH_DIR_BACKUP_DELETE);
		if (!DirDel.exists()) {
			DirDel.mkdir();
		}

	}
	
	public static ManagementFileBackup getInstance() {
		if (instance == null) {
			instance = new ManagementFileBackup();
		}
		return instance;
	}
	/**
	 * Create a file to string for recovery operation
	 * @param list
	 * @param nameFile
	 * @param type (for a recovery a insert o delete type record
	 * @return
	 */
	public  boolean onCreateStringToFile(List<JsonDocument> listJson,String nameFile, Boolean type){
		try {
			File file;
			if (type)
				file = new File(Constant.PATH_DIR_BACKUP_INSERT+"/"+nameFile.replace(",", "_"));
			else
				file =new File(Constant.PATH_DIR_BACKUP_DELETE+"/"+nameFile.replace(",", "_"));


			BufferedWriter writer = null;
			writer = new BufferedWriter(new FileWriter(file,true));
			writer.write("{\"new_edits\":false,\"docs\":[");
			writer.newLine();
			int count=1;
			for (JsonDocument row:listJson) {
				if (count==listJson.size())
					writer.write(row.content().toString());
				else
					writer.write(row.content().toString()+",");
				writer.newLine();
				count++;
			}
			writer.write("]}");
			writer.newLine();
			writer.close();

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			return false;
		}
		return true;

	}

	/**
	 * Delete a temporany file 
	 * @param nameFile
	 * @return
	 */	
	public boolean onDeleteFile(String nameFile, Boolean type){
		try {
			File file;
			if (type)
				file = new File(Constant.PATH_DIR_BACKUP_INSERT+"/"+nameFile.replace(",", "_"));
			else
				file = new File(Constant.PATH_DIR_BACKUP_DELETE+"/"+nameFile.replace(",", "_"));			
			file.delete();
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage());
			return false;
		}
		return true;

	}
	
}
