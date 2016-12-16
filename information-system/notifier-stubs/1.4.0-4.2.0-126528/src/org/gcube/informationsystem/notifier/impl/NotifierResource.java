package org.gcube.informationsystem.notifier.impl;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.xml.namespace.QName;
import org.gcube.common.core.state.GCUBEWSResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.notifier.impl.entities.Consumer;
import org.gcube.informationsystem.notifier.impl.entities.Producer;
import org.gcube.informationsystem.notifier.util.EPR;
import org.gcube.informationsystem.notifier.util.RegistrationEventHandlerImpl;
import org.gcube.informationsystem.notifier.util.TopicMapping;
import org.globus.wsrf.ResourceException;

/**
 * It implements NotifierResource. it follows the singleton pattern 
 * 
 * 
 * @author Andrea Manzi (ISTI-CNR)
 *
 */

public class  NotifierResource  extends GCUBEWSResource{


	//contains mapping btw notifier EPR/SubscriptionObject
	private Hashtable<String,TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl>> topicMappingList;

	private final GCUBELog logger = new GCUBELog(NotifierResource.class);

	@Override 
	public void initialise(Object ...o) throws ResourceException {
		/* Initialize the RP's */
		try {
			this.topicMappingList= new Hashtable<String, TopicMapping<Producer,Consumer,RegistrationEventHandlerImpl>>();
		} catch (Exception e) {
			logger.error("Runtime exception",e);
			throw new ResourceException (e);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public synchronized TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl> getTopicMappingByQName(QName topic) throws Exception{
		logger.trace("searching for topic "+topic);
		return topicMappingList.get(topic.toString());		
	}
	
	
	public synchronized Hashtable<String,TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl>> getTopicMappingList(){
		return this.topicMappingList;
	}
	
	/**
	 * 
	 * @param hashMapSubscribers
	 */
	public synchronized void setTopicMappingList(Hashtable<String, TopicMapping<Producer,Consumer,RegistrationEventHandlerImpl>> listTopicMapping) {
		this.topicMappingList= listTopicMapping;
	}
	
	
	/**
	 * Controls if the topic mapping contains the given topic
	 * 
	 * 
	 * @param topic the topic  to check
	 * @return true if the topic is present
	 */
	public synchronized boolean isTopicPresent(QName topic) {
		if (this.topicMappingList.get(topic.toString())==null)
			return false;
		else return true;
	}
	
	public synchronized void addTopicMapping(TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl> topicMapping){
		this.topicMappingList.put(topicMapping.getTopic().toString(),topicMapping);
	}
	
	public synchronized List<String> getListTopic(){
		List<String> topicList= new ArrayList<String>();
		for (TopicMapping<Producer, Consumer, RegistrationEventHandlerImpl> t:this.topicMappingList.values()){
			topicList.add(t.getTopic().toString());
		}
		return topicList;
	}
	
}
