package org.gcube.dataanalysis.geo.utils.transfer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.net.io.CopyStreamEvent;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.VFS;

/**
 * 
 * @author Andrea Manzi
 * 
 */
public class TransferUtil {

	public static int bufferSize = Util.DEFAULT_COPY_BUFFER_SIZE * 1000;
	
	private long bytesTransferredForCurrent;

	static int connectiontimeout = 100000000;
	static int transferTimeout = 100000000;

	VFileSystemManager localFSManager = null;

	private ExecutorService pool;

	public TransferUtil() throws FileSystemException {

		localFSManager = new VFileSystemManager("/");

		pool = Executors.newFixedThreadPool(1);

	}

	public int getConnectiontimeout() {
		return connectiontimeout;
	}

	public void setConnectiontimeout(int connectiontimeout) {
		TransferUtil.connectiontimeout = connectiontimeout;
	}

	public int getTransferTimeout() {
		return transferTimeout;
	}

	public void setTransferTimeout(int transferTimeout) {
		TransferUtil.transferTimeout = transferTimeout;
	}

	/**
	 * 
	 * @param uri
	 * @param connectionTimeout
	 * @return
	 * @throws FileSystemException
	 */
	public static InputStream getInputStream(URI uri, int connectionTimeout)
			throws FileSystemException {
		connectiontimeout = connectionTimeout;
		FileObject inputFile = TransferUtil.prepareFileObject(uri.toString());

		return inputFile.getContent().getInputStream();
	}

	/**
	 * 
	 * @param uri
	 * @param outfile
	 * @throws Exception
	 */
	public synchronized long transfer(URI uri, String outfile) throws Exception {
		
		bytesTransferredForCurrent = 0;

		FileObject inputFile = TransferUtil.prepareFileObject(uri.toString());

		InputStream sourceFileIn = inputFile.getContent().getInputStream();

		// getting outfile info

		FileOutputStream out = new FileOutputStream(new File(outfile));

		// parameters
		boolean terminate = false;

		bytesTransferredForCurrent = 0;
		CopyStreamHandler handler = new CopyStreamHandler(sourceFileIn, out,
				inputFile.getContent().getSize(), listener);

		try {
			pool.execute(handler);
			pool.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			pool.shutdownNow();
			throw new Exception("Error while executing the transfer",e);
		}

		// waiting for transfer to complete
		terminate = pool.awaitTermination(transferTimeout,
				TimeUnit.MILLISECONDS);
		sourceFileIn.close();
		out.close();
		return bytesTransferredForCurrent;
	}

	/**
	 * 
	 * @param uri
	 * @param outfile
	 * @throws Exception
	 */
	public  synchronized void performTransfer(URI uri, String outfile) throws Exception {
		
		bytesTransferredForCurrent = 0;
		// parameters
		boolean terminate = false;

		FileObject inputFile = TransferUtil.prepareFileObject(uri.toString());

		InputStream sourceFileIn = inputFile.getContent().getInputStream();

		// getting outfile info

		FileOutputStream out = new FileOutputStream(new File(outfile));

		CopyStreamHandler handler = new CopyStreamHandler(sourceFileIn, out,
				inputFile.getContent().getSize(), listener);

		try {
			pool.execute(handler);
			pool.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			pool.shutdownNow();
			throw new Exception("Error while executing the transfer",e);
		}

		// waiting for transfer to complete
		terminate = pool.awaitTermination(transferTimeout,
				TimeUnit.MILLISECONDS);
		sourceFileIn.close();
		out.close();
	}

	/**
	 * 
	 * @param uri
	 * @param outPath
	 * @throws Exception
	 */
	public synchronized void performTransferToFolder(URI uri, String outPath)
			throws Exception {

		FileObject inputFile = TransferUtil.prepareFileObject(uri.toString());

		InputStream sourceFileIn = inputFile.getContent().getInputStream();

		// getting outfile info
		String outputFile;
		outputFile = inputFile.getName().getBaseName();

		if (outPath.endsWith("/"))
			outPath = outPath.substring(0, outPath.length() - 1);

		String relativeOutputFile = outPath + File.separator + outputFile;

		FileObject absoluteOutputFile = localFSManager
				.resolveFile(relativeOutputFile);

		FileObject absolutePath = localFSManager.resolveFile(outPath);

		absolutePath.createFolder();

		// parameters
		boolean terminate = false;

		OutputStream destinationFileOut = absoluteOutputFile.getContent()
				.getOutputStream();

		bytesTransferredForCurrent = 0;
		CopyStreamHandler handler = new CopyStreamHandler(sourceFileIn,
				destinationFileOut, inputFile.getContent().getSize(), listener);

		try {
			pool.execute(handler);
			pool.shutdown();
		} catch (Exception e) {
			e.printStackTrace();
			pool.shutdownNow();
			absoluteOutputFile.delete();
			throw new Exception("Error while executing the transfer",e);
		}

		// waiting for transfer to complete
		terminate = pool.awaitTermination(transferTimeout,
				TimeUnit.MILLISECONDS);
		sourceFileIn.close();
		destinationFileOut.close();

	}

	/**
	 * 
	 * @param URI
	 * @return
	 * @throws FileSystemException
	 */
	private static FileObject prepareFileObject(String URI)
			throws FileSystemException {
		return VFS.getManager().resolveFile(URI, Utils.createDefaultOptions(URI,connectiontimeout));
	}



	CopyStreamListener listener = new CopyStreamListener() {
		@Override
		public void bytesTransferred(long arg0, int arg1, long arg2) {
				// only for the current object
				bytesTransferredForCurrent = bytesTransferredForCurrent + arg1;
			
		}

		@Override
		public void bytesTransferred(CopyStreamEvent arg0) {
		}
	};
	
	


}
