/**
 * 
 */
package org.gcube.portlets.user.tdtemplate.client.resources;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Nov 22, 2013
 * 
 */
public interface ResourcesTemplate extends ClientBundle {


	
	@Source("arrow-down.gif")
	ImageResource getArrowDown();
	
	@Source("modifycolumn.png")
	ImageResource getModifyColumn();
	
	@Source("addcolumn24.png")
	ImageResource getAddColumn();
	
	@Source("deletecolumn24.png")
	ImageResource getRemoveColumn();

	@Source("edit-icon.png")
	ImageResource pencil();
	
	@Source("rule-add.png")
	ImageResource rule();

	/**
	 * @return
	 */
	@Source("close.gif")
	ImageResource delete();

	/**
	 * @return
	 */
	@Source("suggest.gif")
	ImageResource suggest();
	
	@Source("tip.png")
	ImageResource tip();
	/**
	 * @return
	 */
	@Source("thumbs-up-icon.png")
	ImageResource handsup();

	
	/**
	 * @return
	 */
	@Source("submit.png")
	ImageResource submit();
	
	@Source("helptemplate.html")
	TextResource helptemplate();
	
	@Source("newtemplate.png")
	ImageResource newtemplate();

	/**
	 * @return
	 */
	@Source("error.png")
	ImageResource error();

	/**
	 * @return
	 */
	@Source("ok-icon24.png")
	ImageResource okicon();

	/**
	 * @return
	 */
	@Source("alert-icon24.png")
	ImageResource alerticon();

	/**
	 * @return
	 */
	@Source("Info2.png")
	ImageResource info();

	/**
	 * @return
	 */
	@Source("lock2.png")
	ImageResource lock();

	/**
	 * @return
	 */
	@Source("loader2.gif")
	ImageResource loader();

	/**
	 * @return
	 */
	@Source("pencil10.gif")
	ImageResource pencil10();

	/**
	 * @return
	 */
	@Source("step1.png")
	ImageResource step1();

	/**
	 * @return
	 */
	@Source("step2.png")
	ImageResource step2();

	/**
	 * @return
	 */
	@Source("step3.png")
	ImageResource step3();

	/**
	 * @return
	 */
	@Source("filter16.png")
	ImageResource filter16();

	/**
	 * @return
	 */
	@Source("filter24.png")
	ImageResource filter24();
	
	/**
	 * @return
	 */
	@Source("flow.png")
	ImageResource flow();

	/**
	 * @return
	 */
	@Source("flow24.png")
	ImageResource flow24();
	
	/**
	 * @return
	 */
	@Source("flow24_ok.png")
	ImageResource flow24ok();

	/**
	 * @return
	 */
	@Source("close.png")
	ImageResource close();

	/**
	 * @return
	 */
	@Source("view.png")
	ImageResource view();
	
	/**
	 * @return
	 */
	@Source("template-time-aggregate.png")
	ImageResource timeaggregate();

	/**
	 * @return
	 */
	@Source("actions.png")
	ImageResource action();

	/**
	 * @return
	 */
	@Source("back.png")
	ImageResource back();

	/**
	 * @return
	 */
	@Source("template-time-aggregate24.png")
	ImageResource timeaggregate24();

	/**
	 * @return
	 */
	@Source("template-undo_24.png")
	ImageResource undo24();
	
	/**
	 * @return
	 */
	@Source("history24.png")
	ImageResource history24();

	/**
	 * @return
	 */
	@Source("legend-icon.png")
	ImageResource legend();

	/**
	 * @return
	 */
	@Source("template-time-group_24.png")
	ImageResource timeGroup();

	/**
	 * @return
	 */
	@Source("pencil24.png")
	ImageResource pencil24();

	/**
	 * @return
	 */
	@Source("template-normalize_24.png")
	ImageResource normalize24();

	/**
	 * @return
	 */
	@Source("rule-table-add_24.png")
	ImageResource ruleTableAdd();
	
	/**
	 * @return
	 */
	@Source("rule-column-add.png")
	ImageResource ruleColumnAdd();

	/**
	 * @return
	 */
	@Source("column-clone_24.png")
	ImageResource cloneColumn();
}
