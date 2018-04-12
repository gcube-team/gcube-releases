package org.gcube.portlets.user.td.chartswidget.client.help;

import org.gcube.portlets.user.td.widgetcommonevent.shared.charts.ChartType;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * 
 * @author giancarlo email: <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public class ChartHelp extends SimpleContainer {

	public ChartHelp() {
		super();
		init();
	}

	public void init() {
		HTML suggestion = new HTML(
				"<p style='padding: 20px 10px;margin:auto;'></p>");
		add(suggestion);
	}

	public void updateChartType(ChartType type) {
		HTML suggestion;
		clear();
		switch (type) {
		case TopRating:
			suggestion = new HTML(
					"<p style='padding: 20px 10px;margin:auto;'><span style='font-weight: bold;'>Top Rating:</span> given a dataset, "
							+ "crete a chart that has dimension values, with aggregate measure on y-axis and a time-dimension on x-axis</p>");
			add(suggestion);
			break;
		default:
			suggestion = new HTML(
					"<p style='padding: 20px 10px;margin:auto;'></p>");
			add(suggestion);
			break;

		}

		forceLayout();
	}

}
