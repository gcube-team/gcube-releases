package org.gcube.portlets.user.td.gwtservice.shared.tr.paging;

import java.io.Serializable;

/**
 * 
 * @author Giancarlo Panichi
 *
 * 
 */
public class OrderInfo implements Serializable {

	private static final long serialVersionUID = 5217530496987635366L;
	protected Direction direction;
	protected String field;

	public OrderInfo() {

	}

	public OrderInfo(Direction direction, String field) {
		this.direction = direction;
		this.field = field;

	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return "OrderInfo [direction=" + direction + ", field=" + field + "]";
	}

}
