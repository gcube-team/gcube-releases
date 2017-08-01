package org.gcube.portlets.widgets.fileupload.client.state;

import java.util.HashMap;
import java.util.Map;

public final class UploadProgressState extends AbstractState {

  public static final UploadProgressState INSTANCE = new UploadProgressState();
  private Map<String, Integer> uploadProgress;

  private UploadProgressState() {
    uploadProgress = new HashMap<String, Integer>();
  }

   public Integer getUploadProgress(final String filename) {
    return uploadProgress.get(filename);
  }

  public void setUploadProgress(final String filename, final Integer percentage) {
    Integer old = this.uploadProgress.get(filename);
    uploadProgress.put(filename, percentage);
    firePropertyChange("uploadProgress", old, uploadProgress);
  }
}
