package org.gcube.portlets.user.trainingcourse.client.view.binder;


import java.util.List;

import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;
import org.gcube.portal.trainingmodule.shared.TrainingVideoDTO;
import org.gcube.portlets.user.trainingcourse.client.TrainingCourseAppController;
import org.gcube.portlets.user.trainingcourse.client.event.DeleteTrainingUnitItemEvent;
import org.gcube.portlets.user.trainingcourse.client.view.TrainingCourseAppViewController;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Pager;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;



// TODO: Auto-generated Javadoc
/**
 * The Class AssociateVideoTrainingView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 1, 2018
 */
public abstract class AssociateVideoTrainingView extends Composite {

	/** The ui binder. */
	private static AssociateQuestionnaireViewUiBinder uiBinder =
		GWT.create(AssociateQuestionnaireViewUiBinder.class);

	/**
	 * The Interface CreateUnitViewUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jan 12, 2018
	 */
	interface AssociateQuestionnaireViewUiBinder
		extends UiBinder<Widget, AssociateVideoTrainingView> {
	}

	/** The pager. */
	@UiField
	Pager pager;
	
	/** The field course desciption. */
	@UiField
	TextBox field_video_title;
	
	/** The field video description. */
	@UiField
	TextArea field_video_description;
	
	/** The field video url. */
	@UiField
	TextBox field_video_url;
	
	/** The field html panel. */
	@UiField
	HTMLPanel field_html_panel;
	

	/** The cg video title. */
	@UiField
	ControlGroup cg_video_title;
	
	/** The cg video url. */
	@UiField
	ControlGroup cg_video_url;

	/** The unit. */
	private TrainingUnitDTO unit;
	
	
	/**
	 * Submit handler.
	 */
	public abstract void submitHandler();
	
	
	/**
	 * On internal hide.
	 */
	public abstract void onInternalHide();
	
	/**
	 * Sets the error.
	 *
	 * @param visible the visible
	 * @param error the error
	 */
	public abstract void setError(boolean visible, String error);
	

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
	 * @param unit the unit
	 */
	public AssociateVideoTrainingView(TrainingUnitDTO unit) {
		this.unit = unit;
		initWidget(uiBinder.createAndBindUi(this));
		
		pager.getLeft().setVisible(false);
		
		pager.getRight().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setError(false, "");
				boolean isValid = validateSubmit();
				if(isValid)
					submitHandler();
				
			}
		});
		
		loadListOfVideo();

	}
	
	/**
	 * Load list of video.
	 */
	private void loadListOfVideo() {
		
		if(unit==null || unit.getInternalId()<0)
			return;
		
		field_html_panel.clear();
		
		TrainingCourseAppController.trainingService.getListOfVideoForUnit(unit.getInternalId(), new AsyncCallback<List<TrainingVideoDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<TrainingVideoDTO> result) {
				GWT.log("Returned questionnaire: "+result);
				if(result!=null && result.size()>0) {
					Label title = new Label("Video already added:");
					title.setType(LabelType.INFO);
					field_html_panel.add(title);
					FlexTable flt = new FlexTable();
					flt.setCellSpacing(4);
					flt.getElement().getStyle().setMarginTop(10, Unit.PX);
					flt.addStyleName("table-fixed");
					flt.getColumnFormatter().setWidth(0, "42%");
//					flt.getColumnFormatter().setWidth(1, "30%");
					flt.getColumnFormatter().setWidth(2, "42%");
					flt.getColumnFormatter().setWidth(2, "15%");
					flt.setWidget(0, 0, new HTML("<b>Title<b>"));
//					flt.setWidget(0, 1, new HTML("<b>Description<b>"));
					flt.setWidget(0, 1, new HTML("<b>URL<b>"));
					for (int i = 0; i<result.size(); i++) {
						final TrainingVideoDTO videoDTO = result.get(i);
						flt.setWidget(i+1, 0, new HTML(videoDTO.getTitle()));
//						flt.setWidget(i+1, 1, new HTML(trainingUnitQuestionnaireDTO.getDescription()));
						flt.setWidget(i+1, 1, new HTML(videoDTO.getUrl()));
						Button buttRemove = new Button();
						buttRemove.setTitle("Delete the video");
						buttRemove.setIcon(IconType.REMOVE_SIGN);
						buttRemove.setType(ButtonType.LINK);
						buttRemove.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								onInternalHide();
								TrainingCourseAppViewController.eventBus.fireEvent(new DeleteTrainingUnitItemEvent(unit, videoDTO, null));
								
							}
						});
						flt.setWidget(i+1, 2, buttRemove);
					}
					
					field_html_panel.add(flt);
				}
				
			}
			
		});
	}
	
	
	/**
	 * Validate submit.
	 *
	 * @return true, if successful
	 */
	protected boolean validateSubmit() {
		
		cg_video_title.setType(ControlGroupType.NONE);
		cg_video_url.setType(ControlGroupType.NONE);
		
		if(field_video_title.getValue()==null || field_video_title.getValue().isEmpty()){
			cg_video_title.setType(ControlGroupType.ERROR);
			setError(true, "The title field is required");
			return false;
		}
		
		if(field_video_url.getValue()==null || field_video_url.getValue().isEmpty()){
			cg_video_url.setType(ControlGroupType.ERROR);
			setError(true, "The URL field is required");
			return false;
		}
		
		if(!isValidURL(field_video_url.getValue())) {
			cg_video_url.setType(ControlGroupType.ERROR);
			setError(true, "The Video URL must be a valid HTTP/S URL");
			return false;
		}

		
		return true;
	}
	
	private boolean isValidURL(String videoUrl) {
		RegExp regExp = RegExp.compile("^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/|www\\.|ftp:\\/\\/)+[^ \"]+$");
		return regExp.test(videoUrl);
	}
	
	/**
	 * Gets the unit.
	 *
	 * @return the unit
	 */
	public TrainingUnitDTO getUnit() {
		return unit;
	}
	
	
	/**
	 * Gets the video title.
	 *
	 * @return the video title
	 */
	public String getVideoTitle() {
		return field_video_title.getValue();
	}
	
	/**
	 * Gets the video description.
	 *
	 * @return the video description
	 */
	public String getVideoDescription(){
		return field_video_description.getValue();
	}

	/**
	 * Gets the video URL.
	 *
	 * @return the video URL
	 */
	public String getVideoURL(){
		return field_video_url.getValue();
	}
	
	
	

}
