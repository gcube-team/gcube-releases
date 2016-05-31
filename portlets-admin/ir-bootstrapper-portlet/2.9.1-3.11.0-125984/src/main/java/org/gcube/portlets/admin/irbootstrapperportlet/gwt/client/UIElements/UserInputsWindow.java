package org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.IRBootstrapperPortletG;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.AssignUIElement;
import org.gcube.portlets.admin.irbootstrapperportlet.gwt.client.UIElementsData.JobUIElement;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Position;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.form.FieldSet;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.widgets.form.TextField;

public class UserInputsWindow extends Window{

	protected Window getWindow(){return this;}

	private FormPanel formPanel = new FormPanel(Position.LEFT);

	private Button submitBtn = new Button("Submit");
	private Button cancelBtn = new Button("Cancel");

	public UserInputsWindow(final ArrayList<JobUIElement> jobsToExecute) {

		formPanel.setAutoWidth(true);
		formPanel.setAutoScroll(true);
		formPanel.setHeight(400);
		formPanel.setLabelWidth(80);
		formPanel.setWidth(600);
		formPanel.setMargins(5);

		if (jobsToExecute != null) {
			boolean foundAtLeastOneJobForInput = false;
			// For the first job of the chain
			JobUIElement job = jobsToExecute.get(0);
				if (job.requiresUserInput()) {
					foundAtLeastOneJobForInput = true;
					FieldSet jobInputFs = new FieldSet(job.getName() + " job inputs");
					jobInputFs.setId(job.getUID());
					jobInputFs.setCollapsible(true);  
					jobInputFs.setAutoHeight(true); 

					// for each task of the job that requires assignment
					List<AssignUIElement> taskElements = job.getInitAssignments();
					for (AssignUIElement element : taskElements) {
						if (element.requiresUserInput()) {
							TextField inputText = new TextField(element.getUserInputLabel(), element.getUID(), 250);  
							inputText.setId(element.getUID());
							inputText.setAllowBlank(true);  
							jobInputFs.add(inputText);  
						}
					}
					formPanel.add(jobInputFs);
				}
			if (!foundAtLeastOneJobForInput) {
				MessageBox.confirm("Submit Job(s) for execution", "The selected job(s) do not require any user input. Do you want to submit the selected jobs?", new MessageBox.ConfirmCallback() {  
					public void execute(String btnID) {
						if(btnID.equalsIgnoreCase("yes")) {
							submitJobsForExecution(jobsToExecute);
						}
						else
							getWindow().close();
					}});
			}
		}

		formPanel.addButton(submitBtn);  
		formPanel.addButton(cancelBtn); 

		submitBtn.addListener(new ButtonListenerAdapter() {

			public void onClick(Button button, EventObject e) {
				MessageBox.confirm("Submit Job(s) for execution", "Do you want to submit the selected job(s) with their additional inputs?", new MessageBox.ConfirmCallback() {  
					public void execute(String btnID) {
						if(btnID.equalsIgnoreCase("yes")) {
							submitJobsForExecution(jobsToExecute);
						}	
					}});
			}

		});

		cancelBtn.addListener(new ButtonListenerAdapter() {
			public void onClick(Button button, EventObject e) {
				getWindow().close();
			}
		});

		this.add(formPanel);
		this.setResizable(true);
		this.setAutoHeight(true);
		this.setTitle("Inputs for the jobs to be submitted");

	}

	private void submitJobsForExecution(ArrayList<JobUIElement> jobsToExecute) {
		if (jobsToExecute.size() == 1) {
			/* The callback object */
			AsyncCallback<String> callback = new AsyncCallback<String>() {

				public void onFailure(Throwable arg0) {
					com.google.gwt.user.client.Window.alert("Error while submitting selected jobs for execution.");
				}
				//TODO
				public void onSuccess(String arg0) {
					MessageBox.alert("The selected job has been submitted for execution. You can monitor its status through the 'Submitted Jobs' view, which can be enabled in the lower-left corner of the window.");
					//refreshSubmittedJobsTree();
				}
			};
			IRBootstrapperPortletG.bootstrapperService.submitJobForExecution(jobsToExecute.get(0).getUID(), getTasksInputsForJob(jobsToExecute.get(0).getUID()), callback);
			getWindow().close();
		}
		else {
			ArrayList<String> jobsUIDs = new ArrayList<String>();
			for (JobUIElement job : jobsToExecute)
				jobsUIDs.add(job.getUID());
			AsyncCallback<String> callback = new AsyncCallback<String>() {

				public void onFailure(Throwable arg0) {
					com.google.gwt.user.client.Window.alert("Error while submitting selected jobs for execution.");
				}
				//TODO
				public void onSuccess(String arg0) {
					MessageBox.alert("The selected jobs have been submitted for execution. You can monitor their status through the 'Submitted Jobs' view, which can be enabled in the lower-left corner of the window.");
					//refreshSubmittedJobsTree();
				}
			};
			IRBootstrapperPortletG.bootstrapperService.submitJobsForBatchExecution(jobsUIDs, getTasksInputsForJob(jobsToExecute.get(0).getUID()), callback);
			getWindow().close();
		}
	}

	private HashMap<String,String> getTasksInputsForJob(String jobID) {
		//Log.debug("Finding inputs for job with id -> " + jobID);
		HashMap<String,String> inputs = new HashMap<String, String>();
		Component[] f = formPanel.getComponents();
		for (int j=0; j<f.length; j++) {
			if (f[j] instanceof FieldSet ) {
				Component inputscp[] = ((FieldSet) f[j]).getItems();
				for (int i=0; i<inputscp.length; i++) {
					if (inputscp[i] instanceof TextField) {
						//Log.debug("Adding input for task -> " + inputscp[i].getId() + " with value -> " + ((TextField)inputscp[i]).getText());
						inputs.put(inputscp[i].getId(), ((TextField)inputscp[i]).getText());
					}
				}
			}
		}
		return inputs;
	}
}
