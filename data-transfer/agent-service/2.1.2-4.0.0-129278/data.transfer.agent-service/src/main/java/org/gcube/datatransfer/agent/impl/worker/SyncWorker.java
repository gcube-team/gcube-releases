package org.gcube.datatransfer.agent.impl.worker;

import org.gcube.datatransfer.agent.impl.grs.GRSOutComeWriter;

import gr.uoa.di.madgik.grs.writer.GRS2WriterException;

public abstract class SyncWorker extends Worker{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public abstract  String getOutcomeLocator() throws GRS2WriterException;
	
	protected GRSOutComeWriter outcomeWriter = null;

}
