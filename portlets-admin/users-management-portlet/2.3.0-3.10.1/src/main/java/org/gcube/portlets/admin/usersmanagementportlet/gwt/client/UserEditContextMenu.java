package org.gcube.portlets.admin.usersmanagementportlet.gwt.client;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.MessageBox;
import com.gwtext.client.widgets.Window;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;

/**
 * This class extends the Menu and creates a context menu with one option.
 * The "Edit Roles" item is the only one added to the menu
 * 
 * @author Panagiota Koltsida, NKUA
 *
 */
public class UserEditContextMenu extends Menu{
	
	/**
	 * 
	 * @param grid The grid that the context menu refers to
	 */
	public UserEditContextMenu(final GridPanel grid, final UsersInfoGrid referenceWidget) {
		super();
		
		Item editItem = new Item ("Edit Roles");
		editItem.setId("edit");
		editItem.addListener(new BaseItemListenerAdapter(){
			public void onClick(BaseItem item, EventObject e) {
				if(grid.getSelectionModel().getCount()>1){
					MessageBox.alert("Can only edit one user per time");
				}else{
					String fields[] = grid.getSelectionModel().getSelected().getFields();
					String username = grid.getSelectionModel().getSelected().getAsString("username");
					String rolesValues[] = new String[fields.length-UsersInfoGrid.numberOfAdditionalFields];
					String rolesNames[] = new String[fields.length-UsersInfoGrid.numberOfAdditionalFields];
					// find the values for each role
					for (int i=0; i<rolesValues.length; i++) {
						rolesNames[i] = fields[i+UsersInfoGrid.numberOfAdditionalFields];
						rolesValues[i] = grid.getSelectionModel().getSelected().getAsString(fields[i+UsersInfoGrid.numberOfAdditionalFields]);
					}
					Window form= new EditUsersRolesWindow(username, rolesNames, rolesValues, grid, referenceWidget);
					form.show();
				}
				
			}
		});
		this.addItem(editItem);
	}
	
	public void showMenu(GridPanel grid, int rowIndex, EventObject e){
		this.showAt(e.getXY());            
      }

}
