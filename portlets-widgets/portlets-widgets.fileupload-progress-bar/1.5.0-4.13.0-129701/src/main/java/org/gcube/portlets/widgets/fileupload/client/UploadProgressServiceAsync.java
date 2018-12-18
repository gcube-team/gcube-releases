package org.gcube.portlets.widgets.fileupload.client;

import java.util.List;

import org.gcube.portlets.widgets.fileupload.shared.event.Event;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface UploadProgressServiceAsync {
  void initialise(AsyncCallback<Void> asyncCallback);
  void getEvents(AsyncCallback<List<Event>> asyncCallback);
}
