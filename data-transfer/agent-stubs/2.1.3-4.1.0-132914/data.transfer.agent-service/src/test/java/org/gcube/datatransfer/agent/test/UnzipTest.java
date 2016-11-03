package org.gcube.datatransfer.agent.test;

import java.io.File;

import org.gcube.datatransfer.agent.impl.utils.TransferUtils;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class UnzipTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String file = "/tmp/nick_tests/test_tar_file.tar";
	//	String file = "/tmp/nick_tests/SchedulerPortlet.zip";
		String path = "/tmp/nick_tests/";
		File fil = new File(file);
		System.out.println(fil.exists());
		
		try {
			TransferUtils.unzipArchive(path,file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(sourceEndpoint);
	}

}
