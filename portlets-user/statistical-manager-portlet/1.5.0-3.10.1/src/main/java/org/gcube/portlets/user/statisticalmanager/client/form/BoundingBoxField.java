/**
 * 
 */
package org.gcube.portlets.user.statisticalmanager.client.form;

import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.BoundingBoxParameter;
import org.gcube.portlets.user.statisticalmanager.client.bean.parameters.Parameter;
import org.gcube.portlets.user.statisticalmanager.client.resources.Images;
import org.gcube.portlets.user.statisticalmanager.client.widgets.BoundingBoxSelector;
import org.gcube.portlets.user.statisticalmanager.client.widgets.BoundingBoxSelector.Bbox;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author ceras
 *
 */
public class BoundingBoxField extends AbstractField {

	private VerticalPanel vp = new VerticalPanel();
	String value = null;
//	TableSelector tableSelector;
	BoundingBoxSelector bboxSelector;
	
	Button selectButton;
//	Html templatesList;
//	Bbox selectedBbox = null;
	private NumberField x1,y1,x2,y2;
	private String separator;

	/**
	 * @param parameter
	 */
	public BoundingBoxField(Parameter parameter) {
		super(parameter);

		BoundingBoxParameter p = (BoundingBoxParameter)parameter;
		this.separator = p.getSeparator();

		bboxSelector = new BoundingBoxSelector() {
			@Override
			public void onBoundingBoxSelected(Bbox bbox) {
				updateField(bbox);
			}
		};
		
//		tableSelector = new TableSelector(templates) {
//			@Override
//			public void fireSelection(TableItemSimple tableItem) {
//				super.fireSelection(tableItem);
//				selectedTableItem = tableItem;
//				showFieldWithSelection();
//
//				loadTableMetadata(tableItem);
//				// send change message to all listeners
//				// it will be managed by all columnFields and columnListField that depends by tabular field				
//			}
//		};

		selectButton = new Button("Select the Bounding Box", Images.map(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				bboxSelector.show();
			}
		});
		selectButton.setToolTip("Select the Bounding Box");
		selectButton.setStyleAttribute("margin-bottom", "5px");

		vp.add(selectButton);
//		String list = "";
//		boolean first = true;
//		for (String template: templates) {
//			list += (first ? "" : ", ") + template;
//			first = false;
//		}
//		templatesList = new Html("Suitable Data Set Templates: <br>"+ list);
//		templatesList.addStyleName("workflow-templatesList");
		
		createCoordinatesFields();
	}

	/**
	 * 
	 */
	private void createCoordinatesFields() {
		x1 = getNumberedField("x1");
		x2 = getNumberedField("x2");
		y1 = getNumberedField("y1");
		y2 = getNumberedField("y2");
		HorizontalPanel hp;
		
		hp = new HorizontalPanel();
		hp.add(new Html("x1:&nbsp;&nbsp;"));
		hp.add(x1);		
		vp.add(hp);

		hp.add(new Html("&nbsp;&nbsp;&nbsp;&nbsp;y1:&nbsp;&nbsp;"));
		hp.add(y1);
		vp.add(hp);

		vp.add(new Html("<div style='height:10px' />"));
		hp = new HorizontalPanel();
		hp.add(new Html("x2:&nbsp;&nbsp;"));
		hp.add(x2);
		vp.add(hp);
		
		hp.add(new Html("&nbsp;&nbsp;&nbsp;&nbsp;y2:&nbsp;&nbsp;"));
		hp.add(y2);
		vp.add(hp);
	}

	/**
	 * @param x12
	 */
	private NumberField getNumberedField(String name) {
		NumberField nf = new NumberField();
		nf.setPropertyEditorType(Double.class);
		nf.setFieldLabel(name);
		nf.setWidth(50);
		// TODO default value
//		nf.setValue(Double.parseDouble(p.getDefaultValue()));

		return nf;
	}

	/**
	 * 
	 */
	protected void updateField(Bbox bbox) {
		if (bbox!=null) {
			x1.setValue(bbox.getX1());
			x2.setValue(bbox.getX2());
			y1.setValue(bbox.getY1());
			y2.setValue(bbox.getY2());
		}
	}


	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getValue()
	 */
	@Override
	public String getValue() {
		if (x1.getValue()!=null && y1.getValue()!=null && x2.getValue()!=null && y2.getValue()!=null)
		return x1.getValue() + separator 
				+ y1.getValue() + separator 
				+ x2.getValue() + separator 
				+ y2.getValue();
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#getComponent()
	 */
	@Override
	public Widget getWidget() {
		return vp;
	}

//	/* (non-Javadoc)
//	 * @see org.gcube.portlets.user.statisticalmanager.client.form.AbstractField#isValid()
//	 */
//	@Override
//	public boolean isValid() {
//		return (selectedBbox!=null);
//	}

}
