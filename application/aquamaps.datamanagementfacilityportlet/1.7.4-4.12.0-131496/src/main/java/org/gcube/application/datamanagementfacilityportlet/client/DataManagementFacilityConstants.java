package org.gcube.application.datamanagementfacilityportlet.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.application.datamanagementfacilityportlet.client.rpc.Tags;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.Algorithm;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveOperationProgress;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveOperationState;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.data.save.SaveRequest;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.AlgorithmType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientLogicType;
import org.gcube.application.datamanagementfacilityportlet.client.rpc.types.ClientResourceType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DataManagementFacilityConstants {

	public static String COMMONGUIDIV="DATA_MANAGEMENT_FACILITY";
	public static final Map<String,String> servletUrl=new HashMap<String, String>();
	public static final Map<ClientResourceType,String> resourceNames=new HashMap<ClientResourceType, String>();
	public static DateTimeFormat timeFormat=DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_LONG);
	public static Map<ClientLogicType,List<Algorithm>> logicAlgorithmMap=new HashMap<ClientLogicType, List<Algorithm>>();
	
	
	public static void init(String sessionId)
	{
		servletUrl.put(Tags.submittedReportServlet,GWT.getModuleBaseURL()+Tags.submittedReportServlet+";jsessionid=" + sessionId);
		servletUrl.put(Tags.resourceServlet,GWT.getModuleBaseURL()+Tags.resourceServlet+";jsessionid=" + sessionId);
		servletUrl.put(Tags.analysisServlet,GWT.getModuleBaseURL()+Tags.analysisServlet+";jsessionid=" + sessionId);
		servletUrl.put(Tags.resourceMapServlet,GWT.getModuleBaseURL()+Tags.resourceMapServlet);
		servletUrl.put(Tags.resourceLoadServlet,GWT.getModuleBaseURL()+Tags.resourceLoadServlet);
		
		servletUrl.put(Tags.serviceImpl,GWT.getModuleBaseURL()+Tags.serviceImpl);
		servletUrl.put(Tags.directQueryServlet, GWT.getModuleBaseURL()+Tags.directQueryServlet+";jsessionid=" + sessionId);
		
		resourceNames.put(ClientResourceType.HCAF, "Environmental Data ("+ClientResourceType.HCAF+")");
		resourceNames.put(ClientResourceType.HSPEC, "Simulation Data ("+ClientResourceType.HSPEC+")");
		resourceNames.put(ClientResourceType.HSPEN, "Species Envelope Data ("+ClientResourceType.HSPEN+")");
		resourceNames.put(ClientResourceType.OCCURRENCECELLS, "Species Occurrence Cells");
		
		ArrayList<Algorithm> hspecList=new ArrayList<Algorithm>();
		hspecList.add(new Algorithm(AlgorithmType.NativeRange+""));
		hspecList.add(new Algorithm(AlgorithmType.NativeRange2050+""));
		hspecList.add(new Algorithm(AlgorithmType.SuitableRange+""));
		hspecList.add(new Algorithm(AlgorithmType.SuitableRange2050+""));
		logicAlgorithmMap.put(ClientLogicType.HSPEC, hspecList);
		ArrayList<Algorithm> hspenList=new ArrayList<Algorithm>();
		hspenList.add(new Algorithm(AlgorithmType.HSPENRegeneration+""));
		logicAlgorithmMap.put(ClientLogicType.HSPEN, hspenList);
		ArrayList<Algorithm> hcafList=new ArrayList<Algorithm>();
		hcafList.add(new Algorithm(AlgorithmType.LINEAR+""));
		hcafList.add(new Algorithm(AlgorithmType.PARABOLIC+""));
		logicAlgorithmMap.put(ClientLogicType.HCAF, hcafList);
		
		
	}
	
	public static void sendSaveRequest(SaveRequest toSend){
		DataManagementFacility.localService.saveOperationRequest(toSend, new AsyncCallback<SaveOperationProgress>() {
			
			@Override
			public void onSuccess(SaveOperationProgress result) {
				final MessageBox box = MessageBox.progress("Please wait",  
			            "Save data to workspace", "Retrieving data...");
				
				final Timer t = new Timer() {  
					
			          @Override  
			          public void run() {
			        	  DataManagementFacility.localService.getSaveProgress(new AsyncCallback<SaveOperationProgress>() {
			        		  @Override
			        		public void onFailure(Throwable caught) {
			        			  if(box.isVisible()) box.close();
		        				  Info.display("Save Operation", "Unable to complete, "+caught.getMessage());
		        				  Log.error("Unexpected Error while retrieving progress",caught);
		        				  cancel();
			        		}
			        		  @Override
			        		public void onSuccess(SaveOperationProgress result) {
			        			  switch(result.getState()){
			        			  case COMPLETED : {
			        				  if(box.isVisible()) box.close();
			        				  Info.display("Save Operation", "Complete");
			        				  cancel();
				        			  break;	
			        			  }
			        			  case ERROR : {
			        				  if(box.isVisible()) box.close();
			        				  Info.display("Save Operation", "Unable to complete, "+result.getFailureReason());
			        				  Log.error("Unexpected Error in operation",result.getFailureDetails());
			        				  cancel();
			        				  break;
			        			  }
			        			  case RETRIEVING_FILES :{
			        				  //still retrieving, do nothing
			        				  break;
			        			  }
			        			  case SAVING_FILES : {
			        				  box.updateProgress((double)result.getSavedCount()/result.getToSaveCount(), 
			        						  "Saving "+result.getSavedCount()+" of "+result.getToSaveCount());			        				  
			        				  break;
			        			  }
			        			  }
			        		}
						});
			            
			          }  
			        };  
			    t.scheduleRepeating(500);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Info.display("Save request", "Unable to save request, "+caught.getMessage());
				Log.error("Unexpected Error while sending save request",caught);
			}
		});
	}
	
	public static String getImagePreviewUrl(){
		return "http://dl.dropbox.com/u/15737233/ContinentViewAsia.jpg";
	}
	
	
	public static native String getMonitorUrl() /*-{
		return $wnd.getMonitorUrl();
	}-*/;
}
