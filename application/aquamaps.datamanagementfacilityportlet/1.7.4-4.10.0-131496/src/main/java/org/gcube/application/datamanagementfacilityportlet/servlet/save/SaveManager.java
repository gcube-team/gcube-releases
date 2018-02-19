package org.gcube.application.datamanagementfacilityportlet.servlet.save;

import java.util.concurrent.ConcurrentHashMap;

import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveOperationProgress;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveOperationState;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveRequest;
import org.gcube.application.framework.core.session.ASLSession;





public class SaveManager {

	
	
	private static ConcurrentHashMap<String,SaveHandler> handlerSetPerUser=new ConcurrentHashMap<String, SaveHandler>();
	
	
	public static SaveOperationProgress startSaving(SaveRequest request,ASLSession session)throws Exception{
		String userName=session.getUsername();
		if(handlerSetPerUser.containsKey(userName)){			
			SaveOperationProgress progress=handlerSetPerUser.get(userName).getProgress();
			if(progress.getState().equals(SaveOperationState.RETRIEVING_FILES)
					||progress.getState().equals(SaveOperationState.SAVING_FILES)) 
				throw new Exception("User has already a save process ongoing");			
		}
		SaveHandler handler=createHandler(request, session);
		handlerSetPerUser.put(userName, handler);
		handler.startProcess();
		return getProgress(session);
	}
	
	public static SaveOperationProgress getProgress(ASLSession session)throws Exception{
		if(handlerSetPerUser.containsKey(session.getUsername())) return handlerSetPerUser.get(session.getUsername()).getProgress();
		else throw new Exception("No save operation found for user "+session.getUsername());
	}
	
	public static void releaseHandler(ASLSession session){
		if(handlerSetPerUser.containsKey(session.getUsername())) handlerSetPerUser.remove(session.getUsername());
	}
	
	private static SaveHandler createHandler(SaveRequest request, ASLSession session)throws Exception{
		SaveHandler handler=new SaveThread();
		handler.setRequest(session, request);
		return handler;
	}
	
}
