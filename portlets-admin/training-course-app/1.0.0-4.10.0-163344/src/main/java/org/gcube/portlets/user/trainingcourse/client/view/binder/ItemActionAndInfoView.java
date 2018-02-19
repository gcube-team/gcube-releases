package org.gcube.portlets.user.trainingcourse.client.view.binder;

import org.gcube.portlets.user.trainingcourse.client.TrainingCourseAppController;
import org.gcube.portlets.user.trainingcourse.client.dialog.DialogConfirm;
import org.gcube.portlets.user.trainingcourse.client.event.DeleteWorkspaceItemEvent;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingUnitQuestionnaireEvent;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingUnitQuestionnaireEvent.QUESTIONNAIRE_EVENT_TYPE;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingUnitVideoEvent;
import org.gcube.portlets.user.trainingcourse.client.event.TrainingUnitVideoEvent.VIDEO_EVENT_TYPE;
import org.gcube.portlets.user.trainingcourse.client.view.TrainingCourseAppViewController;
import org.gcube.portlets.user.trainingcourse.shared.WorkspaceItemInfo;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Column;
import com.github.gwtbootstrap.client.ui.Heading;
import com.github.gwtbootstrap.client.ui.Row;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;


// TODO: Auto-generated Javadoc
/**
 * The Class ProjectAction.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 12, 2018
 */
public class ItemActionAndInfoView extends Composite{

	/** The ui binder. */
	private static ItemActionUiBinder uiBinder = GWT.create(ItemActionUiBinder.class);
	
	private String DEFAULT_LABEL_TO_QUESTIONNAIRE = "Associate Questionnaire";
	
	private String DEFAULT_LABEL_TO_VIDEO = "Associate Video";

	/**
	 * The Interface ProjectActionUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jan 12, 2018
	 */
	interface ItemActionUiBinder extends UiBinder<Widget, ItemActionAndInfoView> {
	}
	
	/** The create folder. */
	@UiField
	Button btn_download;
	
	/** The create folder. */
	@UiField
	Button btn_delete;
	
	/** The btn add questionnaire. */
	@UiField
	Button btn_add_questionnaire;
	

	/** The btn add video. */
	@UiField
	Button btn_add_video;
	
	
	/** The more info. */
	@UiField
	HTMLPanel more_info;
	
	
	/** The workspace item info. */
	private WorkspaceItemInfo workspaceItemInfo;

	/**
	 * Instantiates a new project action.
	 */
	public ItemActionAndInfoView() {
		initWidget(uiBinder.createAndBindUi(this));
		bindActions();
	}


	/**
	 * Bind actions.
	 */
	private void bindActions() {
		
		btn_download.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				if(workspaceItemInfo.getPublicLink()!=null) {
					Window.open(workspaceItemInfo.getPublicLink(), "_self", null);
				}
				
			}
		});
		
		btn_delete.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(workspaceItemInfo!=null) {
					String type = null;
					switch (workspaceItemInfo.getItemType()) {
					case FILE:
						type = "file";
						break;
					case FOLDER:
						type = "folder";
						break;
					}
					
					final DialogConfirm confirm = new DialogConfirm(null, "Delete "+type+"?", "Deleting the "+type+": <br>"+workspaceItemInfo.getName()+"<br>Confirm?");
					confirm.getYesButton().addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							confirm.hide();
							TrainingCourseAppViewController.eventBus.fireEvent(new DeleteWorkspaceItemEvent(workspaceItemInfo));
							
						}
						
					});
					confirm.center();
					
//					if(Window.confirm("Deleting the "+type+": "+workspaceItemInfo.getName()+". Confirm?")) {
//						TrainingCourseAppViewController.eventBus.fireEvent(new DeleteWorkspaceItemEvent(workspaceItemInfo));
//					}
				}
				
			}
		});
		
		btn_add_questionnaire.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(workspaceItemInfo.getUnit()!=null)
					TrainingCourseAppViewController.eventBus.fireEvent(new TrainingUnitQuestionnaireEvent(workspaceItemInfo.getUnit(), QUESTIONNAIRE_EVENT_TYPE.ASSOCIATED));
				
			}
		});
		
		btn_add_video.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				if(workspaceItemInfo.getUnit()!=null)
					TrainingCourseAppViewController.eventBus.fireEvent(new TrainingUnitVideoEvent(workspaceItemInfo.getUnit(), VIDEO_EVENT_TYPE.ASSOCIATED));
				
			}
		});
	}
	
	/**
	 * Adds the info.
	 *
	 * @param title the title
	 * @param value the value
	 */
	private void addInfo(String title, String value) {
		
		Row row = new Row();
		Column c1 = new Column(2);
		Column c2 = new Column(8);
		HTML t = new HTML("<span style=\"font-style: italic;\">"+title+"</span>");
		t.getElement().getStyle().setColor("#333");
		c1.add(t);
		HTML v = new HTML(value);
		c2.add(v);
		row.add(c1);
		row.add(c2);
		row.getElement().getStyle().setMarginBottom(10, Unit.PX);
		more_info.add(row);
	}
	
	
	/**
	 * Update info.
	 *
	 * @param info the info
	 */
	public void updateInfo(WorkspaceItemInfo info) {
		btn_add_questionnaire.setText(DEFAULT_LABEL_TO_QUESTIONNAIRE);
		btn_add_video.setText(DEFAULT_LABEL_TO_QUESTIONNAIRE);
		btn_download.setVisible(true);
		btn_add_questionnaire.setVisible(false);
		btn_add_video.setVisible(false);
		this.workspaceItemInfo = info;
		more_info.clear();
		more_info.add(new Heading(5, "Details"));
		GWT.log("Show more info for: "+info);

		addInfo("Name", info.getName());

		if(info.isFolder()) {
			btn_download.setVisible(false);
			btn_add_questionnaire.setVisible(true);
			btn_add_video.setVisible(true);
			TrainingCourseAppController.trainingService.countVideosForTrainingUnit(workspaceItemInfo.getUnit().getInternalId(), new AsyncCallback<Integer>() {
				
				@Override
				public void onSuccess(Integer result) {
					if(result>-1)
						btn_add_video.setText(DEFAULT_LABEL_TO_VIDEO +" ("+result+")");
					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}
			});
			
			TrainingCourseAppController.trainingService.countQuestionnairesForTrainingUnit(workspaceItemInfo.getUnit().getInternalId(), new AsyncCallback<Integer>() {

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onSuccess(Integer result) {
					if(result>-1)
						btn_add_questionnaire.setText(DEFAULT_LABEL_TO_QUESTIONNAIRE +" ("+result+")");

					
				}
			});
		}
		
		if (info.getUnit()!=null && info.getUnit().getTitle()!=null) {
			addInfo("Unit Title", info.getUnit().getTitle());
		}
		
		if(info.getDecription()!=null && !info.getDecription().isEmpty()) {
			addInfo("Description", info.getDecription());
		}
		if(info.getMimeType()!=null && !info.getMimeType().isEmpty()) {
			addInfo("Mime Type", info.getMimeType());
		}
		
	}
	
	public HTMLPanel getMoreInfo() {
		return more_info;
	}

}
