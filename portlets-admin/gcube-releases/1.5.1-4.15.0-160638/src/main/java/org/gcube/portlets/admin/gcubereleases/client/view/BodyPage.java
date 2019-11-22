/**
 * 
 */
package org.gcube.portlets.admin.gcubereleases.client.view;

import java.util.List;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

/**
 * The Class BodyPage.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 19, 2015
 */
public class BodyPage extends FlowPanel{

	private LoaderIcon loader = new LoaderIcon();
	
	/**
	 * Instantiates a new body page.
	 */
	public BodyPage() {
		this.init();
	}
	
	/**
	 * Adds the substystem view.
	 *
	 * @param substystemTitle the substystem title
	 * @param packages the packages
	 */
	public void addSubstystemView(String substystemTitle , List<org.gcube.portlets.admin.gcubereleases.shared.Package> packages){
		add(new SubsystemView(substystemTitle, packages, false));
	}
	
	/**
	 * Adds the package view.
	 *
	 * @param packages the packages
	 */
	public void addPackageView(List<org.gcube.portlets.admin.gcubereleases.shared.Package> packages){
		add(new PackageView(packages, false));
	}
	
	/**
	 * Sets the loading.
	 *
	 * @param bool the bool
	 * @param text the text
	 */
	public void setLoading(boolean bool, String text){
		loader.setVisible(bool);
		loader.setText(text);
	}

	/**
	 * Reset.
	 */
	public void reset() {
		this.clear();
		this.init();
	}
	
	/**
	 * Inits the.
	 */
	private void init(){
		addStyleName("margin-FlowPanel");
		add(loader);
		loader.setVisible(false);
	}

	/**
	 * Show error.
	 *
	 * @param label the label
	 */
	public void showError(String label) {
		FlowPanel panel = new FlowPanel();
//		panel.setPaddingLeft(20);
//		panel.setPaddingTop(20);
//		panel.setPaddingBottom(20);
//		panel.setPaddingRight(20);
		
//		panel.setMarginTop(20);
//		panel.setMarginLeft(20);
//		panel.setMarginRight(20);
//		panel.setType(PanelType.DANGER);

		String html = "<div style=\"color:red\">";
		if(label!=null && label.length()>0)
			html+=label;
//			panel.add(new HTML(label));
		else
			html+="No data";
//			panel.add(new HTML("No data"));
		html+="</div>";
		
		panel.add(new HTML(html));
		add(panel);

	}

	/**
	 * Show message.
	 *
	 * @param txt the txt
	 */
	public void showMessage(String txt) {
		FlowPanel panel = new FlowPanel();
//		panel.setPaddingLeft(20);
//		panel.setPaddingTop(20);
//		panel.setPaddingBottom(20);
//		panel.setPaddingRight(20);
//		
//		panel.setMarginTop(20);
//		panel.setMarginLeft(20);
//		panel.setMarginRight(20);
//		panel.setType(type);

		String html = "<div style=\"color:blue\">";
		if(txt!=null && txt.length()>0)
			html+=txt;
//			panel.add(new HTML(label));
		else
			html+="No data";
//			panel.add(new HTML("No data"));
		html+="</div>";
		
		panel.add(new HTML(html));
		add(panel);
		
	}
}
