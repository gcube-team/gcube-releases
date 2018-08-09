package org.gcube.portlets.widgets.fileupload.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import org.gcube.portlets.widgets.fileupload.shared.event.*;
import org.gcube.portlets.widgets.fileupload.shared.dto.*;

@RemoteServiceRelativePath("uploadprogress")
public interface UploadProgressService extends RemoteService {
  void initialise();

  List<Event> getEvents();
}
