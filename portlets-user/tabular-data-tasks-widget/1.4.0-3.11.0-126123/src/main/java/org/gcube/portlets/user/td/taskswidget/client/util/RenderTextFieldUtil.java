/**
 * 
 */
package org.gcube.portlets.user.td.taskswidget.client.util;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jul 18, 2013
 *
 */
public class RenderTextFieldUtil {
	
	public interface Function {
	    public void execute ();
	}

	/**
	* Safe function call on a component, which was rendered or not.
	*
	* @param c Component object that must be not null.
	* @param f Function object with the function that must be called.
	*/
	public static void safeFunctionCallOn(final Component c, final Function f) {
	    c.enableEvents(true);
	    if (c.isRendered()) {
	        f.execute();
	    } else {
	        final Listener<ComponentEvent> lsnr = new Listener<ComponentEvent>() {

				@Override
				public void handleEvent(ComponentEvent be) {
					f.execute();
					
				}
	        };
	        c.addListener(Events.Render, lsnr);
	    }
	}

	
	/* Sets a style attribute for the text-field control */
	public static void updateImageLegend(final FieldSet fielSet, final String imageUrl) {
		
		safeFunctionCallOn(fielSet, new Function() {
			@Override
			public void execute() {
//				System.out.println("textField.el() "+textField.el().firstChild().getId());
//				System.out.println("textField.el() 2 "+textField.el().firstChild().firstChild().getId());
//				System.out.println("attr: "+attrVal);
	

				Element element;
				NodeList<Element> nodeList = fielSet.getElement().getElementsByTagName("img");
				
				if(nodeList==null || nodeList.getLength()==0){
					element = Document.get().createElement("img");
					element.setAttribute("src", imageUrl);
					element.setAttribute("margin-right", "5px");
					fielSet.el().firstChild().insertFirst((com.google.gwt.user.client.Element) element);
				}else{
					
					element = nodeList.getItem(0);
					element.setAttribute("src", imageUrl);
				}
			}
		});
	}
	
	/* Sets a style attribute for the text-field control */
	public static void setTextAttr(final Text text, final String cssAttrNm, final String attrVal) {
		
		safeFunctionCallOn(text, new Function() {
			@Override
			public void execute() {
				text.el().firstChild().setStyleAttribute(cssAttrNm, attrVal);
			}
		});
	}
	
	
	/* Sets a style attribute for the text-field control */
	public static void setButtonVisible(final Button butt, final boolean visible) {
		
		safeFunctionCallOn(butt, new Function() {
			@Override
			public void execute() {
				butt.el().setVisible(visible);
			}
		});
	}



}
