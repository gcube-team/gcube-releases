package org.gcube.portlets.user.messages.client.view;

import org.gcube.portlets.user.messages.client.ConstantsPortletMessages;
import org.gcube.portlets.user.messages.client.view.message.GxtMessagesPanel;
import org.gcube.portlets.user.messages.client.view.message.MessagesTreePanel;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;


/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class GxtBorderLayoutMainPanel extends ContentPanel {

	private ContentPanel north = new ContentPanel();
	private ContentPanel west = new ContentPanel();
	private ContentPanel center = new ContentPanel();
	private ContentPanel east = new ContentPanel();
	private ContentPanel south = new ContentPanel();


	public GxtBorderLayoutMainPanel(MessagesTreePanel messagesPanel, GxtMessagesPanel gxtMessagesPanel) {
		this.initLayout();
		this.createLayouts(messagesPanel, gxtMessagesPanel);
	}

	private void initLayout(){
			
		north.setId("NorthPanel");
		north.setLayout(new FitLayout());
		
		west.setId("WestPanel");
		west.setLayout(new FitLayout());
		west.setHeaderVisible(false);
		
		center.setId("CenterPanel");
		center.setLayout(new FitLayout());
	    center.setHeaderVisible(false);
	    
		east.setId("EastPanel");
		center.setScrollMode(Scroll.AUTOX);
		center.setBorders(false);
		
//	    north.setHeading(ConstantsPortletMessages.MESSAGES);
	    west.setHeading(ConstantsPortletMessages.EXPLORER);
	    center.setHeading(ConstantsPortletMessages.MESSAGES);
	    
//	    south.setLayout(new FitLayout());
//	    east.setVisible(false);
//	    south.setVisible(false);
	    
	    setHeight(ConstantsPortletMessages.DEFAULT_HEIGHT);

	}
		
	public void createLayouts(MessagesTreePanel messagesPanel, GxtMessagesPanel gxtMessagesPanel){
		
		final BorderLayout borderLayout = new BorderLayout();
		setLayout(borderLayout);
		//setStyleAttribute("padding", "10px");
		setHeaderVisible(false);

//		center.add(this.gridFilter);

	    BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 80, 80, 100);  
	    northData.setCollapsible(true);  
	    
	    northData.setSplit(true);  //Split bar between regions
//	    northData.setFloatable(true);  
	    northData.setCollapsible(true);  
//	    northData.setHideCollapseTool(false);  
//	    northData.setSplit(true);  
	    northData.setMargins(new Margins(0, 0, 1, 0));  
	  
	    BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 160,160,200);  
	    westData.setSplit(true);  
	    westData.setCollapsible(true);  
	    westData.setMargins(new Margins(0,1,0,0));
	  
	    BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
	    centerData.setMargins(new Margins(0));
	  
	    BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 150,50,150);  
        eastData.setSplit(true);  
	    eastData.setCollapsible(true);  
	    eastData.setMargins(new Margins(0,0,0,1));  
	  
	    BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 140,140,140);  
	    southData.setSplit(true);  
	    southData.setCollapsible(true);  
	    southData.setMargins(new Margins(1, 0, 0, 0));  
	    
//	    north.add();
	    west.add(messagesPanel);
	    center.add(gxtMessagesPanel);
		
//	    south.add(this.detailsContainer);
	    
	    west.addListener(Events.Resize, new Listener<BoxComponentEvent>(){

			@Override
			public void handleEvent(BoxComponentEvent be) {

			    
			}
			
		});

//	    add(north, northData);  
	    add(west, westData); 
	    add(center, centerData);  
//	    add(east, eastData);
	}

	public void updateHeight(int rootHeight) {
		setHeight(rootHeight);
//		gxtCardLayoutMainPanel.updateHeight(rootHeight-74);
	}

	public void updateWidth(int rootWidth) {
		setWidth(rootWidth);
//		gxtCardLayoutMainPanel.updateWidth(rootWidth);

	}
}