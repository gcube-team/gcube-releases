package org.gcube.portlets.widgets.fileupload.server;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.fileupload.ProgressListener;

public final class UploadProgressInputStream extends FilterInputStream {

  private List<ProgressListener> listeners;
  private long bytesRead = 0;
  private long totalBytes = 0;

  public UploadProgressInputStream(final InputStream in, final long totalBytes) {
    super(in);

    this.totalBytes = totalBytes;

    listeners = new ArrayList<ProgressListener>();
  }

  public void addListener(final ProgressListener listener) {
    listeners.add(listener);
  }

  @Override
  public int read() throws IOException {
    int b = super.read();

    this.bytesRead++;

    updateListeners(bytesRead, totalBytes);

    return b;
  }

  @Override
  public int read(final byte b[]) throws IOException {
    return read(b, 0, b.length);
  }

  @Override
  public int read(final byte b[], final int off, final int len) throws IOException {
    int bytesRead = in.read(b, off, len);

    this.bytesRead = this.bytesRead + bytesRead;

    updateListeners(this.bytesRead, totalBytes);

    return bytesRead;
  }

  @Override
  public void close() throws IOException {
    super.close();

    updateListeners(totalBytes, totalBytes);
  }

  private void updateListeners(final long bytesRead, final long totalBytes) {

    for (ProgressListener listener : listeners) {

      listener.update(bytesRead, totalBytes, listeners.size());
    }
  }
}
