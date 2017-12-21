/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.templatecreator.view;

import org.gcube.portlets.user.tdtemplate.client.TdTemplateController;
import org.gcube.portlets.user.tdtemplate.client.ZIndexReference;
import org.gcube.portlets.user.tdtemplate.client.resources.TdTemplateAbstractResources;
import org.gcube.portlets.user.tdtemplate.shared.TdColumnDefinition;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Apr 14, 2015
 */
public class EditableLabelColumnDefinitionView {

	private HorizontalPanel hpHeader;
	private Image imgEdit;
	private final int OFFSET_ZINDEX = 50;
	private Text textLabel;

	/**
	 * 
	 */
	public EditableLabelColumnDefinitionView(int zIndex) {

		hpHeader = new HorizontalPanel();
//		hpHeader.setWidth("50%");
		hpHeader.setStyleAttribute("margin", "0 auto");
		hpHeader.setStyleAttribute("padding", "10px");
		hpHeader.setHorizontalAlign(HorizontalAlignment.CENTER);
		hpHeader.setVerticalAlign(VerticalAlignment.MIDDLE);
		
		imgEdit = TdTemplateAbstractResources.pencil24().createImage();
		imgEdit.addStyleName("handOnHover");
		imgEdit.setTitle("Edit Label");
		imgEdit.getElement().getStyle().setMarginLeft(5, Unit.PX);
	}
	
	public void updateTextLabel(String label){
		
		if(textLabel==null)
			textLabel = new Text();
		
		String cutLable = cutLongLabel(label);
		textLabel.setText(cutLable);
		textLabel.setTitle(label);
	}
	
	public HorizontalPanel getEditableLabelPanel(final ColumnDefinitionView columnDef, final ZIndexReference zIndex){
		
		if(columnDef.getColumnName()==null || columnDef.getColumnName().isEmpty())
			columnDef.setColumnHeaderValue("Column "+(columnDef.getColumnIndex()+1));
		
		String label = cutLongLabel(columnDef.getColumnName());
		textLabel = new Text(label);
		textLabel.setStyleAttribute("margin-left", "2px");
		textLabel.setTitle(columnDef.getColumnName());
		
		imgEdit.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final ChangeLabelDialog cld = new ChangeLabelDialog("Set new label", columnDef.getColumnName());
				cld.getElement().getStyle().setZIndex(zIndex.getZIndex()+OFFSET_ZINDEX);
				cld.showRelativeTo(imgEdit);
				
				cld.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if(cld.isValidHide()){
							
							updateLabelServerSide(columnDef, cld, textLabel, columnDef.getColumnIndex());
//							columnDef.setColumnHeaderValue(cld.getNewLabel());
//							String label = cutLongLabel(columnDef.getColumnName());
//							textLabel.setText(label);
//							textLabel.setTitle(columnDef.getColumnName());
//							hpHeader.layout(true);
						}
					}
				});
			}
		});
		
		hpHeader.add(imgEdit);
		hpHeader.add(textLabel);
		
		return hpHeader;
	}
	
	
	public void setEditableVisible(boolean bool){
		imgEdit.setVisible(bool);
	}
	
	private String cutLongLabel(String label){
		
		if(label.length()>8)
			return label.substring(0, 8)+"...";
		
		return label;
	}

	/**
	 * @param columnDefined
	 * @return 
	 */
	public HorizontalPanel getEditableLabelPanel(final TdColumnDefinition columnDefined, final ZIndexReference zIndex) {
		
		
		if(columnDefined.getColumnName()==null || columnDefined.getColumnName().isEmpty())
			columnDefined.setColumnName("Column "+(columnDefined.getIndex()+1));
		
		String label = cutLongLabel(columnDefined.getColumnName());
		textLabel = new Text(label);
		textLabel.setStyleAttribute("margin-left", "2px");
		textLabel.setTitle(columnDefined.getColumnName());

		imgEdit.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final ChangeLabelDialog cld = new ChangeLabelDialog("Set new label", columnDefined.getColumnName());
				cld.getElement().getStyle().setZIndex(zIndex.getZIndex()+OFFSET_ZINDEX);
				cld.showRelativeTo(imgEdit);
				
				cld.addCloseHandler(new CloseHandler<PopupPanel>() {
					
					@Override
					public void onClose(CloseEvent<PopupPanel> event) {
						if(cld.isValidHide()){
							
							updateLabelServerSide(columnDefined, cld, textLabel, columnDefined.getIndex());
//							columnDefined.setColumnName(cld.getNewLabel());
////							textLabel.setText(columnDefined.getColumnName());
////							
//							String label = cutLongLabel(columnDefined.getColumnName());
//							textLabel.setText(label);
//							textLabel.setTitle(columnDefined.getColumnName());
//							
//							hpHeader.layout(true);
						}
					}
				});
			}
		});
		

		hpHeader.add(imgEdit);
		hpHeader.add(textLabel);
		
		return hpHeader;
	}
	
	
	private void updateLabelServerSide(final ColumnDefinitionView columnDefined, final ChangeLabelDialog cld, final Text textLabel , int columnIndex){
		
		TdTemplateController.tdTemplateServiceAsync.changeLabel(columnIndex, cld.getNewLabel(), new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				if(!result)
					return;
				
				columnDefined.setColumnHeaderValue(cld.getNewLabel());
				/*String label = cutLongLabel(columnDefined.getColumnName());
				textLabel.setText(label);
				textLabel.setTitle(columnDefined.getColumnName());
				*/
				hpHeader.layout(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Error", "Change Label error", null);
				
			}
		});
	}
	
	private void updateLabelServerSide(final TdColumnDefinition columnDefined, final ChangeLabelDialog cld, final Text textLabel , int columnIndex){
		
		TdTemplateController.tdTemplateServiceAsync.changeLabel(columnIndex, cld.getNewLabel(), new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				if(!result)
					return;
				
				columnDefined.setColumnName(cld.getNewLabel());
				String label = cutLongLabel(columnDefined.getColumnName());
				textLabel.setText(label);
				textLabel.setTitle(columnDefined.getColumnName());
				hpHeader.layout(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Error", "Change Label error", null);
				
			}
		});
	}
}
