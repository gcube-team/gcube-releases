package org.gcube.common.informationsystem.notification.impl.handlers;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.contexts.GCUBERemotePortTypeContext;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBERetryEquivalentException;
import org.gcube.common.core.faults.GCUBERetryEquivalentFault;
import org.gcube.common.core.faults.GCUBERetrySameException;
import org.gcube.common.core.faults.GCUBEUnrecoverableException;
import org.gcube.common.core.faults.GCUBEUnrecoverableFault;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.security.GCUBESecurityManager;
import org.gcube.common.core.utils.handlers.GCUBEServiceHandler.NoQueryResultException;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.notifier.stubs.NotifierPortType;
import org.gcube.informationsystem.notifier.stubs.service.NotifierServiceAddressingLocator;

/**
 * 
 * @author lucio
 *
 * @param <PARAMETER>
 * @param <RETURN>
 */
public abstract class NotifierCall<PARAMETER, RETURN> implements Runnable{
	
	/**
	 * 
	 * @param scope
	 * @param secMan
	 */
	public NotifierCall(GCUBEScope scope, GCUBESecurityManager secMan) {
		super();
		this.scope = scope;
		this.secMan = secMan;
	}

	private static Map<String, EndpointReferenceType> eprCache= new HashMap<String, EndpointReferenceType>();
	
	private static GCUBELog logger= new GCUBELog(NotifierCall.class);
	
	private PARAMETER parameter;
	private RETURN returnValue;
	
	private int maxAttempt=5;
	
	private int waitTime=20;
	
	private GCUBEScope scope;
	
	private GCUBESecurityManager secMan;
	
	public enum State{FAILED, SUCCESS};
	
	private Exception exception=null;
	
	private State state;
	
	/**
	 * 
	 */
	public void run(){
				
		if (eprCache.get(scope.toString())!=null){
			try{
				logger.trace("using cache");
				tryInstances(Collections.singletonList(eprCache.get(scope.toString())));
				return;
			}catch (Exception e) {
				logger.warn("(cached) lastException error is "+e.getMessage(),e);
				eprCache.remove(scope.toString());
			}
		}
		
		int attempt=0;
		List<EndpointReferenceType> eprList= new ArrayList<EndpointReferenceType>();
		do{
			try{
				eprList=findInstance();
				exception=null;
				
			}catch(NoQueryResultException nqre){
				logger.warn("no eprs retrieved, retrying in "+this.getWaitTime()+" secs (attempt number "+attempt+")");
				attempt++;
				exception= nqre;
				try {
					Thread.sleep(20*1000);
				} catch (InterruptedException e) {}
			}
		}while(exception!=null && attempt<=this.maxAttempt);
		
		if (exception!=null) {
			this.state=State.FAILED;
			return;}
		
		try{
			eprCache.put(this.scope.toString(),tryInstances(eprList));
			return;
		}catch(Exception e){
			exception=e;
			this.state=State.FAILED;
		}
	}

	protected void _interact(EndpointReferenceType epr) throws Exception{
		NotifierServiceAddressingLocator notifierLocator = new NotifierServiceAddressingLocator();
        NotifierPortType port = notifierLocator.getNotifierPortTypePort(epr);
        port= GCUBERemotePortTypeContext.getProxy(port, scope, secMan);
		this.returnValue=makeCall(port);
	}
	
	/**
	 * 
	 * @param notifierPort
	 * @return
	 */
	protected abstract RETURN makeCall(NotifierPortType notifierPort) throws Exception; 
	
	/**
	 * 
	 * @return
	 * @throws NoQueryResultException
	 */
	protected List<EndpointReferenceType> findInstance() throws NoQueryResultException{
		try{
			ISClient client = GHNContext.getImplementation(ISClient.class);
			GCUBERIQuery query = client.getQuery(GCUBERIQuery.class);
			query.addAtomicConditions(new AtomicCondition("//ServiceName","IS-Notifier"));
			List<EndpointReferenceType> eprs = new ArrayList<EndpointReferenceType>();
			for (GCUBERunningInstance instance : client.execute(query, scope))
				eprs.add(instance.getAccessPoint().getEndpoint("gcube/informationsystem/notifier/Notifier"));
			if (eprs.size()==0) throw new Exception("no RI of notifier retrieved");
			return eprs;
		} catch (Exception e) {
			logger.warn(e);
			throw new NoQueryResultException();
		}
	}

	
	/**
	 * Interacts with a list of instances of the target port-type, until one is successful.
	 * 
	 * @param eprs the instance EPRs.
	 * @return the EPR of the successful instance.
	 * @throws Exception if no interactions completes successfully.
	 */
	protected EndpointReferenceType tryInstances(List<EndpointReferenceType> eprs) throws Exception {
		List<EndpointReferenceType> busyEprs; //groups 'busy' instances
		int attempts=0;//measures 'patience' with busy instances
		Exception lastException=null;
		
		do {
			busyEprs = new ArrayList<EndpointReferenceType>();//initialises busy list
			attempts++;
			for (EndpointReferenceType epr : eprs) {
				if (epr==null) continue; //sanity check
				try {
					logger.info("trying instance  @ "+epr.getAddress());
					this._interact(epr);	
					return epr;//if we made this far, it's done
				}
				//if instance says so, no point continuing
				catch (GCUBEUnrecoverableException e) {
					throw e;
				}
				//if instance is busy, put in list and try later
				catch(GCUBERetrySameException e) {
					lastException=e;
					logger.info("trying again later @ "+epr.getAddress());
					busyEprs.add(epr);
				}
				//if explicitly indicated, move on silently
				catch (GCUBERetryEquivalentException ignoreSilent) {
					lastException=ignoreSilent;
					logger.warn("error connecting instance "+epr.getAddress(), ignoreSilent);
				}
				//by default, move on too but loudly this time 
				catch (Exception ignoreLoud) {
					lastException=ignoreLoud;
					logger.error("failed @ instance "+epr.getAddress(),ignoreLoud);
				}
			}
			eprs = busyEprs;//try busy instances now
		//but only if there are any && we have not lost patience
		} while (!eprs.isEmpty() && attempts<2);

		//done our best...quitting with last exception encountered
		throw lastException;	
		
	}
	
		
	
	/**
	 * 
	 * @return
	 */
	public PARAMETER getParameter() {
		return parameter;
	}

	/**
	 * 
	 * @param paramenter
	 */
	public void setParameter(PARAMETER parameter) {
		this.parameter = parameter;
	}

	/**
	 * 
	 * @return
	 */
	public RETURN getReturnValue() {
		return returnValue;
	}

	/**
	 * returns the time (in seconds) between two calls to IC
	 * 
	 * @return time in seconds
	 */
	public int getWaitTime() {
		return waitTime;
	}

	/**
	 * set the time (in seconds) between two calls to IC
	 * 
	 * @param waitTime time in seconds
	 */
	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	/**
	 * 	
	 * @return
	 */
	public int getMaxAttempts() {
		return maxAttempt;
	}

	/**
	 * 
	 * @param maxAttempt
	 */
	public void setMaxAttempts(int maxAttempt) {
		this.maxAttempt = maxAttempt;
	}

	/**
	 * 
	 * @return
	 */
	public GCUBEScope getScope() {
		return scope;
	}

	/**
	 * 
	 * @param scope
	 */
	public void setScope(GCUBEScope scope) {
		this.scope = scope;
	}

	/**
	 * 
	 * @return
	 */
	public GCUBESecurityManager getSecMan() {
		return secMan;
	}

	/**
	 * 
	 * @param secMan
	 */
	public void setSecMan(GCUBESecurityManager secMan) {
		this.secMan = secMan;
	}

	/**
	 * 
	 * @return
	 */
	public Exception getException() {
		return exception;
	}

	/**
	 * 
	 * @return
	 */
	public State getState() {
		return state;
	}
	
}
