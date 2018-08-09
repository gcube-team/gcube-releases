/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view.suggestion;

import java.util.ArrayList;

import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;

import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.Image;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Apr 14, 2014
 *
 */
public class StepContainer extends LayoutContainer{
	
	private ArrayList<HorizontalPanel> steps;
	
	private HorizontalPanel mainHP = new HorizontalPanel();
	
	
	/**
	 * start index is 0
	 */
	public StepContainer(int totalSteps, int indexActive) {
		setBorders(false);
		mainHP.setVerticalAlign(VerticalAlignment.MIDDLE);
//		mainHP.setStyleAttribute("margin", "1px");
		
		steps = new ArrayList<HorizontalPanel>(totalSteps);
		for (int i = 0; i < totalSteps; i++) {
			Image img = null;
			HorizontalPanel hp = null;
			if(i==0){
				img = TdTemplateAbstractResources.step1().createImage();
				hp = createStep("Step: Categorize", true, img);
				mainHP.add(hp);
			}
			else if(i==1){
				img = TdTemplateAbstractResources.step2().createImage();
				hp = createStep("Step: Filters", false, img);
				mainHP.add(hp);
			}else if(i==2){
				img = TdTemplateAbstractResources.step3().createImage();
				hp = createStep("Step: Save", false, img);
				mainHP.add(hp);
			}
		
//			mainHP.add(hp);
			steps.add(hp);
		}
		
//		setActive(0);
		add(mainHP);
	}
	


	private HorizontalPanel createStep(String label, boolean isActive, Image icon){
		

		HorizontalPanel hp = new HorizontalPanel();
		hp.setVerticalAlign(VerticalAlignment.MIDDLE);
		if(icon!=null){
			hp.add(icon);
		}
	
		Html html = new Html(label);
		html.setStyleAttribute("padding-left", "5px");
		html.setStyleAttribute("padding-right", "10px");
		hp.add(html);
		
		
		if(isActive)
			hp.setStyleName("stepPanel-selected");
		else
			hp.setStyleName("stepPanel-unselected");
		
		return hp;
	}

	/**
	 * start index is 0
	 */
	public void setActive(int index) {
		
		if(steps.size()>index){
			for (int i = 0; i < steps.size(); i++) {
				HorizontalPanel hp = steps.get(index);
				if(i==index)
					hp.setStyleName("stepPanel-selected");
				else
					hp.setStyleName("stepPanel-unselected");
			}
			mainHP.layout();
		}
	}
	
	/**
	 * start index is 0
	 */
	public void updateActive(int index) {
		
		if(steps.size()>index){
			HorizontalPanel hp = steps.get(index);
			hp.setStyleName("stepPanel-selected");
			mainHP.layout();
		}
	}
	
	
}
