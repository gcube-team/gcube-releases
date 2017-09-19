package org.gcube.data.access.queueManager.utils;

import org.gcube.data.access.queueManager.model.CallBackItem;
import org.gcube.data.access.queueManager.model.LogItem;
import org.gcube.data.access.queueManager.model.RemoteExecutionStatus;
import org.gcube.data.access.queueManager.model.RequestItem;

import com.thoughtworks.xstream.XStream;

public class QueueXStream {

	private static XStream instance;
	
	public static synchronized XStream get(){
		if(instance==null){
			instance=new XStream();
			instance.processAnnotations(new Class[]{
					RequestItem.class,
					CallBackItem.class,
					RemoteExecutionStatus.class,
					LogItem.class
			});
		}
		return instance;
	}
	
}
