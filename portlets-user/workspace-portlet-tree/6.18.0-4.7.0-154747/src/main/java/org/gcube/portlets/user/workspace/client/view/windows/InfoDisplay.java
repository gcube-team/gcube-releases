package org.gcube.portlets.user.workspace.client.view.windows;

import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.InfoConfig;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class InfoDisplay extends Info{
	
	/**
	 * 
	 * @param title
	 * @param text
	 * @param milliseconds - The time in milliseconds to display a message (defaults to 2500).
   */
	public InfoDisplay(String title, String text, int milliseconds){
		
		
		InfoConfig config = new InfoConfig(title, text);
		
		config.display = milliseconds;
		
		Info.display(config);
	 
	}
	
	public InfoDisplay(String title, String text){

		Info.display(title, text);
	 
	}
}
