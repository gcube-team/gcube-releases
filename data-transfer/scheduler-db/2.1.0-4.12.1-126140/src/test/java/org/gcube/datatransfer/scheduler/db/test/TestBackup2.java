package org.gcube.datatransfer.scheduler.db.test;

import java.io.File;

import org.gcube.datatransfer.scheduler.db.DataTransferDBManager;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestBackup2 {

	static DataTransferDBManager dbManager;
	public static void main(String[] args) {
		dbManager=new DataTransferDBManager();
		dbManager.startBackUp();
	}

}
