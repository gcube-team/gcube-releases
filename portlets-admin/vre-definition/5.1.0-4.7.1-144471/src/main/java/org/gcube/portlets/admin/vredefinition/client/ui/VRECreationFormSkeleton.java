package org.gcube.portlets.admin.vredefinition.client.ui;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Row;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.datepicker.client.ui.DateBoxAppended;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class VRECreationFormSkeleton extends Composite{

	private static VRECreationFormSkeletonUiBinder uiBinder = GWT
			.create(VRECreationFormSkeletonUiBinder.class);

	interface VRECreationFormSkeletonUiBinder extends
	UiBinder<Widget, VRECreationFormSkeleton> {
	}

	@UiField VerticalPanel vreFunctionalities;
	@UiField DateBoxAppended vreFromDate;
	@UiField DateBoxAppended vreToDate;
	@UiField TextBox vreNameTextBox;
	@UiField TextBox vreDesignerTextBox;
	@UiField ListBox vreManagersListBox;
	@UiField TextArea vreDescriptionTextArea;

	// error panels
	@UiField HelpInline vreNameError;
	@UiField HelpInline vreDescriptionError;
	@UiField HelpInline vreDesignerError;

	// Path of the image to be shown during loading
	public static final String imagePath = GWT.getModuleBaseURL() + "../images/statistics-loader.gif";

	@SuppressWarnings("deprecation")
	public VRECreationFormSkeleton() {
		initWidget(uiBinder.createAndBindUi(this));

		// update data vreToDate
		Date end = vreFromDate.getValue();
		end.setYear(end.getYear() + 1);

		vreToDate.setValue(end);
	}

	/**
	 * Append a functionality (it must be a Row object)
	 * @param child
	 */
	public void appendRow(Widget child){
		if(child.getClass().equals(Row.class))
			vreFunctionalities.add(child);
	}

	@UiHandler("vreFromDate")
	void ChangeHandlerStart(ValueChangeEvent<Date> event){

		GWT.log("Change handler FROM");

		// get current date
		Date current = new Date();

		// get selected date
		Date selected = vreFromDate.getValue();

		if(selected.compareTo(current) < 0)
			vreFromDate.setValue(current);

	}

	@SuppressWarnings("deprecation")
	@UiHandler("vreToDate")
	void ChangeHandlerTo(ValueChangeEvent<Date> event){

		GWT.log("Change handler TO");

		// get current date start
		Date start = vreFromDate.getValue();

		// get selected date
		Date selected = vreToDate.getValue();

		if(selected.compareTo(start) < 0){

			Date end = vreFromDate.getValue();
			end.setYear(end.getYear() + 1);
			vreToDate.setValue(end);

		}

	}

	/**
	 * remove image loading
	 */
	public void removeLoader() {

		vreFunctionalities.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		vreFunctionalities.clear();

	}

	/**
	 * add a loading image
	 */
	public void showLoader() {

		vreFunctionalities.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		vreFunctionalities.setWidth("100%");
		vreFunctionalities.add(new Image(imagePath));

	}

	/**
	 * Return vre current name
	 * @return
	 */
	public String getCurrentVREName(){
		return vreNameTextBox.getText();
	}

	/**
	 * Return vre current designer
	 * @return
	 */
	public String getCurrentVREDesigner(){
		return vreDesignerTextBox.getText();
	}

	/**
	 * Return vre current manager
	 * @return
	 */
	public String getCurrentVREManager(){
		GWT.log("Selected manager is " + vreManagersListBox.getSelectedItemText());
		return vreManagersListBox.getSelectedItemText();
	}

	/**
	 * Return vre current description
	 * @return
	 */
	public String getCurrentVREDescription(){
		return vreDescriptionTextArea.getText();
	}

	/**
	 * Return vre current start date
	 * @return
	 */
	public Date getCurrentVREStartDate(){
		return vreFromDate.getValue();
	}

	/**
	 * Return vre current to date
	 * @return
	 */
	public Date getCurrentVREToDate(){
		return vreToDate.getValue();
	}

	/**
	 * Set the designer
	 * @param designer
	 */
	public void setVREDesigner(String designer){

		vreDesignerTextBox.setText(designer);

	}

	/**
	 * Set the managers
	 * @param managers
	 * @param a selected manager among the list of managers 
	 */
	public void setVREManagers(List<String> managers, String selectedManager){
		
		// sort the list first
		Collections.sort(managers);
		
		for (String manager : managers) {
			vreManagersListBox.addItem(manager);
		}
		
		if(selectedManager != null)
			vreManagersListBox.setSelectedValue(selectedManager);
	}

	/**
	 * Alert on missing vre name
	 */
	public void showAlertBlockVREName() {

		vreNameError.setVisible(true);
		vreNameTextBox.setFocus(true); // set focus

		vreNameTextBox.addKeyPressHandler(new KeyPressHandler() {

			public void onKeyPress(KeyPressEvent event) {

				vreNameError.setVisible(false);

			}
		});

		vreNameTextBox.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {

				vreNameError.setVisible(false);

			}
		});

	}

	/**
	 * Alert on missing vre description
	 */
	public void showAlertBlockVREDescritption() {

		vreDescriptionError.setVisible(true);
		vreDescriptionTextArea.setFocus(true); // set focus

		vreDescriptionTextArea.addKeyPressHandler(new KeyPressHandler() {

			public void onKeyPress(KeyPressEvent event) {

				vreDescriptionError.setVisible(false);

			}
		});

		vreDescriptionTextArea.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {

				vreDescriptionError.setVisible(false);

			}
		});

	}

	/**
	 * Set the vre name
	 * @param vreName
	 */
	public void setVREName(String vreName) {
		
		vreNameTextBox.setText(vreName);
		
	}

	/**
	 * Set the vre description
	 * @param description
	 */
	public void setVREDescription(String description) {
		
		vreDescriptionTextArea.setText(description);
		
	}

	/**
	 * Start from vre date
	 * @param startDate
	 */
	public void setVREFromDate(Date startDate) {
		
		vreFromDate.setValue(startDate);
		
	}

	/**
	 * Start from vre date
	 * @param toDate
	 */
	public void setVREToDate(Date toDate) {
		
		vreToDate.setValue(toDate);
		
	}
}
