package org.gcube.datatransfer.scheduler.db.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;

import javax.jdo.Query;

import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestBackup {

	static File backupFolder = null;
	static DataTransferDBManager dbManager;

	public static void main(String[] args) {
		backupFolder = new File ((System.getenv("HOME") + File.separator + "DataTransferDBBackup"));
		backupFolder.mkdirs();

		dbManager=new DataTransferDBManager();
		String dbName="scheduler";
		String username= "root";
		String pass = "root";
		
		backupDB(dbName,username,pass,backupFolder.toString());
		//System.out.println(Integer.valueOf(dbManager.getNumberOfBackups())+"");
		try {
			dbManager.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static boolean backupDB(String dbName, String dbUserName, String dbPassword, String path) {
		if(!path.endsWith("/"))path=path+"/";

		int numberOfBackups =10;
		try {
			numberOfBackups= Integer.valueOf(10);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		String backupFilePath = getBackupFilePath(path, dbName, numberOfBackups);

		String executeCmd = "mysql -u " + dbUserName + " -p " + dbPassword + " " + dbName + " > " + backupFilePath;
		//ProcessBuilder builder = new ProcessBuilder("/bin/sh","-c",executeCmd);
		String[] executeCmdArray={"/bin/sh","-c",executeCmd};

		System.out.println("command: '"+executeCmd+"'");
		Process runtimeProcess;
		try {
			runtimeProcess = Runtime.getRuntime().exec(executeCmdArray);
			runtimeProcess.waitFor();  
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Could not create the backup");
			return false;
		}
		System.out.println("Backup created successfully");
		return true;
	}

	public static String getBackupFilePath(String path, String dbName , int maxBackup){
		File folder = new File (path);
		//in case of first backup
		if(!folder.exists()){
			folder.mkdirs();
			return path + dbName+".sql";
		}

		int num=0;
		while(num<maxBackup){
			String backupFile=null;
			if(num==0)backupFile = dbName+".sql";
			else backupFile = dbName+"_"+num+".sql";

			String backupFilePath=path+backupFile;
			File tmpFile = new File(backupFilePath);
			if(!tmpFile.exists())return backupFilePath;
			else ++num;			
		}
		//if we have exceeded the number of backups we replace the older one..
		
		num=0;
		Date present = new Date();
		long oldModifiedFile = present.getTime();
		String olderBackupFilePath="";
		while(num<maxBackup){
			String backupFile=null;
			if(num==0)backupFile = dbName+".sql";
			else backupFile = dbName+"_"+num+".sql";
			
			String backupFilePath=path+backupFile;
			File tmpFile = new File(backupFilePath);
			
			long lastMofidied = tmpFile.lastModified();
			if(num==0){ //initial value
				oldModifiedFile=lastMofidied;
				olderBackupFilePath=backupFilePath;
			}//we replace the initial value with the older one
			else if(lastMofidied < oldModifiedFile){
				oldModifiedFile=lastMofidied;
				olderBackupFilePath=backupFilePath;
			}
			++num;			
		}
		Date oldBackupDate = new Date(oldModifiedFile);
		System.out.println("getBackupFilePath - We're going to replace an old backupFile - lastModifiedDate:"+oldBackupDate);
		return olderBackupFilePath;
	}
}
