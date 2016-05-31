/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jan 30, 2014
 * 
 */
public class SetTypeColumnContainer extends LayoutContainer {

	/**
	 * 
	 */
	private static final int COMBO_WIDTH = 110;
	private SimpleComboBox<String> sbc1;
	private ComboBox<? extends BaseModelData> sbc2;
	private VerticalPanel vp = new VerticalPanel();		
	
	private List<Widget> listCB = new ArrayList<Widget>();

	public SetTypeColumnContainer(SimpleComboBox<String> scbCategory, ComboBox<? extends BaseModelData> scbDataType) {

		this.sbc1 = scbCategory;
		this.sbc2 = scbDataType;

		this.setStyleAttribute("padding", "2px");
		sbc1.setStyleAttribute("margin-bottom", "2px");
		sbc1.setWidth(COMBO_WIDTH);
		sbc2.setWidth(110);
		vp.add(sbc1);
		vp.add(sbc2);
		
		add(vp);
	}

	/**
	 * 
	 * @return is valid form if all combo added to panel are not empty
	 */
	public boolean isValidForm() {
		
		for (Widget widget : listCB) {
			
			if (widget instanceof SimpleComboBox<?>){
				@SuppressWarnings("unchecked")
				SimpleComboBox<String> scb = (SimpleComboBox<String>) widget;
				if(scb.getSimpleValue().isEmpty())
					return false;
			}else if (widget instanceof ComboBox<?>){
				ComboBox<?> scb = (ComboBox<?>) widget;
				if(scb.getSelectedText()==null || scb.getSelectedText().isEmpty())
					return false;
			}
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public void addCombo(Widget widget){
		listCB.add(widget);
		if (widget instanceof SimpleComboBox<?>){
			SimpleComboBox<String> scb = (SimpleComboBox<String>) widget;
			scb.setWidth(110);
		}else if (widget instanceof ComboBox<?>){
			ComboBox<?> scb = (ComboBox<?>) widget;
			scb.setWidth(110);
		}
		
		vp.add(widget);
		vp.layout();
		this.layout(true);
	}
	
	public void removeCombo(Widget sbc, String name){
		
		if(listCB.remove(sbc)){
			GWT.log("Removed combo "+name);
			try{
				vp.remove(sbc);
			}catch (Exception e) {
				GWT.log("Combo dosn't exists");
			}
		this.layout(true);
		}
	}
}
