package org.gcube.datatransfer.agent.impl.handlers;

import java.util.ArrayList;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.datatransfer.agent.impl.jdo.TransferObject;
import org.gcube.datatransfer.agent.stubs.datatransferagent.DestData;
import org.gcube.datatransfer.agent.stubs.datatransferagent.TransferType;

/**
 * 
 * @author Andrea Manzi(CERN)
 *
 */
public abstract class TransferHandler implements Runnable{
	
	protected GCUBELog logger = new GCUBELog (this.getClass());

	protected boolean errorHappened = false;
	
	public boolean isErrorHappened() {
		return errorHappened;
	}


	ArrayList<TransferObject> transferObjs = new ArrayList<TransferObject>();
	
	protected String [] inputFiles = null;
	protected long timeout = 0;
	protected String outPath = "";
	protected String transferId = "";
	protected TransferType transferType = null;
	protected DestData destData;
	protected int startIndex = 0;
	protected int endIndex = 0;

	
	public ArrayList<TransferObject>  getTransferObjList() {
		return transferObjs;
	}

	public void setTransferObj(ArrayList<TransferObject>  transferObjs) {
		this.transferObjs = transferObjs;
	}


}
