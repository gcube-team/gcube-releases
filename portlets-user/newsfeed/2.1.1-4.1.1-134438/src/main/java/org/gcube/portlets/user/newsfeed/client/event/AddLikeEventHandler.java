package org.gcube.portlets.user.newsfeed.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface AddLikeEventHandler extends EventHandler {
  void onAddLike(AddLikeEvent event);
}
