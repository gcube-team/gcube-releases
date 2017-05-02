package org.gcube.datatransfer.agent.vfs.test;


import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.ftp.FtpFileSystemConfigBuilder;
import org.junit.Test;
import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.Util;
import org.apache.commons.net.io.CopyStreamListener;


public class TestVFSCopy {

	@Test
	public void testVFSCopy() throws Exception{
		StandardFileSystemManager fsManager = new StandardFileSystemManager();

		fsManager.init();
		FileObject inputFile =fsManager.resolveFile( "https://dl.dropbox.com/u/8704957/varie.zip" );

		FileObject destinationFile =fsManager.resolveFile( "/Users/andrea/test_1" );

		InputStream sourceFileIn = inputFile.getContent().getInputStream();
		CopyStreamListener listener = new CopyStreamListener() {

			@Override
			public void bytesTransferred(long arg0, int arg1, long arg2) {
				System.out.println("totalBytesTransferred: " +arg0);
				
			}

			@Override
			public void bytesTransferred(CopyStreamEvent arg0) {
				System.out.println(arg0.getBytesTransferred());

			}
		};

		try {
			OutputStream destinationFileOut = destinationFile.getContent().getOutputStream();
			try {
				Util.copyStream(sourceFileIn, destinationFileOut, Util.DEFAULT_COPY_BUFFER_SIZE, inputFile.getContent().getSize(), listener);
			} finally {
				destinationFileOut.close();
			}
		} finally {
			sourceFileIn.close();
		}
	}


	public static FileSystemOptions createDefaultOptions()
			throws FileSystemException {
		// Create SFTP options
		FileSystemOptions opts = new FileSystemOptions();


		// Root directory set to user home
		FtpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, true);

		// Timeout is count by Milliseconds
		FtpFileSystemConfigBuilder.getInstance().setSoTimeout(opts, 10000);

		return opts;
	}

}
