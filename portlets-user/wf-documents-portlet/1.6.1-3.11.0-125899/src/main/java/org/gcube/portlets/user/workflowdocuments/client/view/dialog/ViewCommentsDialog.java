package org.gcube.portlets.user.workflowdocuments.client.view.dialog;

import java.util.ArrayList;

import org.gcube.portlets.admin.wfdocslibrary.shared.UserComment;
import org.gcube.portlets.user.workflowdocuments.client.event.AddCommentEvent;
import org.gcube.portlets.user.workflowdocuments.shared.WorkflowDocument;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.RichTextArea;

public class ViewCommentsDialog  extends Dialog {

	private TabPanel advanced;  

	public ViewCommentsDialog(WorkflowDocument wfDoc,  ArrayList<UserComment> comments) {
		super.setPixelSize(600, 280);
		advanced = new TabPanel();  
		advanced.setSize(585, 250);  
		advanced.setMinTabWidth(115);  
		advanced.setResizeTabs(true);  
		advanced.setAnimScroll(true);  
		advanced.setTabScroll(true);  

		for (UserComment userComment : comments) {
			addTab(userComment, wfDoc.hasUpdateComments(), wfDoc.hasDeleteComments());
		}
		add(advanced);
		if (wfDoc.hasUpdateComments()) {
			setButtons(Dialog.OKCANCEL);
			ButtonBar buttons = this.getButtonBar();

			setHideOnButtonClick(false);
			Button okbutton = (Button) buttons.getItem(0);
			okbutton.setText("Update this comment");

			okbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
				public void componentSelected(ButtonEvent ce) {  
					hide();
				}  
			});  

			Button cancelbutton = (Button) buttons.getItem(1);
			cancelbutton.setText("Close");
			cancelbutton.addSelectionListener( new SelectionListener<ButtonEvent>() {  
				public void componentSelected(ButtonEvent ce) {  
					hide();
				}  
			});  
		}
		else 
			setButtons(Dialog.CLOSE);
	}

	private void addTab(UserComment comment, boolean editMode, boolean closable) {  
		TabItem item = new TabItem();  
		item.setClosable(closable);
		DateTimeFormat fmt = DateTimeFormat.getFormat("dd/MM HH:mm");
		String dateToDisplay = fmt.format(comment.getDate());

		item.setText(dateToDisplay);  
		DateTimeFormat fmt2 = DateTimeFormat.getFormat("dd/MM/yyyy 'at' HH:mm:ss");
		String fullDateToDisplay = fmt2.format(comment.getDate());
		String html = "<div style=\"margin: 3px; font-family: arial,tahoma,verdana,helvetica; font-size: 11px;\" ><b>" + comment.getAuthor()  + "</b> on " + fullDateToDisplay + 
		"<br /><br />" + comment.getComment()+"</div>";
		if (! editMode) {
			Html toAdd = new Html(html);
			item.add(toAdd); 
		} 
		else {
			RichTextArea ta = new RichTextArea();
			ta.setStyleName("viewcomments-textarea");
			ta.setHTML(html);
			ta.setPixelSize(570, 170); 
			item.add(ta); 
		}
 
		item.addStyleName("pad-text");  
		advanced.add(item);  
	}  
}
