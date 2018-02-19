package org.gcube.portlet.user.userstatisticsportlet.client.ui;

import org.gcube.portal.databook.shared.ShowUserStatisticAction;
import org.gcube.portlet.user.userstatisticsportlet.client.events.ShowFeedsRelatedToUserStatisticsEvent;

import com.github.gwtbootstrap.client.ui.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ActivityWidget extends Composite {

	private static CommentsAndLikesWidgetUiBinder uiBinder = GWT
			.create(CommentsAndLikesWidgetUiBinder.class);

	interface CommentsAndLikesWidgetUiBinder extends
	UiBinder<Widget, ActivityWidget> {
	}

	@UiField
	Button likesButton;

	@UiField
	Button commentsButton;

	@UiField
	Button postsButton;

	private HandlerManager busEvents;

	public ActivityWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		// set styles
		commentsButton.addStyleName("buttons-statistics-style");
		likesButton.addStyleName("buttons-statistics-style");
		postsButton.addStyleName("buttons-statistics-style");
	}

	/**
	 * Set comments information
	 * @param value
	 * @param tipIcon
	 * @param tipValue
	 * @param actionToTakeOnClick
	 * @param landingPage
	 */
	public void setComments(String value, String tipValue, final ShowUserStatisticAction actionToTakeOnClick, final String landingPage){

		commentsButton.setText(value);
		commentsButton.setTitle(tipValue);

		if(busEvents != null && actionToTakeOnClick != null){

			commentsButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					busEvents.fireEvent(new ShowFeedsRelatedToUserStatisticsEvent(actionToTakeOnClick, landingPage));

				}
			});

		}

		// set to visible
		commentsButton.setVisible(true);
	}

	/**
	 * Set likes information
	 * @param value
	 * @param tipIcon
	 * @param tipValue
	 * @param actionToTakeOnClick
	 * @param landingPage
	 */
	public void setLikes(String value, String tipValue, final ShowUserStatisticAction actionToTakeOnClick, final String landingPage){

		likesButton.setText(value);
		likesButton.setTitle(tipValue);

		if(busEvents != null && actionToTakeOnClick != null){

			likesButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					busEvents.fireEvent(new ShowFeedsRelatedToUserStatisticsEvent(actionToTakeOnClick, landingPage));

				}
			});

		}

		// set to visible
		likesButton.setVisible(true);
	}

	/**
	 * Set posts information
	 * @param value
	 * @param tipIcon
	 * @param tipValue
	 * @param actionToTakeOnClick
	 * @param landingPage
	 */
	public void setPosts(String value, String tipValue, final ShowUserStatisticAction actionToTakeOnClick, final String landingPage){

		postsButton.setText(value);
		postsButton.setTitle(tipValue);

		if(busEvents != null && actionToTakeOnClick != null){

			postsButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					busEvents.fireEvent(new ShowFeedsRelatedToUserStatisticsEvent(actionToTakeOnClick, landingPage));

				}
			});

		}

		// set to visible
		postsButton.setVisible(true);
	}

	/**
	 * Set the event bus to let this widget fire events
	 * @param busEvents
	 */
	public void setEventBus(HandlerManager busEvents){
		this.busEvents = busEvents;
	}
}
