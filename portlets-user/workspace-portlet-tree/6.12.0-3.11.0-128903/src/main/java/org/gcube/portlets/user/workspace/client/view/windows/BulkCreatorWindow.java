package org.gcube.portlets.user.workspace.client.view.windows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.event.DeleteBulkEvent;
import org.gcube.portlets.user.workspace.client.model.BulkCreatorModel;
import org.gcube.portlets.user.workspace.client.resources.Resources;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.WidgetComponent;
import com.extjs.gxt.ui.client.widget.Window;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class BulkCreatorWindow extends Window {  
	
	/**
	 * This is a singleton
	 */
	private static BulkCreatorWindow instance;
	private static ContentPanel cp;
	private VerticalPanel vp;
	private static HashMap<String, BulkProgressBar> hashProgressBars;
	private static HashMap<String, Boolean> hashLoadCompletedNotify;
	private static HashMap<String, HorizontalPanel> hashHorizontalPanels;
	private static String LASTOPERATION = "Last Operation: ";
	private Html lastOperation = new Html(LASTOPERATION);
	private LayoutContainer lc;
	private static String WINDOWTITLE = "Folder Bulk Creator";
	private static String FAILED = "failed";


	public static synchronized BulkCreatorWindow getInstance() {
		if (instance == null)
			instance = new BulkCreatorWindow();
		return instance;
	}

	private BulkCreatorWindow() {
		
		this.setResizable(false);
		this.setSize(500, 400);
		this.setHeaderVisible(true);
		this.setHeading(WINDOWTITLE);
		
		lc = new LayoutContainer();
		lc.setStyleAttribute("margin", "10px");
		cp = new ContentPanel();
		cp.setBodyBorder(true);
		cp.setStyleAttribute("padding", "10px");
		
		vp = new VerticalPanel();
		
		cp.setSize(470, 350);
		cp.setHeaderVisible(false);
		hashProgressBars = new HashMap<String, BulkProgressBar>();
		hashHorizontalPanels = new HashMap<String, HorizontalPanel>();
		hashLoadCompletedNotify = new HashMap<String, Boolean>();
		cp.setScrollMode(Scroll.AUTO);
		
		cp.add(vp);
		lc.add(lastOperation);
		lc.add(cp);
		
		add(lc);

	}
	
	/**
	 * 
	 * @param listBulks
	 * @return true if almost one progress bar is ongoing
	 */
	public boolean addProgressBar(List<BulkCreatorModel> listBulks){
		
		boolean isLoading = false;
		
		for(final BulkCreatorModel bulk : listBulks){
			
//			System.out.println("bulk " + bulk.getIdentifier());
//			System.out.println("bulk name " + bulk.getName());
//			System.out.println("bulk progress " + bulk.getPercentage());
		
			BulkProgressBar bulkBar = hashProgressBars.get(bulk.getIdentifier());
			
			if(bulkBar!=null){ //is update
				
//				System.out.println("bulkBar.updateProgress "+ bulk.getIdentifier());
//				bulkBar.updateProgress(Integer.parseInt(bulk.getStatus()));
					
				setVisibilityCancel(bulk.getIdentifier(),false);
				
				switch (bulk.getState()) {

				case 1: // COMPLETED = 1;
					
					lastOperation.setHtml(LASTOPERATION + bulkBar.getProgressText() + " uploading completed");
					notifyInfoCompleted(bulk.getIdentifier(), bulkBar.getProgressText());
					
//					new InfoDisplay(infoTitle,  bulkBar.getProgressText() + " uploading completed");
					bulkBar.getElement().getStyle().setBorderColor("#000000");
					bulkBar.updateProgress(100);
					setVisibilityCancel(bulk.getIdentifier(),true);

					break;

				case 0: //ONGOING = 0;

					bulkBar.updateProgress((float) getPercentage(bulk.getPercentage()));
					isLoading = true;
					break;

				case 2:  //FAILED = 2;
					
//					bulkBar.setSuffixText(FAILED);
					bulkBar.getElement().getStyle().setBorderColor("#f00");
					
					bulkBar.updateProgress(getPercentage(bulk.getPercentage()));
					
					if(bulk.getNumFails()==bulk.getNumRequests())
						bulkBar.updateText(FAILED);
					else
						bulkBar.updateText(getPercentage(bulk.getPercentage()) + "% completed with " + bulk.getNumFails() + " of " + bulk.getNumRequests() + " " +  FAILED);

					if(bulk.getPercentage()>=1){ //it's completed
						setVisibilityCancel(bulk.getIdentifier(),true);
						lastOperation.setHtml(LASTOPERATION + bulkBar.getProgressText() + " completed with " + FAILED);
						notifyInfoCompleted(bulk.getIdentifier(), bulkBar.getProgressText());
					}
					
					break;
				}

				vp.layout();
				hashHorizontalPanels.get(bulk.getIdentifier()).layout();
			
			}
			else{ //create new horizontal panel that contains new progress bar
				
				HorizontalPanel hp = new HorizontalPanel();
				hp.setHorizontalAlign(HorizontalAlignment.CENTER);
				hp.setSize(400, 20);
				hp.setId(bulk.getIdentifier());
//				hp.setItemId(bulk.getIdentifier());
				
				Html nameProgress = new Html(bulk.getName());
				nameProgress.setStyleAttribute("margin-right", "5px");
						
				hp.add(nameProgress);
				
				BulkProgressBar bulkPB = new BulkProgressBar(bulk.getIdentifier(), bulk.getName());
				hp.add(bulkPB);
				
				
				final WidgetComponent cancel =  new WidgetComponent(new Image(Resources.getImageDelete()));
				cancel.setStyleName("img-link");
				cancel.setStyleAttribute("margin-left", "5px");
				cancel.setId("delete-"+bulk.getIdentifier());
				cancel.setItemId("delete-"+bulk.getIdentifier());
				cancel.setVisible(false);
				hp.add(cancel);
				
				
				cancel.addListener(Events.OnClick, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						
						System.out.println("In cancel bulk id "  + bulk.getIdentifier());
						
						AppControllerExplorer.getEventBus().fireEvent(new DeleteBulkEvent(bulk.getIdentifier()));
						
//						removeProgress(bulk.getIdentifier());
						
					}
				});
				
				new InfoDisplay("Bulk Creator", "Found new " +bulk.getName());
				
				hashHorizontalPanels.put(bulk.getIdentifier(), hp); //add hp into hashHorizontalPanels
				hashProgressBars.put(bulk.getIdentifier(), bulkPB); //add bulkPB into hashProgressBars
				hashLoadCompletedNotify.put(bulk.getIdentifier(), false); //add false (at load completed event) into hashLoadCompletedNotify

				hp.setStyleAttribute("margin", "10px");
			
				vp.add(hp);
				
			}
			
			vp.layout();
			
		
		}
		
		return isLoading;
		
//		deleteProgressCompleted(progressIdFound);
		
	}
	
	
	private void notifyInfoCompleted(String pgId, String pgText){
		
		if(!hashLoadCompletedNotify.get(pgId)){
			new InfoDisplay(WINDOWTITLE,  pgText + " uploading completed");
			hashLoadCompletedNotify.put(pgId, true);
		}
		
	}
	
	private float getPercentage(float perc){
		
		double percentage = perc*100;
		int precision = 10; //keep 1 digits
		return (float) Math.floor(percentage * precision +.5)/precision;
	}
	
	public void setVisibilityCancel(String hashHPKey, boolean bool){
		
		HorizontalPanel hp = hashHorizontalPanels.get(hashHPKey);
		if(hp!=null){
			
			WidgetComponent cancel = (WidgetComponent) hp.getItemByItemId("delete-"+hashHPKey);
			
			if(cancel!=null)
				cancel.setVisible(bool);
			
		}
			
	}
	
	
	public void removeProgress(String hashHPKey) {
		
	
		vp.remove(hashHorizontalPanels.get(hashHPKey));
		lastOperation.setHtml(LASTOPERATION + hashProgressBars.get(hashHPKey).getProgressText() + " delete");
		hashProgressBars.remove(hashHPKey); //remove progress bar from hash
		hashHorizontalPanels.remove(hashHPKey); //remove hp from hash 
		hashLoadCompletedNotify.remove(hashHPKey); //remove notify event

		vp.layout();
		
	}
	
	@SuppressWarnings("unused")
	private void deleteProgressCompleted(List<String> progressIdFound){
		
		List<String> progressIdNotFound = new ArrayList<String>();
		
		for(String key : hashHorizontalPanels.keySet()){
			
		    System.out.println("Key " + key );
			
			if(!progressIdFound.contains(key)){ //if key isn't not found - progress is completed so is removed
				
				System.out.println("Key is not present " + key );
				 
				BulkProgressBar bulkPB = hashProgressBars.get(key);
				lastOperation.setHtml(LASTOPERATION + bulkPB.getProgressText() + " uploading completed");
				
				bulkPB.updateProgress(100);
				progressIdNotFound.add(key);
			}
		}
		
		for(String key : progressIdNotFound){
			
			BulkProgressBar bulkPB = hashProgressBars.get(key);
			lastOperation.setHtml(LASTOPERATION + bulkPB.getProgressText() + " uploading completed");
			
			new InfoDisplay("Bulk Creator",  bulkPB.getProgressText() + " uploading completed");
			
//			vp.remove(hashHorizontalPanels.get(key)); //remove hp from view
			hashProgressBars.remove(key); //remove progress bar from hash
			hashHorizontalPanels.remove(key); //remove hp from hash 
		}
		
		vp.layout();
		cp.layout();
	}
	
	

}
