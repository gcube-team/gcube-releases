package org.gcube.portlets.user.td.widgetcommonevent.client;

import org.gcube.portlets.user.td.widgetcommonevent.client.event.RibbonEvent;
import org.gcube.portlets.user.td.widgetcommonevent.client.type.RibbonType;
import org.junit.Test;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * Test Event 
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class GwtTestWidgetCommonEvent extends GWTTestCase {

  /**
   * Must refer to a valid module that sources this class.
   */
  @Override
  public String getModuleName() {
    return "org.gcube.portlets.user.td.widgetcommonevent.WidgetCommonEventJUnit";
  }

 
  /**
   * Test Events fire
   */
  @Test
  public void testEvents() {
    
	  SimpleEventBus eventBus=new SimpleEventBus();
	  
	  eventBus.addHandler(RibbonEvent.TYPE,
				new RibbonEvent.RibbonEventHandler() {
					
					
					public void onRibbon(RibbonEvent event) {
						doRibbonCommand(event);
						
					}
				});
	  
	  eventBus.fireEvent(new RibbonEvent(RibbonType.IMPORT_SDMX));
	  eventBus.fireEvent(new RibbonEvent(RibbonType.IMPORT_CSV));
	  eventBus.fireEvent(new RibbonEvent(RibbonType.IMPORT_JSON));
	  
	
  }

  private void doRibbonCommand(RibbonEvent event) {
		System.out.println("doRibbonCommand Type: " + event.getRibbonType());
		try {
			switch (event.getRibbonType()) {
			case IMPORT_SDMX:
				System.out.println("Start Import SDMX");
				break;
			case IMPORT_CSV:
				System.out.println("Start Import CSV");
				break;
			case IMPORT_JSON:
				System.out.println("Start Import JSON");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			System.out.println("doRibbonCommand Error : " + e.getLocalizedMessage()
					+ " \n " + e.getMessage());
		}
	}

}
