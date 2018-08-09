package org.gcube.data.access.ui.widgets;

import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.analysis.tabulardata.commons.templates.model.Representable;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.ArgumentInstance;
import org.gcube.data.analysis.tabulardata.commons.templates.model.actions.arguments.InvalidationEventListener;

@Slf4j
public class SingleSelectorWidget<K extends Representable> extends JComboBox<String> implements InvalidationEventListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2269322693560835310L;

	ArgumentInstance<K> instance;
	
	public SingleSelectorWidget(ArgumentInstance<K> instance) {
		super();
		this.instance = instance;
		this.instance.addInvalidatorListener(this);
		setSelectableValues();
	}

	@Override
	protected synchronized void selectedItemChanged() {
		int selectedIndex = this.getSelectedIndex();
		if (selectedIndex!=-1)
			instance.set(instance.getSelector().get(selectedIndex));
		super.selectedItemChanged();
	}

	@Override
	public void onInvalid(String instanceId) {
		this.setSelectedIndex(-1);		
	}
	
	synchronized void setSelectableValues(){
		List<K> selectableValues = instance.getSelector();
		log.trace("setting selector for "+instance.getName()+" "+selectableValues);
		String[] list =new String[selectableValues.size()];
		for (int i =0; i< selectableValues.size(); i++)
			if (selectableValues.get(i) instanceof Representable)
				list[i] = ((Representable)selectableValues.get(i)).getRepresentation();
			else list[i] = selectableValues.get(i).toString();
		this.setModel(new DefaultComboBoxModel<String>(list));
		this.setSelectedIndex(-1);
	}

	@Override
	public void onSelectorChanged(String instanceId) {
		log.trace("selector changed in "+instance.getName()+" with "+instance.getSelector());
		setSelectableValues();
	}
	
}
