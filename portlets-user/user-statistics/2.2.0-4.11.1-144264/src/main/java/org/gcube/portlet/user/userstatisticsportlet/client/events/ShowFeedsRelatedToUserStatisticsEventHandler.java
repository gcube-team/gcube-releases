package org.gcube.portlet.user.userstatisticsportlet.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler related to the ShowFeedsRelatedToUserStatisticsEvent class
 * @author Costantino Perciante at ISTI-CNR 
 */
public interface ShowFeedsRelatedToUserStatisticsEventHandler extends EventHandler {
  void onShowRelatedFeeds(ShowFeedsRelatedToUserStatisticsEvent event);
}
