package org.gcube.portlets.user.td.columnwidget.client.resources;


import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author "Giancarlo Panichi" 
 * <a href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a> 
 *
 */
public interface ResourceBundle extends ClientBundle {
	
	public static final ResourceBundle INSTANCE=GWT.create(ResourceBundle.class);
	
	@Source("Dialog.css")
	DialogCSS dialogCSS();
	
	@Source("arrow-refresh_16.png")
	ImageResource refresh();
	
	@Source("arrow-refresh_24.png")
	ImageResource refresh24();
	
	
	@Source("arrow-refresh_32.png")
	ImageResource refresh32();
	
	@Source("magnifier.png")
	ImageResource magnifier();
	
	@Source("magnifier_32.png")
	ImageResource magnifier32();
	
	@Source("column-values.png")
	ImageResource columnValue();
	
	@Source("column-values_32.png")
	ImageResource columnValue32();
	
	@Source("column-add.png")
	ImageResource columnAdd();
	
	@Source("column-add_32.png")
	ImageResource columnAdd32();
	
	@Source("column-delete.png")
	ImageResource columnDelete();
	
	@Source("column-delete_32.png")
	ImageResource columnDelete32();
	
	@Source("column-label.png")
	ImageResource columnLabel();
	
	@Source("column-label_32.png")
	ImageResource columnLabel32();
	
	@Source("column-type.png")
	ImageResource columnType();
	
	@Source("column-type_32.png")
	ImageResource columnType32();
	
	@Source("column-reorder.png")
	ImageResource columnReorder();
	
	@Source("column-reorder_32.png")
	ImageResource columnReorder32();
	
	
	@Source("column-replace.png")
	ImageResource replace();
	
	@Source("column-replace_32.png")
	ImageResource replace32();
	
	@Source("column-replace-all.png")
	ImageResource replaceAll();
	
	@Source("column-replace-all_32.png")
	ImageResource replaceAll32();
	
	
	@Source("column-replace-batch.png")
	ImageResource replaceBatch();
	
	@Source("column-replace-batch_32.png")
	ImageResource replaceBatch32();
	
	@Source("close-red.png")
	ImageResource close();
	
	@Source("close-red_32.png")
	ImageResource close32();
	
	@Source("disk.png")
	ImageResource save();
	
	@Source("disk_32.png")
	ImageResource save32();
	
	@Source("codelist-link.png")
	ImageResource codelistLink();
	
	@Source("codelist-link_24.png")
	ImageResource codelistLink24();
	
	@Source("codelist-link_32.png")
	ImageResource codelistLink32();
	
	@Source("codelist-link-break.png")
	ImageResource codelistLinkBreak();
	
	@Source("codelist-link-break_24.png")
	ImageResource codelistLinkBreak24();
	
	@Source("codelist-link-break_32.png")
	ImageResource codelistLinkBreak32();
	
	@Source("add.png")
	ImageResource add();
	
	@Source("add_32.png")
	ImageResource add32();
	
	@Source("delete.png")
	ImageResource delete();
	
	@Source("delete_32.png")
	ImageResource delete32();
	
}
 