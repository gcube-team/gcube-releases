package org.gcube.portlets.widgets.fileupload.client.events;

import com.google.gwt.event.shared.EventHandler;

public interface FileUploadSelectedEventHandler extends EventHandler {
  void onFileSelected(FileUploadSelectedEvent event);
}
