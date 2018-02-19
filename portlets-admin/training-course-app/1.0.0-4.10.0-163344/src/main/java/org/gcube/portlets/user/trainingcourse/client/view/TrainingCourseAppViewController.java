package org.gcube.portlets.user.trainingcourse.client.view;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitQuestionnaireDTO;
import org.gcube.portal.trainingmodule.shared.TrainingVideoDTO;
import org.gcube.portlets.user.trainingcourse.client.TrainingCourseAppController;
import org.gcube.portlets.user.trainingcourse.client.dialog.DialogConfirm;
import org.gcube.portlets.user.trainingcourse.client.dialog.DialogResult;
import org.gcube.portlets.user.trainingcourse.client.event.CreateUnitEvent;
import org.gcube.portlets.user.trainingcourse.client.event.CreateUnitEventHandler;
import org.gcube.portlets.user.trainingcourse.client.event.DeleteTrainingUnitItemEvent;
import org.gcube.portlets.user.trainingcourse.client.event.DeleteTrainingUnitItemEventHandler;
import org.gcube.portlets.user.trainingcourse.client.event.DeleteWorkspaceItemEvent;
import org.gcube.portlets.user.trainingcourse.client.event.DeleteWorkspaceItemEventHandler;
import org.gcube.portlets.user.trainingcourse.client.event.LoadListOfCourseEvent;
import org.gcube.portlets.user.trainingcourse.client.event.NavigationCourseEvent;
import org.gcube.portlets.user.trainingcourse.client.event.NavigationCourseEvent.NavigationEventType;
import org.gcube.portlets.user.trainingcourse.client.event.NavigationCourseEventHandler;
import org.gcube.portlets.user.trainingcourse.client.event.OpenProjectEvent;
import org.gcube.portlets.user.trainingcourse.client.event.SelectedWorkspaceItemEvent;
import org.gcube.portlets.user.trainingcourse.client.event.SelectedWorkspaceItemEventHandler;
import org.gcube.portlets.user.trainingcourse.client.event.ShareTrainingProjectEvent;
import org.gcube.portlets.user.trainingcourse.client.event.ShareTrainingProjectEventHandler;
import org.gcube.portlets.user.trainingcourse.client.event.ShowMoreInfoEvent;
import org.gcube.portlets.user.trainingcourse.client.event.ShowMoreInfoEventHandler;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingCourseEvent;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingCourseEvent.EventType;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingUnitQuestionnaireEvent;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingUnitQuestionnaireEventHandler;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingUnitVideoEvent;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingUnitVideoEventHandler;
import org.gcube.portlets.user.trainingcourse.client.view.binder.AbstractViewDialogBox;
import org.gcube.portlets.user.trainingcourse.client.view.binder.AssociateQuestionnaireView;
import org.gcube.portlets.user.trainingcourse.client.view.binder.AssociateVideoTrainingView;
import org.gcube.portlets.user.trainingcourse.client.view.binder.CreateCourseView;
import org.gcube.portlets.user.trainingcourse.client.view.binder.CreateUnitView;
import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;
import org.gcube.portlets.user.trainingcourse.shared.WorkspaceItemInfo;
import org.gcube.portlets.widgets.workspacesharingwidget.client.SmartConstants;
import org.gcube.portlets.widgets.workspacesharingwidget.client.WorkspaceSmartSharingController;
import org.gcube.portlets.widgets.workspacesharingwidget.client.view.sharing.SmartShare;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.CredentialModel;
import org.gcube.portlets.widgets.workspacesharingwidget.shared.FileModel;
import org.gcube.portlets.widgets.wsexplorer.shared.Item;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


// TODO: Auto-generated Javadoc
/**
 * The Class TrainingCourseAppViewController.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 10, 2018
 */
public class TrainingCourseAppViewController {
	
	/** The main view. */
	private MainPanelView mainView;
	
	/** The widht dialog. */
	//private final int WIDHT_DIALOG = 600;

	private TrainingCourseObj currentManagingProject;
	
	/** The event bus. */
	public static HandlerManager eventBus;

	/**
	 * Instantiates a new training course app view controller.
	 *
	 * @param eventBus the event bus
	 */
	public TrainingCourseAppViewController(HandlerManager eventBus) {
		TrainingCourseAppViewController.eventBus = eventBus;
		mainView = new MainPanelView();
		mainView.setVisibleMoreInfo(false);
		bindEvents();
	}
	

	/**
	 * Gets the main panel.
	 *
	 * @return the main panel
	 */
	public MainPanelView getMainPanel(){
		return mainView;
	}
	
	/**
	 * Update view size.
	 *
	 * @param width the width
	 * @param height the height
	 */
	public void updateViewSize(int width, int height){
		mainView.updateSize(width,height);
	}

	/**
	 * Update list of courses.
	 *
	 * @param result the result
	 */
	public void updateListOfCourses(List<TrainingCourseObj> result) {
		mainView.getNavigationBarView().addSelectableItems(result);
		
	}
	
	/**
	 * Bind events.
	 */
	public void bindEvents() {
		
		TrainingCourseAppController.eventBus.addHandler(CreateUnitEvent.TYPE, new CreateUnitEventHandler() {
			
			@Override
			public void onCreateFolder(CreateUnitEvent createFolderEvent) {
				
				if(mainView.getProjectInfoView().getCurrentProject()==null)
					return;
				
				long currentTPid = mainView.getProjectInfoView().getCurrentProject().getInternalId();
				
				if(currentTPid<=0)
					return;
				
				final Modal box = new Modal(true);
				//box.setWidth(WIDHT_DIALOG+"px");
				box.setTitle("Create a New Training Unit...");
				//box.getElement().getStyle().setZIndex(10000);
				
				final AbstractViewDialogBox panelView = new AbstractViewDialogBox() {
					
					@Override
					public void closeHandler() {
						box.hide();
						
					}
				};
				
				CreateUnitView createUnitView = new CreateUnitView(currentTPid+"") {
					
					@Override
					public void submitHandler() {
						box.hide();
						TrainingCourseAppController.eventBus.fireEvent(new LoadListOfCourseEvent());
						
						
//						String title, String workspaceFolderName, String description,
//						String workspaceFolderId, String scope, String ownerLogin, TrainingCourseObj trainingProjectRef
						
//						String title, String workspaceFolderName, String description,
//						String workspaceFolderId, String scope, String ownerLogin, TrainingCourseObj trainingProjectRef)
						TrainingUnitDTO tu = new TrainingUnitDTO(this.getUnitTitle(), this.getFolderName(), this.getUnitDescription(), null, null, null, mainView.getProjectInfoView().getCurrentProject());
						TrainingCourseAppController.trainingService.createUnitFolder(mainView.getProjectInfoView().getCurrentProject(), tu, new AsyncCallback<TrainingUnitDTO>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onSuccess(TrainingUnitDTO result) {
								mainView.getWorkspaceExplorerView().refreshRootFolder();
								
							}
						});
						
					}
					
					@Override
					public void setError(boolean visible, String error) {
						panelView.setError(visible, error);
						
					}
				};
				
				panelView.addViewAsWidget(createUnitView);
				box.add(panelView);
				box.show();
				
			}
		});
		
		TrainingCourseAppController.eventBus.addHandler(NavigationCourseEvent.TYPE, new NavigationCourseEventHandler() {
			
			@Override
			public void onNavigationInteractionCourse(NavigationCourseEvent activeGroupingView) {

				final Modal box = new Modal(true);
//				box.setWidth(WIDHT_DIALOG+"px");
//				box.setHeight(WIDHT_DIALOG+"px");
				
				CreateCourseView courseView = null;
				
				final AbstractViewDialogBox panelView = new AbstractViewDialogBox() {
					
					@Override
					public void closeHandler() {
						box.hide();
						
					}
				};
				
				if(activeGroupingView.getEventType().equals(NavigationEventType.CREATE)) {
				
					box.setTitle("Create a New Training Course...");

					courseView = new CreateCourseView() {
						
						@Override
						public void submitHandler() {
							
							panelView.showLoading(true, "Creating: "+this.getCourseTitle());
	
							TrainingCourseObj tp = new TrainingCourseObj(-1, this.getCourseTitle(), this.getCourseDescription(), this.getCommitment(), this.getLanguages(), null, null, null, this.getFolderName(), this.getCreatedBy(), false, null);
							
							TrainingCourseAppController.trainingService.createNewCourse(tp, new AsyncCallback<TrainingCourseObj>() {
								
								@Override
								public void onSuccess(TrainingCourseObj result) {
									box.hide();
									TrainingCourseAppController.eventBus.fireEvent(new LoadListOfCourseEvent());
									TrainingCourseAppController.eventBus.fireEvent(new TrainingCourseEvent(result, EventType.CREATED));
								}
								
								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
									
								}
							});
							
						}
						
						@Override
						public void setError(boolean visible, String error) {
							panelView.setError(visible, error);
							
						}
	
					};
				}else if(activeGroupingView.getEventType().equals(NavigationEventType.EDIT)) {
					
					box.setTitle("Update Course: "+currentManagingProject.getTitle());
					
					courseView = new CreateCourseView() {
						
						@Override
						public void submitHandler() {
							
							panelView.showLoading(true, "Updating: "+this.getCourseTitle());
	
							TrainingCourseObj tp = new TrainingCourseObj(
									currentManagingProject.getInternalId(), 
									this.getCourseTitle(), 
									this.getCourseDescription(), 
									this.getCommitment(), 
									this.getLanguages(), 
									currentManagingProject.getScope(), 
									currentManagingProject.getOwnerLogin(), 
									currentManagingProject.getWorkspaceFolderId(), 
									currentManagingProject.getWorkspaceFolderName(), 
									this.getCreatedBy(), 
									currentManagingProject.isCourseActive(), 
									currentManagingProject.getSharedWith());
							
							TrainingCourseAppController.trainingService.updateCourse(tp, new AsyncCallback<TrainingCourseObj>() {
								
								@Override
								public void onSuccess(TrainingCourseObj result) {
									box.hide();
									TrainingCourseAppController.eventBus.fireEvent(new LoadListOfCourseEvent());
									TrainingCourseAppController.eventBus.fireEvent(new TrainingCourseEvent(result, EventType.UPDATED));
								}
								
								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
									
								}
							});
							
						}
						
						@Override
						public void setError(boolean visible, String error) {
							panelView.setError(visible, error);
							
						}
	
					};
					
					courseView.editCourse(currentManagingProject);
					
				}
				
				panelView.addViewAsWidget(courseView);
				box.add(panelView);
				box.show();
			}
		});		
		
		
		TrainingCourseAppController.eventBus.addHandler(SelectedWorkspaceItemEvent.TYPE, new SelectedWorkspaceItemEventHandler() {
			
			@Override
			public void onSelectedItem(SelectedWorkspaceItemEvent selectedWorkspaceItemEvent) {
				
				if(selectedWorkspaceItemEvent.getItemId()==null || selectedWorkspaceItemEvent.getItemId().isEmpty())
					return;
				
				mainView.setVisibleMoreInfo(true);
				final LoaderIcon loader = new LoaderIcon("Loading Details...");
				mainView.getMoreInfoPanel().clear();
				mainView.getMoreInfoPanel().add(loader);
				
				TrainingCourseAppController.trainingService.getWorkspaceItemInfo(selectedWorkspaceItemEvent.getItemId(), new AsyncCallback<WorkspaceItemInfo>() {

					@Override
					public void onFailure(Throwable caught) {
						try {
							mainView.getMoreInfoPanel().remove(loader);
						}catch (Exception e) {
							// TODO: handle exception
						}
						Window.alert(caught.getMessage());
						
					}

					@Override
					public void onSuccess(WorkspaceItemInfo result) {
						try {
							mainView.getMoreInfoPanel().remove(loader);
						}catch (Exception e) {
							// TODO: handle exception
						}
						
						mainView.getItemActionAndInfoView().updateInfo(result);
						
					}
				});
				
			}
		});
		
		TrainingCourseAppController.eventBus.addHandler(DeleteWorkspaceItemEvent.TYPE, new DeleteWorkspaceItemEventHandler() {
			
			@Override
			public void onRemoveWsItem(final DeleteWorkspaceItemEvent removeWorkspaceItemEvent) {
				
				if(removeWorkspaceItemEvent.getWorkspaceItem()==null)
					return;
				
				TrainingCourseAppController.trainingService.deleteWorkspaceItem(removeWorkspaceItemEvent.getWorkspaceItem().getId(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						
					}

					@Override
					public void onSuccess(Void result) {
						
						if(removeWorkspaceItemEvent.getWorkspaceItem().getParentId()!=null) {
	
							Item item = new Item(removeWorkspaceItemEvent.getWorkspaceItem().getParentId(), "", true);
							mainView.getWsExplorer().refreshInternalFolder(item);
						}else
							mainView.getWsExplorer().refreshRootFolder();
						
						mainView.setVisibleMoreInfo(false);
						
					}
				});
				
			}
		});
		
		TrainingCourseAppController.eventBus.addHandler(ShowMoreInfoEvent.TYPE, new ShowMoreInfoEventHandler() {
			
			@Override
			public void onShowMoreInfo(ShowMoreInfoEvent showMoreInfoEvent) {
				mainView.setVisibleMoreInfo(showMoreInfoEvent.getShowMoreInfo());
				
			}
		});
		
		
		TrainingCourseAppController.eventBus.addHandler(TrainingUnitQuestionnaireEvent.TYPE, new TrainingUnitQuestionnaireEventHandler() {
			
			@Override
			public void onQuestionnaireEvent(final TrainingUnitQuestionnaireEvent tqe) {
				
				if(tqe.getUnit()!=null) {
					
					final Modal box = new Modal(true);
//					box.setWidth(WIDHT_DIALOG+"px");
//					box.setHeight(WIDHT_DIALOG+"px");
					box.setTitle("Add a Questionnaire to Unit: "+tqe.getUnit().getTitle());
					//box.getElement().getStyle().setZIndex(10000);
					
					final AbstractViewDialogBox panelView = new AbstractViewDialogBox() {
						
						@Override
						public void closeHandler() {
							box.hide();
							
						}
					};
					
					
					AssociateQuestionnaireView questView = new AssociateQuestionnaireView(tqe.getUnit()) {
						
						@Override
						public void submitHandler() {
							box.hide();
							TrainingUnitQuestionnaireDTO q = new TrainingUnitQuestionnaireDTO("", "", this.getQuestionnaireId(), "");
							TrainingCourseAppController.trainingService.addQuestionnaireToUnit(getUnit(), q, new AsyncCallback<TrainingUnitQuestionnaireDTO>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
									
								}

								@Override
								public void onSuccess(TrainingUnitQuestionnaireDTO result) {
									DialogResult dr = new DialogResult(null, "Questionnaire added!", "The questionnaire id: <br>\""+getQuestionnaireId()+"\"<br> has been associated correctly");
									dr.center();
									
									if(tqe.getUnit().getWorkspaceFolderId()!=null)
										TrainingCourseAppViewController.eventBus.fireEvent(new SelectedWorkspaceItemEvent(tqe.getUnit().getWorkspaceFolderId()));
									
								}
							});
						
							
						}
						
						@Override
						public void setError(boolean visible, String error) {
							panelView.setError(visible, error);
							
						}
						
						public void onInternalHide() {
							box.hide();
						};
					};
					
					panelView.addViewAsWidget(questView);
					box.add(panelView);
					box.show();
					
				}
				
			}
		});
		
		TrainingCourseAppController.eventBus.addHandler(DeleteTrainingUnitItemEvent.TYPE, new DeleteTrainingUnitItemEventHandler() {
			
			@Override
			public void onRemoveTrainingUnitItem(final DeleteTrainingUnitItemEvent deleteUnitItem) {
				
				if(deleteUnitItem.getVideo()!=null) {
					
					final DialogConfirm confirm = new DialogConfirm(null, "Delete Video?", "Deleting the Video: <br>"+deleteUnitItem.getVideo().getTitle()+" <br>Confirm?");
					confirm.getYesButton().addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							confirm.hide();
							
							final DialogConfirm dg = new DialogConfirm(null, "On going", "Deleting the Video...");
							dg.loader("Deleting the Video...");
							dg.center();
							
							TrainingCourseAppController.trainingService.deleteVideoForId(deleteUnitItem.getVideo().getInternalId(), new AsyncCallback<Integer>() {

								@Override
								public void onFailure(Throwable caught) {
									dg.hide();
									Window.alert(caught.getMessage());
									
								}

								@Override
								public void onSuccess(Integer result) {
									dg.hide();
									if(result!=null && result>0) {
										DialogResult dr = new DialogResult(null, "Video deleted!", "The video has been deleted correctly");
										dr.center();
										if(deleteUnitItem.getUnit().getWorkspaceFolderId()!=null)
											TrainingCourseAppViewController.eventBus.fireEvent(new SelectedWorkspaceItemEvent(deleteUnitItem.getUnit().getWorkspaceFolderId()));
										//mainView.getProjectInfoView().updateProjectInfo(result);
									}
							
									
								}
							});
							
							
						}
					});
					
					confirm.center();
					
				}else if(deleteUnitItem.getQuestionnaire()!=null) {
					
					final DialogConfirm confirm = new DialogConfirm(null, "Delete Questionnaire?", "Deleting the Questionnaire: <br>"+deleteUnitItem.getQuestionnaire().getQuestionnaireId()+" <br>Confirm?");
					confirm.getYesButton().addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							confirm.hide();
							
							final DialogConfirm dg = new DialogConfirm(null, "On going", "Deleting the Questionnaire...");
							dg.loader("Deleting the Questionnaire...");
							dg.center();
							
							TrainingCourseAppController.trainingService.deleteQuestionnaireForId(deleteUnitItem.getQuestionnaire().getInternalId(), new AsyncCallback<Integer>() {

								@Override
								public void onFailure(Throwable caught) {
									dg.hide();
									Window.alert(caught.getMessage());
									
								}

								@Override
								public void onSuccess(Integer result) {
									dg.hide();
									if(result!=null && result>0) {
										DialogResult dr = new DialogResult(null, "Questionnaire deleted!", "The questionnaire has been deleted correctly");
										dr.center();
										if(deleteUnitItem.getUnit().getWorkspaceFolderId()!=null)
											TrainingCourseAppViewController.eventBus.fireEvent(new SelectedWorkspaceItemEvent(deleteUnitItem.getUnit().getWorkspaceFolderId()));
										//mainView.getProjectInfoView().updateProjectInfo(result);
									}
							
									
								}
							});
							
							
						}
					});
					
					confirm.center();
		
				}
				
			}
		});
		
		TrainingCourseAppController.eventBus.addHandler(ShareTrainingProjectEvent.TYPE, new ShareTrainingProjectEventHandler() {
			
			@Override
			public void onShareProject(ShareTrainingProjectEvent shareTrainingProjectEvent) {
				
				TrainingCourseObj shareP = shareTrainingProjectEvent.getProject()==null?currentManagingProject:null;
				
				if(shareP==null)
					return;
				
				
				if(shareTrainingProjectEvent.isGroup()) {
					
					final DialogConfirm confirm = new DialogConfirm(null, "Confirm share with VRE Members?", "Sharing with all VRE Members, Confirm?");
					
					confirm.getYesButton().addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							confirm.hide();
							
							final DialogConfirm dg = new DialogConfirm(null, "On going", "Sharing the course...");
							dg.loader("Sharing the course...");
							dg.center();
							
							TrainingCourseAppController.trainingService.shareWithCurrentScope(mainView.getProjectInfoView().getCurrentProject(), new AsyncCallback<TrainingCourseObj>() {

								@Override
								public void onFailure(Throwable caught) {
									dg.hide();
									Window.alert(caught.getMessage());
								}

								@Override
								public void onSuccess(TrainingCourseObj result) {
									dg.hide();
									if(result!=null) {
										DialogResult dr = new DialogResult(null, "Course shared!", "The course has been shared correctly");
										dr.center();
										TrainingCourseAppController.eventBus.fireEvent(new OpenProjectEvent(result));
										//mainView.getProjectInfoView().updateProjectInfo(result);
									}
									
								}
							});
							
						}
					});
					
					confirm.center();
					
					return;
				}
				
				
				
				if(shareP!=null) {
					FileModel file = new FileModel(shareP.getWorkspaceFolderId(), shareP.getWorkspaceFolderName(), false);
					 
					/**
					 *  This controller instance the smart sharing dialog
					 * @param file - a fake file to display the field name ("filename") into dialog
					 * @param listAlreadySharedContact
					 */
				    SmartConstants.HEADER_TITLE = "Invite Users to your Training course.."; //if null sets the header title for the panel as "Share [Folder Name]"
				    SmartConstants.ADD_MORE = "Add More";
				    SmartConstants.ERROR_NO_USER_SELECTED = "You must pick at least one user with which share the folder";
		 
					WorkspaceSmartSharingController controller = new WorkspaceSmartSharingController(file, null);
		 
		                        /*Set those values to customize the labels of Smart Share Dialog fields*/
		 
					final SmartShare sharingWindow = controller.getSharingDialog();
					sharingWindow.show();
//					Button openSharingWindow = new Button("Show Sharing Window", new ClickHandler() { 
//						public void onClick(ClickEvent event) {
//								
//							}
//					});
					
			       sharingWindow.getButtonById(Dialog.OK).addListener(Events.Select, new Listener<BaseEvent>() {
		 
					@Override
					public void handleEvent(BaseEvent be) {
		 
						if(sharingWindow.isValidForm(true)){
							
							final DialogConfirm dg = new DialogConfirm(null, "On going", "Sharing the course...");
							dg.loader("Sharing the course...");
							dg.center();
							
							//THAT'S OK
							GWT.log("Sharing with: "+sharingWindow.getSharedListUsersCredential().toString());
							List<String> toLogins = new ArrayList<String>(sharingWindow.getSharedListUsersCredential().size());
							for (CredentialModel cm : sharingWindow.getSharedListUsersCredential()) {
								toLogins.add(cm.getLogin());
							}
							TrainingCourseAppController.trainingService.shareWithUsers(mainView.getProjectInfoView().getCurrentProject(), toLogins, new AsyncCallback<TrainingCourseObj>() {

								@Override
								public void onFailure(Throwable caught) {
									dg.hide();
									Window.alert(caught.getMessage());
									
								}

								@Override
								public void onSuccess(TrainingCourseObj result) {
									dg.hide();
									if(result!=null) {
										DialogResult dr = new DialogResult(null, "Course shared!", "The course has been shared correctly");
										dr.center();
										TrainingCourseAppController.eventBus.fireEvent(new OpenProjectEvent(result));
										//mainView.getProjectInfoView().updateProjectInfo(result);
									}
							
										
									
								}
							});
		 
						}
		 
					}
				});
					
				}
				
			}
		});
		
		TrainingCourseAppController.eventBus.addHandler(TrainingUnitVideoEvent.TYPE, new TrainingUnitVideoEventHandler() {
			
			@Override
			public void onVideoEvent(final TrainingUnitVideoEvent tuve) {
				
				if(tuve.getUnit()!=null) {
					
					final Modal box = new Modal(true);
//					box.setWidth(WIDHT_DIALOG+"px");
//					box.setHeight(WIDHT_DIALOG+"px");
					box.setTitle("Add a Video to Unit: "+tuve.getUnit().getTitle());
					//box.getElement().getStyle().setZIndex(10000);
					
					final AbstractViewDialogBox panelView = new AbstractViewDialogBox() {
						
						@Override
						public void closeHandler() {
							box.hide();
							
						}
					};
					
					AssociateVideoTrainingView questView = new AssociateVideoTrainingView(tuve.getUnit()) {
						
						@Override
						public void submitHandler() {
							box.hide();
							TrainingVideoDTO vdto = new TrainingVideoDTO(-1, this.getVideoTitle(), this.getVideoDescription(), this.getVideoURL());
							TrainingCourseAppController.trainingService.addVideoToUnit(getUnit(), vdto, new AsyncCallback<TrainingVideoDTO>() {

								@Override
								public void onFailure(Throwable caught) {
									Window.alert(caught.getMessage());
									
								}

								@Override
								public void onSuccess(TrainingVideoDTO result) {
									DialogResult dr = new DialogResult(null, "Video added!", "The video: <br>\""+getVideoTitle()+"\"<br> has been associated correctly");
									dr.center();
									
									if(tuve.getUnit().getWorkspaceFolderId()!=null)
										TrainingCourseAppViewController.eventBus.fireEvent(new SelectedWorkspaceItemEvent(tuve.getUnit().getWorkspaceFolderId()));
									
								}
							});
						}
						
						@Override
						public void setError(boolean visible, String error) {
							panelView.setError(visible, error);
							
						}
						
						@Override
						public void onInternalHide() {
							box.hide();
						};
					};
					
					panelView.addViewAsWidget(questView);
					box.add(panelView);
					box.show();
					
				}
				
			}
		});
		
	}

	
	/**
	 * Update project info.
	 *
	 * @param project the project
	 */
	public void updateProjectInfo(TrainingCourseObj project) {
		this.currentManagingProject = project;
		mainView.setVisibleMoreInfo(false);
		
		if(project==null)
			mainView.showWestPanel(false);
		else
			mainView.showWestPanel(true);
		
		mainView.getProjectInfoView().updateProjectInfo(project);
		
	}
	
	
	/**
	 * Reset main panel.
	 */
	public void resetViewComponents() {
		mainView.resetComponents();
	}

	
	/**
	 * Load resource explorer.
	 *
	 * @param workspaceFolderId the workspace folder id
	 */
	public void loadResourceExplorer(String workspaceFolderId) {
		mainView.getWorkspaceExplorerView().loadExplorerToRootFolderID(workspaceFolderId);
		mainView.setWidgetCenterPanelSize(mainView.getCenterPanelWidth(), mainView.getCenterPanelHeight());
		
	}

}
