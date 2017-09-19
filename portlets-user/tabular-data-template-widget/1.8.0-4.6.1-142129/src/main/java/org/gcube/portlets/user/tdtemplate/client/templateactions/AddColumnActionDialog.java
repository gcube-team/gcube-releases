package org.gcube.portlets.user.tdtemplate.client.templateactions;

import java.util.List;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateConstants;
import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.event.operation.AddColumnOperationEvent;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.ColumnDefinitionView;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.TemplateSwitcherInteface;
import org.gcube.portlets.user.tdtemplate.client.templatecreator.view.external.AddExpressionDialogManager;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;
import org.gcube.portlets.user.tdtemplate.shared.TemplateExpression;
import org.gcube.portlets.user.tdtemplateoperation.shared.ServerObjectId;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The Class AddColumnActionDialog.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 10, 2014
 */
public class AddColumnActionDialog extends Window {
	protected ServerObjectId srId;
	protected EventBus eventBus;
	protected AddColumnAction addColumnAction;
	protected AddColumnActionDialog INSTANCE = this;
	private Button buttonExpression;
	private List<TdColumnDefinition> otherColumns;
	private AddExpressionDialogManager addExpressionDlg;
	private ToolBar bar = new ToolBar();
	private Button confirmButton;
	/**
	 * Instantiates a new adds the column action dialog.
	 *
	 * @param srId the sr id
	 * @param actionsBus the actions bus
	 * @param switcherInterface the switcher interface
	 * @param controller the controller
	 * @param columnIndex the column index
	 * @param otherColumns the other columns
	 */
	public AddColumnActionDialog(ServerObjectId srId, EventBus actionsBus, TemplateSwitcherInteface switcherInterface, TdTemplateController controller, int columnIndex, List<TdColumnDefinition> otherColumns) {
		this.srId = srId;
		this.eventBus=actionsBus;
		this.otherColumns = otherColumns;
		this.setHeading("Add Column");
//		setBodyBorder(false);
		this.setLayout(new FitLayout());
		try {
			addColumnAction = new AddColumnAction(switcherInterface, controller, columnIndex, actionsBus);		
			add(addColumnAction.getPanel());
			initInitialValue();
			initConfirm();
			bar.add(buttonExpression);
			bar.add(new FillToolItem());
			bar.add(confirmButton);
			this.setBottomComponent(bar);
			this.layout(true);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

//		setButtonExpressionEnabled(false);
	}
	
	/**
	 * 
	 */
	private void initInitialValue() {

		buttonExpression = new Button("Set Value");
//		buttonExpression.setArrowAlign(ButtonArrowAlign.RIGHT);
		
		buttonExpression.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
//				buttonExpression.getMenu().show();
				
				if(!addColumnAction.isValidAdd()){
					MessageBox.alert("Attention!", "Set before the colum type", null).addCallback(new Listener<MessageBoxEvent>() {
						
						@Override
						public void handleEvent(MessageBoxEvent be) {
						}
					});
				}else{
					
					List<ColumnDefinitionView> columns = addColumnAction.getAddColumnTemplatePanel().getColumnsDefined();
					if(columns.size()>0){
						ColumnDefinitionView column = columns.get(0);
						TdColumnDefinition col = TdTemplateController.createTdColumnDefinitionFromView(column);
						addExpressionDlg = new AddExpressionDialogManager(col, otherColumns, eventBus, addColumnAction.getAddColumnTemplatePanel());
						addExpressionDlg.showDialog();
					}	
				}	
				
			}
		});
		
		Menu menu = new Menu();
		
		MenuItem add = new MenuItem("Set Value");
		add.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				
				if(!addColumnAction.isValidAdd()){
					MessageBox.alert("Attention!", "Set before the colum type", null).addCallback(new Listener<MessageBoxEvent>() {
						
						@Override
						public void handleEvent(MessageBoxEvent be) {
						}
					});
				}else{
					
					List<ColumnDefinitionView> columns = addColumnAction.getAddColumnTemplatePanel().getColumnsDefined();
					if(columns.size()>0){
						ColumnDefinitionView column = columns.get(0);
						TdColumnDefinition col = TdTemplateController.createTdColumnDefinitionFromView(column);
						addExpressionDlg = new AddExpressionDialogManager(col, otherColumns, eventBus, addColumnAction.getAddColumnTemplatePanel());
						addExpressionDlg.showDialog();
					}	
				}	
			}
		
		});
		
		MenuItem remove = new MenuItem("Remove");
		
		remove.addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				if(addExpressionDlg!=null)
					addExpressionDlg.resetTemplateExpression();
				
			}
		});
		
//		menu.add(add);
//		menu.add(remove);
//		buttonExpression.setMenu(menu);
	}
	
	private void setButtonExpressionEnabled(boolean bool){
		buttonExpression.setEnabled(bool);
	}

	public void initConfirm(){

		confirmButton = new Button("Confirm");
		confirmButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!addColumnAction.isValidAdd()){
					MessageBox.alert("Attention!", TdTemplateConstants.PLEASE_SET_THE_COLUMN_TYPE, null);
				}else if(addExpressionDlg==null || addExpressionDlg.getReplaceExpression()==null){
					MessageBox.alert("Attention!", "You must set a value!", null);
				}else{
					
					List<ColumnDefinitionView> columns = addColumnAction.getAddColumnTemplatePanel().getColumnsDefined();

					if(columns.size()>0){
						ColumnDefinitionView column = columns.get(0);
						TemplateExpression expression = null;
						if(addExpressionDlg!=null)
							expression = addExpressionDlg.getReplaceExpression();
						
						eventBus.fireEvent(new AddColumnOperationEvent(column, expression));
					}	
					
					INSTANCE.hide();
				}
			}
		});
	}

	/**
	 * Show.
	 *
	 * @param width the width
	 * @param height the height
	 * @param modal the modal
	 */
	public void show(int width, int height, boolean modal){
		setSize(width, height);
		setModal(false);
		show();
	}
	
	/**
	 * 
	 */
	public void setColumnHeader(String header) {
		addColumnAction.setColumnHeader(header);
	}
	
	public void setVisibleAddRule(boolean b) {
		addColumnAction.setVisibleAddRule(b);
	}
}
