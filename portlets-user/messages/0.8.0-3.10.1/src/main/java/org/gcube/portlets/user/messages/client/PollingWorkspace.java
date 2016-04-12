package org.gcube.portlets.user.messages.client;

import java.util.List;

import org.gcube.portlets.user.messages.client.alert.InfoDisplay;
import org.gcube.portlets.user.messages.client.event.LoadMessagesEvent;
import org.gcube.portlets.user.messages.shared.GXTCategoryItemInterface;
import org.gcube.portlets.user.messages.shared.MessageModel;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class PollingWorkspace {
	
	static int counterNotOpenMessages = -1;
	
	public static void pollReceivedMessages(int numberMessagesNotOpen, int milliseconds){
		
		  counterNotOpenMessages = numberMessagesNotOpen;
		  
		  if(counterNotOpenMessages==-1){
			  
			  MessagesApplicationController.rpcMessagesManagementService.getNewMessagesReceived(new AsyncCallback<List<MessageModel>>() {

				@Override
				public void onFailure(Throwable caught) {
					System.out.println("Error in PollingWorkspace - in function getNewMessagesReceived ");
					
				}

				@Override
				public void onSuccess(List<MessageModel> result) {
//					System.out.println("result size " +result.size());
					counterNotOpenMessages = result.size();
					
				}
			});
		  }
		
		  
	      Timer timer = new Timer() {
              @Override
              public void run()
              { 
            	  MessagesApplicationController.rpcMessagesManagementService.getNewMessagesReceived(new AsyncCallback<List<MessageModel>>() {
					
					@Override
					public void onSuccess(List<MessageModel> result) {
							if(result.size()>counterNotOpenMessages){
							
							int newMess = result.size() - counterNotOpenMessages;
							String message = "message";
							if(newMess>1)
								message+="s";
							
							new InfoDisplay("Messages", "You have received " + newMess + " new " + message);
				
							counterNotOpenMessages = result.size();
							
							MessagesApplicationController.getEventBus().fireEvent(new LoadMessagesEvent(GXTCategoryItemInterface.MS_RECEIVED, true));
						}
						
						counterNotOpenMessages = result.size(); //update current counter
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						System.out.println("Failure rpc get new message/s");
						
					}
				});
              }
          };
          timer.scheduleRepeating(milliseconds);
	}


	public static int getCounterNotOpenMessages() {
		return counterNotOpenMessages;
	}


	public static void setCounterNotOpenMessages(int counterNotOpenMessages) {
		PollingWorkspace.counterNotOpenMessages = counterNotOpenMessages;
	}
}
