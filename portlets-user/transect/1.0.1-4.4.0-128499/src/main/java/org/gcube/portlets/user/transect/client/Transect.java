package org.gcube.portlets.user.transect.client;

import org.gcube.portlets.user.transect.client.commands.ChartCommands;
import org.gxt.adapters.highcharts.codegen.sections.options.types.ChartType;

import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Transect implements EntryPoint {
	public static final String CONTAINER_DIV = "MyUniqueDIV";
	final ChartType types[] = new ChartType[] {
			new ChartType("spline"),
			new ChartType("column"),
			new ChartType("areaspline"),
			new ChartType("area"),
			new ChartType("line")
	};

	public void onModuleLoad() {
		Viewport vp = new Viewport();
		vp.setLayout(new FitLayout());
		ChartCommands.createHighChart(vp);
		RootPanel.get(CONTAINER_DIV).add(vp);
	}
}
