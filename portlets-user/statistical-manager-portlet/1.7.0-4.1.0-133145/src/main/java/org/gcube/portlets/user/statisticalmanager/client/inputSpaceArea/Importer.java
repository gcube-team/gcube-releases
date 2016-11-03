
package org.gcube.portlets.user.statisticalmanager.client.inputSpaceArea;

import org.gcube.portlets.user.statisticalmanager.client.resources.Images;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author ceras
 *
 */
public class Importer extends TabItem {

	private TableImporter tableImporter = new TableImporter();
	private FileImporter fileImporter = new FileImporter();
	private UploadMonitor uploadMonitor = new UploadMonitor();

	/**
	 * 
	 */
	public Importer() {
		super(".: Importer");

		this.setStyleAttribute("background-color", "#FFFFFF");
		this.setIcon(Images.table());
		this.setScrollMode(Scroll.AUTO);

		this.setLayout(new FitLayout());
		HorizontalPanel hp = new HorizontalPanel();
		//tableImporter.setSize(450, 580);
		//fileImporter.setSize(450, 580);
		//uploadMonitor.setSize(450, 580);
		tableImporter.setHeight(580);
		fileImporter.setHeight(580);
		uploadMonitor.setHeight(580);
		tableImporter.setWidth(390);
		fileImporter.setWidth(380);
		uploadMonitor.setWidth(390);
	
		hp.add(tableImporter);
		hp.add(fileImporter);
		hp.add(uploadMonitor);
		this.add(hp);
	}
	
}
