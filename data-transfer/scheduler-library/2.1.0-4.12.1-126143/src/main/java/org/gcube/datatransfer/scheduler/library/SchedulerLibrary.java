package org.gcube.datatransfer.scheduler.library;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.datatransfer.common.agent.Types.CancelTransferMessage;
import org.gcube.datatransfer.scheduler.library.fws.SchedulerServiceJAXWSStubs;
import org.gcube.datatransfer.scheduler.library.obj.InfoCancelSchedulerMessage;
import org.gcube.datatransfer.scheduler.library.obj.SchedulerObj;
import org.gcube.datatransfer.scheduler.library.outcome.CallingSchedulerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class SchedulerLibrary{

	private final AsyncProxyDelegate<SchedulerServiceJAXWSStubs> delegate;
	Logger logger = LoggerFactory.getLogger(this.getClass().toString());

	public SchedulerLibrary(ProxyDelegate<SchedulerServiceJAXWSStubs> config) {
		this.delegate=new AsyncProxyDelegate<SchedulerServiceJAXWSStubs>(config);
	}


	/*
	 * scheduleTransfer
	 * input: SchedulerObj
	 * return: String with the transferId
	 *  if exception the returned values is null
	 */
	public String scheduleTransfer(SchedulerObj schedulerObj){

		final String msgStr=schedulerObj.toXML();

		Call<SchedulerServiceJAXWSStubs,String> call = new Call<SchedulerServiceJAXWSStubs,String>() {
			@Override 
			public String call(SchedulerServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.storeInfoScheduler(msgStr);
			}
		};

		String transferId=null;
		try {
			transferId= delegate.make(call);
		}catch(Exception e1) {
			logger.error("SchedulerLibrary (scheduleTransfer)- Exception.. Something wrong in the storeInfo(msgStr)");
			e1.printStackTrace();
		}		
		//return value
		if(transferId==null)return null;
		else {
			return transferId;	
		}
	}
	
	
	/*
	 * cancelTransfer
	 * input: String with the transferId (the one in the schedulerDB) 
	 * input: boolean value for force cancel or not
	 * return: CallingSchedulerResult
	 *  if exception the returned values is null
	 */
	public CallingSchedulerResult cancelTransfer(String transferId, boolean force){

		InfoCancelSchedulerMessage infoCancelSchedulerMessage= new InfoCancelSchedulerMessage();
		
		CancelTransferMessage cancelTransferMessage = new CancelTransferMessage();
		cancelTransferMessage.setForceStop(true);
		cancelTransferMessage.setTransferId(transferId);

		infoCancelSchedulerMessage.setCancelTransferMessage(cancelTransferMessage);

		final String msgStr=infoCancelSchedulerMessage.toXML();

		Call<SchedulerServiceJAXWSStubs,String> call = new Call<SchedulerServiceJAXWSStubs,String>() {
			@Override 
			public String call(SchedulerServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.cancelScheduledTransfer(msgStr);
			}
		};

		String result=null;
		try {
			result = delegate.make(call);
		}catch(Exception e1) {
			logger.error("SchedulerLibrary (cancelTransfer)- Exception.. Something wrong");
			e1.printStackTrace();
		}

		//return value
		if (result==null)return null;
		else {
			String tmpMsg=result;
			tmpMsg.replaceAll("&lt;", "<");
			tmpMsg=tmpMsg.replaceAll("&gt;", ">");

			XStream xstream = new XStream();
			CallingSchedulerResult callingSchedulerResult= new CallingSchedulerResult();
			callingSchedulerResult=(CallingSchedulerResult)xstream.fromXML(tmpMsg);

			return callingSchedulerResult;
		}
	}

	/*
	 * monitorTransfer
	 * input: String with the transferId (the one in the schedulerDB)
	 * return: CallingSchedulerResult
	 *  if exception the returned values is null
	 */
	public CallingSchedulerResult monitorTransfer(String transferId){
		final String msgStr=transferId;
		CallingSchedulerResult callingSchedulerResult= new CallingSchedulerResult();

		Call<SchedulerServiceJAXWSStubs,String> call = new Call<SchedulerServiceJAXWSStubs,String>() {
			@Override 
			public String call(SchedulerServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.monitorScheduledTransfer(msgStr);
			}
		};

		String result=null;
		try {
			result = delegate.make(call);
		}catch(Exception e1) {
			logger.error("SchedulerLibrary (monitorTransfer)- Exception.. Something wrong");
			e1.printStackTrace();
		}

		//return value
		if(result==null)return null;
		else{
			callingSchedulerResult.setMonitorResult(result);
			return callingSchedulerResult;	
		}
	}

	/*
	 * getOutcomesOfTransfer
	 * input: String with the transferId (the one in the schedulerDB)
	 * return: CallingSchedulerResult
	 *  if exception the returned values is null
	 */
	public CallingSchedulerResult getOutcomesOfTransfer(String transferId){
		final String msgStr=transferId;

		Call<SchedulerServiceJAXWSStubs,String> call = new Call<SchedulerServiceJAXWSStubs,String>() {
			@Override 
			public String call(SchedulerServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.getScheduledTransferOutcomes(msgStr);
			}
		};

		String result=null;
		try {
			result = delegate.make(call);
		}catch(Exception e1) {
			logger.error("SchedulerLibrary (getOutcomesOfTransfer)- Exception.. Something wrong ");
			e1.printStackTrace();
		}

		//return value
		if (result==null)return null;
		else {
			String tmpMsg=result;
			tmpMsg.replaceAll("&lt;", "<");
			tmpMsg=tmpMsg.replaceAll("&gt;", ">");

			XStream xstream = new XStream();
			CallingSchedulerResult callingSchedulerResult= new CallingSchedulerResult();
			callingSchedulerResult=(CallingSchedulerResult)xstream.fromXML(tmpMsg);

			return callingSchedulerResult;
		}
	}
}
