package org.gcube.portlets.user.trainingcourse.client.view.binder;


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
 * The Class CreateUnitView.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 8, 2018
 */
public abstract class CreateUnitView extends Composite {

	/** The ui binder. */
	private static CreateUnitViewUiBinder uiBinder =
		GWT.create(CreateUnitViewUiBinder.class);

	/**
	 * The Interface CreateUnitViewUiBinder.
	 *
	 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
	 * Jan 12, 2018
	 */
	interface CreateUnitViewUiBinder
		extends UiBinder<Widget, CreateUnitView> {
	}

	/** The pager. */
	@UiField
	Pager pager;
	
	/** The training course id. */
	private String trainingCourseId;
	
	/** The field course desciption. */
	@UiField
	TextArea field_unit_desciption;
	
	/** The field course title. */
	@UiField
	TextBox field_unit_title;
	
	/** The field folder name. */
	@UiField
	TextBox field_folder_name;
	
	/** The cg course title. */
	@UiField
	ControlGroup cg_unit_title;

	/** The cg folder name. */
	@UiField
	ControlGroup cg_folder_name;
	
	
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
	 *
	 * @param trainingCourseId the training course id
	 */
	public CreateUnitView(String trainingCourseId) {
		this.trainingCourseId = trainingCourseId;

		initWidget(uiBinder.createAndBindUi(this));
		
		field_unit_desciption.setResize(ResizeType.BOTH);
		
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

	}
	
	
	protected boolean validateSubmit() {
		cg_unit_title.setType(ControlGroupType.NONE);
		cg_folder_name.setType(ControlGroupType.NONE);
		
		if(field_unit_title.getValue()==null || field_unit_title.getValue().isEmpty()){
			cg_unit_title.setType(ControlGroupType.ERROR);
			setError(true, "Unit Title field is required");
			return false;
		}
		
		if(field_folder_name.getValue()==null || field_folder_name.getValue().isEmpty()){
			cg_folder_name.setType(ControlGroupType.ERROR);
			setError(true, "Folder Name field is required");
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
	 * Gets the unit title.
	 *
	 * @return the unit title
	 */
	public String getUnitTitle(){
		return field_unit_title.getValue();
	}
	
	
	/**
	 * Gets the unit description.
	 *
	 * @return the unit description
	 */
	public String getUnitDescription(){
		return field_unit_desciption.getValue();
	}




}
