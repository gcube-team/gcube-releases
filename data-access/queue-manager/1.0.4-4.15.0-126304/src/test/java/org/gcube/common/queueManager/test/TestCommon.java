package org.gcube.common.queueManager.test;

import org.gcube.data.access.queueManager.FactoryConfiguration;
import org.gcube.data.access.queueManager.impl.QueueConsumerFactory;
import org.gcube.data.access.queueManager.impl.QueueProducerFactory;

public class TestCommon {
	
	
	
	
	private static final String endpoint="tcp://message-broker.d.d4science.research-infrastructures.eu:6166";
//	private static final String endpoint="vm://localhost:8161";
	
	public static FactoryConfiguration config=new FactoryConfiguration("SomeClass", "SomeService", endpoint, null, null);

	public static QueueProducerFactory producerFactory;

	public static QueueConsumerFactory consumerFactory=null;

	public static final int numExecutions=1;

	public static final int consumerAmount=1;
	
	public static final int topicsAmount=1;
	
	public static final String [] topics=new String[topicsAmount];
	
	static {
		
		for(int i=0;i<topicsAmount;i++){
			topics[i]="test.Topic_"+(i+1);
		}
			
		
		
		config.setInitialRedeliveryDelay(200);
		config.setMaximumRedeliveries(50);
		config.setUseExponentialRedelivery(true);
	}
	
	
	
}
