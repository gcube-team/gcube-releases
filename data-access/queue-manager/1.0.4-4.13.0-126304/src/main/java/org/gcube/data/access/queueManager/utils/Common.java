package org.gcube.data.access.queueManager.utils;

import org.gcube.data.access.queueManager.QueueType;

public class Common {

	/**
	 * CALLBACK_ OR LOG_ prefix added to topic before return
	 *  
	 * @param serviceClass
	 * @param serviceName
	 * @param type
	 * @param topic
	 * @return serviceClass.serviceName.topic
	 */
	public static String formTopic(String serviceClass,String serviceName, QueueType type,String topic){
		switch(type){
		case CALLBACK : topic="CALLBACK_"+topic;
						break;
		case LOG 		: topic="LOG_"+topic;
						break;			
		}
		return serviceClass+"."+serviceName+"."+topic;
	}
	
}
