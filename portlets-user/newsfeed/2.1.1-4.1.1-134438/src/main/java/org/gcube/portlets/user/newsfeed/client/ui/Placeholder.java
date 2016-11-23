package org.gcube.portlets.user.newsfeed.client.ui;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;
/**
 * This panel will contain the attachments/previews
 * @author Massimiliano Assante at ISTI CNR
 * @author Costantino Perciante at ISTI CNR
 *
 */
public class Placeholder extends FlowPanel {

	private static final String SHOW_OTHER_ATTACHMENTS_LABEL = "Show All";
	
	// check if we need to show more attachments
	private boolean appendShowMoreLabel;

	/**
	 * Modified version of the add method.
	 */
	public void add(AttachmentPreviewer atPrev){

		// retrieve the list of children
		WidgetCollection listOfChildren = this.getChildren();

		// check the size 
		int size = listOfChildren.size();

		if(size % 2 == 0){
			// in this case the next attachment we are going to add remains with the same width
			add((Widget)atPrev);
			GWT.log("added without changing size");
		}
		else{
			// we need to change the length of the last element added and of this new one
			((AttachmentPreviewer) listOfChildren.get(size -1)).changeAttachmentWidth(45, Unit.PCT);
			atPrev.changeAttachmentWidth(45, Unit.PCT);

			// add it finally
			add((Widget)atPrev);
		}

		if(size >= 4){
			// ok, we are going to add the 5th attachment and so forth but we hide them..
			atPrev.setVisible(false);

			// remember to add the button to let the user show them later
			appendShowMoreLabel = true;
		}
	}

	/**
	 * Append "Show All" label to the post template.
	 */
	public void appendShowMoreLabel(){

		if(appendShowMoreLabel){
			
			final WidgetCollection listOfChildren = this.getChildren();

			final SimplePanel sp = new SimplePanel();
			sp.setStyleName("centered");
			
			final Anchor showMoreAttachments = new Anchor(SHOW_OTHER_ATTACHMENTS_LABEL);
			showMoreAttachments.setTitle("Show all the attached files");
			showMoreAttachments.setStyleName("link");
			sp.add(showMoreAttachments);
			
			showMoreAttachments.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {

					// retrieve the list of attachmentPreviewers and show them
					for(Widget w: listOfChildren){
						w.setVisible(true);
					}

					sp.setVisible(false);

				}
			});

			// show the panel
			this.add(sp);
		}
	}
}
