package org.gcube.portlets.user.newsfeed.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface OpenFeedEventHandler extends EventHandler {
  void onOpenFeed(OpenFeedEvent event);
}
