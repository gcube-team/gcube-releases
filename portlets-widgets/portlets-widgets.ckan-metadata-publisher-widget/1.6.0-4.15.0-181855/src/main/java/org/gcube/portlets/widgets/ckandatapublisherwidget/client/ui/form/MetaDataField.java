package org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.form;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.widgets.ckandatapublisherwidget.client.ui.metadata.MetaDataFieldSkeleton;
import org.gcube.portlets.widgets.ckandatapublisherwidget.shared.metadata.MetadataFieldWrapper;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Label;
import com.github.gwtbootstrap.client.ui.constants.LabelType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * The Class MetaDataField.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * 
 * Jun 10, 2019
 */
public class MetaDataField extends Composite {

	private static MetaDataFieldUiBinder uiBinder = GWT.create(MetaDataFieldUiBinder.class);
	
	@UiField VerticalPanel panelMetaDataFieldsSkeleton;
	
	@UiField Label repeatabilityLabel;
	
	//@UiField ControlGroup cgMetaDataFieldSkeletonFields;

	@UiField Button addFieldButton;
	
	@UiField Button removeFieldButton;
	
	private List<MetaDataFieldSkeleton> listOfMetadataFields = new ArrayList<MetaDataFieldSkeleton>();

	private MetadataFieldWrapper fieldWrapper;

	private HandlerManager eventBus;

	/**
	 * The Interface MetaDataFieldUiBinder.
	 *
	 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
	 * 
	 * Jun 10, 2019
	 */
	interface MetaDataFieldUiBinder extends UiBinder<Widget, MetaDataField> {
	}


	/**
	 * Instantiates a new meta data field.
	 *
	 * @param field the field
	 * @param eventBus the event bus
	 * @throws Exception the exception
	 */
	public MetaDataField(final MetadataFieldWrapper field, HandlerManager eventBus) throws Exception {
		initWidget(uiBinder.createAndBindUi(this));
		this.fieldWrapper = field;
		this.eventBus = eventBus;
		addNewOccurrenceOfField();
		checkAllowedAddField();
		checkAllowedRemoveField();
		
		addFieldButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addNewOccurrenceOfField();
				checkAllowedAddField();
				checkAllowedRemoveField();
			}
		});
		
		removeFieldButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				removeLatestOccurrenceOfFiled();
				checkAllowedAddField();
				checkAllowedRemoveField();
			}
		});
		
		//The field is repeatable
		if(field.getMaxOccurs()>1) {
			repeatabilityLabel.setVisible(true);
			repeatabilityLabel.setType(LabelType.INFO);
			addFieldButton.setTitle("Add another "+field.getFieldName());
			removeFieldButton.setTitle("Remove latest "+field.getFieldName());
//			String maxTxt = field.getMaxOccurs()==Integer.MAX_VALUE?"":"(max occurs declared are "+field.getMaxOccurs()+" times)";
		}
	}


	/**
	 * Check allowed remove field.
	 */
	private void checkAllowedRemoveField() {
		boolean removeCond = fieldWrapper.getMaxOccurs()>1 && listOfMetadataFields.size()>1;
		removeFieldButton.setVisible(removeCond);
	}
	
	/**
	 * Checks if is allowed add occurrence.
	 */
	private void checkAllowedAddField() {
		
		boolean repeatibilityCond = fieldWrapper.getMaxOccurs()>1 && listOfMetadataFields.size()<fieldWrapper.getMaxOccurs();
		addFieldButton.setVisible(repeatibilityCond);
	
	}
	
	
	/**
	 * Removes the latest occurrence of filed.
	 */
	private void removeLatestOccurrenceOfFiled() {
		int size = listOfMetadataFields.size();
		MetaDataFieldSkeleton skeleton = listOfMetadataFields.get(size-1);
		try {
			panelMetaDataFieldsSkeleton.remove(skeleton);
			listOfMetadataFields.remove(size-1);
		}catch (Exception e) {
			GWT.log("Error: ",e);
		}
	}
	
	/**
	 * Adds the new occurrence of field.
	 */
	private void addNewOccurrenceOfField() {
		try {
			MetaDataFieldSkeleton fieldWidget = new MetaDataFieldSkeleton(fieldWrapper, eventBus);
			listOfMetadataFields.add(fieldWidget);
			panelMetaDataFieldsSkeleton.add(fieldWidget);
		} catch (Exception e) {
			GWT.log("Error: ",e);
		}
	}
	
	/**
	 * Gets the list of metadata fields.
	 *
	 * @return the list of metadata fields
	 */
	public List<MetaDataFieldSkeleton> getListOfMetadataFields() {
		return listOfMetadataFields;
	}

}
