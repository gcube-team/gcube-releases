package org.gcube.portlets.user.reportgenerator.client.targets;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;
import org.gcube.portlets.d4sreporting.common.shared.Tuple;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateComponent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SequenceWidget extends Composite {
	/**
	 * for the RSG case, whenever a new Sequence is added it sets the metadata named "bindingContext" to ...[-1] to identify it is new
	 */
	protected static final String RSG_BINDING_PROPERTY = "binding";
	protected static final String RSG_BINDING_CONTEXT_PROPERTY = "bindingContext";
	private static final int RSG_BINDING_PROPERTY_VALUE = -1;

	private VerticalPanel myPanel;

	private Button clearEntryB = new Button("Remove Content");
	private Button addAnotherB = new Button("Add another Entry");
	private Button removeThisB = new Button("Remove Entry");
	private Button selectNewB = new Button("Add Source");
	private Button clearB = new Button("Clear Association");

	private HorizontalPanel buttonPanel = new HorizontalPanel();
	HorizontalPanel buttonsWrapperPanel = new HorizontalPanel();
	private ClientSequence owner;
	private RepeatableSequence repSequence;
	private ArrayList<TemplateComponent> seqGroupedComponents = new ArrayList<TemplateComponent>();

	//in case of references tells if the ref is empty
	private boolean isEmptyRef = false;

	public SequenceWidget(Presenter p, ClientSequence owner, RepeatableSequence repSequence, boolean notRemovable, boolean isNew) {
		this.owner = owner;
		this.repSequence = repSequence;
		addAnotherB.getElement().getStyle().setMargin(10, Unit.PX);
		addAnotherB.getElement().getStyle().setWidth(130, Unit.PX);
		addAnotherB.addStyleName("addEntryButton");
		buttonPanel.getElement().getStyle().setMarginTop(20, Unit.PX);
		myPanel = new VerticalPanel();
		myPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		myPanel.setStyleName("seqWidget");
		int size = repSequence.getGroupedComponents().size();
		//GWT.log("SequenceWidget Size = " + size);
		for (int j = 0; j < size; j++) {
			BasicComponent sComp = repSequence.getGroupedComponents().get(j);
			if (j == size-1) {
				myPanel.add(buttonPanel); //add the button before the last element
				buttonPanel.setWidth("100%");
				buttonPanel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);

				buttonPanel.add(buttonsWrapperPanel);
			}
			//if isNew and is not locked and is of type BODY then put fields to blank
			if (isNew && (!sComp.isLocked()) && 
					(sComp.getType() == ComponentType.BODY || 
					sComp.getType() == ComponentType.BODY_NOT_FORMATTED ||  
					sComp.getType() == ComponentType.DYNA_IMAGE) ) {
				sComp.setPossibleContent("");
				//copy the metadata and signal is new
				sComp.setMetadata(setPropertiesForRSG(sComp.getMetadata()));
			}
			
			//copy the metadata and signal is new
			if (isNew && (sComp.getType() == ComponentType.ATTRIBUTE_MULTI || sComp.getType() == ComponentType.ATTRIBUTE_UNIQUE)) {
				sComp.setMetadata(setPropertiesForRSG(sComp.getMetadata()));
			}
			
	
			TemplateComponent toAdd = new TemplateComponent(p.getModel(), sComp, p, false, null);
			if (isNew && sComp.getType() == ComponentType.REPORT_REFERENCE ) { //when adding a new Sequence blanks the Ref 
				GWT.log("Ref j=" + j);
				ClientReportReference cRef = (ClientReportReference) toAdd.getContent();
				//need to clone the Tuple otherwise when blanking the ref it blanks the source too
				ArrayList<Tuple>  clonedTuples = new ArrayList<Tuple>();
				for (Tuple tuple : cRef.getTupleList()) {
					Tuple clonedTuple = tuple.clone();
					clonedTuple.setKey("-1");
					clonedTuples.add(clonedTuple);
				}

				ClientReportReference clonedRef = new ClientReportReference(p, cRef.getRefType(), clonedTuples, cRef.isSingleRelation());
				clonedRef.setMetadata(setPropertiesForRSG(sComp.getMetadata())); //copy the metadata and signal is new
				toAdd.setContent(clonedRef);
				clonedRef.clear();		
			}
			add(toAdd);


		} //end for
		initWidget(myPanel);

		if (notRemovable) {
			buttonsWrapperPanel.add(addAnotherB);
			addAnotherB.addClickHandler(new ClickHandler() {			
				@Override
				public void onClick(ClickEvent event) {
					addAnother();
				}
			});
		}
		else {

			removeThisB.getElement().getStyle().setWidth(130, Unit.PX);
			removeThisB.getElement().getStyle().setMargin(10, Unit.PX);
			removeThisB.addStyleName("deleteEntryButton");
			buttonsWrapperPanel.add(removeThisB);

			removeThisB.addClickHandler(new ClickHandler() {			
				@Override
				public void onClick(ClickEvent event) {
					removeMe();
				}
			});

			buttonsWrapperPanel.add(addAnotherB);
			addAnotherB.addClickHandler(new ClickHandler() {			
				@Override
				public void onClick(ClickEvent event) {
					addAnother();		
				}
			});
		}		
	}

	/**
	 * this is specific for the RSG case
	 * 
	 * whenever a new Sequence is added it sets the metadata named bindingContext to ...[-1] to identify it is new
	 * @param properties the properties of the compoenent
	 * @return the list of metadata with bindingContext set to -1
	 */
	private List<Metadata> setPropertiesForRSG(List<Metadata> properties) {
		List<Metadata> toReturn = new ArrayList<Metadata>(); 

		//cloning metadata
		for (Metadata metadata : properties) {
			Metadata toAdd = new Metadata(metadata.getAttribute(), metadata.getValue());
			toReturn.add(toAdd);
		}

		boolean keepLooking = true;
		for (Metadata prop : toReturn) {
			if (prop.getAttribute().equals(RSG_BINDING_PROPERTY) && (prop.getValue().contains("[") && prop.getValue().contains("]")) ) {		
				prop.setValue(replaceIndexFromBinding(prop.getValue(), RSG_BINDING_PROPERTY_VALUE));
				keepLooking = false;
			} 
		}
		if (keepLooking) {
			for (Metadata prop : toReturn) {
				if (prop.getAttribute().equals(RSG_BINDING_CONTEXT_PROPERTY)  && (prop.getValue().contains("[") && prop.getValue().contains("]")) ) {		
					prop.setValue(replaceIndexFromBindingContext(prop.getValue(), RSG_BINDING_PROPERTY_VALUE));
				}
			}
		}
		return toReturn;
	}

	private String replaceIndexFromBindingContext(String binding, int newIndex) {
		String toReturn = "";
		GWT.log("got bindingContext = " + binding +  " newIndex = " + newIndex);
		int openSquareBracket = binding.lastIndexOf("[")+1;
		int closeSquareBracket = binding.lastIndexOf("]");
		toReturn = binding.substring(0, openSquareBracket) + newIndex + binding.substring(closeSquareBracket);
		return toReturn;
	}

	private String replaceIndexFromBinding(String binding, int newIndex) {
		String toReturn = "";
		GWT.log("got binding = " + binding +  " newIndex = " + newIndex);
		int openSquareBracket = binding.lastIndexOf("[")+1;
		int closeSquareBracket = binding.lastIndexOf("]");
		toReturn = binding.substring(0, openSquareBracket) + newIndex + binding.substring(closeSquareBracket);
		return toReturn;
	}


	protected boolean hideClearAssociationButton() {
		return buttonsWrapperPanel.remove(clearB);
	}

	protected boolean hideAddEntryButton() {
		return buttonsWrapperPanel.remove(addAnotherB);
	}

	private void addAnother() {
		owner.AddButtonClicked(repSequence);
	}

	private void selectNew() {
		owner.AddButtonClicked(repSequence);
	}

	public void enableClear() {
		clearB.getElement().getStyle().setWidth(130, Unit.PX);
		clearB.getElement().getStyle().setMargin(10, Unit.PX);
		clearB.addStyleName("deleteAssociationButton");

		clearB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				clearAssociation();
			}
		});

		buttonsWrapperPanel.insert(clearB, 0);

	}

	public void clearAssociation() {

		GWT.log("Clearing Association");	
		//in the MODEL leave only the delimiters
		ArrayList<BasicComponent> cleanedRef = new ArrayList<BasicComponent>();
		owner.cleanInModel();
		for (BasicComponent bc : repSequence.getGroupedComponents()) {
			if (bc.getType() == ComponentType.REPEAT_SEQUENCE_DELIMITER) {
				cleanedRef.add(bc);
			}			
		}
		repSequence.setGroupedComponents(cleanedRef);

		//in the VIEW
		for (TemplateComponent tc : seqGroupedComponents) {
			if (tc.getType() == ComponentType.BODY_NOT_FORMATTED || tc.getType() == ComponentType.HEADING_2) {
				myPanel.remove(tc.getContent());
			}
		}

		buttonsWrapperPanel.clear();
		selectNewB.getElement().getStyle().setWidth(130, Unit.PX);
		selectNewB.getElement().getStyle().setMargin(10, Unit.PX);
		selectNewB.addStyleName("deleteAssociationButton");

		selectNewB.addClickHandler(new ClickHandler() {			
			@Override
			public void onClick(ClickEvent event) {
				selectNew();
			}
		});

		buttonsWrapperPanel.add(selectNewB);
		isEmptyRef = true;
	}

	protected boolean isAnEmptyRef() {
		return isEmptyRef;
	}

	public void add(TemplateComponent toAdd) {
		if (toAdd.getType() != ComponentType.REPEAT_SEQUENCE_INNER) {
			owner.addTemplateComponent(toAdd);
			seqGroupedComponents.add(toAdd);
			myPanel.add(toAdd.getContent());
		}
	}

	public void showResetFields(boolean show) {
		clearEntryB.setVisible(show);

		if (buttonsWrapperPanel.getWidgetIndex(clearEntryB) == -1) {
			buttonsWrapperPanel.insert(clearEntryB, 0);
			clearEntryB.getElement().getStyle().setWidth(130, Unit.PX);
			clearEntryB.getElement().getStyle().setMargin(10, Unit.PX);
			clearEntryB.addStyleName("deleteAssociationButton");

			clearEntryB.addClickHandler(new ClickHandler() {			
				@Override
				public void onClick(ClickEvent event) {
					clearContent();
				}
			});
		}

	}

	private void clearContent() {
		int widgetsCount = myPanel.getWidgetCount();
		for (int i = 0; i < widgetsCount; i++) {
			if (myPanel.getWidget(i) instanceof ClientReportReference) {
				ClientReportReference toClear = (ClientReportReference) myPanel.getWidget(i);
				toClear.clear();
				toClear.cleanInModel();
			}
			else if (myPanel.getWidget(i) instanceof D4sRichTextarea) {
				D4sRichTextarea toClear = (D4sRichTextarea) myPanel.getWidget(i);
				toClear.setHTML("");
			}
			else if (myPanel.getWidget(i) instanceof BasicTextArea) {
				BasicTextArea toClear = (BasicTextArea) myPanel.getWidget(i);
				toClear.setText("");
			}
			else if (myPanel.getWidget(i) instanceof ClientImage) {
				ClientImage toClear = (ClientImage) myPanel.getWidget(i);
				toClear.resetImage();
			}
			else if (myPanel.getWidget(i) instanceof AttributeMultiSelection) {
				AttributeMultiSelection toClear = (AttributeMultiSelection) myPanel.getWidget(i);
				toClear.reset();
			}
			else if (myPanel.getWidget(i) instanceof AttributeSingleSelection) {
				AttributeSingleSelection toClear = (AttributeSingleSelection) myPanel.getWidget(i);
				toClear.reset();
			}

		}
		
		//clean the fields in the model
		int size = repSequence.getGroupedComponents().size();
		//GWT.log("SequenceWidget Size = " + size);
		for (int j = 0; j < size; j++) {
			BasicComponent sComp = repSequence.getGroupedComponents().get(j);
			//if is not locked and is of type BODY then put fields to blank
			if ((!sComp.isLocked()) && (sComp.getType() == ComponentType.BODY || sComp.getType() == ComponentType.BODY_NOT_FORMATTED ||  
					sComp.getType() == ComponentType.DYNA_IMAGE) || sComp.getType() == ComponentType.ATTRIBUTE_MULTI || sComp.getType() == ComponentType.ATTRIBUTE_UNIQUE) {
				sComp.setPossibleContent("");
				sComp.setMetadata(setPropertiesForRSG(sComp.getMetadata()));
			}
		}

	}

	public ArrayList<TemplateComponent> getSeqGroupedComponents() {
		return seqGroupedComponents;
	}

	public void removeAddAnotherButton() {
		buttonsWrapperPanel.remove(addAnotherB);
	}



	private void removeMe() {
		owner.remove(this);
	}

	public RepeatableSequence getSequence() {
		return repSequence;
	}

	public void alignButtonsLeft() {
		buttonPanel.setWidth("10%");
	}
}
