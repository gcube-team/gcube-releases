package org.gcube.portlets.user.accountingdashboard.shared.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ScopeData implements Serializable, Comparator<ScopeData>, Comparable<ScopeData> {

	private static final long serialVersionUID = -8445665293115680236L;
	private String id;
	private String name;
	private String scope;
	private ArrayList<ScopeData> children;

	public ScopeData() {
		super();
	}

	public ScopeData(String id, String name, String scope, ArrayList<ScopeData> children) {
		super();
		this.id = id;
		this.name = name;
		this.scope = scope;
		this.children = children;
	}

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

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public ArrayList<ScopeData> getChildren() {
		return children;
	}

	public void setChildren(ArrayList<ScopeData> children) {
		this.children = children;
	}

	@Override
	public int compare(ScopeData o1, ScopeData o2) {
		if (o1 == o2) {
			return 0;
		}

		if (o1 == null) {
			return -1;
		} else {
			if (o2 == null) {
				return 1;
			} else {
				int diff = -1;
				if (o1 != null) {
					if (o1.getScope() != null) {
						diff = ((o2 != null) && (o2.getScope() != null)) ? o1.getScope().compareTo(o2.getScope()) : 1;
					}
				}
				return diff;

			}
		}
	}

	@Override
	public int compareTo(ScopeData o) {
		return compare(this, o);
	}

	@Override
	public String toString() {
		return "ScopeData [id=" + id + ", name=" + name + ", scope=" + scope + ", children=" + children + "]";
	}

}