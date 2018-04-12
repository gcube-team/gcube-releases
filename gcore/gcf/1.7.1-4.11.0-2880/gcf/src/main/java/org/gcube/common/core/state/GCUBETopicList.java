package org.gcube.common.core.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.globus.wsrf.Topic;
import org.globus.wsrf.impl.SimpleTopicList;

/**
 * Set of event topics for {@link GCUBEWSResource GCUBEWSResources}.
 * @author Fabio Simeoni (University of Strathclyde), Manuele Simi (ISTI-CNR)
 */
public class GCUBETopicList extends SimpleTopicList {

	/**
	 * Creates an instance for a given resource.
	 * @param resource the resource.
	 */
	GCUBETopicList(GCUBEWSResource resource) {super(resource);}
	
	/**
	 * Returns the topics.
	 * @return the topics.
	 */
	public List<Topic> asList() {
		List<Topic> list = new ArrayList<Topic>();
		Iterator<?> i = this.topicIterator();
		while (i.hasNext()) list.add((Topic)i.next());
		return list;
	}
}
