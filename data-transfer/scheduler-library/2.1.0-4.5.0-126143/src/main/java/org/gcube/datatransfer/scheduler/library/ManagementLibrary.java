
package org.gcube.datatransfer.scheduler.library;

import org.gcube.common.clients.Call;
import org.gcube.common.clients.delegates.AsyncProxyDelegate;
import org.gcube.common.clients.delegates.ProxyDelegate;
import org.gcube.datatransfer.scheduler.library.fws.ManagementServiceJAXWSStubs;
import org.gcube.datatransfer.scheduler.library.outcome.CallingManagementResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class ManagementLibrary {
	private final AsyncProxyDelegate<ManagementServiceJAXWSStubs> delegate;
	Logger logger = LoggerFactory.getLogger(this.getClass().toString());

	public ManagementLibrary(ProxyDelegate<ManagementServiceJAXWSStubs> config) {
		this.delegate=new AsyncProxyDelegate<ManagementServiceJAXWSStubs>(config);
	}

	/*
	 * getAllTransfersInfo
	 * input: The name of the submitter that we want to print info about (name of resource)
	 * ...... if the name is "ALL" there is no filter in the results 
	 * input: String with the scope
	 * return: CallingManagementResult
	 *  if exception the returned values is null
	 */
	public CallingManagementResult getAllTransfersInfo(String nameOfClient){
		final String message=nameOfClient;
		Call<ManagementServiceJAXWSStubs,String> call = new Call<ManagementServiceJAXWSStubs,String>() {
			@Override 
			public String call(ManagementServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.getAllTransfersInfo(message);
			}
		};

		String result=null;
		try {
			result= delegate.make(call);
		}catch(Exception e) {
			logger.error("getAllTransfersInfo - Exception when calling endpoint.getAllTransfersInfo(message)");
			e.printStackTrace();
		}

		//return value
		if (result==null)return null;
		else {
			String tmpMsg=result;
			tmpMsg.replaceAll("&lt;", "<");
			tmpMsg=tmpMsg.replaceAll("&gt;", ">");

			XStream xstream = new XStream();
			CallingManagementResult callingManagementResult= new CallingManagementResult();
			callingManagementResult=(CallingManagementResult)xstream.fromXML(tmpMsg);

			return callingManagementResult;
		}
	}
	
	public String getObjectsFromIS(String type){
		final String typeOfObj=type;
		if(typeOfObj.compareTo("Agent")!=0 && typeOfObj.compareTo("DataSource")!=0 
				&& typeOfObj.compareTo("DataStorage")!=0 ){
			logger.debug("getObjectsFromIS - input tupe != 'Agent','DataSource','DataStorage' --- input type="+typeOfObj);
			return null;
		}
		
		Call<ManagementServiceJAXWSStubs,String> call = new Call<ManagementServiceJAXWSStubs,String>() {
			@Override 
			public String call(ManagementServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.getObjectsFromIS(typeOfObj);
			}
		};
		String result=null;
		try {
			result= delegate.make(call);
		}catch(Exception e) {
			logger.error("getObjectsFromIS - Exception when calling endpoint.getObjectsFromIS(message)");
			e.printStackTrace();
		}
		//return value
		return result;
	}
	public String existAgentInIS(String agent){
		final String message=agent;
		Call<ManagementServiceJAXWSStubs,String> call = new Call<ManagementServiceJAXWSStubs,String>() {
			@Override 
			public String call(ManagementServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.existAgentInIS(message);
			}
		};
		String result=null;
		try {
			result= delegate.make(call);
		}catch(Exception e) {
			logger.error("existAgentInIS - Exception when calling endpoint.getAllTransfersInfo(message)");
			e.printStackTrace();
		}
		//return value
		return result;
	}
	public String existAgentInDB(String agent){
		final String message=agent;
		Call<ManagementServiceJAXWSStubs,String> call = new Call<ManagementServiceJAXWSStubs,String>() {
			@Override 
			public String call(ManagementServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.existAgentInDB(message);
			}
		};
		String result=null;
		try {
			result= delegate.make(call);
		}catch(Exception e) {
			logger.error("existAgentInDB - Exception when calling endpoint.existAgentInDB(message)");
			e.printStackTrace();
		}
		//return value
		return result;

	}
	
	public String getAgentStatistics(){
		final String nothing="";
		Call<ManagementServiceJAXWSStubs,String> call = new Call<ManagementServiceJAXWSStubs,String>() {
			@Override 
			public String call(ManagementServiceJAXWSStubs endpoint) throws Exception {
				return endpoint.getAgentStatistics(nothing);
			}
		};
		String result=null;
		try {
			result= delegate.make(call);
		}catch(Exception e) {
			logger.error("getAgentStatistics - Exception when calling endpoint.getAgentStatistics(message)");
			e.printStackTrace();
		}
		//return value
		return result;
	}
}
