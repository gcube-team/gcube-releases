package org.gcube.portlets.user.workspace;

import java.util.List;

import org.gcube.portlets.user.workspace.client.interfaces.GXTFolderItemTypeEnum;
import org.gcube.portlets.user.workspace.client.model.FileModel;

import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.dnd.Insert;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.store.TreeStoreModel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.Element;
  
/**
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 *
 */
public class BasicDNDExample extends LayoutContainer {  
  
  @Override  
  protected void onRender(Element parent, int index) {  
    super.onRender(parent, index);  
      
//    El.fly(parent).addStyleName("dnd-example");  
      
    HorizontalPanel hp = new HorizontalPanel();  
    hp.setSpacing(10);  
  
    final LayoutContainer container = new LayoutContainer();  
    container.setLayoutOnChange(true);  
    container.setBorders(true);  
    container.setSize(200, 200);  
  
    DropTarget target = new DropTarget(container) {  
      @Override  
      protected void onDragDrop(DNDEvent event) {  

        super.onDragDrop(event); 

		if(event.getData() != null){
			
			List<TreeStoreModel> listItemsSource =  event.getData();
			
			System.out.println("Number of move " + listItemsSource.size());
			
			FileModel sourceFileModel = null; //for print
			
			for(TreeStoreModel itemSource : listItemsSource){

				sourceFileModel = (FileModel) itemSource.getModel();

				if(sourceFileModel.getParentFileModel()!=null)
					
					System.out.println("Source Name " + sourceFileModel.getName() + " id " + sourceFileModel.getIdentifier() + " end drag " + " Parent Name: " + sourceFileModel.getParentFileModel().getName() + "id " + sourceFileModel.getParentFileModel().getIdentifier());
				else
					System.out.println("Source Name " + sourceFileModel.getName() + " id " + sourceFileModel.getIdentifier() + " end drag ");
				
				System.out.println("Child count: " + itemSource.getChildCount());
				
				container.add(new Html(sourceFileModel.getName()));
				
			}
		}
      } 
      
     
      
		@Override
	    protected void showFeedback(DNDEvent event) {
	        if (!isValidDropTarget(event)) {
	            Insert.get().hide();
	            event.getStatus().setStatus(false);
	            return;
	        }
	        super.showFeedback(event);
	    }
      
  	 @SuppressWarnings("unchecked")
  	 private boolean isValidDropTarget(DNDEvent event) {
	        TreePanel<FileModel> source = (TreePanel<FileModel>) event.getDragSource().getComponent();
	        List<FileModel> selection = source.getSelectionModel().getSelection();

	        for (FileModel model : selection) {
	                // check the "model" against "zone" and return false
	                // if "zone" is not a valid drop target for "model", otherwise check the next "model"
	                // example:
	        	
	        	if(model.getGXTFolderItemType()!=null){
	        	
	                if (model.getGXTFolderItemType().equals(GXTFolderItemTypeEnum.EXTERNAL_IMAGE) || model.getGXTFolderItemType().equals(GXTFolderItemTypeEnum.IMAGE_DOCUMENT)) 
	                	return true;
	                
	                if (model.getGXTFolderItemType().equals(GXTFolderItemTypeEnum.TIME_SERIES)) 
	                	return true;
	                
//		                if(source.getStore().getParent(model) == target.getModel())
//		                	return false;
	        	}
	        }

	        return false;
	    }
    };  

    //IMPORTANT
    target.setOperation(Operation.COPY);
  
    final LayoutContainer sourceContainer = new LayoutContainer();  
    sourceContainer.setLayoutOnChange(true);  
    sourceContainer.setWidth(100);  
    
    hp.add(container);  
    hp.add(sourceContainer);  
    add(hp);  
  }  
  
}
