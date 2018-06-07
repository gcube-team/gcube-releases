package org.gcube.data.access.bean;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Rule")
@XmlAccessorType(XmlAccessType.FIELD)
public class Rules {

	private String id;
	private String priority;
	private String service;
	private String request;
	private String workspace;
	private String layer;
	@XmlAttribute
	private GrantType grant;

	private User user = new User();
	private Group group = new Group();
	private Instance instance = new Instance();
	private Position position = new Position();
	
	public Rules(){
	}
	
	public Rules(String priority, String service, String request, String workspace, 
			String layer, String userId, String groupId, String instanceId){
		this.priority = priority;
		this.service = service;
		this.request = request;
		this.workspace = workspace;
		this.layer = layer;
		this.user.id = userId;
		this.group.id = groupId;
		this.instance.id = instanceId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getWorkspace() {
		return workspace;
	}

	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public GrantType getGrant() {
		return grant;
	}

	public void setGrant(GrantType grant) {
		this.grant = grant;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUser(String id, String name) {
		this.user.setId(id);
		this.user.setName(name);
		;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public void setGroup(String id, String name) {
		this.group.setId(id);
		this.group.setName(name);
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public void setInstance(String id, String name) {
		this.instance.setId(id);
		this.instance.setName(name);
	}
	
	public Position getPosition() {
		return position;
	}

	public void setPosition(String position, String value) {
		this.position.position = position;
		this.position.setValue(value);
		
	}

	public void setPosition(Position position) {
		this.position = position;
	}


	public static class User {
		private String id;
		private String name;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class Group {
		private String id;
		private String name;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class Instance {
		private String id;
		private String name;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	public static class Position {

		private String position;
		private String value;

		@XmlAttribute(name = "position")
		public String getPosition() {
			return position;
		}

		public void setPosition(String position) {
			this.position = position;
		}
		
		@XmlAttribute(name = "value")
		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
