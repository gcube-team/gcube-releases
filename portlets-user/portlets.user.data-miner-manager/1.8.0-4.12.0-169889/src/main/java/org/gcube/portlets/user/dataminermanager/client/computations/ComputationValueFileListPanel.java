package org.gcube.portlets.user.dataminermanager.client.computations;

import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationValue;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationValueFile;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationValueFileList;
import org.gcube.data.analysis.dataminermanagercl.shared.data.computations.ComputationValueImage;

import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ComputationValueFileListPanel extends SimpleContainer {
	private ComputationValueFileList computationValueFileList;

	public ComputationValueFileListPanel(ComputationValueFileList computationValueFileList) {
		this.computationValueFileList = computationValueFileList;
		init();
		create();
	}
	
	private void init(){
		setBorders(false);
	}

	private void create() {
		VerticalLayoutContainer lc = new VerticalLayoutContainer();
		SimpleContainer simpleContainer;
		TextField textField;
		for(ComputationValue computationValue: computationValueFileList.getFileList()){
			if(computationValue instanceof ComputationValueFile){
				ComputationValueFile computationValueFile=(ComputationValueFile) computationValue;
				simpleContainer=new ComputationValueFilePanel(computationValueFile);
				lc.add(simpleContainer, new VerticalLayoutData(1, -1, new Margins(0)));
			} else {
				if(computationValue instanceof ComputationValueImage){
					ComputationValueImage computationValueImage=(ComputationValueImage) computationValue;
					simpleContainer=new ComputationValueImagePanel(computationValueImage);
					lc.add(simpleContainer, new VerticalLayoutData(1, -1, new Margins(0)));
				} else {
					textField = new TextField();
					textField.setValue(computationValue.getValue());
					textField.setReadOnly(true);
					lc.add(textField, new VerticalLayoutData(1, -1, new Margins(0)));
				}
			}
		}
		add(lc);
	}

}
