/**
 * 
 */
package org.gcube.portlets.user.workspace.client.workspace.folder.item;

import java.util.Date;

import org.gcube.portlets.user.workspace.client.workspace.GWTProperties;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceFolder;
import org.gcube.portlets.user.workspace.client.workspace.GWTWorkspaceItemAction;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItem;
import org.gcube.portlets.user.workspace.client.workspace.folder.GWTFolderItemType;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * @author Federico De Faveri defaveriAtisti.cnr.it
 *
 */
public class GWTAquaMapsItem extends GWTFolderItem implements IsSerializable{
	
	protected String mapName;
	protected String mapType;
	protected String author;
	protected int numberOfSpecies;
	protected String boundingBox;
	protected float psoThreshold;
	protected int numberOfGeneratedImages;
	
	protected GWTAquaMapsItem()
	{}
	
	
	public GWTAquaMapsItem(String id, String name, String description, String owner, Date creationTime, GWTProperties properties, Date lastModificationTime,
			GWTWorkspaceItemAction lastAction, GWTWorkspaceFolder parent, long length, String mapName, String mapType, String author, int numberOfSpecies,
			String boundingBox, float psoThreashold, int numberOfGeneratedImages) {
		super(id, name, description, owner, creationTime, properties, lastModificationTime, lastAction, parent, length);
		this.mapName = mapName;
		this.mapType = mapType;
		this.author = author;
		this.numberOfSpecies = numberOfSpecies;
		this.boundingBox = boundingBox;
		this.psoThreshold = psoThreashold;
		this.numberOfGeneratedImages = numberOfGeneratedImages;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public GWTFolderItemType getFolderItemType() {
		return GWTFolderItemType.AQUAMAPS_ITEM;
	}

	/**
	 * @return the map name.
	 */
	public String getMapName() {
		return mapName;
	}

	/**
	 * @return the map type.
	 */
	public String getMapType() {
		return mapType;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @return the numberOfSpecies
	 */
	public int getNumberOfSpecies() {
		return numberOfSpecies;
	}

	/**
	 * @return the boundingBox
	 */
	public String getBoundingBox() {
		return boundingBox;
	}

	/**
	 * @return the psoThreshold
	 */
	public float getPsoThreshold() {
		return psoThreshold;
	}

	/**
	 * @return the numberOfGeneratedImages
	 */
	public int getNumberOfGeneratedImages() {
		return numberOfGeneratedImages;
	}

}
