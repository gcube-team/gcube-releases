package org.gcube.datatransfer.agent.impl.worker;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.agent.impl.context.AgentContext;
import org.gcube.datatransfer.agent.impl.context.ServiceContext;
import org.gcube.datatransfer.agent.impl.handlers.TransferHandler;
import org.gcube.datatransfer.agent.impl.state.AgentResource;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.SourceData;


/**
 * 
 * @author Andrea Manzi(CERN)
 */
public abstract class Worker<T extends TransferHandler> implements Callable, Serializable {
	
	protected ArrayList<T> list = new ArrayList<T>();
	
	
	/** The UUIDGen */
	protected static final UUIDGen uuidgen = UUIDGenFactory.getUUIDGen();

	protected AgentResource getResource() throws Exception {
		return (AgentResource) AgentContext.getContext().getAgent();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected GCUBELog logger = new GCUBELog(this.getClass());
	
	protected SourceData sourceParameters;
	
	protected DestData destParameters;	
	
	protected ThreadGroup threadList;
	
	protected String transferId;
	
	protected FutureTask<Worker> task= null;

	
	public FutureTask<Worker>  getTask() {
		return task;
	}

	public void setTask(FutureTask<Worker>  task) {
		this.task = task;
	}

	public ThreadGroup getThreadList() {
		return threadList;
	}

	public void setThreadList(ThreadGroup threadList) {
		this.threadList = threadList;
	}
	
	
	
}
