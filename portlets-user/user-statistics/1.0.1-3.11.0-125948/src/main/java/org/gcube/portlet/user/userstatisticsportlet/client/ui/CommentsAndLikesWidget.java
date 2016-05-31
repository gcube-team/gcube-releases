package org.gcube.portlet.user.userstatisticsportlet.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class CommentsAndLikesWidget extends Composite {

	private static CommentsAndLikesWidgetUiBinder uiBinder = GWT
			.create(CommentsAndLikesWidgetUiBinder.class);

	interface CommentsAndLikesWidgetUiBinder extends
	UiBinder<Widget, CommentsAndLikesWidget> {
	}

	@UiField
	Image likesImage;

	@UiField
	Image commentsImage;

	@UiField
	Label likesValue;

	@UiField
	Label commentsValue;

	public CommentsAndLikesWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setComments(String url, String value, String tipIcon, String tipValue){

		this.commentsImage.setUrl(url);
		this.commentsImage.setTitle(tipIcon);
		this.commentsValue.setText(value);
		this.commentsValue.setTitle(tipValue);
	}

	public void setLikes(String url, String value, String tipIcon, String tipValue){

		this.likesImage.setUrl(url);
		this.likesImage.setTitle(tipIcon);
		this.likesValue.setText(value);
		this.likesValue.setTitle(tipValue);
	}
}
