/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.util;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.Field;

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
	public static void setTextFieldAttr(final Field<?> textField, final String cssAttrNm, final String attrVal) {
		
		safeFunctionCallOn(textField, new Function() {
			@Override
			public void execute() {
				textField.el().firstChild().setStyleAttribute(cssAttrNm, attrVal);
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



}
