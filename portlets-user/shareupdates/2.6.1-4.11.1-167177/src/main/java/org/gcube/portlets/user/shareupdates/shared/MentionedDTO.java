package org.gcube.portlets.user.shareupdates.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class MentionedDTO implements IsSerializable{
	public String id, value, type;

	public MentionedDTO() {
		super();
	}

	public MentionedDTO(String id, String value, String type) {
		super();
		this.id = id;
		this.value = value;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "MentionedDTO [id=" + id + ", value=" + value + ", type=" + type + "]";
	}
	
}
