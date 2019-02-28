package org.gcube.portlets.user.trainingcourse.client;

import java.util.List;

import org.gcube.portlets.user.trainingcourse.client.dialog.DialogConfirm;
import org.gcube.portlets.user.trainingcourse.client.event.CourseChangeStatusEvent;
import org.gcube.portlets.user.trainingcourse.client.event.CourseChangeStatusEventHandler;
import org.gcube.portlets.user.trainingcourse.client.event.DeleteProjectEvent;
import org.gcube.portlets.user.trainingcourse.client.event.DeleteProjectEventHandler;
import org.gcube.portlets.user.trainingcourse.client.event.LoadListOfCourseEvent;
import org.gcube.portlets.user.trainingcourse.client.event.LoadListOfCourseEventHandler;
import org.gcube.portlets.user.trainingcourse.client.event.OpenProjectEvent;
import org.gcube.portlets.user.trainingcourse.client.event.OpenProjectEventHandler;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingCourseEvent;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingCourseEvent.EventType;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingCourseEventHandler;
import org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppService;
import org.gcube.portlets.user.trainingcourse.client.rpc.TrainingCourseAppServiceAsync;
import org.gcube.portlets.user.trainingcourse.client.view.CourseStatus;
import org.gcube.portlets.user.trainingcourse.client.view.TrainingCourseAppViewController;
import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;
import org.gcube.portlets.widgets.workspaceuploader.client.DialogResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ComplexPanel;


// TODO: Auto-generated Javadoc
/**
 * The Class TrainingCourseAppController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 9, 2018
 */
public class TrainingCourseAppController {

	/** The Constant eventBus. */
	public final static HandlerManager eventBus = new HandlerManager(null);
	
	/**
	 * Create a remote service proxy to talk to the server-side TrainingCourseAppService service.
	 */
	public static final TrainingCourseAppServiceAsync trainingService = GWT.create(TrainingCourseAppService.class);
	
	
	private TrainingCourseAppViewController viewController;
	
	private TrainingCourseObj displayingProject;
	/**
	 * Instantiates a new training course app controller.
	 */
	public TrainingCourseAppController() {
		
		viewController = new TrainingCourseAppViewController(eventBus);
		bindEvents();	
		eventBus.fireEvent(new LoadListOfCourseEvent());
	}
	

	private void bindEvents() {
		
		eventBus.addHandler(LoadListOfCourseEvent.TYPE, new LoadListOfCourseEventHandler() {
			
			@Override
			public void onLoadListOfCourses(LoadListOfCourseEvent loadListOfCourseEvent) {
				
				final DialogConfirm dg = new DialogConfirm(null, "Loading", "Loading Training Courses...");
				dg.loader("Loading Training Courses...");
				dg.center();
				
				trainingService.getOwnedTrainingCoursesForCurrentVRE(new AsyncCallback<List<TrainingCourseObj>>() {
					
					@Override
					public void onSuccess(List<TrainingCourseObj> result) {
						GWT.log("Load list of Courses: "+result);
						viewController.updateListOfCourses(result);
						dg.hide();
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						dg.hide();
						
					}
				});
				
			}
		});
		
		eventBus.addHandler(TrainingCourseEvent.TYPE, new TrainingCourseEventHandler() {
			

			@Override
			public void onCourseEvent(TrainingCourseEvent trainingCourseCreatedEvent) {
				if(trainingCourseCreatedEvent.getProject()==null)
					return;
				
				if(trainingCourseCreatedEvent.getEventType().equals(EventType.CREATED)) {
	
					showProject(trainingCourseCreatedEvent.getProject());
						
				}else if(trainingCourseCreatedEvent.getEventType().equals(EventType.UPDATED)) {
					
					showProject(trainingCourseCreatedEvent.getProject());
				}
				
			}
		});
		
		eventBus.addHandler(CourseChangeStatusEvent.TYPE, new CourseChangeStatusEventHandler() {
			
			@Override
			public void onChangeStatus(final CourseChangeStatusEvent courseChangeStatusEvent) {
				
				if(displayingProject!=null) {
					
					String state = courseChangeStatusEvent.isActive()?CourseStatus.ACTIVE.getPresentParticiple():CourseStatus.IDLE.getPresentParticiple();
					
					final DialogConfirm confirm = new DialogConfirm(null, "Confirm changing status?", "<b>"+state+"</b> the Course: <br>"+displayingProject.getTitle()+"<br><br> Confirm?");
					
					confirm.getYesButton().addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							confirm.loader("Changing status...");
							trainingService.changeStatus(displayingProject, courseChangeStatusEvent.isActive(), new AsyncCallback<TrainingCourseObj>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
									confirm.hide();
									
								}

								@Override
								public void onSuccess(TrainingCourseObj result) {
									confirm.hide();
									if(result!=null) {
										showProject(result);
									}
									
								}
							});
						}
					});
					
					confirm.center();
				}
				
			}
		});
		
		
		eventBus.addHandler(OpenProjectEvent.TYPE, new OpenProjectEventHandler() {
			
			@Override
			public void onLoadProject(OpenProjectEvent openProjectEvent) {
				
				if(openProjectEvent.getProject()!=null) {
					
					final DialogConfirm dg = new DialogConfirm(null, "Loading", "Loading Course... ");
					dg.loader("Loading Course...");
					dg.center();
				
					trainingService.loadTrainingCourse(openProjectEvent.getProject().getInternalId(), new AsyncCallback<TrainingCourseObj>() {

						@Override
						public void onFailure(Throwable caught) {
							dg.hide();
							Window.alert(caught.getMessage());
							
						}

						@Override
						public void onSuccess(TrainingCourseObj result) {
							dg.hide();
							if(result!=null) {
								showProject(result);
							}
							
						}
					});
					
				}
				
			}
		});
		
		TrainingCourseAppController.eventBus.addHandler(DeleteProjectEvent.TYPE, new DeleteProjectEventHandler() {
			
			@Override
			public void onDeleteProject(final DeleteProjectEvent deleteProjectEvent) {
				
				final TrainingCourseObj tp = deleteProjectEvent.getProject()==null?displayingProject:deleteProjectEvent.getProject();

				if(tp!=null) {
					
					final DialogConfirm confirm = new DialogConfirm(null, "Confirm delete?", "Deleting the Course: <br>\""+tp.getTitle()+"\"<br><br>Confirm?");
					
					confirm.getYesButton().addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							confirm.loader("Deleting course...");
							TrainingCourseAppController.trainingService.deleteTrainingProject(tp, new AsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									eventBus.fireEvent(new LoadListOfCourseEvent());
									confirm.hide();
									
								}

								@Override
								public void onSuccess(Boolean result) {
									confirm.hide();
									if(result) {
										DialogResult dg = new DialogResult(null, "Couse Deleted!", "Training course: "+tp.getTitle() +" deleted correctly");
										dg.center();
									}
									
									resetView();
									
								}
								
							});
						}
					});
					
					confirm.center();
				}
				
			}
		}); 
		
	}
	
	private void resetView() {
		eventBus.fireEvent(new LoadListOfCourseEvent());
		displayingProject = null;
		getViewController().updateProjectInfo(null);
		getViewController().resetViewComponents();
	}

	
	/**
	 * Show project.
	 *
	 * @param project the project
	 */
	private void showProject(TrainingCourseObj project) {
		this.displayingProject = project;
		getViewController().updateProjectInfo(project);
		getViewController().loadResourceExplorer(project.getWorkspaceFolderId());
	}

	/**
	 * Gets the view.
	 *
	 * @return the view
	 */
	public ComplexPanel getView() {
		return getViewController().getMainPanel();
	}
	
	public TrainingCourseAppViewController getViewController() {
		return viewController;
	}
}
