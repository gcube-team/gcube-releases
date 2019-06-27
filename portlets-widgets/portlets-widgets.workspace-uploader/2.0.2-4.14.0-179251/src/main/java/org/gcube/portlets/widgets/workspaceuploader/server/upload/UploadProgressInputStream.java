package org.gcube.portlets.widgets.workspaceuploader.server.upload;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.fileupload.ProgressListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class UploadProgressInputStream.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Sep 1, 2015
 */
public final class UploadProgressInputStream extends FilterInputStream {

  private List<ProgressListener> listeners;
  private long bytesRead = 0L;
  private long totalBytes = 0L;
  private static Logger logger = LoggerFactory.getLogger(UploadProgressInputStream.class);

  /**
   * Instantiates a new upload progress input stream.
   *
   * @param in the in
   * @param totalBytes the total bytes
   */
  public UploadProgressInputStream(final InputStream in, final long totalBytes) {
    super(in);
    this.totalBytes = totalBytes;
    listeners = new ArrayList<ProgressListener>();
  }

  /**
   * Adds the listener.
   *
   * @param listener the listener
   */
  public void addListener(final ProgressListener listener) {
    listeners.add(listener);
  }

  /* (non-Javadoc)
   * @see java.io.FilterInputStream#read()
   */
  @Override
  public int read() throws IOException {
    int b = super.read();
    this.bytesRead++;
    updateListeners(bytesRead, totalBytes);

    return b;
  }

  /* (non-Javadoc)
   * @see java.io.FilterInputStream#read(byte[])
   */
  @Override
  public int read(final byte b[]) throws IOException {
    return read(b, 0, b.length);
  }

  /* (non-Javadoc)
   * @see java.io.FilterInputStream#read(byte[], int, int)
   */
  @Override
  public int read(final byte b[], final int off, final int len) throws IOException {
    int bytesRead = in.read(b, off, len);
    this.bytesRead = this.bytesRead + bytesRead;
    updateListeners(this.bytesRead, totalBytes);

    return bytesRead;
  }

  /* (non-Javadoc)
   * @see java.io.FilterInputStream#close()
   */
  @Override
  public void close() throws IOException {
    super.close();
    updateListeners(totalBytes, totalBytes);
  }

  /**
   * Update listeners.
   *
   * @param bytesRead the bytes read
   * @param totalBytes the total bytes
   */
  /*private void updateListeners(final long bytesRead, final long totalBytes) throws IOException{

	try{
	    for (ProgressListener listener : listeners)
	      listener.update(bytesRead, totalBytes, listeners.size());
	}catch(UploadCanceledException e){
		logger.warn("Update Listener thrown UploadCanceledException: closing stream..");
		try {
			close();
		}catch (IOException e1) {
			logger.warn("Close stream thrown this Exception, silent catch");
		}
		throw new IOException("Upload cancelled from Client");
	}
  }*/

  /**
   * Update listeners.
   *
   * @param bytesRead the bytes read
   * @param totalBytes the total bytes
   */
  private void updateListeners(final long bytesRead, final long totalBytes){

    for (ProgressListener listener : listeners)
      listener.update(bytesRead, totalBytes, listeners.size());

    //UploadCanceledException IS PROPAGATED HERE

  }
}
