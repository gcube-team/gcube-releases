package org.gcube.datatransfer.agent.impl.handlers;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.io.CopyStreamException;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.io.Util;
import org.gcube.datatransfer.agent.impl.jdo.TransferObject;

public class CopyStreamHandler implements Runnable {
	private InputStream in;
	private OutputStream out;
	private long streamSize;
	CopyStreamListener listener;

	CopyStreamHandler(InputStream in, OutputStream out, long streamSize,
			CopyStreamListener listener) {
		this.in = in;
		this.out = out;
		this.streamSize = streamSize;
		this.listener = listener;

	}

	public void run() {
		try {
			Util.copyStream(in, out, LocalFileTransferAsyncHandler.bufferSize,
					streamSize, listener);
		} catch (CopyStreamException e) {
			e.printStackTrace();
		} finally {
			Util.closeQuietly(in);
			Util.closeQuietly(out);

		}

	}


}