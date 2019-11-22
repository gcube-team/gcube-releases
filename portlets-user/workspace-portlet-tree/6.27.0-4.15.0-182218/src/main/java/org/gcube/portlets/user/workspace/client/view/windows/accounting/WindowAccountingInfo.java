package org.gcube.portlets.user.workspace.client.view.windows.accounting;

import java.util.List;

import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.model.FileModel;
import org.gcube.portlets.user.workspace.shared.accounting.GxtAccountingField;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;


/**
 * The Class WindowAccountingInfo.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
 * Nov 10, 2015
 */
public class WindowAccountingInfo extends Window {

	private List<GxtAccountingField> accountingsFields;
	private AccoutingInfoContainer accountingsContainers;

	/**
	 * Instantiates a new window accounting info.
	 */
	public WindowAccountingInfo() {
		initAccounting();
	}

	/**
	 * Instantiates a new window accounting info.
	 *
	 * @param fileModel the file model
	 * @param title the title
	 */
	public WindowAccountingInfo(FileModel fileModel, String title) {
		initAccounting();
	    setIcon(fileModel.getAbstractPrototypeIcon());
	    setHeading(title);
	}
	
	/**
	 * Adds the resize listner.
	 */
	public void addResizeListner(){
		
		 this.addListener(Events.Resize, new Listener<WindowEvent>() {

	        @Override
	        public void handleEvent(WindowEvent we )
	        {
	        	
	        	if(accountingsContainers!=null){
//		        	System.out.println("Size in event: " + we.getWidth() + "x" + we.getHeight() );
//	        		accountingsContainers.setPanelSize(we.getWidth()-14, we.getHeight()-30);
	        	}
	        }

		 });
	}

	/**
	 * Instantiates a new window accounting info.
	 *
	 * @param accountingsFields the accountings fields
	 */
	public WindowAccountingInfo(List<GxtAccountingField> accountingsFields) {
		updateInfoContainer(accountingsFields);
	}

	/**
	 * Inits the accounting.
	 */
	private void initAccounting() {
		setModal(true);
		setLayout(new FitLayout());
		setSize(700, 350);
		setResizable(true);
		setMaximizable(true);
//		setCollapsible(true);
		this.accountingsContainers = new AccoutingInfoContainer();
		add(accountingsContainers);
	}

	/**
	 * Sets the window title.
	 *
	 * @param title the new window title
	 */
	public void setWindowTitle(String title) {
		this.setHeading(title);

	}
	
	

	/**
	 * Update info container.
	 *
	 * @param accountingsFields the accountings fields
	 */
	public void updateInfoContainer(List<GxtAccountingField> accountingsFields) {

		this.accountingsContainers.resetStore();
		this.accountingsFields = accountingsFields;
		this.accountingsContainers.updateListAccounting(accountingsFields);
	}

	/**
	 * Gets the accountings fields.
	 *
	 * @return the accountings fields
	 */
	public List<GxtAccountingField> getAccountingsFields() {
		return accountingsFields;
	}
	
	/**
	 * Mask accounting info.
	 *
	 * @param bool the bool
	 */
	public void maskAccountingInfo(boolean bool){
		
		if(bool)
			this.mask(ConstantsExplorer.LOADING, ConstantsExplorer.LOADINGSTYLE);
		else
			this.unmask();
	}
}
