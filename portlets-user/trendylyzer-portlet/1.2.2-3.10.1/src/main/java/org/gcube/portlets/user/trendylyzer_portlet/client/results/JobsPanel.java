/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.results;



import java.util.logging.Level;
import java.util.logging.Logger;

import org.gcube.portlets.user.trendylyzer_portlet.client.TrendyLyzer_portlet;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author ceras
 *
 */
public class JobsPanel extends LayoutContainer implements JobsGridHandler {

	private JobsGrid jobsGrid;
	private JobViewer jobViewer;
	private BorderLayout layout;
	private Button detachButton = new Button("To Window");
	private Logger logger = Logger.getLogger("");

	/**
	 * @param statisticalManager
	 * @param statisticalService
	 */
	public JobsPanel() {
		super();
		
		this.setHeight(700);
		
		layout = new BorderLayout();
		
		this.setLayout(layout);
		this.setStyleAttribute("background-color", "#FFFFFF");
		
		this.jobsGrid = new JobsGrid(this);
		this.jobViewer = new JobViewer();
		
		// CENTER
		BorderLayoutData centerPanelData = new BorderLayoutData(LayoutRegion.CENTER);
		centerPanelData.setMargins(new Margins(0));
		this.add(jobsGrid, centerPanelData);
		
		// SOUTH
		BorderLayoutData southPanelData = new BorderLayoutData(LayoutRegion.SOUTH, 340);
		southPanelData.setMargins(new Margins(5, 0, 0, 0));
		southPanelData.setSplit(true);
		southPanelData.setCollapsible(true);
		southPanelData.setFloatable(true);
		this.add(jobViewer, southPanelData);
		
		//TODO removed Federico layout.collapse(LayoutRegion.SOUTH);
		
		
		//TODO removed Federico addDetachButton();
		
		this.setStyleAttribute("margin-right", "20px");
		jobViewer.setEmpty();
		
	}

	/**
	 * 
	 */
	private void addDetachButton() {
//		BorderLayoutDetacher.setDetachable(jobViewer, southPanelData, this);
		detachButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				jobViewer.showCurrentJobToWindow();
			}
		});
		jobViewer.getHeader().addTool(detachButton);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.jobsArea.JobsGridHandler#jobSelected(org.gcube.portlets.user.statisticalmanager.client.bean.JobItem)
	 */
	@Override
	public void jobSelected(JobItem jobItem) {
		
//		if (jobViewer.getCurrentJobItem()==null || !jobViewer.getCurrentJobItem().getId().equals(jobItem.getId()))
		jobViewer.showJob(jobItem);
		layout.expand(LayoutRegion.SOUTH);
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.jobsArea.JobsGridHandler#removeComputation(org.gcube.portlets.user.statisticalmanager.client.bean.JobItem)
	 */
	@Override
	public void removeComputation(final JobItem jobItem) {
		logger.log(Level.SEVERE, "call service");
		TrendyLyzer_portlet.getService().removeComputation(jobItem.getId(), new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				Info.display("", "The computation "+jobItem.getName()+" was correctly removed.");
				
				if (jobViewer.getCurrentJobItem()!=null && jobViewer.getCurrentJobItem().getId().equals(jobItem.getId())) {
					jobViewer.setEmpty();
					layout.collapse(LayoutRegion.SOUTH);
				}
				
			}

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Error", "Impossible to remove the computation.", null);
			}
		});
	}

}
