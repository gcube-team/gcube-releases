package org.gcube.portlets.user.workspace.client.view.windows;

import com.extjs.gxt.ui.client.widget.MessageBox;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class MessageBoxWait {
	
	private MessageBox box = null;
	
	public MessageBoxWait(String title, String msg, String progressText){
		
		   box = MessageBox.wait(title, msg, progressText);  
		   
//		   Timer t = new Timer() {  
//		          @Override  
//		          public void run() {  
//		            Info.display("Message", "Your fake data was saved", "");  
//		            box.close();  
//		          }  
//		        };  
//		        t.schedule(3000);  
	}
	
	
	public MessageBox getMessageBoxWait() {
		return box;
	}
	
}
