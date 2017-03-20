package org.gcube.portlets.user.newsfeed.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface AddCommentEventHandler extends EventHandler {
  void onAddComment(AddCommentEvent event);
}
