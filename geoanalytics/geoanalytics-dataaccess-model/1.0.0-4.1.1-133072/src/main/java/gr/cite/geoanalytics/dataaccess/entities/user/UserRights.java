package gr.cite.geoanalytics.dataaccess.entities.user;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="rights")
public class UserRights
{
	private List<String> roles = new ArrayList<String>();
	private List<String> layers = new ArrayList<String>();
	private boolean locked = false;

	public List<String> getRoles()
	{
		return roles;
	}

	@XmlElementWrapper(name="roles")
	@XmlElement(name="role")
	public void setRoles(List<String> roles)
	{
		this.roles = roles;
	}

	public List<String> getLayers()
	{
		return layers;
	}

	@XmlElementWrapper(name="layers")
	@XmlElement(name="layer")
	public void setLayers(List<String> layers)
	{
		this.layers = layers;
	}
	
	public boolean isLocked()
	{
		return locked;
	}
	
	@XmlElement
	public void setLocked(boolean locked)
	{
		this.locked = locked;
	}
}
