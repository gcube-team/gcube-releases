package org.gcube.portlet.user.userstatisticsportlet.client.events;

import org.gcube.portal.databook.shared.ShowUserStatisticAction;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event fired when the user wants to see feeds related to certain statistics.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class ShowFeedsRelatedToUserStatisticsEvent  extends GwtEvent<ShowFeedsRelatedToUserStatisticsEventHandler> {
	public static Type<ShowFeedsRelatedToUserStatisticsEventHandler> TYPE = new Type<ShowFeedsRelatedToUserStatisticsEventHandler>();
	
	private ShowUserStatisticAction action;
	private String currentSiteLandingPage;
	
	public ShowFeedsRelatedToUserStatisticsEvent(ShowUserStatisticAction actionToTake, String currentSiteLandingPage) {
		this.action = actionToTake;
		this.currentSiteLandingPage = currentSiteLandingPage;
	}
	
	public ShowUserStatisticAction getAction() {
		return action;
	}
	
	public String getLandingPage() {
		return currentSiteLandingPage;
	}

	@Override
	public Type<ShowFeedsRelatedToUserStatisticsEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowFeedsRelatedToUserStatisticsEventHandler handler) {
		handler.onShowRelatedFeeds(this);
	}
}
