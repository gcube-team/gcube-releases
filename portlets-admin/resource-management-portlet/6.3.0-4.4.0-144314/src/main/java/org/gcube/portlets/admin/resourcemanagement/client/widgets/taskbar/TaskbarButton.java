/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: TaskbarButton.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.portlets.admin.resourcemanagement.client.widgets.taskbar;
import static com.google.gwt.query.client.GQuery.$;

import java.util.ArrayList;

import org.gcube.portlets.admin.resourcemanagement.client.utils.FWSTranslate;
import org.gcube.resourcemanagement.support.client.views.ResourceTypeDecorator;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.IconSupport;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * @author Massimiliano Assante (ISTI-CNR)
 * 
 * A selectable icon (post-it styled) and text added to the pinned resources (TaskbarWindow). 
 */
public class TaskbarButton extends Component implements IconSupport {
	private ResourceTypeDecorator type;
	private String text;
	private AbstractImagePrototype icon;
	private El iconEl;

	/**
	 * Creates a new shortcut.
	 */
	public TaskbarButton() {

	}

	/**
	 * Creates a new shortcut.
	 *
	 * @param id the shortcut id
	 * @param text the shortcut text
	 */
	public TaskbarButton(final String id, final ResourceTypeDecorator type, final String text) {
		setId(id);
		setText(text);
		this.type = type;
		
		//for IE
		final Timer t2 = new Timer() {
			@Override
			public void run() {
				getElement().getFirstChildElement().getStyle().setOpacity(0.8);
				
			}
		};
		//need to make sure the element is attached to the DOM
		Timer t = new Timer() {
			@Override
			public void run() {
				$(getElement().getFirstChildElement()).animate("opacity:'0.8'", 500);
				t2.schedule(500);
			}
		};
		
		t.schedule(100);
	}

	/**
	 * Adds a selection listener.
	 *
	 * @param listener the listener to add
	 */
	public final void addSelectionListener(final SelectionListener<? extends ComponentEvent> listener) {
		addListener(Events.Select, listener);
	}

	public final AbstractImagePrototype getIcon() {
		return icon;
	}

	/**
	 * Returns the shortcuts text.
	 *
	 * @return the text
	 */
	public final String getText() {
		return text;
	}

	@Override
	public final void onComponentEvent(final ComponentEvent ce) {
		super.onComponentEvent(ce);
		if (ce.getEventTypeInt() == Event.ONCLICK) {
			onClick(ce);
		}
	}

	/**
	 * Removes a previously added listener.
	 *
	 * @param listener the listener to be removed
	 */
	public final void removeSelectionListener(final SelectionListener<? extends ComponentEvent> listener) {
		removeListener(Events.Select, listener);
	}

	public final void setIcon(final AbstractImagePrototype icon) {
		if (rendered) {
			iconEl.setInnerHtml("");
			iconEl.appendChild((Element) icon.createElement().cast());
		}
		this.icon = icon;

	}

	public final void setIconStyle(final String icon) {
		setIcon(IconHelper.create(icon, 48, 48));

	}

	/**
	 * Sets the shortcuts text.
	 *
	 * @param text the text
	 */
	public final void setText(final String text) {
		this.text = text;
	}

	protected void onClick(final ComponentEvent ce) {
		ce.stopEvent();
		fireEvent(Events.Select, ce);
	}

	@Override
	protected final void onRender(final Element target, final int index) {
		super.onRender(target, index);



		String fontStyle = "font-size:12px; font-weight: 300; margin-top: 5px; padding: 5px; word-wrap: break-word;";
		setElement(DOM.createElement("dt"), target, index);




		final El a = el().createChild("<div class=\"iosItem\" style=\"background-color: "+ getExaBackgroundColor(type) + "; opacity: 0;\"><a href='#'></a></div>");
		iconEl = a.createChild(FWSTranslate.getFWSNameFromLabel(type.toString()));
		El txt = a.createChild("<div style=\""+fontStyle+"\" align=\"center\"></div>");

		if (txt != null) {
			txt.setInnerHtml(text);
		}

		el().updateZIndex(0);
		sinkEvents(Event.ONCLICK);
		if (icon != null) {
			setIcon(icon);
		}
	}

	private String getExaBackgroundColor(ResourceTypeDecorator type) {
		switch (type) {
		case Collection:
			return "#aa84c1";	//violet
		case RunningInstance:
			return "#8aacd2";  	//blue
		case GenericResource:
			return "#79bd84"; 	//green
		case GHN:
			return "#a4d03b"; 	//ramarro
		case RuntimeResource:
			return "#e4df00";	//yellow
		case Service:
			return "#fea500";	//orange	
		case WSResource:
			return "#f6634f";	//red
		default:
			return "#CAEBFA"; 	//azur
		}
	}
}
