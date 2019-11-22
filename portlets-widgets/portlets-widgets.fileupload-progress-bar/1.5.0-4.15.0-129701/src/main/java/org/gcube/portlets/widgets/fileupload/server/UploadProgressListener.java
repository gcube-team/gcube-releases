package org.gcube.portlets.widgets.fileupload.server;


import org.apache.commons.fileupload.ProgressListener;
import org.gcube.portlets.widgets.fileupload.shared.event.UploadProgressChangeEvent;

public final class UploadProgressListener implements ProgressListener {

  private static final double COMPLETE_PERECENTAGE = 100d;
  private int percentage = -1;
  private String fileName;
  private String absolutePath;
  private UploadProgress uploadProgress;

  public UploadProgressListener(final String fileName, final UploadProgress uploadProgress, final String absolutePath) {
    this.fileName = fileName;
    this.uploadProgress = uploadProgress;
    this.absolutePath = absolutePath;
  }

  @Override
  public void update(final long bytesRead, final long totalBytes, final int items) {
	
    int percentage = (int) Math.floor(((double) bytesRead / (double) totalBytes) * COMPLETE_PERECENTAGE);
    
    if (this.percentage == percentage) {
      return;
    }

    this.percentage = percentage;   

    UploadProgressChangeEvent event = new UploadProgressChangeEvent();
    event.setFilename(this.fileName);
    event.setPercentage(percentage);
    event.setAbsolutePath(this.absolutePath);

    synchronized (this.uploadProgress) {
      this.uploadProgress.add(event);
      this.uploadProgress.notifyAll();
    }
  }
}
