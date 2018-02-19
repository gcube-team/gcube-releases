package org.gcube.datacatalogue.grsf_manage_widget.client.view.subwidgets;

import com.google.gwt.user.client.ui.Widget;


/**
 * A class of elements: two widgets and an object
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class Tuple {
	private Object o;
	private Widget w;
	private Widget w2;
	public Tuple(Object o, Widget w, Widget w2){
		this.o = o;
		this.w = w;
		this.w2 = w2;
	}
	public Object 
	getO(){ 
		return o; }
	public Widget getW(){ 
		return w;
	}
	public void setO(Object o){ 
		this.o = o; 
	}
	public void setW(Widget w){ 
		this.w = w; 
	}
	public Widget getW2() {
		return w2;
	}
	public void setW2(Widget w2) {
		this.w2 = w2;
	}
	@Override
	public String toString() {
		return "Tuple [o=" + o + ", w=" + w + ", w2=" + w2 + "]";
	}
}