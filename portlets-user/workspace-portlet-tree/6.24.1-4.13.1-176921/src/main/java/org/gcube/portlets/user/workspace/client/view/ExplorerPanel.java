package org.gcube.portlets.user.workspace.client.view;

import org.gcube.portlets.user.workspace.client.AppControllerExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer;
import org.gcube.portlets.user.workspace.client.ConstantsExplorer.ViewSwitchType;
import org.gcube.portlets.user.workspace.client.event.SwitchViewEvent;
import org.gcube.portlets.user.workspace.client.resources.Resources;
import org.gcube.portlets.user.workspace.client.view.tree.AsyncTreePanel;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class ExplorerPanel extends LayoutContainer {

	private Radio radioTree = null;
	private Radio radioSmartFolder = null;
	private AsyncTreePanel asycTreePanel = null;
	private final RadioGroup radioGroup = new RadioGroup();
	private ContentPanel expPanel = null;
	private ToolBar toolBar = new ToolBar();
	private Radio radioMessages;
	private Button buttBulk = new Button(ConstantsExplorer.LOADER);
	private boolean isMessagesInstanced;
	private boolean isSmartFolderInstanced;
	private boolean isOnlyTreeInstanced;


	/**
	 * Default instance an async tree to navigate user workspace
	 * @param instancingSmartFolder
	 * @param instancingMessages
	 */
	public ExplorerPanel(boolean instancingSmartFolder, boolean instancingMessages, boolean selectRootItem) {

		this.isMessagesInstanced= instancingMessages;
		this.isSmartFolderInstanced = instancingSmartFolder;

		setBorders(false);
		initRadioButtons();
		initToolBar();
		this.asycTreePanel = new AsyncTreePanel();

		this.expPanel = new ContentPanel();
		this.expPanel.setHeaderVisible(false);


//		smartFolderPanel.setVisible(false);
//		messagesPanel.setVisible(false);
		asycTreePanel.setVisible(true);
		expPanel.add(asycTreePanel);
//		expPanel.add(smartFolderPanel);
//		expPanel.add(messagesPanel);



		//BULK
		//REMOVED 2016-09-15 CHECK SVN
//		buttBulk.setIcon(Resources.getIconBulkUpdate());
//		buttBulk.setVisible(false);
//		addListnerOnBulk();

		add(expPanel);
	}


	/**
	 * Instance only tree
	 * @param instancingOnlyTree
	 */
	public ExplorerPanel(boolean instancingOnlyTree, boolean selectRootItem) {

		this.isOnlyTreeInstanced = instancingOnlyTree;

		setBorders(false);
		initRadioButtons();
		this.asycTreePanel = new AsyncTreePanel();
		this.asycTreePanel.loadRootItem(selectRootItem); //load root item
		this.expPanel = new ContentPanel();
		this.expPanel.setHeaderVisible(false);
//		asycTreePanel.setVisible(true);
		expPanel.add(asycTreePanel);
		add(expPanel);

	}

	private void addListnerOnBulk() {
		buttBulk.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
//				BulkCreatorWindow.getInstance().show();

			}
		});
	}

	public void setLoadingBulk(boolean bool){

		buttBulk.setVisible(true);

		if(!bool)
			buttBulk.setIcon(Resources.getIconLoadingOff());
		else
			buttBulk.setIcon(Resources.getIconLoading());


		toolBar.layout();
	}

	private void initRadioButtons() {

		this.radioTree = new Radio();
		this.radioSmartFolder = new Radio();
		this.radioMessages = new Radio();

//		radioTree.setId(ConstantsExplorer.TREE);
		radioTree.setBoxLabel(ConstantsExplorer.TREE);
		radioTree.setValue(true);
		radioTree.setValueAttribute(ConstantsExplorer.TREE);
		radioGroup.add(radioTree);

//		radioSmartFolder.setId(ConstantsExplorer.SMARTFOLDER);

		if(isSmartFolderInstanced){
			radioSmartFolder.setBoxLabel(ConstantsExplorer.SMARTFOLDER);
			radioSmartFolder.setValueAttribute(ConstantsExplorer.SMARTFOLDER);

			radioGroup.add(radioSmartFolder);
		}

		if(isMessagesInstanced){
			radioMessages.setBoxLabel(ConstantsExplorer.MESSAGES);
			radioMessages.setValueAttribute(ConstantsExplorer.MESSAGES);
			radioGroup.add(radioMessages);
		}

//		radioGroup.setFieldLabel("Afecto");

		radioGroup.setStyleAttribute("margin-left", "20px");

		radioGroup.addListener(Events.Change, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {

				Radio selectedRadio = radioGroup.getValue();
//				System.out.println("radio value:" +selectedRadio.getValueAttribute());
//				System.out.println("id" + selectedRadio.getId());
				if (selectedRadio.getValueAttribute().compareTo(ConstantsExplorer.SMARTFOLDER.toString()) == 0)
					switchView(ViewSwitchType.SmartFolder);
				else if(selectedRadio.getValueAttribute().compareTo(ConstantsExplorer.TREE.toString()) == 0)
						switchView(ViewSwitchType.Tree);
					else
						switchView(ViewSwitchType.Messages);
			}
		});

	}

	private void initToolBar(){

	    toolBar.add(radioGroup);
//	    toolBar.add(buttBulk);
	}

	private void switchView(ViewSwitchType type) {

		if (type.compareTo(ViewSwitchType.Tree)==0) {



			asycTreePanel.setVisible(true);
			AppControllerExplorer.getEventBus().fireEvent(new SwitchViewEvent(ViewSwitchType.Tree));
//			asycTreePanel.setSearch(false);

		} else if (type.compareTo(ViewSwitchType.SmartFolder)==0){
			asycTreePanel.setVisible(false);




			AppControllerExplorer.getEventBus().fireEvent(new SwitchViewEvent(ViewSwitchType.SmartFolder));

			// this.expPanel.setVisible(shortuctsPanel.getElement(), false);
			// this.expPanel.setVisible(asycTreePanel.getElement(), true);
		}
		else{

			asycTreePanel.setVisible(false);


		}

	}

	public AsyncTreePanel getAsycTreePanel() {
		return asycTreePanel;
	}


	public boolean isSmartFolderInstanced() {
		return isSmartFolderInstanced;
	}


	public void setMessagesInstanced(boolean isMessagesInstanced) {
		this.isMessagesInstanced = isMessagesInstanced;
	}


	public void setSmartFolderInstanced(boolean isSmartFolderInstanced) {
		this.isSmartFolderInstanced = isSmartFolderInstanced;
	}


	public boolean isOnlyTreeInstanced() {
		return isOnlyTreeInstanced;
	}


	public void setOnlyTreeInstanced(boolean isOnlyTreeInstanced) {
		this.isOnlyTreeInstanced = isOnlyTreeInstanced;
	}
}
