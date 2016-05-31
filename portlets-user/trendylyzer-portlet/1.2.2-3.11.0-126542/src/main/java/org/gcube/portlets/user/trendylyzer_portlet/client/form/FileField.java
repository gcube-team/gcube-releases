/**
 * 
 */
package org.gcube.portlets.user.trendylyzer_portlet.client.form;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.trendylyzer_portlet.client.form.TableItemSimple;
import org.gcube.portlets.user.trendylyzer_portlet.client.resources.Images;
import org.gcube.portlets.user.trendylyzer_portlet.client.widgets.FileSelector;
import org.gcube.portlets.user.trendylyzer_portlet.shared.parameters.Parameter;



import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.Widget;


public class FileField extends AbstractField {

	private VerticalPanel vp = new VerticalPanel();
	String value = null;
	FileSelector fileSelector;
	Button selectButton, selectButton2, cancelButton;
	TableItemSimple selectedFileItem = null;

	
	/**
	 * @param parameter
	 */
	public FileField(Parameter parameter) {
		super(parameter);

		fileSelector = new FileSelector() {
			public void fireSelection(TableItemSimple fileItem) {
				super.fireSelection(fileItem);
				selectedFileItem = fileItem;
				showFieldWithSelection();
			}
		};

		selectButton = new Button("Select File", Images.folderExplore(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				fileSelector.show();
			}
		});
		selectButton.setToolTip("Select File");

		selectButton2 = new Button("", Images.folderExplore(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				fileSelector.show();
			}
		});
		selectButton2.setToolTip("Select Another File");

		cancelButton = new Button("", Images.cancel(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				selectedFileItem = null;
				showNoSelectionField();
			}
		}); 

		showNoSelectionField();		
	}

	private void showNoSelectionField() {
		vp.removeAll();
		vp.add(selectButton);
		vp.layout();
	}

	private void showFieldWithSelection() {
		final String fileId = selectedFileItem.getId();
		final String fileName = selectedFileItem.getName();

		vp.removeAll();
		HorizontalPanel hp = new HorizontalPanel();
		hp.add(new Html("<div class='workflow-parameters-tableDescription'>"+ Format.ellipse(fileName, 30) +"</div>"));
		hp.add(selectButton2);
		hp.add(cancelButton);
		vp.add(hp);
		vp.layout();
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#isValid()
	 */
	@Override
	public boolean isValid() {
		return (selectedFileItem!=null);
	}

	@Override
	public Widget getWidget() {
		return vp;
	}

	@Override
	public String getValue() {
		return (selectedFileItem==null) ? null : selectedFileItem.getId();
	}

}
