package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf;

import java.io.Serializable;

import com.google.gwt.view.client.ProvidesKey;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
public class RangeData implements Serializable, Comparable<RangeData> {

	private static final long serialVersionUID = -6854880075565748144L;

	/**
	 * The key provider that provides the unique ID of a variable.
	 */
	public static final ProvidesKey<RangeData> KEY_PROVIDER = new ProvidesKey<RangeData>() {
		@Override
		public Object getKey(RangeData rangeData) {
			return rangeData == null ? null : rangeData.getId();
		}
	};

	private int id;
	private int n; // number of elements
	private int first; // first value in range
	private int stride; // stride, must be >= 1
	private String name; // optional name

	public RangeData() {
		super();
	}

	public RangeData(int id, int n, int first, int stride, String name) {
		super();
		this.id = id;
		this.n = n;
		this.first = first;
		this.stride = stride;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public int getStride() {
		return stride;
	}

	public void setStride(int stride) {
		this.stride = stride;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int compareTo(RangeData rangeData) {
		return (id < rangeData.id) ? -1 : ((id == rangeData.id) ? 0 : 1);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof RangeData) {
			return id == ((RangeData) o).id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public String toString() {
		return "RangeData [id=" + id + ", n=" + n + ", first=" + first + ", stride=" + stride + ", name=" + name + "]";
	}

}
