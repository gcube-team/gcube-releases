package org.gcube.portlets.user.reportgenerator.client.targets;

import java.util.List;

import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.user.reportgenerator.client.UUID;
import org.gcube.portlets.user.reportgenerator.client.Presenter.Presenter;

import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TextArea;
/**
 * <code> BasicTextArea </code> is a resizable textArea
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 * @version July 2013 
 */
public class BasicTextArea extends TextArea {
	//the id
	private String id;
	//the properties associated
	private List<Metadata> metas;
	// neeeded for js autosize
	private String myId;
	
	public BasicTextArea(String id, ComponentType type, final Presenter presenter, int left, int top, final int width,  final int height, boolean hasComments, boolean showClose) {
		this.id = id;
		myId = UUID.uuid(16); // neeeded for js autosize
		this.setPixelSize(width, height);

		this.setStyleName("report-ui-component");
		this.addStyleName("d4sFrame");
		this.getElement().setId(myId);
		
		switch (type) {

		case BODY_NOT_FORMATTED:
			this.addStyleName("simpleText");
			break;
		default:
			break;
		}		

		this.addKeyUpHandler(new KeyUpHandler() {
			public void onKeyUp(KeyUpEvent event) {
				//presenter.resizeTemplateComponentInModel(myInstance, width, newHeight);
			}			
		});

		addFocusHandler(new FocusHandler() {			
			@Override
			public void onFocus(FocusEvent event) {
				autoSizeIt(myId, height);	
				presenter.addTextToolBar(false);
			}
		});
	}


	public RichTextArea getRichTextArea() {
		return null;
	}
	
	

	public List<Metadata> getMetadata() {
		return metas;
	}


	public void setMetadata(List<Metadata> metas) {
		this.metas = metas;
	}


	/**
	 * This method actually makes resizable the textArea
	 * @param id
	 */
	public static native void autoSizeIt(String id, int height) /*-{
	    var text = $doc.getElementById(id);
     	var observe;
		if (window.attachEvent) {
	    	observe = function (element, event, handler) {
	        	element.attachEvent('on'+event, handler);
	    	};
		}
		else {
	    	observe = function (element, event, handler) {
	        	element.addEventListener(event, handler, false);
	    	};
		}

	    function resize () {
	        text.style.height = height+'px';
	        text.style.height = text.scrollHeight+'px';
	    }

	    function delayedResize () {
	        window.setTimeout(resize, 0);
	    }

	    observe(text, 'change',  resize);
	    observe(text, 'cut',     delayedResize);
	    observe(text, 'paste',   delayedResize);
	    observe(text, 'drop',    delayedResize);
	    observe(text, 'keydown', delayedResize);

	    text.focus();
	    text.select();
	    resize();
	}-*/;


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}
	
}
