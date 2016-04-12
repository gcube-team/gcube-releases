package org.gcube.portlets.widgets.guidedtour.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.layout.client.Layout.Layer;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
/**
 * 
 * @author Massimiliano Assante ISTI-CNR
 * 
 * @version 1.0 Feb 1st 2012
 *
 */
public class TransitionPanel extends ResizeComposite implements HasWidgets {

	private final List<Widget> widgets = new ArrayList<Widget>();
	private final LayoutPanel layoutPanel; 
	private int currentIndex = -1;
	private boolean isAnimationEnabled = true;
	/**
	 * 
	 * @param width
	 * @param height
	 * @param isAnimationEnabled
	 */
	public TransitionPanel(String width, String height, boolean isAnimationEnabled) {
		layoutPanel = new LayoutPanel();
		layoutPanel.setHeight(height);
		layoutPanel.setWidth(width);
		this.isAnimationEnabled = isAnimationEnabled;
		initWidget(layoutPanel);
	}

	public void add(IsWidget w) {
		add(w.asWidget());
	}

	public void add(Widget w) {
		widgets.remove(w);
		widgets.add(w);

		// Display the first widget added by default
		if (currentIndex < 0) {
			layoutPanel.add(w);
			currentIndex = 0;
		}
	}

	public void clear() {
		setWidget(null);
		widgets.clear();
	}

	public Widget getWidget() {
		return widgets.get(currentIndex);
	}

	public Iterator<Widget> iterator() {
		return Collections.unmodifiableList(widgets).iterator();
	}

	public boolean remove(Widget w) {
		return widgets.remove(w);
	}

	public void setWidget(IsWidget w) {
		setWidget(w.asWidget());
	}

	public void setWidget(Widget widget) {
		int newIndex = widgets.indexOf(widget);

		if (newIndex < 0) {
			newIndex = widgets.size();
			add(widget);
		}

		show(newIndex);
	}
	
	/**
	 *  Enable or disable the animation feature. When enabled, the popup will use
	 *  animated transitions when the user clicks next or prev buttons
	 * @param enable true to enable animation, false to disable
	 */
	public void setAnimationEnabled(boolean enable) {
		isAnimationEnabled = enable;
	}

	private void show(int newIndex) {
		if (newIndex == currentIndex) {
			return;
		}

		boolean fromLeft = newIndex < currentIndex;
		currentIndex = newIndex;

		Widget widget = widgets.get(newIndex);
		final Widget current = layoutPanel.getWidget(0);

		layoutPanel.remove(current);
		layoutPanel.add(widget);
		if (fromLeft) {
			layoutPanel.setWidgetLeftWidth(widget, 50, Unit.PCT, 10, Unit.PCT);
		} else 
			layoutPanel.setWidgetLeftWidth(widget, 50, Unit.PCT, 10, Unit.PCT);

		layoutPanel.forceLayout();

		layoutPanel.setWidgetLeftWidth(widget, 0, Unit.PCT, 100, Unit.PCT);
		if (isAnimationEnabled) {
			layoutPanel.animate(150, new Layout.AnimationCallback() {
				public void onAnimationComplete() {
				}

				public void onLayout(Layer layer, double progress) {
				}
			});
		}
	}
}