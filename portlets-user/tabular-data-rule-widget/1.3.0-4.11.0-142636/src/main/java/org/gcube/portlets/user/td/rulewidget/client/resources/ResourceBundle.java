package org.gcube.portlets.user.td.rulewidget.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * 
 * @author "Giancarlo Panichi" <a
 *         href="mailto:g.panichi@isti.cnr.it">g.panichi@isti.cnr.it</a>
 *
 */
public interface ResourceBundle extends ClientBundle {

	public static final ResourceBundle INSTANCE = GWT
			.create(ResourceBundle.class);

	@Source("Dialog.css")
	DialogCSS dialogCSS();

	@Source("arrow-refresh_16.png")
	ImageResource refresh();

	@Source("arrow-refresh_32.png")
	ImageResource refresh32();

	@Source("magnifier.png")
	ImageResource magnifier();

	@Source("magnifier_32.png")
	ImageResource magnifier32();

	@Source("close-red.png")
	ImageResource close();

	@Source("close-red_32.png")
	ImageResource close32();

	@Source("disk.png")
	ImageResource save();

	@Source("disk_32.png")
	ImageResource save32();

	@Source("add.png")
	ImageResource add();

	@Source("add_32.png")
	ImageResource add32();

	@Source("delete.png")
	ImageResource delete();

	@Source("delete_32.png")
	ImageResource delete32();
	
	
	@Source("rule-add_32.png")
	ImageResource ruleAdd32();

	@Source("rule-add.png")
	ImageResource ruleAdd();
	
	@Source("rule-edit_32.png")
	ImageResource ruleEdit32();

	@Source("rule-edit.png")
	ImageResource ruleEdit();
	
	@Source("rule-close_32.png")
	ImageResource ruleClose32();

	@Source("rule-close.png")
	ImageResource ruleClose();

	@Source("rule-open_32.png")
	ImageResource ruleOpen32();

	@Source("rule-open.png")
	ImageResource ruleOpen();

	@Source("rule-delete.png")
	ImageResource ruleDelete();

	@Source("rule-delete_32.png")
	ImageResource ruleDelete32();

	@Source("rule-apply.png")
	ImageResource ruleApply();

	@Source("rule-apply_32.png")
	ImageResource ruleApply32();

	@Source("rule-share.png")
	ImageResource ruleShare();

	@Source("rule-share_32.png")
	ImageResource ruleShare32();

	@Source("rule-column-add.png")
	ImageResource ruleColumnAdd();
	
	@Source("rule-column-add_32.png")
	ImageResource ruleColumnAdd32();
	
	@Source("rule-column-apply.png")
	ImageResource ruleColumnApply();
	
	@Source("rule-column-apply_32.png")
	ImageResource ruleColumnApply32();
	
	@Source("rule-column-detach.png")
	ImageResource ruleColumnDetach();
	
	@Source("rule-column-detach_32.png")
	ImageResource ruleColumnDetach32();
	
	@Source("rule-table-add.png")
	ImageResource ruleTableAdd();
	
	@Source("rule-table-add_32.png")
	ImageResource ruleTableAdd32();
	
	@Source("rule-table-apply.png")
	ImageResource ruleTableApply();
	
	@Source("rule-table-apply_32.png")
	ImageResource ruleTableApply32();
	
	
	@Source("rule-tabularresource.png")
	ImageResource ruleTabularResource();
	
	@Source("rule-tabularresource_32.png")
	ImageResource ruleActive32();

	@Source("information_32.png")
	ImageResource information32();

	@Source("information.png")
	ImageResource information();
	
	@Source("column-add_32.png")
	ImageResource columnAdd32();

	@Source("column-add_24.png")
	ImageResource columnAdd24();

	@Source("column-add.png")
	ImageResource columnAdd();

	
	

}
