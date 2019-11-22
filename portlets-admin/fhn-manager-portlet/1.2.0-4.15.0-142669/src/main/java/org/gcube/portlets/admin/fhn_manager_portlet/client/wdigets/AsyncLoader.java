package org.gcube.portlets.admin.fhn_manager_portlet.client.wdigets;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.admin.fhn_manager_portlet.client.FhnManagerController;
import org.gcube.portlets.admin.fhn_manager_portlet.client.FhnManagerEntryPoint;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.FutureEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.client.event.ShowMessageEvent;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.OperationTicket;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.communication.ProgressMessage;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.ProgressBar;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AsyncLoader extends Composite implements AsyncCallback<OperationTicket> {

	private static Logger logger = Logger.getLogger(AsyncLoader.class+"");
	
	private static AsyncLoaderUiBinder uiBinder = GWT
			.create(AsyncLoaderUiBinder.class);

	interface AsyncLoaderUiBinder extends UiBinder<Widget, AsyncLoader> {
	}

	
	@UiField Modal m;
	@UiField Icon icon;
	@UiField Label content;
	@UiField ProgressBar progress;
	@UiField Button button;
	
	private GwtEvent theEvent;
	private boolean continuePolling;
	private boolean waitForHumanAction=false;
	
	@UiHandler("button")
	public void buttonClickHandler(ClickEvent e){
		hide();
		if(theEvent!=null){
			logger.log(Level.FINE," Firing event "+theEvent);
			FhnManagerController.eventBus.fireEvent(theEvent);
		}
	}
	
	String title;
	
	public AsyncLoader(String title,String msg,FutureEvent event, Boolean waitForHumanAction) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.title=title;
		logger.log(Level.FINE, "Creating asinc loader, future Event is "+event.getClass().getCanonicalName());
		theEvent=(GwtEvent) event;
		logger.log(Level.FINE, theEvent.getAssociatedType().toString());
		
		m.setTitle(title);
		content.setText(msg);
		
		m.show();
		this.waitForHumanAction=waitForHumanAction;
	}

	public AsyncLoader(String title,String msg,FutureEvent event){
		this(title,msg,event,true);
	}
	
	public void hide(){
		m.hide();
	}
	
	
	@Override
	public void onFailure(Throwable caught) {
		hide();
		FhnManagerController.eventBus.fireEvent(new ShowMessageEvent(title, "Unexpected Error : "+caught.getMessage()));
	}
	
	@Override
	public void onSuccess(final OperationTicket result) {
		continuePolling=true;
		
		Timer timer=new Timer(){
			@Override
			public void run() {
				if(continuePolling)
					FhnManagerEntryPoint.managerService.getProgress(result, progressCallback);
				else cancel();				
			}
		};		
		timer.scheduleRepeating(500);
		
	}
	
	
	private AsyncCallback<ProgressMessage> progressCallback=new AsyncCallback<ProgressMessage>() {
		@Override
		public void onFailure(Throwable caught) {
			continuePolling=false;
			content.setText(caught.getMessage());
			icon.setSpin(false);
			icon.setType(IconType.EXCLAMATION);
			icon.addStyleName("btn-danger");
			button.setEnabled(true);
		}
		
		@Override
		public void onSuccess(ProgressMessage result) {
			switch(result.getStatus()){
			case ERROR : continuePolling=false;
						 content.setText(result.getMessage());
						 icon.setSpin(false);
						 icon.setType(IconType.EXCLAMATION);
						 icon.addStyleName("btn-danger");
						 button.setEnabled(true);
						 break;
			case ONGOING : content.setText(result.getMessage());
						progress.setPercent(new Double(result.getProgressCount()*100).intValue());
			break;
			case SUCCESS : continuePolling=false;
			
			if(theEvent!=null&&(theEvent instanceof FutureEvent)){
				logger.log(Level.FINE," Cascade event is Future. Setting result : "+result.getResult());
				((FutureEvent)theEvent).setResult(result.getResult());
			}
			 content.setText(result.getMessage());
			 progress.setPercent(100);
			 icon.setSpin(false);
			 icon.setType(IconType.CHECK);
			 icon.addStyleName("btn-success");
			 button.setEnabled(true);
			 if(!waitForHumanAction) buttonClickHandler(null);
			 break;
			}
		}
	};
}
