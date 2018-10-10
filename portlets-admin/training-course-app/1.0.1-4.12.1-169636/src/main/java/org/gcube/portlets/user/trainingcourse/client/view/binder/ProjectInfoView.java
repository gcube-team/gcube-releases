package org.gcube.portlets.user.trainingcourse.client.view.binder;


import org.gcube.portlets.user.trainingcourse.client.TrainingCourseAppController;
import org.gcube.portlets.user.trainingcourse.client.dialog.NewBrowserWindow;
import org.gcube.portlets.user.trainingcourse.client.view.CourseStatus;
import org.gcube.portlets.user.trainingcourse.shared.TrainingContact;
import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.PageHeader;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.github.gwtbootstrap.client.ui.resources.ButtonSize;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


// TODO: Auto-generated Javadoc
/**
 * The Class CourseInfoView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 8, 2018
 */
public class ProjectInfoView extends Composite {

	/** The ui binder. */
	private static CreateUnitViewUiBinder uiBinder =
		GWT.create(CreateUnitViewUiBinder.class);

	/**
	 * The Interface CreateUnitViewUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jan 11, 2018
	 */
	interface CreateUnitViewUiBinder
		extends UiBinder<Widget, ProjectInfoView> {
	}

	/** The field course desciption. */
	@UiField
	HTMLPanel field_course_desciption;
	
	/** The field course title. */
//	@UiField
//	HTMLPanel field_course_title;
//	
	@UiField
	PageHeader page_header;
	
	/** The field folder name. */
	@UiField
	HTMLPanel field_folder_name;
	
	/** The field folder name. */
	@UiField
	HTMLPanel field_commitment;
	
	/** The field folder name. */
	@UiField
	HTMLPanel field_language;
	
	/** The field shared with. */
	@UiField
	HTMLPanel field_shared_with;
	
	@UiField
	HTMLPanel field_created_by;
	
//	@UiField
//	HTMLPanel field_course_status;
	
	@UiField
	Label l_course_status;
	
	@UiField
	Label l_participants;
	
	
	/** The project. */
	private TrainingCourseObj currentProject;
	

	/**
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 *
	 * @param project the project
	 */
	public ProjectInfoView(TrainingCourseObj project) {
		this();
		updateProjectInfo(project);
	}

	

	public ProjectInfoView() {
		initWidget(uiBinder.createAndBindUi(this));
		this.currentProject = null;
		updateProjectInfo(null);
	}

	public void resetView() {
		this.page_header.setText("");
		//this.field_course_title.add(new HTML(project.getTitle()!=null?project.getTitle():""));
		this.field_course_desciption.clear();
		this.field_commitment.clear();
		this.field_language.clear();
		this.field_shared_with.clear();
		this.field_shared_with.add(new HTML("No participants"));
		this.field_folder_name.clear();
		this.field_created_by.clear();
//		this.field_course_status.clear();
	}


	/**
	 * Update project info.
	 *
	 * @param project the project
	 */
	public void updateProjectInfo(TrainingCourseObj project) {
		this.currentProject = project;
		
		if(project==null)
			return;
			//project = new TrainingProject(); //EMPTY PROJECT
		
		this.page_header.setText(project.getTitle());
		this.field_course_desciption.clear();
		this.field_course_desciption.add(new HTML(project.getDescription()!=null?project.getDescription():""));
		this.field_commitment.clear();
		this.field_commitment.add(new HTML(project.getCommitment()!=null?project.getCommitment():""));
		this.field_language.clear();
		this.field_language.add(new HTML(project.getLanguages()!=null?project.getLanguages():""));
		
		this.field_shared_with.clear();
		
		if(project.getGroupSharedWith()!=null && project.getGroupSharedWith().size()>0) {
			String groupFullName = project.getGroupSharedWith().get(0).getFullname();
			groupFullName=groupFullName!=null?groupFullName:project.getGroupSharedWith().get(0).getUsername();
			this.field_shared_with.add(new HTML("All members of "+groupFullName));
			
			FlexTable flex = new FlexTable();
			flex.setWidth("100%");
			flex.getColumnFormatter().setWidth(0, "100%");
			flex.addStyleName("course-table-group");
			flex.setWidget(0, 1, new HTML(groupFullName));
			Button buttShowProgress = new Button();
			buttShowProgress.setType(ButtonType.LINK);
			buttShowProgress.setText("Show Progress");
			buttShowProgress.setSize(ButtonSize.SMALL);
			addClickHandlerToShowProgress(project.getGroupSharedWith().get(0), buttShowProgress);
			this.field_shared_with.add(flex);
			flex.setWidget(0, 0, buttShowProgress);
		}else if(project.getSharedWith()!=null && project.getSharedWith().size()>0) {
			//String listUsername = "";
			int number = project.getSharedWith().size();
			String text = number>1?number+" users":number+" user";
			this.field_shared_with.add(new HTML(text));
			
			FlexTable flex = new FlexTable();
			flex.setWidth("100%");
			flex.getColumnFormatter().setWidth(0, "100%");
			flex.addStyleName("course-table");
//			flex.getColumnFormatter().setWidth(0, "80%");
//			flex.getColumnFormatter().setWidth(1, "20%");
			for (int i = 0; i<project.getUserSharedWith().size(); i++) {
				final TrainingContact user = project.getUserSharedWith().get(i);
				final String name=user.getFullname()!=null&&!user.getFullname().isEmpty()?user.getFullname():user.getUsername();
				flex.setWidget(i, 1, new HTML(name));
				
				Button buttShowProgress = new Button();
				buttShowProgress.setType(ButtonType.LINK);
				buttShowProgress.setText("Show Progress");
				buttShowProgress.setSize(ButtonSize.SMALL);
				addClickHandlerToShowProgress(user, buttShowProgress);
				flex.setWidget(i, 0, buttShowProgress);
			}
			this.field_shared_with.add(flex);
		}else {
			this.field_shared_with.add(new HTML("No participants"));
		}
			
		
		this.field_folder_name.clear();
		this.field_folder_name.add(new HTML(project.getWorkspaceFolderName()!=null?project.getWorkspaceFolderName():""));
		
		this.field_created_by.clear();
		this.field_created_by.add(new HTML(project.getCreatedBy()));
		
//		this.field_course_status.clear();
		
		if(project.isCourseActive()) {
			this.l_course_status.setType(LabelType.SUCCESS);
			this.l_course_status.setText("Course status: "+CourseStatus.ACTIVE.getPastParticiple());
//			this.field_course_status.add(new HTML("Active"));
		}else {
			this.l_course_status.setType(LabelType.WARNING);
			this.l_course_status.setText("Course status: "+CourseStatus.IDLE.getPastParticiple());
//			this.field_course_status.add(new HTML("Idle"));
		}
	}
	
	
	
	/**
	 * Butt click hanler.
	 *
	 * @param user the user
	 * @param butt the butt
	 */
	public void addClickHandlerToShowProgress(final TrainingContact user, Button butt) { 
	
		butt.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final NewBrowserWindow newBrowserWindow = NewBrowserWindow.open("", "_blank", "");
				TrainingCourseAppController.trainingService.getQueryStringToShowUserProgress(currentProject, user.getUsername(), new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String result) {
						if(result!=null) {
							String url = Window.Location.getHref();
							String toTakeCourse = url.substring(0, url.lastIndexOf("/")) +"/available-courses";
							toTakeCourse+="?"+result;
							GWT.log("Redirect to: "+toTakeCourse);
							newBrowserWindow.setUrl(toTakeCourse);
						}else
							Window.alert("Sorry an error occurred on showing progress for user: "+user.getUsername());
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						
					}
				});
			}
		});
	}

	
	public TrainingCourseObj getCurrentProject() {
		return currentProject;
	}


}
