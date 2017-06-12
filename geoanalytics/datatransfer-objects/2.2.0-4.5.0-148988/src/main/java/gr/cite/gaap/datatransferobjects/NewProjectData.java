package gr.cite.gaap.datatransferobjects;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NewProjectData {
	private static Logger logger = LoggerFactory.getLogger(NewProjectData.class);
	private String[] users;
	UsersAndRights[] userRights;
	TheCoords coords;
	TheLayers layers;
	NameAndDescriptionObject nameAndDescriptionObject;
	UserinfoObject userinfoObject;
	UUID oldprojectId = null;
	
	

	public NewProjectData() {
		super();
		logger.trace("Initialized default contructor for NewProjectData");
	}

	public UsersAndRights[] getUserRights() {
		return userRights;
	}

	public void setUserRights(UsersAndRights[] userRights) {
		this.userRights = userRights;
	}
	public UUID getOldprojectId() {
		return oldprojectId;
	}

	public void setOldprojectId(UUID oldprojectId) {
		this.oldprojectId = oldprojectId;
	}

	public UserinfoObject getUserinfoObject() {
		return userinfoObject;
	}

	public void setUserinfoObject(UserinfoObject userinfoObject) {
		this.userinfoObject = userinfoObject;
	}
	
	public String[] getUsers() {
		return users;
	}
	public void setUsers(String[] users) {
		this.users = users;
	}
	public TheCoords getCoords() {
		return coords;
	}
	public void setCoords(TheCoords coords) {
		this.coords = coords;
	}
	public TheLayers getLayers() {
		return layers;
	}
	public void setLayers(TheLayers layers) {
		this.layers = layers;
	}
	public NameAndDescriptionObject getNameAndDescriptionObject() {
		return nameAndDescriptionObject;
	}
	public void setNameAndDescriptionObject(NameAndDescriptionObject nameAndDescriptionObject) {
		this.nameAndDescriptionObject = nameAndDescriptionObject;
	}
}