package org.gcube.portlets.user.newsfeed.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface DeleteFeedEventHandler extends EventHandler {
  void onDeleteFeed(DeleteFeedEvent event);
}
