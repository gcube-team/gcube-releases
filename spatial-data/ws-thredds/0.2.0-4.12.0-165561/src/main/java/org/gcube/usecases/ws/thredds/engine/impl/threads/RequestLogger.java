package org.gcube.usecases.ws.thredds.engine.impl.threads;

import java.io.BufferedWriter;
import java.io.FileWriter;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.usecases.ws.thredds.Constants;
import org.gcube.usecases.ws.thredds.SyncEngine;

import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestLogger {

	
	private static RequestLogger instance=null;
	
	@Synchronized
	public static RequestLogger get() {
		if(instance==null) {
			instance=new RequestLogger();
		}
		return instance;
	}
	
	
	
	BufferedWriter writer = null;
	
	private RequestLogger() {
		SyncEngine engine=SyncEngine.get();
		if(engine.isRequestLoggerEnabled()) {
			String path=engine.getRequestLoggerPath();
			log.info("Initializing Request Logger to path {} ",path);
			try{
				writer = new BufferedWriter(new FileWriter(path, false));
			}catch(Throwable t) {
				log.warn("Unable to initialize Writer on path {} ",path,t);
			}
		}
	}
	
	
	
	public void log(SynchronizationRequest request) {
		if(writer!=null) {
			String logString="INVALID";
			if(request instanceof DeleteRemoteRequest) {
				DeleteRemoteRequest deleteRequest=(DeleteRemoteRequest) request;
				logString=String.format("DELETE %s from %s (WS-Path: %s)", deleteRequest.getToRemoveName(),getRemotePath(deleteRequest.getLocation()),getWSPath(deleteRequest.getLocation()));
			}else if(request instanceof TransferToThreddsRequest) {
				TransferToThreddsRequest transferRequest=(TransferToThreddsRequest) request;
				logString=String.format("EXPORT %s to %s from %s", getName(transferRequest.getToTransfer()),getRemotePath(transferRequest.getToTransfer()),getWSPath(transferRequest.getToTransfer()));
			}else if(request instanceof TransferFromThreddsRequest) {
				TransferFromThreddsRequest transferRequest=(TransferFromThreddsRequest)request;
				if(transferRequest.getTargetItem()!=null) 
					logString=String.format("UPDATE LOCAL %s in %s from %s ", getName(transferRequest.getTargetItem()),getWSPath(transferRequest.getLocation()),getRemotePath(transferRequest.getLocation()));
				else
					logString=String.format("IMPORT LOCAL %s in %s from %s ", transferRequest.getRemoteFilename(),getWSPath(transferRequest.getLocation()),getRemotePath(transferRequest.getLocation()));
			}
			try {
				writer.write(logString+"\n");
			}catch(Throwable t) {
				log.warn("Exception wile trying to write log",t);
			}
		}
	}
	
	
	public void close() {
		if(writer!=null) {
			try {
			writer.flush();
			writer.close();
			}catch(Throwable t) {
				log.warn("Unable to close writer ",t);
			}
		}
	}
	
	
	private static final String getRemotePath(WorkspaceItem item) {
		try{
			if(item.isFolder()) return item.getProperties().getPropertyValue(Constants.WorkspaceProperties.REMOTE_PATH);
			else return getRemotePath(item.getParent());
		}catch(InternalErrorException e) {
			log.warn("Unable to get Remote Path ",e);
			return "N/A";
		}
	}
	
	
	private static final String getName(WorkspaceItem item) {
		try {
			return item.getName();
		}catch(InternalErrorException e) {
			log.warn("Unable to get name ",e);
			return "N/A";
		}
	}
	
	private static final String getWSPath(WorkspaceItem item) {
		try{
			if(item.isFolder()) return item.getPath();
			else return getRemotePath(item.getParent());
		}catch(InternalErrorException e) {
			log.warn("Unable to get WS Path ",e);
			return "N/A";
		}
	}
}
