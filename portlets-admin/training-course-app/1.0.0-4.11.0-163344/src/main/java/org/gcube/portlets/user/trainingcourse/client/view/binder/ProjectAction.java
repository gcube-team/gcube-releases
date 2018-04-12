package org.gcube.portlets.user.trainingcourse.client.view.binder;

import org.gcube.portlets.user.trainingcourse.client.TrainingCourseAppController;
import org.gcube.portlets.user.trainingcourse.client.event.CourseChangeStatusEvent;
import org.gcube.portlets.user.trainingcourse.client.event.CreateUnitEvent;
import org.gcube.portlets.user.trainingcourse.client.event.DeleteProjectEvent;
import org.gcube.portlets.user.trainingcourse.client.event.NavigationCourseEvent;
import org.gcube.portlets.user.trainingcourse.client.event.NavigationCourseEvent.NavigationEventType;
import org.gcube.portlets.user.trainingcourse.client.event.ShareTrainingProjectEvent;
import org.gcube.portlets.user.trainingcourse.client.view.CourseStatus;
import org.gcube.portlets.user.trainingcourse.client.view.TrainingCourseAppViewController;

import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


// TODO: Auto-generated Javadoc
/**
 * The Class ProjectAction.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 12, 2018
 */
public class ProjectAction extends Composite{

	/** The ui binder. */
	private static ProjectActionUiBinder uiBinder = GWT.create(ProjectActionUiBinder.class);

	/**
	 * The Interface ProjectActionUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jan 12, 2018
	 */
	interface ProjectActionUiBinder extends UiBinder<Widget, ProjectAction> {
	}
	
	/** The create folder. */
	@UiField
	NavLink create_folder;
	
	/** The share. */
	@UiField
	NavLink share;
	
//	@UiField
//	DropdownTab drop_change_status;
//	
	/** The share. */
	@UiField
	NavLink share_group;
	
	@UiField
	NavLink btn_delete_project;
	
	@UiField
	NavLink activeCourseNavlink;
	
	@UiField
	NavLink idleCourseNavlink;
	
	@UiField
	NavLink btn_edit_project;

	/**
	 * Instantiates a new project action.
	 */
	public ProjectAction() {
		initWidget(uiBinder.createAndBindUi(this));
		activeCourseNavlink.setText(CourseStatus.ACTIVE.getInfinitive() +" the Course");
		idleCourseNavlink.setText(CourseStatus.IDLE.getInfinitive()+" the Course");
		bindActions();
	}


	/**
	 * Bind actions.
	 */
	private void bindActions() {
		
		create_folder.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TrainingCourseAppViewController.eventBus.fireEvent(new CreateUnitEvent());
				
			}
		});
		
		share.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TrainingCourseAppViewController.eventBus.fireEvent(new ShareTrainingProjectEvent(null, false));
				
			}
		});
		
		share_group.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TrainingCourseAppViewController.eventBus.fireEvent(new ShareTrainingProjectEvent(null, true));
				
			}
		});
		
		btn_delete_project.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TrainingCourseAppController.eventBus.fireEvent(new DeleteProjectEvent(null));
				
			}
		});
		
		btn_edit_project.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TrainingCourseAppViewController.eventBus.fireEvent(new NavigationCourseEvent(NavigationEventType.EDIT));
				
			}
		});
		
		activeCourseNavlink.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TrainingCourseAppController.eventBus.fireEvent(new CourseChangeStatusEvent(true));
				
			}
		});
		
		
		idleCourseNavlink.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TrainingCourseAppController.eventBus.fireEvent(new CourseChangeStatusEvent(false));
				
			}
		});
	}

}
