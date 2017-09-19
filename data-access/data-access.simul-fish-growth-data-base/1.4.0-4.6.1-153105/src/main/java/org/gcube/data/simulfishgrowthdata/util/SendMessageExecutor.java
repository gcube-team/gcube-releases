package org.gcube.data.simulfishgrowthdata.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class SendMessageExecutor extends SocialNetworkingExecutor {

	static final Logger logger = LoggerFactory.getLogger(SendMessageExecutor.class);

	Message mMessage;

	public SendMessageExecutor(String endpoint) {
		super(endpoint);
		mMessage = new Message();
	}

	public SendMessageExecutor setSubject(String subject) {
		mMessage.subject = subject;

		return this;
	}

	public SendMessageExecutor setBody(String body) {
		mMessage.body = body;

		return this;
	}

	public SendMessageExecutor addRecipient(String recipient) {
		mMessage.recipients.add(new Recipient(recipient));

		return this;
	}

	@Override
	protected HttpUriRequest createRequest(final URI uri) {
		HttpUriRequest request = new HttpPost(uri);
		return request;
	}

	@Override
	protected String makeUri() {
		return mEndpoint + "/2/messages/write-message";
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
		public String body;
		public String subject;
		public List<Recipient> recipients = new ArrayList<>();
	}

	static class Recipient {
		public Recipient(String id) {
			this.id = id;
		}

		String id;
	}

}
