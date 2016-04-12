package org.gcube.portlets.user.shareupdates.client.view;


import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;
/**
 * This panel will contain the attachments/previews
 * @author Massimiliano Assante at ISTI CNR
 * @author Costantino Perciante at ISTI CNR
 *
 */
public class Placeholder extends FlowPanel {

	/**
	 * Modified version of the add method.
	 */
	public void add(AttachmentPreviewer atPrev){

		WidgetCollection listOfChildren = this.getChildren();

		// check the size 
		int size = listOfChildren.size();
		
		GWT.log("Size is " + size);

		if(size % 2 == 0){
			// in this case the next attachment we are going to add remains with the same width
			add((Widget)atPrev);
			GWT.log("added without changing size");
		}
		else{
			// we need to change the length of the last element added and of this new one
			((AttachmentPreviewer) listOfChildren.get(size -1)).changeAttachmentWidth(278, Unit.PX);
			atPrev.changeAttachmentWidth(278, Unit.PX);

			// add it finally
			add((Widget)atPrev);
		}
	}

	/**
	 * When a user delete an attachment we ask the last to adapt to the new free space if it can.
	 */
	public void resizeLastElementWidth() {
		
		WidgetCollection listOfChildren = this.getChildren();
		
		// if the number is odd
		if(listOfChildren.size()%2 != 0)
			((AttachmentPreviewer) listOfChildren.get(listOfChildren.size() -1)).changeAttachmentWidth(579, Unit.PX);
		
	}
}
