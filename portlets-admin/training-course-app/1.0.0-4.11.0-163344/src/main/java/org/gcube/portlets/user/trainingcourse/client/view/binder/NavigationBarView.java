package org.gcube.portlets.user.trainingcourse.client.view.binder;

import java.util.List;

import org.gcube.portlets.user.trainingcourse.client.TrainingCourseAppController;
import org.gcube.portlets.user.trainingcourse.client.event.CourseChangeStatusEvent;
import org.gcube.portlets.user.trainingcourse.client.event.CreateUnitEvent;
import org.gcube.portlets.user.trainingcourse.client.event.DeleteProjectEvent;
import org.gcube.portlets.user.trainingcourse.client.event.NavigationCourseEvent;
import org.gcube.portlets.user.trainingcourse.client.event.NavigationCourseEvent.NavigationEventType;
import org.gcube.portlets.user.trainingcourse.client.event.OpenProjectEvent;
import org.gcube.portlets.user.trainingcourse.client.event.ShareTrainingProjectEvent;
import org.gcube.portlets.user.trainingcourse.client.view.CourseStatus;
import org.gcube.portlets.user.trainingcourse.client.view.TrainingCourseAppViewController;
import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;

import com.github.gwtbootstrap.client.ui.Dropdown;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


// TODO: Auto-generated Javadoc
/**
 * The Class NavigationBarView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 9, 2018
 */
public class NavigationBarView extends Composite {

	/** The ui binder. */
	private static NavigationBarViewUiBinder uiBinder = GWT.create(NavigationBarViewUiBinder.class);

	/**
	 * The Interface NavigationBarViewUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jan 9, 2018
	 */
	interface NavigationBarViewUiBinder extends UiBinder<Widget, NavigationBarView> {
	}
	
	/** The button create new. */
	@UiField
	NavLink button_create_new;
	
	@UiField
	Dropdown button_open_course;

	private List<TrainingCourseObj> openableItems;
	
	
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
	 * Instantiates a new navigation bar view.
	 */
	public NavigationBarView() {
		initWidget(uiBinder.createAndBindUi(this));	
		button_create_new.getElement().getStyle().setMarginRight(10, Unit.PX);
		activeCourseNavlink.setText(CourseStatus.ACTIVE.getInfinitive() +" the Course");
		idleCourseNavlink.setText(CourseStatus.IDLE.getInfinitive()+" the Course");
		bindActions();
	}

	/**
	 * Bind actions.
	 */
	private void bindActions() {
		
		button_create_new.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				TrainingCourseAppController.eventBus.fireEvent(new NavigationCourseEvent(NavigationEventType.CREATE));
				
			}
		});
		
		//ACTIONS
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

	public void addSelectableItems(List<TrainingCourseObj> result) {
		if (result == null)
			return;
		
		this.openableItems = result;
		
		button_open_course.clear();
		for (final TrainingCourseObj trainingProject : result) {
			NavLink lnk = new NavLink(trainingProject.getTitle());
			lnk.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					TrainingCourseAppController.eventBus.fireEvent(new OpenProjectEvent(trainingProject));
					
				}
			});
			button_open_course.add(lnk);
		}
		
	}
	

}
