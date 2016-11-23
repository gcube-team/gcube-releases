/**
 * 
 */
package org.gcube.portlets.user.speciesdiscovery.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public interface Resources extends ClientBundle {
	
	public Resources INSTANCE = GWT.create(Resources.class);

	@Source("loading-balls.gif")
	ImageResource loadingBalls();
	
	@Source("detailsview.png")
	ImageResource getDescriptiveSpeciesIcon();
	
	@Source("gridview.png")
	ImageResource getGridSpeciesIcon();
	
	@Source("imagesGrid.png")
	ImageResource getGridImagesIcon();
	
	@Source("collapse.png")
	ImageResource getCollapse();
	
	@Source("expand.png")
	ImageResource getExpand();
	
	@Source("Toggle.js")
	TextResource getToggleJavaScript();
	
	@Source("nopicavailable.png")
	ImageResource getNoPictureAvailable();
	
	@Source("show_products.png")
	ImageResource getShowProducts();
	
	@Source("save_products.png")
	ImageResource getSaveProducts();
	
	@Source("save_products24.png")
	ImageResource getSaveProducts24();
	
	@Source("gis_products.png")
	ImageResource getGisProducts();
	
//	@Source("arrow-down.png")
//	ImageResource getArrowDown();
//	
//	@Source("arrow-right.png")
//	ImageResource getArrowRight();
	
	@Source("arrow-down.gif")
	ImageResource getArrowDown();
	
	@Source("arrow-right.gif")
	ImageResource getArrowRight();
	
	
	@Source("arrow-turn.png")
	ImageResource getArrowTurn();
	
	@Source("gbif.gif")
	ImageResource getGbif();
	
	@Source("delete.png")
	ImageResource getDelete();
	
//	@Source("order_alphabetical_asc.gif")
	@Source("sortascending.gif")
	ImageResource getSortIcon();
	
	@Source("info-icon.png")
	ImageResource getInfoIcon();
	
//	@Source("help.jpeg")
//	ImageResource getHelpIcon();
	
	@Source("question-mark.gif")
	ImageResource getHelpIcon();
	
	@Source("checkyes.png")
	ImageResource getCheckYes();
	
	@Source("checkno.png")
	ImageResource getCheckNo();
	
	@Source("gear.png")
	ImageResource getReSubmit();
	
	@Source("credits.png")
	ImageResource getCredits();
	
	@Source("image-notfound.png")
	ImageResource getImageNotFound();
	
	@Source("getSynonyms.png")
	ImageResource getSynonyms();
	
	@Source("search-icon.png")
	ImageResource getSearch();

	@Source("occurrence/blueplace.png")
	ImageResource getBluePlace();
	
	@Source("occurrence/blueplace16px.png")
	ImageResource getBluePlace16px();
	
	@Source("occurrence/blueplace1.png")
	ImageResource getBluePlace1();
	
	@Source("occurrence/blueplace2.png")
	ImageResource getBluePlace2();
	
	@Source("occurrence/blueplace3.png")
	ImageResource getBluePlace3();
	
	@Source("occurrence/blueplace4.png")
	ImageResource getBluePlace4();
	
	@Source("occurrence/blueplace4+.png")
	ImageResource getBluePlace4More();
	
	@Source("taxonomy/taxonomy.png")
	ImageResource getTaxonomy();
	
	@Source("taxonomy/taxonomy16px.png")
	ImageResource getTaxonomy16px();
	
	@Source("taxonomy/taxonomy1.png")
	ImageResource getTaxonomy1();
	
	@Source("taxonomy/taxonomy2.png")
	ImageResource getTaxonomy2();
	
	@Source("taxonomy/taxonomy3.png")
	ImageResource getTaxonomy3();
	
	@Source("taxonomy/taxonomy4.png")
	ImageResource getTaxonomy4();
	
	@Source("taxonomy/taxonomy4More.png")
	ImageResource getTaxonomy4More();

	@Source("refresh.gif")
	ImageResource getRefresh();
	
	@Source("reload-icon.png")
	ImageResource getReload();
	
	@Source("checkbox-empty.png")
	ImageResource getCheckBoxEmpty();
	
	@Source("checkbox-full.png")
	ImageResource getCheckBoxFull();
	
	@Source("select-icon.png")
	ImageResource getCheckSelected();
	
	@Source("datailsWindow.png")
	ImageResource getDetailsWindow();
	
	@Source("attention.png")
	ImageResource getAttention();
	
	@Source("help.html")
	TextResource help();


}
