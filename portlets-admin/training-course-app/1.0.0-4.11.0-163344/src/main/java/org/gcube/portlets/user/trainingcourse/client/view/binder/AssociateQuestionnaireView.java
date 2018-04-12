package org.gcube.portlets.user.trainingcourse.client.view.binder;


import java.util.List;

import org.gcube.portal.trainingmodule.shared.TrainingUnitDTO;
import org.gcube.portal.trainingmodule.shared.TrainingUnitQuestionnaireDTO;
import org.gcube.portlets.user.trainingcourse.client.TrainingCourseAppController;
import org.gcube.portlets.user.trainingcourse.client.event.DeleteTrainingUnitItemEvent;
import org.gcube.portlets.user.trainingcourse.client.view.TrainingCourseAppViewController;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.Pager;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
 * The Class AssociateQuestionnaireView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 1, 2018
 */
public abstract class AssociateQuestionnaireView extends Composite {

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
		extends UiBinder<Widget, AssociateQuestionnaireView> {
	}

	/** The pager. */
	@UiField
	Pager pager;
	
	/** The field course desciption. */
	@UiField
	TextBox field_questionnaire_id;
	
	/** The field table questionnaire. */
	@UiField
	HTMLPanel quest_footer_panel;
	
	/** The cg questionnaire id. */
	@UiField
	ControlGroup cg_questionnaire_id;

	/** The unit. */
	private TrainingUnitDTO unit;
	
	
	/**
	 * Submit handler.
	 */
	public abstract void submitHandler();
	
	/**
	 * Sets the error.
	 *
	 * @param visible the visible
	 * @param error the error
	 */
	public abstract void setError(boolean visible, String error);
	
	
	/**
	 * On internal hide.
	 */
	public abstract void onInternalHide();
	

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
	public AssociateQuestionnaireView(TrainingUnitDTO unit) {
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
		

		loadListOfQuestionnaire();

	}
	
	/**
	 * Load list of questionnaire.
	 */
	private void loadListOfQuestionnaire() {
		
		if(unit==null || unit.getInternalId()<0)
			return;
		
		quest_footer_panel.clear();
		
		TrainingCourseAppController.trainingService.getListOfQuestionnaireForUnit(unit.getInternalId(), new AsyncCallback<List<TrainingUnitQuestionnaireDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<TrainingUnitQuestionnaireDTO> result) {
				GWT.log("Returned questionnaire: "+result);
				if(result!=null && result.size()>0) {
					Label title = new Label("Questionnaire already added:");
					title.setType(LabelType.INFO);
					quest_footer_panel.add(title);
					
					
					FlexTable flt = new FlexTable();
					flt.setCellSpacing(4);
					flt.getElement().getStyle().setMarginTop(10, Unit.PX);
					flt.addStyleName("table-fixed");
					flt.setWidget(0, 0, new HTML("<b>ID<b>"));
					flt.getColumnFormatter().setWidth(0, "90%");
					flt.getColumnFormatter().setWidth(1, "9%");
//					flt.setWidget(0, 1, new HTML("<b>Description<b>"));
					for (int i = 0; i<result.size(); i++) {
						final TrainingUnitQuestionnaireDTO trainingUnitQuestionnaireDTO = result.get(i);
						flt.setWidget(i+1, 0, new HTML(trainingUnitQuestionnaireDTO.getQuestionnaireId()));
						Button buttRemove = new Button();
						buttRemove.setIcon(IconType.REMOVE_SIGN);
						buttRemove.setType(ButtonType.LINK);
						buttRemove.setTitle("Delete the questionnaire");
						buttRemove.addClickHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								onInternalHide();
								TrainingCourseAppViewController.eventBus.fireEvent(new DeleteTrainingUnitItemEvent(unit, null, trainingUnitQuestionnaireDTO));
								
							}
						});
						flt.setWidget(i+1, 1, buttRemove);
					}
					
					quest_footer_panel.add(flt);
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
		cg_questionnaire_id.setType(ControlGroupType.NONE);
		
		if(field_questionnaire_id.getValue()==null || field_questionnaire_id.getValue().isEmpty()){
			cg_questionnaire_id.setType(ControlGroupType.ERROR);
			setError(true, "Questionnaire Id field is required");
			return false;
		}
		
		return true;
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
	 * Gets the questionnaire id.
	 *
	 * @return the questionnaire id
	 */
	public String getQuestionnaireId() {
		return field_questionnaire_id.getValue();
	}
	
	

}
