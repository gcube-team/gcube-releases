package org.gcube.portlets.user.td.tablewidget.client.validation.tree;

import java.util.ArrayList;

import org.gcube.portlets.user.td.gwtservice.shared.task.JobS;
import org.gcube.portlets.user.td.gwtservice.shared.task.TaskS;
import org.gcube.portlets.user.td.gwtservice.shared.task.ValidationsTasksMetadata;
import org.gcube.portlets.user.td.gwtservice.shared.tr.table.Validations;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * 
 * @author giancarlo
 * email: <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public class TreeDataGenerator {
	protected DateTimeFormat sdf = DateTimeFormat
			.getFormat("yyyy-MM-dd HH:mm:ss");

	protected FolderDto root;

	public TreeDataGenerator(ValidationsTasksMetadata validationsTasksMetadata) {
		Log.debug("TreeDataGenerator");
		ArrayList<BaseDto> childrens = new ArrayList<BaseDto>();
		if (validationsTasksMetadata.getTasks().size() > 0) {
			for (TaskS task : validationsTasksMetadata.getTasks()) {
				ArrayList<BaseDto> jobs = new ArrayList<BaseDto>();
				for (JobS job : task.getJobs()) {
					ArrayList<BaseDto> validations = new ArrayList<BaseDto>();
					for (Validations v : job.getValidations()) {
						ValidationDto validationDto = new ValidationDto(task.getId()+"-"+job.getId()+"-"+v.getId(),
								v.getTitle(), v.getDescription(), v.isValid(), v.getConditionCode(), v.getValidationColumnColumnId(),job.getInvocation());
						validations.add(validationDto);
					}
					JobSDto foldJob = new JobSDto("job",task.getId()+"-"+job.getId(),
							job.getJobClassifier(),job.getDescription(), validations);
					jobs.add(foldJob);
				}
				TaskSDto foldTask = new TaskSDto("task",task.getId(),
						sdf.format(task.getStartTime()), jobs);
				childrens.add(foldTask);
			}
			root = new FolderDto("root","-1", "root", childrens);
			Log.debug("Generated root");
			//printRecorsive(root);
			
		} else {
			root = new FolderDto("root","-1", "root", childrens);
			Log.debug("Generated root without childrens");
		}
	}

	public FolderDto getRoot() {
		return root;
	}
	
	protected void printRecorsive(FolderDto root){
		for(BaseDto base:root.getChildrens()){
			Log.debug("+++");
			Log.debug("Children:"+base.toString());
			if(base instanceof FolderDto){	
				printRecorsive((FolderDto) base);
			}
			Log.debug("---");
			
		}
	}
	

}
