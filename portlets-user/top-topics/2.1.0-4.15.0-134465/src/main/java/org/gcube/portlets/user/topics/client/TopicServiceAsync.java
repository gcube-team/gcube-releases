package org.gcube.portlets.user.topics.client;

import org.gcube.portlets.user.topics.shared.HashtagsWrapper;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TopicServiceAsync {

	void getHashtags(AsyncCallback<HashtagsWrapper> callback);

}
