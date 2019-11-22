package org.apache.jackrabbit.j2ee.workspacemanager.search;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;

import org.gcube.common.homelibary.model.items.SearchItemDelegate;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;

public class JCRSearchFolderItem extends JCRSearchFolder {

	protected static final String CONTENT			= "jcr:content";
	protected static final String DATA 	  			= "jcr:data";
	private static final String  MIME_TYPE         	= "jcr:mimeType";
	private static final String SIZE				= "hl:size";

	long size;
	String mimeType;
	String folderItemType;


	public JCRSearchFolderItem(Node node, String itemName) throws RepositoryException  {
		super(node, itemName);

		this.folderItemType = node.getPrimaryNodeType().getName();

		String[] nameGlobs = {MIME_TYPE, SIZE};

		try {

			PropertyIterator properties = node.getNode(CONTENT).getProperties(nameGlobs);
			while (properties.hasNext()){
				Property propery = properties.nextProperty();
				switch (propery.getName()) {

				case MIME_TYPE:
					this.mimeType = propery.getString();
					break;

				case SIZE:
					this.size = propery.getLong();
					break;

				default:
					break;
				}

			}

		} catch (RepositoryException e) {

		}

	}


	public SearchItemDelegate getSearchItemDelegate() {

		super.getSearchItemDelegate();

		item.setType(WorkspaceItemType.FOLDER_ITEM);
		item.setMimeType(mimeType);
		item.setSize(size);
//		item.setFolderItemType(folderItemType);
		return item;
	}



}
