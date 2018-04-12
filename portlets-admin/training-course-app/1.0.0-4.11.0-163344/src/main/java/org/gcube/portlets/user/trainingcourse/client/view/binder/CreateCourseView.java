package org.gcube.portlets.user.trainingcourse.client.view.binder;


import org.gcube.portlets.user.trainingcourse.shared.TrainingCourseObj;

import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.Pager;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.ResizeType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;


// TODO: Auto-generated Javadoc
/**
 * The Class CreateCourseView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 8, 2018
 */
public abstract class CreateCourseView extends Composite {

	/** The ui binder. */
	private static CreateCourseViewUiBinder uiBinder =
		GWT.create(CreateCourseViewUiBinder.class);

	/**
	 * The Interface CreateCourseViewUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jan 11, 2018
	 */
	interface CreateCourseViewUiBinder
		extends UiBinder<Widget, CreateCourseView> {
	}

	/** The pager. */
	@UiField
	Pager pager;
	
	/** The field folder name. */
	@UiField
	TextBox field_folder_name;
	
	/** The field languages. */
	@UiField
	TextBox field_languages;
	
	/** The field commitment. */
	@UiField
	TextBox field_commitment;
	
	/** The field course desciption. */
	@UiField
	TextArea field_course_desciption;
	
	/** The field course title. */
	@UiField
	TextBox field_course_title;
	
	/** The field creator name. */
	@UiField
	TextBox field_creator_name;
	
	/** The cg course title. */
	@UiField
	ControlGroup cg_course_title;

	/** The cg commitment. */
	@UiField
	ControlGroup cg_commitment;
	
	/** The cg commitment. */
	@UiField
	ControlGroup cg_languages;

	/** The cg folder name. */
	@UiField
	ControlGroup cg_folder_name;
	
	/** The cg created by. */
	@UiField
	ControlGroup cg_created_by;

	/** The instance. */
	CreateCourseView INSTANCE = this;
	
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
	 * Because this class has a default constructor, it can
	 * be used as a binder template. In other words, it can be used in other
	 * *.ui.xml files as follows:
	 * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
	 *   xmlns:g="urn:import:**user's package**">
	 *  <g:**UserClassName**>Hello!</g:**UserClassName>
	 * </ui:UiBinder>
	 * Note that depending on the widget that is used, it may be necessary to
	 * implement HasHTML instead of HasText.
	 */
	public CreateCourseView() {

		initWidget(uiBinder.createAndBindUi(this));
		
		field_course_desciption.setResize(ResizeType.BOTH);
		
		pager.getLeft().setVisible(false);
		
		pager.getRight().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setError(false, "");
				boolean isValid = validateSubmit();
				if(isValid) {
					pager.getRight().setDisabled(true);
					submitHandler();
				}
				
			}
		});

	}
	

	/**
	 * Edits the course.
	 *
	 * @param trainingCourse the training course
	 */
	public void editCourse(TrainingCourseObj trainingCourse) {
		field_course_title.setValue(trainingCourse.getTitle());
		field_course_desciption.setValue(trainingCourse.getDescription());
		field_commitment.setValue(trainingCourse.getCommitment());
		field_creator_name.setValue(trainingCourse.getCreatedBy());
		field_languages.setValue(trainingCourse.getLanguages());
		field_folder_name.setValue(trainingCourse.getWorkspaceFolderName());
		field_folder_name.setEnabled(false);
		pager.getRight().setText("Update Course");
	}
	
	/**
	 * Validate submit.
	 *
	 * @return true, if successful
	 */
	public boolean validateSubmit(){
		
		cg_course_title.setType(ControlGroupType.NONE);
		cg_folder_name.setType(ControlGroupType.NONE);
		
		if(field_course_title.getValue()==null || field_course_title.getValue().isEmpty()){
			cg_course_title.setType(ControlGroupType.ERROR);
			setError(true, "Course Title field is required");
			return false;
		}
		
		if(field_commitment.getValue()==null || field_commitment.getValue().isEmpty()){
			cg_commitment.setType(ControlGroupType.ERROR);
			setError(true, "Commitment field is required");
			return false;
		}
		
		if(field_languages.getValue()==null || field_languages.getValue().isEmpty()){
			cg_course_title.setType(ControlGroupType.ERROR);
			setError(true, "Language field is required");
			return false;
		}
		
		if(field_folder_name.getValue()==null || field_folder_name.getValue().isEmpty()){
			cg_folder_name.setType(ControlGroupType.ERROR);
			setError(true, "Folder Name field is required");
			return false;
		}
		
		if(field_creator_name.getValue()==null || field_creator_name.getValue().isEmpty()){
			cg_created_by.setType(ControlGroupType.ERROR);
			setError(true, "Creator By field is required");
			return false;
		}
	
		return true;
	}
	
	
	
	/**
	 * Gets the folder name.
	 *
	 * @return the folder name
	 */
	public String getFolderName() {
		return field_folder_name.getValue();
	}
	
	
	/**
	 * Gets the course title.
	 *
	 * @return the course title
	 */
	public String getCourseTitle(){
		return field_course_title.getValue();
	}
	
	

	/**
	 * Gets the course description.
	 *
	 * @return the course description
	 */
	public String getCourseDescription(){
		return field_course_desciption.getValue();
	}
	
	
	/**
	 * Gets the commitment.
	 *
	 * @return the commitment
	 */
	public String getCommitment() {
		return field_commitment.getValue();
	}

	
	/**
	 * Gets the languages.
	 *
	 * @return the languages
	 */
	public String getLanguages() {
		return field_languages.getValue();
	}
	
	
	/**
	 * Gets the created by.
	 *
	 * @return the created by
	 */
	public String getCreatedBy(){
		return field_creator_name.getValue();
	}
}
