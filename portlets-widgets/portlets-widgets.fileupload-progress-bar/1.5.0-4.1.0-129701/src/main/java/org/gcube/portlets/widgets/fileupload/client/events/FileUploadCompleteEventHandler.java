package org.gcube.portlets.widgets.fileupload.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface FileUploadCompleteEventHandler extends EventHandler {
  void onUploadComplete(FileUploadCompleteEvent event);
}
