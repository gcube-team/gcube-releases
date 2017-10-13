package org.gcube.portlets.user.newsfeed.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DeleteCommentEventHandler extends EventHandler {
  void onDeleteComment(DeleteCommentEvent event);
}
