package org.gcube.datatransfer.agent.storagemanager.test;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.gcube.datatransfer.agent.impl.utils.TransferUtils;

public class ResolveSmpUrl {

	
	public static void main(String[] args) {
		String url = "smp://Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeSxqJkp9OeWKkznDnXYgDz7F/ELBV1lV8qTh/bosrhjOzQb50+GI/1DUWNMQZdZbHMJfYMmmXptQ==";
		FileObject inputFile;
		try {
			inputFile = TransferUtils.prepareFileObject(url);
			System.out.println("inputFile.getURL()= "+ inputFile.getURL());	
		} catch (FileSystemException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
