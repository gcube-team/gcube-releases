package org.gcube.portlets.user.speciesdiscovery.client.job.occurrence;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class OccurrenceJobsInfoContainer extends LayoutContainer {

	private ContentPanel cp;
	private TextArea textArea = new TextArea();
	private String description;
	

	public OccurrenceJobsInfoContainer(String description) {
		textArea.setReadOnly(true);
		initContentPanel();
		updateDescription(description);
	}

	private void initContentPanel() {
		setLayout(new FitLayout());
		getAriaSupport().setPresentation(true);
		cp = new ContentPanel();
		cp.setHeaderVisible(false);
		cp.setBodyBorder(true);
		cp.setLayout(new FitLayout());
		cp.setButtonAlign(HorizontalAlignment.CENTER);
		cp.setLayout(new FitLayout());
//		cp.getHeader().setIconAltText("Grid Icon");
		cp.setSize(400, 250);
		
		cp.add(textArea);

		add(cp);
	}
	
	public void updateDescription(String description){
		this.description = description;
		textArea.reset();
		textArea.setValue(this.description);
		cp.layout();
	}

	public void setHeaderTitle(String title) {
		cp.setHeading(title);
		cp.layout();
	}

}