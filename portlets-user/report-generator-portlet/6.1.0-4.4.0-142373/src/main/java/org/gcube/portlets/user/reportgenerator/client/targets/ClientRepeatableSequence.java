package org.gcube.portlets.user.reportgenerator.client.targets;

import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;
import org.gcube.portlets.user.reportgenerator.client.model.TemplateComponent;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ClientRepeatableSequence extends Composite implements ClientSequence {
	protected ArrayList<TemplateComponent> groupedComponents = new ArrayList<TemplateComponent>();

	private String id;
	private List<Metadata> metas;
	private VerticalPanel myPanel = new VerticalPanel();
	private Presenter p;
	private String key;
	protected RepeatableSequence repSequence;

	protected RepeatableSequence originalSequence = new RepeatableSequence();

	/**
	 * constructor used by the system when reading the model 
	 * @param sRS the sequence to repeat
	 */
	public ClientRepeatableSequence(Presenter p, RepeatableSequence sRS) {
		this.p = p;
		this.repSequence = sRS;
		this.key = sRS.getKey();
		this.originalSequence = extractOriginalSequence(sRS);
		SequenceWidget seqW = new SequenceWidget(p, this, originalSequence, true, false); 
		myPanel.add(seqW);

		ArrayList<RepeatableSequence> repeats = getRepeats();
		if (repeats == null || repeats.size() == 0) {
			seqW.showResetFields(true);
		}
		for (RepeatableSequence repeatSeq : repeats) {			
			addAnother(repeatSeq);
		}
		

		
		initWidget(myPanel);
	}

	/**
	 * when you repeat a sequence the model is not able to recognize if this was a sequence or not
	 * to overcome this limitation you have to identify the original sequence
	 * to identify it you can use the  REPEAT_SEQUENCE_DELIMITER ELEMENT
	 * @param sRS
	 */
	private RepeatableSequence extractOriginalSequence(RepeatableSequence sRS) {
		RepeatableSequence toReturn = null;
		int repeatDelimiterCounter = 2;
		ArrayList<BasicComponent> groupedComponents = new ArrayList<BasicComponent>();

		for (BasicComponent comp : sRS.getGroupedComponents()) {			
			if (comp.getType() == ComponentType.REPEAT_SEQUENCE_DELIMITER) {
				repeatDelimiterCounter--;
			}
			if (comp.getType() != ComponentType.REPEAT_SEQUENCE_INNER) 
				groupedComponents.add(comp);

			if (repeatDelimiterCounter == 0) {
				toReturn = new RepeatableSequence(groupedComponents, sRS.getHeight());			
				return toReturn;
			}				
		}
		return toReturn;
	}
	/**
	 * 
	 * @return
	 */
	private ArrayList<RepeatableSequence> getRepeats() {
		ArrayList<RepeatableSequence> toReturn = new ArrayList<RepeatableSequence>();
		int repeatDelimiterCounter = 2;

		ArrayList<BasicComponent> groupedComponents = new ArrayList<BasicComponent>();
		for (BasicComponent comp : repSequence.getGroupedComponents()) {			
			if (comp.getType() == ComponentType.REPEAT_SEQUENCE_DELIMITER) {
				repeatDelimiterCounter--;
			}
			if (comp.getType() != ComponentType.REPEAT_SEQUENCE_INNER) 
				groupedComponents.add(comp);

			if (repeatDelimiterCounter == 0) {
				toReturn.add(new RepeatableSequence(groupedComponents, repSequence.getHeight()));
				repeatDelimiterCounter = 2;
				groupedComponents = new ArrayList<BasicComponent>();
			}				
		}
		//if there is only the first  sequence return nothing
		if (toReturn.size() <= 1) {
			toReturn = new ArrayList<RepeatableSequence>();
		}
		else //return all of them without the first one
			toReturn.remove(0);
		return toReturn;
	}

	public void add(TemplateComponent toAdd) {
		if (toAdd.getType() != ComponentType.REPEAT_SEQUENCE_INNER) {
			groupedComponents.add(toAdd);
			myPanel.add(toAdd.getContent());
		}
	}

	protected void addAnother(RepeatableSequence sRS) {
		SequenceWidget seqW = new SequenceWidget(p, this, sRS, false, false);
		myPanel.add(seqW);
	}


	protected void addNew(RepeatableSequence sRS) {
		SequenceWidget seqW = new SequenceWidget(p, this, sRS, false, true);
		myPanel.add(seqW);
	}
	/**
	 * remove the widget and its components
	 * @param toRemove
	 */
	protected void removeSeqWidget(SequenceWidget toRemove) {
		myPanel.remove(toRemove);
		for (TemplateComponent tc : toRemove.getSeqGroupedComponents()) {
			groupedComponents.remove(tc);
		}
	}
	/**
	 * add the template component in the model
	 */
	@Override
	public void addTemplateComponent(TemplateComponent toAdd) {
		groupedComponents.add(toAdd);		
	}	
	/**
	 * return the grouped components of a sequence. For the specific RSG case if it finds a @see {@link SequenceWidget} RSG_BINDING_PROPERTY
	 * with value -1 it update it to the max value so far (requirement for RSG needed to wrote back the report in the DB)
	 * Note that a Sequence can contain multiple sequences and they are separeted by componentType.REPEAT_SEQUENCE_DELIMITER
	 */
	@Override
	public ArrayList<TemplateComponent> getGroupedComponents() {
		int maxBindingContext = getMaxIndex();
		for (int i = 0; i < groupedComponents.size(); i++) {			
			TemplateComponent co = groupedComponents.get(i);

			if (co.getType() == ComponentType.REPEAT_SEQUENCE_DELIMITER) {
				maxBindingContext++;
			}

			//specific for report References in Sequences
			if (co.getType() == ComponentType.REPORT_REFERENCE) {
				GWT.log("Reference found");
				ClientReportReference refGroup = (ClientReportReference) co.getContent();
				if (refGroup != null) {
					boolean keepLooking = true;
					for (Metadata prop : refGroup.getMetadata()) {
						if ( prop.getAttribute().equals(SequenceWidget.RSG_BINDING_PROPERTY) &&  (extractIndexFromBindingContext(prop.getValue()) == -1) ) {		
							prop.setValue(replaceIndexFromBindingContext(prop.getValue(), maxBindingContext));	
							keepLooking = false;
							break;
						}
					}
					if (keepLooking) {
						for (Metadata prop : refGroup.getMetadata()) {
							GWT.log("-" + prop.getAttribute()+":"+prop.getValue());
							//if there's a new component
							if ( prop.getAttribute().equals(SequenceWidget.RSG_BINDING_CONTEXT_PROPERTY) &&  (extractIndexFromBindingContext(prop.getValue()) == -1) ) {		
								prop.setValue(replaceIndexFromBindingContext(prop.getValue(), maxBindingContext));	
								break;
							}
						}	
					}				
				}
			}
			if (co.getAllMetadata() != null) {
				//for any other component
				for (Metadata prop : co.getAllMetadata()) {
					//if there's a new component
					if ( prop.getAttribute().equals(SequenceWidget.RSG_BINDING_CONTEXT_PROPERTY) &&  (extractIndexFromBindingContext(prop.getValue()) == -1) ) {		
						prop.setValue(replaceIndexFromBindingContext(prop.getValue(), maxBindingContext));	
						break;
					}
				}	
			}
		}
		return groupedComponents;
	}

	/**
	 * 
	 * @param binding it expect sth like #.profileList[0] or #.specificMeasureList[0] etc
	 *  and return the integer into square brackets 
	 * @return the integer into square brackets 
	 */
	private int extractIndexFromBindingContext(String binding) {
		if (binding.contains("[") && binding.contains("]")) {
			int openSquareBracket = binding.lastIndexOf("[")+1;
			int closeSquareBracket = binding.lastIndexOf("]");
			return Integer.parseInt(binding.substring(openSquareBracket, closeSquareBracket));
		}
		return 0;
	}
	/**
	 * 
	 * @param binding it expect sth like #.profileList[0] or #.specificMeasureList[0] etc
	 * @param newIndex
	 * @return return the newIndex into square brackets 
	 */
	private String replaceIndexFromBindingContext(String binding, int newIndex) {
		if (binding.contains("[") && binding.contains("]")) {
			int openSquareBracket = binding.lastIndexOf("[")+1;
			int closeSquareBracket = binding.lastIndexOf("]");
			String toReturn = binding.substring(0, openSquareBracket) + newIndex + binding.substring(closeSquareBracket);
			return toReturn;
		} 
		return binding;
	}
	/**
	 * @return specific cor RSG Case search for binding="geoRefList[3]" and return the max number into the square brackets
	 */
	private int getMaxIndex() {
		int max = 0;
		for (TemplateComponent co : groupedComponents) {
			if (co.getAllMetadata() != null) {
				for (Metadata prop : co.getAllMetadata()) {
					if (prop.getAttribute().equals(SequenceWidget.RSG_BINDING_CONTEXT_PROPERTY) || prop.getAttribute().equals(SequenceWidget.RSG_BINDING_PROPERTY)) {
						int currValue = extractIndexFromBindingContext(prop.getValue());
						if (currValue > max) 
							max = currValue;					
					}
				}
			} else {
				GWT.log("getAllMetadata() null for co=" +co.getType());
			}
		}
		GWT.log("return max=" +max);
		return max;
	}
	public void setGroupedComponents(ArrayList<TemplateComponent> groupedComponents) {
		this.groupedComponents = groupedComponents;
	}

	@Override
	public boolean add(String id, RepeatableSequence sequence, boolean isSingleRelation) {
		SequenceWidget seqW = new SequenceWidget(p, this, sequence, false, true);
		myPanel.add(seqW);
		//disable clear content
		if (myPanel.getWidgetCount() > 1) {
			SequenceWidget first = (SequenceWidget) myPanel.getWidget(0);
			first.showResetFields(false);
		}
		return true;
	}

	@Override
	public SequenceWidget remove(SequenceWidget toRemove) {
		myPanel.remove(toRemove);
		for (TemplateComponent tc : toRemove.getSeqGroupedComponents()) {
			groupedComponents.remove(tc);
		}
		//enable clear content
		if (myPanel.getWidgetCount() == 1) {
			SequenceWidget first = (SequenceWidget) myPanel.getWidget(0);
			first.showResetFields(true);
		}
		return toRemove;
	}

	@Override
	public void AddButtonClicked(RepeatableSequence sequence) {		
		add("not needed", sequence, false);
	}

	public String getKey() {
		return key;
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

	@Override
	public void cleanInModel() {
		//not needed		
	}
}
