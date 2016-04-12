package org.gcube.portlets.widgets.guidedtour.client.breadcrumb;

import java.util.ArrayList;

import org.gcube.portlets.widgets.guidedtour.client.types.ThemeColor;
import org.gcube.portlets.widgets.guidedtour.shared.TourLanguage;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class Breadcrumb extends Composite {

	private HTML panel = new HTML("");
	private ArrayList<String> stepNames;
	int stepsNo = 0;
	int width = 0;
	ThemeColor themeColor;
	/**
	 * 
	 * @param stepNames .
	 * @param width .
	 * @param themeColor the color for the current selected element
	 */
	public Breadcrumb(ArrayList<String> stepNames, int width, ThemeColor themeColor) {
		this.stepNames = stepNames;
		stepsNo = stepNames.size();
		this.width = width;
		this.themeColor = themeColor;
		panel.setWidth(""+width);
		showSelected(0);
		initWidget(panel);
	}
	/**
	 * 
	 * @param selectedIndex
	 */
	public void showSelected(int selectedIndex) {
		String selectedClass = " selected";
		if (themeColor != ThemeColor.BLUE)
			selectedClass = getThemeStyleName();
		
		String html = "<div class=\"breadcrumbs\" style=\"width:" + width + "px\"><ul>";	
		for (int j = 0; j < stepsNo; j++) {
			String className = "";
			if ((selectedIndex == j) && (j == stepsNo-1)) 	className = "final " + selectedClass;
			else { 
				if (selectedIndex == j)		className = selectedClass;
				else if (j == stepsNo-1) 	className = "final";
			}
			html += "<li class=\""+ className + "\" style=\"width:" + (width/stepsNo - 25) + "px\">";
			html += "<span>" + stepNames.get(j) + "</span></li>";
		}
		html += "</ul></div>";
		panel.setHTML(html);		
	}
	/**
	 * (ThemeColor color)
	 * @return
	 */
	private String getThemeStyleName() {
		return themeColor.toString().toLowerCase();		
	}
	
	public void switchLanguage(ArrayList<String> stepNames, int currSelected) {
		this.stepNames = stepNames;
		showSelected(currSelected);
	}
}
