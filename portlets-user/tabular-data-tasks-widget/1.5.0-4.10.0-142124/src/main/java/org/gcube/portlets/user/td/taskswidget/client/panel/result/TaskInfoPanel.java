/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.panel.result;

import java.util.Date;

import org.gcube.portlets.user.td.taskswidget.client.ConstantsTdTasks;
import org.gcube.portlets.user.td.taskswidget.client.resources.Resources;
import org.gcube.portlets.user.td.taskswidget.client.util.GwtDataFormatter;
import org.gcube.portlets.user.td.taskswidget.client.util.RenderTextFieldUtil;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskModel;
import org.gcube.portlets.user.td.taskswidget.shared.job.TdTaskStatusType;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 21, 2013
 * 
 */
public class TaskInfoPanel extends ContentPanel{
	
	private TextField<String> name = new TextField<String>();
	private Field<String> status = new TextField<String>();
	private Field<String> progress = new TextField<String>();
	
	private FormData formData = new FormData("-20");
	private VerticalPanel vp = new VerticalPanel();
	private FormPanel form = new FormPanel();
	private FieldSet fieldSet = new FieldSet();
	
	private HorizontalPanel hpStatusView = new HorizontalPanel();

	
	public TaskInfoPanel() {
	}

	public TaskInfoPanel(TdTaskModel taskModel) {
		initPanel();
		setBorders(false);
		setHeaderVisible(false);
		setBodyBorder(false);
		updateFormFields(taskModel);
	}
	
	

	private void initPanel() {
		setId("TaskInfoPanel");
		setLayout(new FitLayout());
		vp.setLayout(new FitLayout());
		
		vp.setSpacing(5);
		
		form.setStyleName("formatFieldText");
		
		form.setWidth(ConstantsTdTasks.MAINWIDTH-25);
		form.setHeaderVisible(false);
		form.setFrame(true);
		form.setLayout(new FlowLayout());
		fieldSet.setLayout(new FormLayout());
		

		name.setFieldLabel("Name");
		name.setReadOnly(true);
		fieldSet.add(name, formData);

		status.setFieldLabel("Status");
		status.setReadOnly(true);
		fieldSet.add(status, formData);
		
		progress.setFieldLabel("Progress");
		progress.setReadOnly(true);
		fieldSet.add(progress, formData);
		
//		fieldSet.add(hpStatusView);
		
		form.add(fieldSet);
		fieldSet.setStyleName("fieldset-style-reduce-padding");
		
		vp.add(form);
		add(vp);
		
	}
	
	public Image getProgressStatusView(TdTaskStatusType state){
		
		if(state==null)
			return null;
		
		switch (state) {
			case INITIALIZING:return new Image(Resources.INSTANCE.initializing());
//			case FALLBACK: return new Image(Resources.INSTANCE.attention());
			case VALIDATING_RULES: return new Image(Resources.INSTANCE.validating());
			case COMPLETED: return new Image(Resources.INSTANCE.success());
			case RUNNING: return new Image(Resources.INSTANCE.working());
			case FAILED: return new Image(Resources.INSTANCE.failicon());
			case STOPPED: return new Image(Resources.INSTANCE.stop());
			case PENDING: return new Image(Resources.INSTANCE.pending());
			case GENERATING_VIEW: return new Image(Resources.INSTANCE.validating2());
//			case GENERATING_VIEW:
//			case PENDING:
			default: return null;
		}
	}

	public void updateFormFields(TdTaskModel taskModel){
		
		fieldSet.setHeading("Task: "+formatDates(taskModel.getStartTime(), taskModel.getEndTime()));
		
		String submitter = taskModel.getSubmitter();
		
		String tooltip="";
		if(submitter!=null && !submitter.isEmpty()){
			tooltip+="Submitter: "+submitter +" ";
		}
		
		tooltip+= "Start time: "+GwtDataFormatter.getDateFormat(taskModel.getStartTime());
		tooltip+= "- End time: "+GwtDataFormatter.getDateFormat(taskModel.getEndTime());
		
		fieldSet.setToolTip(tooltip);
		
//		System.out.println("Job Name Is: "+taskModel.getJobName());
		name.setValue(taskModel.getName());
		status.setValue(taskModel.getStatus().toString());
		progress.setValue(GwtDataFormatter.fmtToInt(taskModel.getPercentage()*100)+"%");
		
//		hpStatusView.removeAll();
		Image statusView = getProgressStatusView(taskModel.getStatus());
		
		if(statusView!=null){
			statusView.setTitle(taskModel.getStatus().toString());
			RenderTextFieldUtil.updateImageLegend(fieldSet, statusView.getUrl());
		}
		
		this.layout();
	}
	
	private String formatDates(Date start, Date end){

		String formatter = "[";
		
		if(start!=null){
			formatter+=GwtDataFormatter.getDateFormat(start);
		}else
			formatter+="Not found";
		
		formatter+=" - ";
		
		if(end!=null){
			formatter+=GwtDataFormatter.getDateFormat(end);
		}else
			formatter+="Not found";

		return formatter+="]";
	}
	
	public void setTastName(String taskName){
		name.setValue(taskName);
		this.layout();
	}
	
}
