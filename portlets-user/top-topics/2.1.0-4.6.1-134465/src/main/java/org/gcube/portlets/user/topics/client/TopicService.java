package org.gcube.portlets.user.topics.client;

import org.gcube.portlets.user.topics.shared.HashtagsWrapper;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("topicservice")
public interface TopicService extends RemoteService {
	HashtagsWrapper getHashtags();
}
