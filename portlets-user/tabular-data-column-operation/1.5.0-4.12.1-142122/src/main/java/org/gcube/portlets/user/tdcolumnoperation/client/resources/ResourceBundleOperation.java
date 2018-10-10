package org.gcube.portlets.user.tdcolumnoperation.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.TextResource;

/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Jun 6, 2014
 * 
 */
public interface ResourceBundleOperation extends ClientBundle {

	public static final ResourceBundleOperation INSTANCE = GWT
			.create(ResourceBundleOperation.class);

	@Source("column-merge_32.png")
	ImageResource columnMerge32();

	@Source("column-merge.png")
	ImageResource columnMerge16();

	@Source("column-split.png")
	ImageResource columnSplit16();

	@Source("column-split_32.png")
	ImageResource columnSplit32();

	@Source("help.png")
	ImageResource help();

	@Source("splitmergehelper.html")
	TextResource smHelper();

	@Source("groupbyhelper.html")
	TextResource groupbyHelper();

	@Source("add16.png")
	ImageResource add();

	@Source("delete16.png")
	ImageResource delete();

	@Source("close.gif")
	ImageResource close();
	
	@Source("table-group.png")
	ImageResource tablegroupby();
	
	@Source("time-aggregate.png")
	ImageResource timeaggregate();
	
	@Source("alert-icon.png")
	ImageResource alert();

}
