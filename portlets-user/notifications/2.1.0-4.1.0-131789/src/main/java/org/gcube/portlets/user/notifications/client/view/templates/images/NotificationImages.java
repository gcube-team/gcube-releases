package org.gcube.portlets.user.notifications.client.view.templates.images;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface NotificationImages extends ClientBundle {
	@Source("post-alert.png")
	ImageResource postAlert();
	
	@Source("comment.png")
	ImageResource comment();
	
	@Source("tag_blue.png")
	ImageResource mention();
	
	@Source("star_blue.png")
	ImageResource like();
	
	@Source("mail.png")
	ImageResource message();
	
	@Source("calendar.png")
	ImageResource calendar();
	
	@Source("calendar_edit.png")
	ImageResource calendarEdit();
	
	@Source("calendar_delete.png")
	ImageResource calendarDelete();
	
	@Source("share_blue.png")
	ImageResource share();
	
	@Source("unshare_blue.png")
	ImageResource unshare();
	
	@Source("connection_new.png")
	ImageResource connectionRequest();
	
	@Source("job_ok.png")
	ImageResource jobOK();
	
	@Source("job_nok.png")
	ImageResource jobNOK();
	
	@Source("document-workflow.png")
	ImageResource documentWorkflow();
	
	@Source("document-workflow-new.jpg")
	ImageResource documentWorkflowNew();
	
	@Source("workflow-forward.png")
	ImageResource workflowForward();
	
	@Source("workflow-forward-complete.jpg")
	ImageResource workflowForwardComplete();
	
	@Source("notification-generic.png")
	ImageResource generic();
	
	@Source("table_share.png")
	ImageResource tableShare();
}
