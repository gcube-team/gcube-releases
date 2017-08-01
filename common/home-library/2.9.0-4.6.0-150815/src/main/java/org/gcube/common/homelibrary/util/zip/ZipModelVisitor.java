/**
 * 
 */
package org.gcube.common.homelibrary.util.zip;

import java.util.List;

import org.gcube.common.homelibrary.util.IndentedVisitor;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipFolder;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipItem;
import org.gcube.common.homelibrary.util.zip.zipmodel.ZipItemType;


/**
 * A zip model visitor.
 * Visits the zip model printing the tree.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class ZipModelVisitor extends IndentedVisitor {

	/**
	 * 
	 */
	public ZipModelVisitor() {
	}


//	/**
//	 * @param items the items to visit.
//	 */
//	public void visit(List<ZipItem> items)
//	{
//		for (ZipItem item:items){
//			visitItem(item);
//		}
//	}
	
//	/**
//	 * @param item the item to visit.
//	 */
//	public void visitItem(ZipItem item)
//	{
//		println(item.toString());
//		
//		if (item.getType() == ZipItemType.FOLDER){
//			indent();
//			for (ZipItem child:((ZipFolder)item).getChildren()) visitItem(child);
//			outdent();
//		}
//	}

}
