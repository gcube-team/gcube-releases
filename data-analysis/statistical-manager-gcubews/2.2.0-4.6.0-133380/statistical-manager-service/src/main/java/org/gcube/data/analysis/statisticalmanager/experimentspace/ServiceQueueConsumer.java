package org.gcube.data.analysis.statisticalmanager.experimentspace;

import javax.jms.JMSException;

import org.gcube.common.core.contexts.GCUBEStatefulPortTypeContext;
import org.gcube.common.core.state.GCUBEWSResourceKey;
import org.gcube.data.access.queueManager.QueueItemHandler;
import org.gcube.data.access.queueManager.QueueType;
import org.gcube.data.access.queueManager.impl.QueueConsumerFactory;
import org.gcube.data.access.queueManager.model.RequestItem;
import org.gcube.data.analysis.statisticalmanager.Configuration;
import org.gcube.data.analysis.statisticalmanager.exception.SMResourcesNotAvailableException;
import org.gcube.data.analysis.statisticalmanager.experimentspace.computation.ComputationContext;
import org.gcube.data.analysis.statisticalmanager.experimentspace.computation.ComputationResource;
import org.gcube.data.analysis.statisticalmanager.stubs.SMComputationRequest;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils;
import org.gcube.data.analysis.statisticalmanager.util.ScopeUtils.ScopeBean;
import org.gcube.data.analysis.statisticalmanager.util.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceQueueConsumer implements QueueItemHandler<RequestItem> {

	private static Logger logger = LoggerFactory.getLogger(ServiceQueueConsumer.class);


	
	
	private static int consumerCount=0;
	
	private static int receivedCount=0;
	private static int errorCount=0;
	private static int servedCount=0;
	
	
	public static ConsumerReport getReport(){
		return new ConsumerReport(receivedCount, errorCount, servedCount, consumerCount);
	}
	
	private QueueConsumerFactory consumerFactory;
	private String topicName;
	
	
	public ServiceQueueConsumer(String topicName,QueueConsumerFactory consumerFactory) {
		logger.debug("Created consumer N° "+(consumerCount++)+" topic is "+topicName);
		this.consumerFactory = consumerFactory;
		this.topicName=topicName;
	}

	@Override
	public void handleQueueItem(RequestItem item) throws Exception {
		try{
			logger.debug("Message received" + item.getId());
			receivedCount++;

			
			
			SMComputationRequest request = (SMComputationRequest) item.getParameters().get(Configuration.getProperty(Configuration.JMS_MESSAGE_REQUEST));
			
			String computationId = (String)item.getParameters().get(Configuration.getProperty(Configuration.JMS_MESSAGE_COMPUTATION_ID));
			logger.debug("ComputationId " + computationId);
			GCUBEStatefulPortTypeContext stfctx = ComputationContext.getContext();
			
			logger.debug("User" + request.getUser());
			
			GCUBEWSResourceKey key = stfctx.makeKey(request.getUser());
			String scope = (String) item.getParameters().get(Configuration.getProperty(Configuration.JMS_MESSAGE_SCOPE));
			String token = (String) item.getParameters().get(Configuration.getProperty(Configuration.JMS_MESSAGE_TOKEN));
			ScopeBean scopeBean=new ScopeBean(scope,token);
			logger.debug("**************************Scope Bean"+ scopeBean);
			
			ScopeUtils.setAuthorizationSettings(scopeBean);
			
			ComputationResource wsResource = (ComputationResource) ComputationContext.getContext().getWSHome().create(key, request.getUser(), scope);
			
			logger.debug("Resource created");
			wsResource.executeComputation(request.getConfig(), Long.parseLong(computationId));
			servedCount++;
		}catch(SMResourcesNotAvailableException e){
			errorCount++;
			logger.debug("Resource Not Available, rethrowing message.. ");
			throw e;
		}catch(Exception e){
			errorCount++;
			logger.warn("Exception handling Item "+item.getId(),e);
			throw e;
		}
	}

	@Override
	public void close() {

	}

	private void retryToConnect() {
		try {
			consumerFactory.close();
			logger.debug("reconnecting consumer to topic "+topicName);
			consumerFactory.register(topicName, QueueType.REQUEST,this);
		} catch (JMSException e) {
			logger.error("Consumer connection exception ....retry to connect",e);
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e1) {
				logger.error("Consumer wait time to connect error",e);
			}
			retryToConnect();
		}
	}

	@Override
	public void onException(JMSException arg0) {
		logger.debug("Consumer connection exception ....retry to connect",arg0);
		retryToConnect();
	}

	
	//*************************** REPORT
	public static class ConsumerReport{
		
		private long receivedCount=0;
		private long errorCount=0;
		private long servedCount=0;
		private int consumerCount=0;
		private String reportTime=ServiceUtil.getDateTime();
		
		private ConsumerReport(long receivedCount, long errorCount,
				long servedCount, int consumerCount) {
			super();
			this.receivedCount = receivedCount;
			this.errorCount = errorCount;
			this.servedCount = servedCount;
			this.consumerCount = consumerCount;			
		}

		
		
		
		/**
		 * @return the receivedCount
		 */
		public long getReceivedCount() {
			return receivedCount;
		}




		/**
		 * @return the errorCount
		 */
		public long getErrorCount() {
			return errorCount;
		}




		/**
		 * @return the servedCount
		 */
		public long getServedCount() {
			return servedCount;
		}




		/**
		 * @return the consumerCount
		 */
		public int getConsumerCount() {
			return consumerCount;
		}




		/**
		 * @return the reportTime
		 */
		public String getReportTime() {
			return reportTime;
		}




		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("consumerReport [receivedCount=");
			builder.append(receivedCount);
			builder.append(", errorCount=");
			builder.append(errorCount);
			builder.append(", servedCount=");
			builder.append(servedCount);
			builder.append(", consumerCount=");
			builder.append(consumerCount);
			builder.append(", reportTime=");
			builder.append(reportTime);
			builder.append("]");
			return builder.toString();
		}
		
	}
}
