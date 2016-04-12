package org.gcube.datatransfer.portlets.user.test;

import org.gcube.datatransfer.portlets.user.server.workers.ListFiles;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;

public class ListFilesTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String rootPath="/home/nick/tmp/";
		ListFiles listFiles = new ListFiles(rootPath);
		FolderDto folder = listFiles.process();
		listFiles.printFolder(folder, 0);
	}
}
