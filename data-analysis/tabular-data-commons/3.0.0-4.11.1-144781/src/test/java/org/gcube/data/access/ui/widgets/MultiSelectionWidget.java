package org.gcube.data.access.ui.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Representable;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentInstance;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.InvalidationEventListener;

@Slf4j
public class MultiSelectionWidget<K extends Representable> extends JList<String> implements InvalidationEventListener, ListSelectionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3823307127685548345L;

	ArgumentInstance<K> instance;

	public MultiSelectionWidget(ArgumentInstance<K> instance) {
		super();
		this.instance = instance;
		this.instance.addInvalidatorListener(this);
		setSelectableValues();
		this.addListSelectionListener(this);
	}



	@Override
	public void onInvalid(String identifier) {
		this.setSelectedIndex(-1);		
	}

	synchronized void setSelectableValues(){
		List<K> selectableValues = instance.getSelector();
		String[] list =new String[selectableValues.size()];
		for (int i =0; i< selectableValues.size(); i++)
			if (selectableValues.get(i) instanceof Representable)
				list[i] = ((Representable)selectableValues.get(i)).getRepresentation();
			else list[i] = selectableValues.get(i).toString();
		this.setModel(new DefaultComboBoxModel<String>(list));
		this.setSelectedIndex(-1);
	}

	@Override
	public void onSelectorChanged(String identifier) {
		log.trace("selector changed in "+instance.getParent().getName()+" with "+instance.getSelector());
		setSelectableValues();
	}



	@Override
	public void valueChanged(ListSelectionEvent e) {
		System.out.print("First index: " + e.getFirstIndex());
		System.out.print(", Last index: " + e.getLastIndex());
		boolean adjust = e.getValueIsAdjusting();
		System.out.println(", Adjusting? " + adjust);
		if (!adjust) {
			int selections[] = this.getSelectedIndices();
			System.out.println("selected index are : "+selections);
			List<K> newvalues = new ArrayList<K>();
			for (int i = 0, n = selections.length; i < n; i++) 
				newvalues.add(instance.getSelector().get(selections[i]));	
			instance.set(newvalues);
			System.out.println();
		}
	}


}
