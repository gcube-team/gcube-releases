package gr.cite.gaap.datatransferobjects;

import java.util.UUID;

public class NewProjectData {
	private String[] users;
	TheCoords coords;;
	TheLayers layers;
	NameAndDescriptionObject nameAndDescriptionObject;
	UserinfoObject userinfoObject;
	UUID oldprojectId = null;

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