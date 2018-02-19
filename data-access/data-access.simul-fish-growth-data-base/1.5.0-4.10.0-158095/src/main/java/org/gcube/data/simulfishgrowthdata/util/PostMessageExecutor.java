package org.gcube.data.simulfishgrowthdata.util;

import java.net.URI;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PostMessageExecutor extends SocialNetworkingExecutor {

	static final Logger logger = LoggerFactory.getLogger(PostMessageExecutor.class);

	Message mMessage;

	public PostMessageExecutor(String endpoint) {
		super(endpoint);
		mMessage = new Message();
	}

	public PostMessageExecutor setText(String text) {
		mMessage.text = text;

		return this;
	}

	public PostMessageExecutor setPreviewDescription(String previewDescription) {
		mMessage.preview_description = previewDescription;

		return this;
	}

	public PostMessageExecutor setPreviewTitle(String previewTitle) {
		mMessage.preview_title = previewTitle;

		return this;
	}

	public PostMessageExecutor setEnableNotification(boolean enableNotification) {
		mMessage.enable_notification = enableNotification;

		return this;
	}

	@Override
	protected HttpUriRequest createRequest(final URI uri) {
		HttpUriRequest request = new HttpPost(uri);
		return request;
	}

	@Override
	protected String makeUri() {
		return mEndpoint + "/2/posts/write-post-user";
	}

	@Override
	protected void prepareRequest(HttpUriRequest request) {
		String messageJson = new Gson().toJson(mMessage);
		if (logger.isTraceEnabled())
			logger.trace("posting ~~" + messageJson + "~~");

		StringEntity entity = new StringEntity(messageJson, ContentType.APPLICATION_JSON);
		((HttpPost) request).setEntity(entity);

	}

	@Override
	protected Response processOutput(String output) throws Exception {
		Response response = super.processOutput(output);
		if (!response.isSuccess())
			throw new Exception(response.message);
		return response;
	}

	static class Message {
		public String text;
		public String preview_title;
		public String preview_description;
		public boolean enable_notification;
	}

}
