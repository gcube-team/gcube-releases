package org.gcube.common.couchdb.connector;

import java.util.UUID;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public abstract class Entity {

	private String _id;
	private String _rev;
	
	public Entity(){
		this._id = UUID.randomUUID().toString();
	}
		
	public Entity(String id) {
		this._id = id;
	}
	
	@JsonIgnore
	public String get_id() {
		return _id;
	}

	@JsonProperty("_id")
	protected void set_id(String _id) {
		this._id = _id;
	}

	@JsonIgnore
	public String get_rev() {
		return _rev;
	}

	@JsonProperty("_rev")
	protected void set_rev(String _rev) {
		this._rev = _rev;
	}

	@Override
	public String toString() {
		return "Entity [_id=" + _id + ", _rev=" + _rev + "]";
	}

}
