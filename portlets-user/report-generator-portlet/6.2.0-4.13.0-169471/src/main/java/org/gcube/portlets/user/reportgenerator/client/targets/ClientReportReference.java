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
import org.gcube.portlets.user.reportgenerator.client.model.TemplateModel;
import org.gcube.portlets.user.reportgenerator.shared.VMETypeIdentifier;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
/**
 * 
 * @author massi
 *
 */
public class ClientReportReference extends Composite implements ClientSequence, Cloneable {
	
	private final int indentationValue = 20;
	private String id;
	

	private List<Metadata> metas;
	protected ArrayList<TemplateComponent> groupedComponents = new ArrayList<TemplateComponent>();
	private VerticalPanel myPanel = new VerticalPanel();

	private String refType;
	private  ArrayList<Tuple> tupleList;
	private SequenceWidget first;
	private Presenter p;
	private boolean singleRelation; //allow or not to add new entries

	/**
	 * s
	 * @param p
	 * @param ms
	 */
	public ClientReportReference(Presenter p, String refType, ArrayList<Tuple> tupleList, boolean singleRelation) {
		this.p = p;
		this.refType = refType;
		this.tupleList = tupleList;
		this.singleRelation = singleRelation;

		shrinkComponents(tupleList); //(beacuse the ref is indented

		for (Tuple seq : tupleList) {	
			if (myPanel.getWidgetCount()==0) { // if is the first one
				first = getMasterSequence(seq);
				if (isSingleRelation())
					first.removeAddAnotherButton();
				myPanel.add(first);
			} else {
				SequenceWidget seqW = getRefSequence(p, this, seq, false, false);
				myPanel.add(seqW);
			}			
		}
		initWidget(myPanel);
	}

	/**
	 * to make the components less in width (beacuse the ref is indented)
	 */
	private void shrinkComponents(ArrayList<Tuple> tupleList) {
		for (Tuple seq : tupleList) {	
			for (BasicComponent bc : seq.getGroupedComponents()) {
				if (bc.getWidth() >= (TemplateModel.TEMPLATE_WIDTH - 50))
					bc.setWidth(bc.getWidth()-(indentationValue+5));
			}
		}
	}
	/**
	 * the first seq has to behave differently
	 */
	private SequenceWidget getMasterSequence(RepeatableSequence seq) {
		SequenceWidget toReturn = getRefSequence(p, this, seq, true, false);

		if (tupleList.size() == 1) {
			toReturn.enableClear();	
			if (isTupleEmpty(tupleList.get(0))) {
				toReturn.clearAssociation();
			}
		}
		return toReturn;
	}

	private boolean isTupleEmpty(Tuple toCheck) {
		boolean toReturn = true;
		for (BasicComponent bc : toCheck.getGroupedComponents()) {
			if ((bc.getType() == ComponentType.BODY_NOT_FORMATTED ||bc.getType() == ComponentType.BODY) && bc.getPossibleContent() != null) 
				return false;			
		}
		return toReturn;
	}

	public ArrayList<Tuple> getTupleList() {
		return tupleList;
	}


	public void setTupleList(ArrayList<Tuple> tupleList) {
		this.tupleList = tupleList;
	}


	public boolean isSingleRelation() {
		return singleRelation;
	}

	/**
	 * add the template component in the model
	 */
	@Override
	public void addTemplateComponent(TemplateComponent toAdd) {
		groupedComponents.add(toAdd);		
	}	

	@Override
	public ArrayList<TemplateComponent> getGroupedComponents() {
		return groupedComponents;
	}
	@Override
	public void cleanInModel() {
		this.id = "-1";		
	}
	@Override
	public boolean add(String id, RepeatableSequence sequence, boolean isSingleRelation) {
		//(beacuse the ref is indented
		for (BasicComponent bc : sequence.getGroupedComponents()) {
			bc.setWidth(bc.getWidth()-(indentationValue+5));
		}

		if (first.isAnEmptyRef()) {  //this is the case when the current ref is not set and is only one
			GWT.log("isEmpty");
			first = getRefSequence(p, this, sequence, true, true);
			myPanel.clear();
			myPanel.add(first);
			tupleList.clear();
			first.enableClear();
			if (isSingleRelation)
				first.removeAddAnotherButton();			
		} else {
			SequenceWidget seqW = getRefSequence(p, this, sequence, false, false);
			myPanel.add(seqW);
			first.hideClearAssociationButton();
		}
		this.id = id;
		//needed for the model 
		Tuple toAdd = new Tuple(id, sequence.getGroupedComponents()); 
		tupleList.add(toAdd);
		return true;
	}


	@Override
	public SequenceWidget remove(SequenceWidget toRemove) {
		myPanel.remove(toRemove);
		for (TemplateComponent tc : toRemove.getSeqGroupedComponents()) {
			groupedComponents.remove(tc);
		}
		tupleList.remove(toRemove.getSequence());
		if (tupleList.size() == 1)
			first.enableClear();	

		GWT.log("List Tuple Size = "+tupleList.size());
		return toRemove;
	}


	@Override
	public void AddButtonClicked(RepeatableSequence sequence) {
		VMETypeIdentifier type2Pass = null;
		try {
			type2Pass = p.getTypeIdFromString(refType);
		} catch (Exception e) {
			e.printStackTrace();
		}
		p.showVMERefAssociateDialog(type2Pass);		
		p.setClientSequenceSelected(this); //important!		
	}

	public String getRefType() {
		return refType;
	}	

	public void clear() {
		first.clearAssociation();
	}

	/**
	 * A Ref is a seqeunce with diffrent style (indented, white background and less wide)
	 *
	 */
	private SequenceWidget getRefSequence(Presenter p, ClientSequence owner, RepeatableSequence repSequence, boolean notRemovable, boolean isNew) {
		SequenceWidget toReturn = new SequenceWidget(p, this, repSequence, notRemovable, isNew);
		toReturn.getElement().getStyle().setMarginLeft(indentationValue, Unit.PX);
		toReturn.addStyleName("seqWidget-shadow");
		toReturn.alignButtonsLeft();
		return toReturn;
	}

	public List<Metadata> getMetadata() {
		return metas;
	}

	public void setMetadata(List<Metadata> metas) {
		this.metas = metas;
	}
	
	public String getId() {
		if (id == null)
			return "-1";
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
